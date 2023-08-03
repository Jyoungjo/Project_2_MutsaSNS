package com.example.mutsasns.domain.user.dto;

import com.example.mutsasns.domain.user.domain.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;
    private String username;
    private String password;
    @Getter
    private String profileImg;
    @Getter
    private String phone;
    @Getter
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails fromEntity(User user) {
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .profileImg(user.getProfileImg())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    public User getInstance() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setProfileImg(profileImg);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }
}
