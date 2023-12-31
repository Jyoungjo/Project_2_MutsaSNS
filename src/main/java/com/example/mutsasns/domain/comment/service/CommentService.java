package com.example.mutsasns.domain.comment.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.article.repository.ArticleRepository;
import com.example.mutsasns.domain.comment.domain.Comment;
import com.example.mutsasns.domain.comment.dto.RequestCommentDto;
import com.example.mutsasns.domain.comment.dto.ResponseCommentsDto;
import com.example.mutsasns.domain.comment.repository.CommentRepository;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.mutsasns.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    // 댓글 작성 -> api/articles/{articleId}/comments
    @Transactional
    public void createComment(String username, Long articleId, RequestCommentDto dto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new CustomException(ARTICLE_NOT_FOUND));

        Comment comment = new Comment(
                user, article, dto.getContent()
        );

        commentRepository.save(comment);
        log.info("댓글 등록 성공");
    }

    // 댓글 목록 조회 -> api/articles/{articleId}/comments
    public List<ResponseCommentsDto> readComments(String username, Long articleId) {
        if (!userRepository.existsByUsername(username)) throw new CustomException(USER_NOT_FOUND);
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new CustomException(ARTICLE_NOT_FOUND));

        List<Comment> commentList = commentRepository.findAllByArticleAndDeletedFalse(article);

        log.info("댓글 목록 조회 성공");
        return commentList.stream().map(ResponseCommentsDto::fromEntity).toList();
    }

    // 댓글 수정 -> api/articles/{articleId}/comments/{commentId}
    @Transactional
    public void updateComment(String username, Long articleId, Long commentId, RequestCommentDto dto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!articleRepository.existsById(articleId)) throw new CustomException(ARTICLE_NOT_FOUND);

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        checkAuthority(user, comment);

        comment.update(dto.getContent());
        commentRepository.save(comment);
        log.info("댓글 수정 성공");
    }

    // 댓글 삭제 -> api/articles/{articleId}/comments/{commentId}
    @Transactional
    public void deleteComment(String username, Long articleId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!articleRepository.existsById(articleId)) throw new CustomException(ARTICLE_NOT_FOUND);

        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        checkAuthority(user, comment);

        commentRepository.delete(comment);
        log.info("댓글 삭제 성공");
    }

    // 인증 메소드
    private void checkAuthority(User user, Comment comment) {
        if (!comment.getUser().getUsername().equals(user.getUsername())) {
            throw new CustomException(USER_INCONSISTENT_USERNAME_PASSWORD);
        }
    }
}
