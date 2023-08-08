package com.example.mutsasns.domain.follow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseFollowDto {
    private String targetUsername;
    private String myUsername;
    private Integer followVariation;
    private Integer followingVariation;
}
