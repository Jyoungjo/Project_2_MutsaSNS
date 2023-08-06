package com.example.mutsasns.domain.images.domain;

import com.example.mutsasns.domain.article.domain.Article;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class ArticleImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private String imageName; // 파일 원본명

    private String imageUrl; // 파일 저장 경로

    private Long fileSize;

    @Builder
    public ArticleImage(String imageName, String imageUrl, Long fileSize) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.fileSize = fileSize;
    }

    public void setArticle(Article article) {
        this.article = article;

        // 게시글에 현재 이미지가 존재하지 않는다면
        if (!article.getImages().contains(this)) {
            // 게시글에 이미지를 추가한다.
            article.getImages().add(this);
        }
    }
}
