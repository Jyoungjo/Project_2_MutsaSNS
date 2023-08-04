package com.example.mutsasns.domain.images.service;


import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.dto.RequestImageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileHandler {
    public static final String DEFAULT_IMG_PATH = "src/main/resources/static/default_img.jpg";

    public List<ArticleImage> parseFileInfo(List<MultipartFile> multipartFiles, String username) throws IOException {
        // 반환할 파일 리스트
        List<ArticleImage> imageList = new ArrayList<>();

        // 파일이 하나라도 존재한다
        if (!CollectionUtils.isEmpty(multipartFiles)) {
            // 파일명을 업로드 한 날짜로 변환하여 저장
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String currentDate = now.format(dateTimeFormatter);

            // 절대 경로 설정
            // 경로 구분자 File.separator 사용
            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

            // 파일 저장할 세부 경로
            String path = "feed" + File.separator + username + File.separator + currentDate;
            File file = new File(path);

            // 디렉토리 존재 여부 체크
            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                // 디렉토리 생성 실패 시
                if (!wasSuccessful) {
                    log.error("디렉토리 생성 실패");
                }
            }

            for (MultipartFile multipartFile : multipartFiles) {
                // 파일 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                // 확장자명 존재 여부 체크
                if (ObjectUtils.isEmpty(contentType)) {
                    break;
                } else { // 그게 아니라면 확장자 설정해준다.
                    if (contentType.contains("image/jpeg")) originalFileExtension = ".jpg";
                    else if (contentType.contains("image/png")) originalFileExtension = ".png";
                    else break;
                }

                // 파일명 중복 없애기 위해 UUID 사용
                UUID uuid = UUID.randomUUID();
                String newFileName = uuid + originalFileExtension;

                // 파일 DTO 생성
                RequestImageDto imageDto = RequestImageDto.builder()
                        .imageName(multipartFile.getOriginalFilename())
                        .imageUrl(path + File.separator + newFileName)
                        .fileSize(multipartFile.getSize())
                        .build();

                // DTO 로 엔티티 생성
                ArticleImage articleImage = new ArticleImage(
                        imageDto.getImageName(),
                        imageDto.getImageUrl(),
                        imageDto.getFileSize());

                imageList.add(articleImage);

                // 업로드 한 파일 데이터를 지정한 파일에 저장
                file = new File(absolutePath + path + File.separator + newFileName);
                multipartFile.transferTo(file);
            }
        }

        return imageList;
    }

    public boolean isPossibleDelete(String path) {
        if (path.contains("default-img")) return true;

        String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
        File file = new File(absolutePath);

        return file.delete();
    }
}