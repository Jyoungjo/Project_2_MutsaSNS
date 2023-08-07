package com.example.mutsasns.domain.like_article.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseLikeDto {
    private String username;
    private Long articleId;
    private Integer variation;
}
