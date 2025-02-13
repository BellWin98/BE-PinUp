package com.pinup.domain.bookmark.repository.querydsl;

import com.pinup.domain.bookmark.dto.response.BookMarkResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;

import java.util.List;

public interface BookMarkRepositoryQueryDsl {
    List<BookMarkResponse> findAllByMemberAndFilter(
            Member member,
            PlaceCategory category,
            SortType sortType,
            double currentLatitude,
            double currentLongitude
    );
}