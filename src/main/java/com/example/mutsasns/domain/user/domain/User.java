package com.example.mutsasns.domain.user.domain;

import com.example.mutsasns.domain.user.dto.UserUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Data;

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

    public void updateInfo(UserUpdateRequestDto dto) {
        this.password = dto.getPassword();
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
    }
}
