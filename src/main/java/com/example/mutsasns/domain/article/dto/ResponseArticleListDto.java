package com.example.mutsasns.domain.article.dto;

import com.example.mutsasns.domain.article.domain.Article;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseArticleListDto {
    private String username;
    private String title;
    private String thumbnail;
    private String content;

    public static ResponseArticleListDto fromEntity(Article article) {
        return new ResponseArticleListDto(
                article.getUser().getUsername(),
                article.getTitle(),
                article.getThumbnail(),
                article.getContent()
        );
    }
}
