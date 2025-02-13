package com.pinup.domain.bookmark.repository;

import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.bookmark.repository.querydsl.BookMarkRepositoryQueryDsl;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long>, BookMarkRepositoryQueryDsl {
    boolean existsByMemberAndPlace(Member member, Place place);
    Optional<BookMark> findByMemberAndPlace(Member member, Place place);
    List<BookMark> findAllByMember(Member member);

}