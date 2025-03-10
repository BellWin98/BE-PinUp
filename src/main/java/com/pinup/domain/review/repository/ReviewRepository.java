package com.pinup.domain.review.repository;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByMemberAndTypeOrderByCreatedAtDesc(Pageable pageable, Member member, ReviewType reviewType);
}
