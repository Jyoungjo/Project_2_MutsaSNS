package com.example.mutsasns.domain.article.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.repository.ArticleRepository;
import com.example.mutsasns.domain.follow.domain.Follow;
import com.example.mutsasns.domain.follow.repository.FollowRepository;
import com.example.mutsasns.domain.friend.repository.FriendRepository;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.repository.ArticleImageRepository;
import com.example.mutsasns.domain.images.service.ImageHandler;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.mutsasns.domain.images.service.ImageHandler.*;
import static com.example.mutsasns.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleImageRepository imageRepository;
    private final FollowRepository followRepository;
    private final FriendRepository friendRepository;
    private final ImageHandler imageHandler;

    // 게시글 등록(이미지를 등록해서 글을 작성할수도 있지만 글만 올릴수도 있고, 이미지만 올릴수도 있다. -> /api/articles
    @Transactional
    public Article createArticle(RequestArticleDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Article article = new Article(
                user, dto.getTitle(), dto.getContent(), DEFAULT_IMG_PATH
        );

        log.info("게시글 작성 성공");
        return articleRepository.save(article);
    }

    // 게시글 목록 조회 -> /api/articles
    public List<ResponseArticleListDto> readAllArticles(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<Article> articleList = articleRepository.findAllByUserAndDeletedFalse(user);

        log.info("게시글 목록 조회 성공");
        return articleList.stream().map(ResponseArticleListDto::fromEntity).toList();
    }

    // 게시글 단일 조회 -> /api/articles/{articleId}
    public ResponseArticleDto readArticle(String username, Long articleId) {
        if (!userRepository.existsByUsername(username)) throw new CustomException(USER_NOT_FOUND);

        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new CustomException(ARTICLE_NOT_FOUND));

        log.info("게시글 단일 조회 성공");
        return ResponseArticleDto.fromEntity(article);
    }

    // 팔로워의 게시글 목록 조회
    public List<ResponseArticleListDto> readAllArticlesByFollower(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 팔로우 하고 있는 user 의 팔로워 리스트를 가져온다.
        List<Follow> followList = followRepository.findAllByFollowing(user);

        // 팔로워 리스트에서 팔로워들을 넣을 리스트를 새로 생성
        List<User> followers = new ArrayList<>();

        // 팔로워를 리스트에서 추출
        for (Follow follow : followList) followers.add(follow.getFollower());

        List<Article> articleList = articleRepository.findAllByUserInOrderByIdDesc(followers);

        return articleList.stream().map(ResponseArticleListDto::fromEntity).toList();
    }

    // 친구의 게시글 목록 조회
    public List<ResponseArticleListDto> readAllArticlesByFriend(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<User> friends = friendRepository.findFriendsByUser(user);

        List<Article> articleList = articleRepository.findAllByUserInOrderByIdDesc(friends);

        return articleList.stream().map(ResponseArticleListDto::fromEntity).toList();
    }

    // 게시글 수정(수정 또한 이미지를 수정할수도 있고, 글만 수정할수도 있고, 둘다 수정이 가능하다) -> /api/articles/{articleId}
    @Transactional
    public void updateArticle(
            List<MultipartFile> newImgList, RequestArticleDto dto, String username, Long articleId
    ) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new CustomException(ARTICLE_NOT_FOUND));

        // 작성자가 맞는지 체크
        checkAuthority(user, article);

        List<ArticleImage> addedImageList = imageHandler.parseFileInfo(newImgList, username, article);

        if (addedImageList != null) {
            imageRepository.saveAll(addedImageList);
        }

        article.update(dto.getTitle(), dto.getContent());
        articleRepository.save(article);
        log.info("게시글 수정 성공");
    }

    // 게시글 삭제 (soft delete -> deleted = true) -> /api/articles/{articleId}
    @Transactional
    public void deleteArticle(String username, Long articleId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new CustomException(ARTICLE_NOT_FOUND));

        checkAuthority(user, article);

        articleRepository.delete(article);
        log.info("게시글 삭제 성공");
    }

    // 사용자와 토큰 내의 정보 일치 여부 체크 메소드
    private void checkAuthority(User user, Article article) {
        if (!user.getUsername().equals(article.getUser().getUsername())) {
            throw new CustomException(USER_INCONSISTENT_USERNAME_PASSWORD);
        }
    }
}
