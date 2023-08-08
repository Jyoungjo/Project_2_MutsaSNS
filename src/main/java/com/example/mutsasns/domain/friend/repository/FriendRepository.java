package com.example.mutsasns.domain.friend.repository;

import com.example.mutsasns.domain.friend.domain.Friend;
import com.example.mutsasns.domain.user.domain.RequestStatus;
import com.example.mutsasns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByResponseUserAndStatus(User resUser, RequestStatus status);

    Boolean existsByRequestUserAndResponseUserAndStatus(User reqUser, User resUser, RequestStatus status);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Friend f " + "WHERE f.isFriend = TRUE AND ((f.requestUser = :user1 AND f.responseUser = :user2) OR "
            + "(f.requestUser = :user2 AND f.responseUser = :user1))")
    Boolean existsFriendBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT f.requestUser FROM Friend f WHERE f.responseUser = :user AND f.isFriend = true " +
            "UNION SELECT f.responseUser FROM Friend f WHERE f.requestUser = :user AND f.isFriend = true")
    List<User> findFriendsByUser(@Param("user") User user);
}
