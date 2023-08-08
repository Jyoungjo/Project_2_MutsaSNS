package com.example.mutsasns.domain.friend.domain;

import com.example.mutsasns.domain.user.domain.RequestStatus;
import com.example.mutsasns.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.mutsasns.domain.user.domain.RequestStatus.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "user_friends")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "req_user_id")
    private User requestUser;

    @ManyToOne
    @JoinColumn(name = "res_user_id")
    private User responseUser;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean isFriend;

    @Builder
    public Friend(User requestUser, User responseUser) {
        this.requestUser = requestUser;
        this.responseUser = responseUser;
        this.status = PENDING;
        this.isFriend = Boolean.FALSE;
    }

    public void acceptRequest(RequestStatus status) {
        this.status = status;
        this.isFriend = Boolean.TRUE;
    }

    public void rejectRequest(RequestStatus status) {
        this.status = status;
    }
}
