package com.example.mutsasns.domain.images.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDto {
    private String imageName;
    private String imageUrl;
    private Long fileSize;
}
