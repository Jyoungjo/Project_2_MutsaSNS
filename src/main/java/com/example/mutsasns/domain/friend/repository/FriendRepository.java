package com.example.mutsasns.domain.friend.repository;

import com.example.mutsasns.domain.friend.domain.Friend;
import com.example.mutsasns.domain.user.domain.RequestStatus;
import com.example.mutsasns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByResponseUserAndStatus(User resUser, RequestStatus status);

    Boolean existsByRequestUserAndResponseUserAndStatus(User reqUser, User resUser, RequestStatus status);
}
