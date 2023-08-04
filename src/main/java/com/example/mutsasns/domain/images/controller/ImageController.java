package com.example.mutsasns.domain.images.controller;

import com.example.mutsasns.domain.images.service.ImageService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/{userId}/articles/{articleId}")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/images")
    public ResponseEntity<ResponseDto> postImg(
            @RequestParam("image") List<MultipartFile> multipartFiles, Authentication authentication,
            @PathVariable("articleId") Long articleId, @PathVariable("userId") Long userId
    ) throws IOException {
        String username = authentication.getName();
        imageService.updateImage(multipartFiles, username, userId, articleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_ARTICLE_IMAGE));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ResponseDto> deleteImg(
            Authentication authentication,
            @PathVariable("userId") Long userId,
            @PathVariable("articleId") Long articleId,
            @PathVariable("imageId") Long imageId
    ) {
        String username = authentication.getName();
        imageService.deleteImage(username, userId, articleId, imageId);
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.DELETE_ARTICLE_IMAGE));
    }
}
