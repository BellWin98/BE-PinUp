package com.pinup.domain.review.service;

import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import com.pinup.domain.review.repository.ReviewImageRepository;
import com.pinup.global.common.image.entity.Image;
import com.pinup.global.common.image.repository.ImageRepository;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.FileProcessingException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewImageService {

    private static final int IMAGES_LIMIT = 3;
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final ReviewImageRepository reviewImageRepository;

    public void saveReviewImages(Review review, List<String> requestImageUrls) {
        validateImagesLimit(requestImageUrls);
        List<Image> images = imageRepository.findByImageUrlIn(new HashSet<>(requestImageUrls));
        List<ReviewImage> reviewImages = convertToReviewImages(review, images);
        review.addImages(reviewImages);
    }

    public void updateReviewImages(Review review, List<String> requestReviewImageUrls) {
        List<ReviewImage> existingReviewImages = review.getReviewImages();
        if (requestReviewImageUrls != null && !requestReviewImageUrls.isEmpty()) {
            validateImagesLimit(requestReviewImageUrls);
            // 텍스트 리뷰 -> 포토 리뷰
            if (existingReviewImages.isEmpty()) {
                List<Image> images = imageRepository.findByImageUrlIn(new HashSet<>(requestReviewImageUrls));
                List<ReviewImage> reviewImages = convertToReviewImages(review, images);
                review.addImages(reviewImages);

                return;
            }
            if (isReviewImageListIdentical(existingReviewImages, requestReviewImageUrls)) {
                log.debug("리뷰 이미지 변경 없음: {}", review.getId());
                return;
            }
            // 이미지 분류 (유지/삭제)
            Map<Boolean, List<ReviewImage>> partitionedReviewImages = existingReviewImages.stream()
                    .collect(Collectors.partitioningBy(
                            existingReviewImage -> requestReviewImageUrls.contains(existingReviewImage.getImage().getImageUrl())
                    ));
            List<ReviewImage> reviewImagesToKeep = partitionedReviewImages.get(true);
            List<ReviewImage> reviewImagesToDelete = partitionedReviewImages.get(false);
            if (!reviewImagesToDelete.isEmpty()) {
                List<String> reviewImageKeysToDelete = reviewImagesToDelete.stream()
                        .map(reviewImage -> reviewImage.getImage().getImageKey())
                        .toList();
                reviewImageRepository.deleteByImageKeys(reviewImageKeysToDelete);
                imageRepository.deleteByImageKeys(reviewImageKeysToDelete);
                s3Service.deleteFilesAsync(reviewImageKeysToDelete);
                log.debug("S3에서 기존 리뷰 이미지 삭제: {}", reviewImageKeysToDelete);
            }
            Set<String> reviewImageUrlsToAdd = new HashSet<>(requestReviewImageUrls);
            reviewImagesToKeep.forEach(reviewImage -> reviewImageUrlsToAdd.remove(reviewImage.getImage().getImageUrl()));
            List<Image> imagesToAdd = imageRepository.findByImageUrlIn(reviewImageUrlsToAdd);
            List<ReviewImage> reviewImagesToAdd = convertToReviewImages(review, imagesToAdd);
            review.addImages(reviewImagesToAdd);
        } else {
            // 포토 리뷰 -> 텍스트 리뷰
            if (!existingReviewImages.isEmpty()) {
                List<String> reviewImageKeysToDelete = existingReviewImages.stream()
                        .map(reviewImage -> reviewImage.getImage().getImageUrl())
                        .toList();
                reviewImageRepository.deleteByImageKeys(reviewImageKeysToDelete);
                s3Service.deleteFilesAsync(reviewImageKeysToDelete);
                review.removeAllImages();
                log.debug("모든 리뷰 이미지 삭제: {}", review.getId());
            }
        }
    }

    public void deleteReviewImages(Review review) {
        List<ReviewImage> reviewImages = review.getReviewImages();
        List<String> reviewImageKeysToDelete = reviewImages.stream()
                .map(reviewImage -> reviewImage.getImage().getImageKey())
                .toList();
        s3Service.deleteFilesAsync(reviewImageKeysToDelete);
    }

    private void validateImagesLimit(List<String> imageUrls) {
        if (imageUrls.size() > IMAGES_LIMIT) {
            throw new FileProcessingException(ErrorCode.IMAGES_LIMIT_EXCEEDED);
        }
    }

    private boolean isReviewImageListIdentical(List<ReviewImage> existingImages, List<String> requestImageUrls) {
        if (existingImages.size() != requestImageUrls.size()) {
            return false;
        }
        Set<String> existingUrls = existingImages.stream()
                .map(existingImage -> existingImage.getImage().getImageUrl())
                .collect(Collectors.toSet());

        return existingUrls.containsAll(requestImageUrls);
    }

    private List<ReviewImage> convertToReviewImages(Review review, List<Image> images) {
        List<ReviewImage> reviewImages = new ArrayList<>();
        for (Image image : images) {
            ReviewImage reviewImage = new ReviewImage(review, image);
            reviewImages.add(reviewImage);
        }

        return reviewImages;
    }
}
