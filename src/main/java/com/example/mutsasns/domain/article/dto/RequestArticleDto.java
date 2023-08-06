package com.example.mutsasns.domain.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RequestArticleDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
