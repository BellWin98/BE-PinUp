package com.pinup.domain.review.repository;

import com.pinup.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    @Modifying
    @Query("DELETE FROM ReviewImage ri WHERE ri.url IN :s3Urls")
    void deleteByS3Urls(Set<String> s3Urls);
}
