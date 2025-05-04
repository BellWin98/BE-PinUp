package com.pinup.domain.review.service;

import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import com.pinup.domain.review.repository.ReviewImageRepository;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.FileProcessingException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewImageService {

    private static final int IMAGES_LIMIT = 3;
    private final S3Service s3Service;
    private final ReviewImageRepository reviewImageRepository;

    public void saveReviewImages(Review review, Set<String> requestImageUrls) {
        validateImagesLimit(requestImageUrls);
        List<ReviewImage> reviewImages = convertToReviewImages(review, requestImageUrls);
        review.addImages(reviewImages);
    }

    public void updateReviewImages(Review review, Set<String> requestReviewImageUrls) {
        List<ReviewImage> prevReviewImages = review.getReviewImages();
        if (requestReviewImageUrls != null && !requestReviewImageUrls.isEmpty()) {
            validateImagesLimit(requestReviewImageUrls);
            // 텍스트 리뷰 -> 포토 리뷰
            if (prevReviewImages.isEmpty()) {
                List<ReviewImage> reviewImages = convertToReviewImages(review, requestReviewImageUrls);
                review.addImages(reviewImages);

                return;
            }
            if (isReviewImageListIdentical(prevReviewImages, requestReviewImageUrls)) {
                log.debug("리뷰 이미지 변경 없음: {}", review.getId());
                return;
            }
            // 이미지 분류 (유지/삭제)
            Map<Boolean, List<ReviewImage>> partitionedReviewImages = prevReviewImages.stream()
                    .collect(Collectors.partitioningBy(
                            prevReviewImage -> requestReviewImageUrls.contains(prevReviewImage.getUrl())
                    ));
            List<ReviewImage> reviewImagesToKeep = partitionedReviewImages.get(true);
            List<ReviewImage> reviewImagesToDelete = partitionedReviewImages.get(false);
            if (!reviewImagesToDelete.isEmpty()) {
                Set<String> reviewImageUrlsToDelete = reviewImagesToDelete.stream()
                        .map(ReviewImage::getUrl)
                        .collect(Collectors.toSet());
                List<String> imageKeysToDelete = reviewImageUrlsToDelete.stream()
                        .map(s3Service::extractKeyFromUrl)
                        .toList();
                reviewImageRepository.deleteByS3Urls(reviewImageUrlsToDelete);
                s3Service.deleteFilesAsync(imageKeysToDelete);
                log.debug("S3에서 기존 리뷰 이미지 삭제: {}", reviewImageUrlsToDelete);
            }
            reviewImagesToKeep.forEach(reviewImage -> requestReviewImageUrls.remove(reviewImage.getUrl()));
            List<ReviewImage> reviewImagesToAdd = convertToReviewImages(review, requestReviewImageUrls);
            review.addImages(reviewImagesToAdd);
        } else {
            // 포토 리뷰 -> 텍스트 리뷰
            if (!prevReviewImages.isEmpty()) {
                Set<String> prevImageUrls = prevReviewImages.stream()
                        .map(ReviewImage::getUrl)
                        .collect(Collectors.toSet());
                List<String> imageKeysToDelete = prevImageUrls.stream()
                        .map(s3Service::extractKeyFromUrl).toList();
                reviewImageRepository.deleteByS3Urls(prevImageUrls);
                s3Service.deleteFilesAsync(imageKeysToDelete);
                review.removeAllImages();
                log.debug("모든 리뷰 이미지 삭제: {}", review.getId());
            }
        }
    }

    public void deleteReviewImages(Review review) {
        List<ReviewImage> reviewImages = review.getReviewImages();
        List<String> reviewImageKeysToDelete = reviewImages.stream()
                .map(reviewImage -> s3Service.extractKeyFromUrl(reviewImage.getUrl()))
                .toList();
        s3Service.deleteFilesAsync(reviewImageKeysToDelete);
    }

    private void validateImagesLimit(Set<String> imageUrls) {
        if (imageUrls.size() > IMAGES_LIMIT) {
            throw new FileProcessingException(ErrorCode.IMAGES_LIMIT_EXCEEDED);
        }
    }

    private boolean isReviewImageListIdentical(List<ReviewImage> prevImages, Set<String> requestImageUrls) {
        if (prevImages.size() != requestImageUrls.size()) {
            return false;
        }
        Set<String> prevImageUrls = prevImages.stream()
                .map(ReviewImage::getUrl)
                .collect(Collectors.toSet());

        return prevImageUrls.containsAll(requestImageUrls);
    }

    private List<ReviewImage> convertToReviewImages(Review review, Set<String> reviewImageUrls) {
        List<ReviewImage> reviewImages = new ArrayList<>();
        for (String reviewImageUrl : reviewImageUrls) {
            ReviewImage reviewImage = new ReviewImage(review, reviewImageUrl);
            reviewImages.add(reviewImage);
        }

        return reviewImages;
    }
}
