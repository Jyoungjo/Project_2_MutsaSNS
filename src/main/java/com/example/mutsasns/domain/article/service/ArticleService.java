package com.example.mutsasns.domain.article.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.repository.ArticleRepository;
import com.example.mutsasns.domain.images.service.FileHandler;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // 게시글 등록 -> /api/users/{userId}/articles
    public void createArticle(RequestArticleDto dto, String username, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = Article.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .thumbnail(FileHandler.DEFAULT_IMG_PATH)
                .deleted(0)
                .build();

        articleRepository.save(article);
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

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        // TODO 댓글, 좋아요 구현해서 코드 리팩토링
        if (article.getDeleted() != 1) {
            return ResponseArticleDto.builder()
                    .username(user.getUsername())
                    .title(article.getTitle())
                    .images(article.getImages())
                    .content(article.getContent())
                    .build();
        } else throw new CustomException(ErrorCode.ARTICLE_DELETED);
    }

    // 게시글 수정 -> /api/users/{userId}/articles/{articleId}
    public void updateArticle(
            RequestArticleDto dto, String username, Long userId, Long articleId
    ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        article.update(dto.getTitle(), dto.getContent());
        articleRepository.save(article);
    }

    // 게시글 삭제 (soft delete -> deleted = true) -> /api/users/{userId}/articles/{articleId}
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
