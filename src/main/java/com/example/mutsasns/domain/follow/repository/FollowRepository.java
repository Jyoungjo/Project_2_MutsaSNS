package com.example.mutsasns.domain.follow.repository;

import com.example.mutsasns.domain.follow.domain.Follow;
import com.example.mutsasns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findAllByFollowing(User following);
}
