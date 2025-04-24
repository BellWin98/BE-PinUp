package com.pinup.global.common.image.util;

import com.pinup.global.exception.FileProcessingException;
import com.pinup.global.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ImageValidator {
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    ));

    // 최대 이미지 크기
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    // 최대 이미지 크기 (픽셀)
    private static final int MAX_WIDTH = 5000;
    private static final int MAX_HEIGHT = 5000;

    public void validate(MultipartFile file) {
        validateFileIsEmpty(file);
        validateFileFormat(file);
        validateFileSize(file);
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            validateImageIsEmpty(image);
//            validateImagePixel(image);
        } catch (IOException e) {
            log.error("이미지 검증 중 오류 발생", e);
            throw new FileProcessingException(ErrorCode.IMAGE_PROCESSING_ERROR);
        }
    }

    private void validateFileIsEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileProcessingException(ErrorCode.IMAGE_IS_EMPTY);
        }
    }

    private void validateFileFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new FileProcessingException(ErrorCode.FILE_EXTENSION_INVALID);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileProcessingException(ErrorCode.IMAGE_SIZE_IS_TOO_BIG);
        }
    }

    private void validateImagePixel(BufferedImage image) {
        if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
            throw new FileProcessingException(ErrorCode.IMAGE_PIXEL_IS_TOO_BIG);
        }
    }

    private void validateImageIsEmpty(BufferedImage image) {
        if (image == null) {
            throw new FileProcessingException(ErrorCode.IMAGE_PROCESSING_ERROR);
        }
    }
}
