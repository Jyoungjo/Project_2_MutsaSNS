package com.example.mutsasns.domain.user.domain;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.comment.domain.Comment;
import com.example.mutsasns.domain.user.dto.UserUpdateRequestDto;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Article> articles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public void updateInfo(UserUpdateRequestDto dto) {
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
    }
}
