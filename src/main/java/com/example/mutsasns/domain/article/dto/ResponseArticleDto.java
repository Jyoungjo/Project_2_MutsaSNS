package com.example.mutsasns.domain.article.dto;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.comment.domain.Comment;
import com.example.mutsasns.domain.comment.dto.ResponseCommentsDto;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.dto.ResponseImageDto;
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
    private List<ResponseImageDto> images;
    private String content;
    // 댓글들
    private List<ResponseCommentsDto> comments;
    // 좋아요

    public static ResponseArticleDto fromEntity(Article article) {
        ResponseArticleDto dto = new ResponseArticleDto();

        dto.setId(article.getId());
        dto.setUsername(article.getUser().getUsername());
        dto.setTitle(article.getTitle());
        dto.setImages(new ArrayList<>());
        dto.setComments(new ArrayList<>());
        dto.setContent(article.getContent());

        for (ArticleImage image : article.getImages()) {
            dto.getImages().add(ResponseImageDto.builder()
                    .id(image.getId())
                    .articleId(image.getArticle().getId())
                    .imageName(image.getImageName())
                    .imageUrl(image.getImageUrl())
                    .build()
            );
        }

        for (Comment comment : article.getComments()) {
            dto.getComments().add(ResponseCommentsDto.builder()
                    .id(comment.getId())
                    .articleId(comment.getArticle().getId())
                    .username(comment.getUser().getUsername())
                    .content(comment.getContent())
                    .build()
            );
        }

        return dto;
    }
}
