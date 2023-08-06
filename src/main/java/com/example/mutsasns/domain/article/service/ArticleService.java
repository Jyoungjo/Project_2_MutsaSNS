package com.example.mutsasns.domain.article.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.repository.ArticleRepository;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.repository.ArticleImageRepository;
import com.example.mutsasns.domain.images.service.ImageHandler;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleImageRepository imageRepository;
    private final ImageHandler imageHandler;

    // 게시글 등록(이미지를 등록해서 글을 작성할수도 있지만 글만 올릴수도 있고, 이미지만 올릴수도 있다. -> /api/users/{userId}/articles
    @Transactional
    public void createArticle(List<MultipartFile> multipartFiles, RequestArticleDto dto, String username, Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

//        Article article = Article.builder()
//                .title(dto.getTitle())
//                .content(dto.getContent())
//                .user(user)
//                .thumbnail(ImageHandler.DEFAULT_IMG_PATH)
//                .build();

        Article article = new Article(
                user, dto.getTitle(), dto.getContent(), ImageHandler.DEFAULT_IMG_PATH
        );

        articleRepository.save(article);

        List<ArticleImage> imageList = imageHandler.parseFileInfo(multipartFiles, username, article);

        if (multipartFiles != null) {
            for (ArticleImage image : imageList) {
                article.addImage(imageRepository.save(image));
            }
            article.setThumbnail(imageList.get(0).getImageUrl());
        }
    }

    // 게시글 목록 조회 -> /api/users/{userId}/articles
    public List<ResponseArticleListDto> readAllArticles(String username, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        List<Article> articleList = articleRepository.findAllByUserAndDeletedFalse(user);

        if (articleList.isEmpty()) throw new CustomException(ErrorCode.ARTICLE_LIST_NOT_FOUND);

        return articleList.stream().map(ResponseArticleListDto::fromEntity).toList();
    }

    // 게시글 단일 조회 -> /api/users/{userId}/articles/{articleId}
    public ResponseArticleDto readArticle(String username, Long userId, Long articleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = articleRepository.findByIdAndDeletedFalse(articleId);

        if (article == null) {
            throw new CustomException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // TODO 댓글, 좋아요 구현해서 코드 리팩토링
        return ResponseArticleDto.fromEntity(article);
    }

    // 게시글 수정(수정 또한 이미지를 수정할수도 있고, 글만 수정할수도 있고, 둘다 수정이 가능하다) -> /api/users/{userId}/articles/{articleId}
    @Transactional
    public void updateArticle(
            List<MultipartFile> multipartFiles, RequestArticleDto dto, String username, Long userId, Long articleId
    ) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        List<ArticleImage> imageList = imageHandler.parseFileInfo(multipartFiles, username, article);

        if (imageList != null) {
            imageRepository.saveAll(imageList);
        }

        article.update(dto.getTitle(), dto.getContent());
        articleRepository.save(article);
    }

    // 게시글 삭제 (soft delete -> deleted = true) -> /api/users/{userId}/articles/{articleId}
    @Transactional
    public void deleteArticle(String username, Long userId, Long articleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        articleRepository.delete(article);
    }

    // 사용자와 토큰 내의 정보 일치 여부 체크 메소드
    private void checkUser(String username, User user) {
        if (!user.getUsername().equals(username)) {
            throw new CustomException(ErrorCode.USER_INCONSISTENT_USERNAME_PASSWORD);
        }
    }
}
