package com.pinup.domain.review.repository;

import com.pinup.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    @Query("SELECT ri.imageKey FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    List<String> findImageKeysByReviewId(@Param("reviewId") Long reviewId);

    void deleteAllByReviewId(Long reviewId);
}
