package com.example.mutsasns.domain.like_article.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.article.repository.ArticleRepository;
import com.example.mutsasns.domain.like_article.domain.LikeArticle;
import com.example.mutsasns.domain.like_article.dto.ResponseLikeDto;
import com.example.mutsasns.domain.like_article.repository.LikeArticleRepository;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.mutsasns.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeArticleService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final LikeArticleRepository likeRepository;

    // 좋아요 관련 -> api/articles/{articleId}/like
    @Transactional
    public ResponseLikeDto pushLike(String username, Long articleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Article article = articleRepository.findByIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new CustomException(ARTICLE_NOT_FOUND));

        if (article.getUser().getUsername().equals(user.getUsername()))
            throw new CustomException(LIKE_NOT_PUSH_MY_ARTICLE);

        Optional<LikeArticle> optionalLike = likeRepository.findByUserAndArticle(user, article);

        if (optionalLike.isPresent()) {
            likeRepository.delete(optionalLike.get());
            article.minusCount();
            return ResponseLikeDto.builder()
                    .username(user.getUsername())
                    .articleId(articleId)
                    .variation(-1)
                    .build();
        }

        LikeArticle likeArticle = LikeArticle.builder()
                .user(user)
                .article(article)
                .build();

        likeRepository.save(likeArticle);
        article.plusCount();

        return ResponseLikeDto.builder()
                .username(user.getUsername())
                .articleId(articleId)
                .variation(+1)
                .build();
    }
}
