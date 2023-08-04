package com.example.mutsasns.domain.images.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.article.repository.ArticleRepository;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.repository.ArticleImageRepository;
import com.example.mutsasns.domain.user.domain.User;
import com.example.mutsasns.domain.user.repository.UserRepository;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FileHandler fileHandler;

    // 게시글 이미지 업로드 -> /api/users/{userId}/articles/{articleId}/images
    public void updateImage(
            List<MultipartFile> multipartFiles, String username, Long userId, Long articleId
    ) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        List<ArticleImage> imageList = fileHandler.parseFileInfo(multipartFiles, username);

        if (imageList.isEmpty()) throw new CustomException(ErrorCode.ARTICLE_IMAGE_UNSELECTED);

        for (ArticleImage image : imageList) {
            article.addImage(imageRepository.save(image));
        }
        article.setThumbnail(imageList.get(0).getImageUrl());

        // TODO dto 를 통해 어느 사진을 삭제해서 수정할건지 구현하기
//        else if (dto.getImageIdx() != null) {
//            article.getImages().remove(dto.getImageIdx());
//        }

        articleRepository.save(article);
    }

    // 게시글 이미지 삭제 -> /api/users/{userId}/articles/{articleId}/images/{imageId}
    public void deleteImage(String username, Long userId, Long articleId, Long imageId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        checkUser(username, user);

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        ArticleImage image = imageRepository.findById(imageId).orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_IMAGE_NOT_FOUND));

        if (!fileHandler.isPossibleDelete(image.getImageUrl())) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (image.getArticle() != article) throw new CustomException(ErrorCode.ARTICLE_IMAGE_NOT_MATCH);

        if (article.getThumbnail().equals(image.getImageUrl())) {
            List<ArticleImage> imageList = imageRepository.findArticleImagesByArticleIdOrderById(articleId);

            if (!imageList.isEmpty()) {
                article.setThumbnail(imageList.get(1).getImageUrl());
            }

            // 이미지 없을 경우 -> 기본이미지 설정
            article.setThumbnail(FileHandler.DEFAULT_IMG_PATH);
        }

        imageRepository.delete(image);
    }

    // 사용자와 토큰 내의 정보 일치 여부 체크 메소드
    private void checkUser(String username, User user) {
        if (!user.getUsername().equals(username)) {
            throw new CustomException(ErrorCode.USER_INCONSISTENT_USERNAME_PASSWORD);
        }
    }
}
