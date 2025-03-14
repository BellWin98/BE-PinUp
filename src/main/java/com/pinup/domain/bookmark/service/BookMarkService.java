package com.pinup.domain.bookmark.service;

import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.bookmark.repository.BookMarkRepository;
import com.pinup.domain.bookmark.dto.response.BookMarkResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.place.entity.SortType;
import com.pinup.domain.place.repository.PlaceRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final PlaceRepository placeRepository;
    private final AuthUtil authUtil;

    @Transactional
    public Long create(String kakaoPlaceId) {
        Member loginMember = authUtil.getLoginMember();
        Place place = placeRepository.findByKakaoPlaceId(kakaoPlaceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PLACE_NOT_FOUND));

        if (bookMarkRepository.existsByMemberAndPlace(loginMember, place)) {
            throw new EntityAlreadyExistException(ErrorCode.ALREADY_EXIST_BOOKMARK);
        }

        BookMark bookMark = BookMark.builder()
                .member(loginMember)
                .place(place)
                .build();

        return bookMarkRepository.save(bookMark).getId();
    }

    @Transactional(readOnly = true)
    public List<BookMarkResponse> getFilteredBookmarks(
            String category,
            String sort,
            Double currentLatitude,
            Double currentLongitude
    ) {
        Member loginMember = authUtil.getLoginMember();
        PlaceCategory placeCategory = PlaceCategory.getCategory(category);
        SortType sortType = SortType.getSortType(sort);

        return bookMarkRepository.findAllByMemberAndFilter(
                loginMember,
                placeCategory,
                sortType,
                currentLatitude,
                currentLongitude
        );
    }

    @Transactional
    public void delete(String kakaoPlaceId) {
        Member loginMember = authUtil.getLoginMember();
        Place place = placeRepository.findByKakaoPlaceId(kakaoPlaceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PLACE_NOT_FOUND));

        BookMark bookMark = bookMarkRepository.findByMemberAndPlace(loginMember, place)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookMarkRepository.delete(bookMark);
    }
}