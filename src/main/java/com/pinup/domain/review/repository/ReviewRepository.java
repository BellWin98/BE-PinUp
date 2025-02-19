package com.pinup.domain.review.repository;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
