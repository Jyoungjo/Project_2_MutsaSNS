package com.example.mutsasns.domain.article.dto;

import com.example.mutsasns.domain.images.domain.ArticleImage;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ResponseArticleDto {
    private String username;
    private String title;
    private List<ArticleImage> images;
    private String content;
    // 댓글들
    // 좋아요
}
