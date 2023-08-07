package com.example.mutsasns.domain.comment.controller;

import com.example.mutsasns.domain.comment.dto.RequestCommentDto;
import com.example.mutsasns.domain.comment.dto.ResponseCommentsDto;
import com.example.mutsasns.domain.comment.service.CommentService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto> create(
            @RequestBody RequestCommentDto dto,
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        commentService.createComment(username, articleId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_COMMENT));
    }

    @GetMapping
    public ResponseEntity<List<ResponseCommentsDto>> readAll(
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(commentService.readComments(username, articleId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto> update(
            @RequestBody RequestCommentDto dto,
            @PathVariable("articleId") Long articleId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        commentService.updateComment(username, articleId, commentId, dto);
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.UPDATE_COMMENT));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable("articleId") Long articleId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        commentService.deleteComment(username, articleId, commentId);
        return ResponseEntity.noContent().build();
    }
}
