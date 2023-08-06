package com.example.mutsasns.domain.article.dto;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseArticleDto {
    private Long id;
    private String username;
    private String title;
    private List<String> images;
    private String content;
    // 댓글들
    // 좋아요

    public static ResponseArticleDto fromEntity(Article article) {
        ResponseArticleDto dto = new ResponseArticleDto();

        dto.setId(article.getId());
        dto.setUsername(article.getUser().getUsername());
        dto.setTitle(article.getTitle());
        dto.setImages(new ArrayList<>());
        dto.setContent(article.getContent());

        for (ArticleImage image : article.getImages()) {
            dto.getImages().add(image.getImageUrl());
        }

        return dto;
    }
}
