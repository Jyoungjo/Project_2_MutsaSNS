package com.example.mutsasns.domain.article.domain;

import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@SQLDelete(sql = "UPDATE article SET deleted = true WHERE id = ?")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 여러 게시글을 한명의 유저가 등록 가능
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String content;
    private String thumbnail;
    private boolean deleted = Boolean.FALSE;

    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ArticleImage> images = new ArrayList<>();

    @Builder
    public Article(User user, String title, String content, String thumbnail) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
    }

    public void setThumbnail(String path) {
        this.thumbnail = path;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addImage(ArticleImage articleImage) {
        this.images.add(articleImage);

        // 게시글에 이미지가 저장되어있지 않다면?
        if (articleImage.getArticle() != this) {
            // 게시글에 이미지를 저장한다.
            articleImage.setArticle(this);
        }
    }
}
