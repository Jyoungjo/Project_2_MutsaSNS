package com.example.mutsasns.domain.user.service;

import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.dto.CustomUserDetails;
import com.example.mutsasns.domain.user.exception.ExistentUserException;
import com.example.mutsasns.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당 계정을 찾을 수 없습니다."));

        return CustomUserDetails.fromEntity(user);
    }

    @Override
    public void createUser(UserDetails user) {
        if (this.userExists(user.getUsername())) {
            log.error("중복 username");
            throw new ExistentUserException();
        }

        userRepository.save(((CustomUserDetails) user).getInstance());
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
