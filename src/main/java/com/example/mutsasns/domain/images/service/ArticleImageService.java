package com.example.mutsasns.domain.images.service;

import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.dto.ImageDto;
import com.example.mutsasns.domain.images.repository.ArticleImageRepository;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleImageService {
    private final ArticleImageRepository imageRepository;

    public ImageDto getImageById(Long imageId) {
        ArticleImage image = imageRepository.findById(imageId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_IMAGE_NOT_FOUND));

        return ImageDto.builder()
                .imageName(image.getImageName())
                .imageUrl(image.getImageUrl())
                .fileSize(image.getFileSize())
                .build();
    }

    public List<ArticleImage> getAllImagesByArticle(Long articleId) {
        return imageRepository.findAllByArticleId(articleId);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ArticleImage image = imageRepository.findById(imageId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_IMAGE_NOT_FOUND));
        imageRepository.delete(image);
    }
}
