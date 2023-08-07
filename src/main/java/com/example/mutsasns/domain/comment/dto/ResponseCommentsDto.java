package com.example.mutsasns.domain.comment.dto;

import com.example.mutsasns.domain.comment.domain.Comment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCommentsDto {
    private Long id;
    private String username;
    private String content;

    public static ResponseCommentsDto fromEntity(Comment comment) {
        return new ResponseCommentsDto(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getContent()
        );
    }
}
