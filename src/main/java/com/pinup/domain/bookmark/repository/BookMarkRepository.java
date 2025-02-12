package com.pinup.domain.bookmark.repository;


import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    List<BookMark> findAllByMember(Member member);
    boolean existsByMemberAndPlace(Member member, Place place);
}