package com.example.mutsasns.domain.images.dto;

import com.example.mutsasns.domain.images.domain.ArticleImage;
import lombok.Getter;

@Getter
public class ResponseImageDto {
    private Long fileId;

    public ResponseImageDto(ArticleImage articleImage) {
        this.fileId = articleImage.getId();
    }
}
