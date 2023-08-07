package com.example.mutsasns.domain.images.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseImageDto {
    private Long id;
    private String imageName;
    private String imageUrl;
}
