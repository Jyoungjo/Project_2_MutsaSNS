package com.example.mutsasns.domain.user.domain;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.comment.domain.Comment;
import com.example.mutsasns.domain.like_article.domain.LikeArticle;
import com.example.mutsasns.domain.user.dto.RequestUserUpdateDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String profileImg;

    private String email;

    private String phone;

    private Integer followerCount = 0;

    private Integer followingCount = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Article> articles;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<LikeArticle> likeArticles;

    public void updateInfo(RequestUserUpdateDto dto) {
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
    }

    public void addFollower() {
        this.followerCount += 1;
    }

    public void addFollowing() {
        this.followingCount += 1;
    }

    public void cancelFollower() {
        this.followerCount -= 1;
    }

    public void cancelFollowing() {
        this.followingCount -= 1;
    }
}
