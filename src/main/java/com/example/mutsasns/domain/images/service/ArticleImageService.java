package com.example.mutsasns.domain.images.service;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.dto.ImageDto;
import com.example.mutsasns.domain.images.repository.ArticleImageRepository;
import com.example.mutsasns.global.exception.CustomException;
import com.example.mutsasns.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleImageService {
    private final ArticleImageRepository imageRepository;
    private final ImageHandler imageHandler;

    public void createImage(List<MultipartFile> multipartFiles, String username, Article article) throws IOException {
        List<ArticleImage> imageList = imageHandler.parseFileInfo(multipartFiles, username, article);

        if (multipartFiles != null) {
            for (ArticleImage image : imageList) {
                article.addImage(imageRepository.save(image));
            }
            article.setThumbnail(imageList.get(0).getImageUrl());
        } else {
            article.setThumbnail(ImageHandler.DEFAULT_IMG_PATH);
        }
    }

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

    @Transactional
    public List<MultipartFile> updateImg(
            List<MultipartFile> requestImages,
            Long articleId
    ) {
        // DB에 저장되어있던 이미지 리스트
        List<ArticleImage> dbImageList = this.getAllImagesByArticle(articleId);

        // 새로 전달된 이미지들을 저장할 리스트
        List<MultipartFile> newImgList = new ArrayList<>();

        // DB에 이미지가 존재하지 않을 때
        if (CollectionUtils.isEmpty(dbImageList)) {
            // 전달된 이미지가 하나라도 존재한다면 (이미지를 추가한다는 뜻)
            if (!CollectionUtils.isEmpty(requestImages)) {
                // 해당 이미지를 newImgList 에 추가
                newImgList.addAll(requestImages);
            }
        }
        // DB에 이미지가 하나라도 존재할 때
        else {
            // 전달된 이미지가 하나도 없다면 (현재 등록된 이미지 전부를 삭제했다는 뜻)
            if (CollectionUtils.isEmpty(requestImages)) {
                // 사진 삭제
                for (ArticleImage image : dbImageList) {
                    deleteServerAndDBImg(image);
                }
            }
            // 전달된 이미지가 하나라도 있다면 (추가 및 삭제 여부 체크)
            else {
                // DB에 저장된 파일 원본명 저장할 리스트 생성
                List<String> dbOriginalImgNameList = new ArrayList<>();

                // DB 내 이미지 원본명 추출
                for (ArticleImage image : dbImageList) {
                    ImageDto imageDto = this.getImageById(image.getId());
                    String dbImgName = imageDto.getImageName();

                    // 전달된 파일들 중에 DB 내의 이미지 이름이 존재하지 않는다면
                    if (!requestImages.contains(dbImgName)) {
                        // 사진 삭제
                        deleteServerAndDBImg(image);
                    } else {
                        // 그게 아니라면 추가
                        dbOriginalImgNameList.add(dbImgName);
                    }
                }
                // 전달된 이미지들 체크
                for (MultipartFile reqImg : requestImages) {
                    String multipartOriginalName = reqImg.getOriginalFilename();
                    if (!dbOriginalImgNameList.contains(multipartOriginalName)) {
                        newImgList.add(reqImg);
                    }
                }
            }
        }
        return newImgList;
    }

    private void deleteServerAndDBImg(ArticleImage image) {
        // 서버 내의 사진 및 피드 아이디 폴더 먼저 삭제
        String path = image.getImageUrl();
        String folder = path.substring(0, path.lastIndexOf(File.separator));
        File folderFile = new File(folder);
        if (folderFile.exists()) {
            File[] filesInFolder = folderFile.listFiles();
            for (File file : filesInFolder) {
                file.delete();
            }

            if (filesInFolder.length == 0 && folderFile.isDirectory()) {
                folderFile.delete();
            }
        }
        // 그 다음 DB 삭제
        this.deleteImage(image.getId());
    }
}
