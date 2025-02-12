package com.pinup.domain.bookmark.service;


import com.pinup.domain.bookmark.entity.BookMark;
import com.pinup.domain.bookmark.repository.BookMarkRepository;
import com.pinup.domain.bookmark.response.BookMarkResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.Place;
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
    public Long create(Long placeId) {
        Member loginMember = authUtil.getLoginMember();
        Place place = placeRepository.findById(placeId)
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
    public List<BookMarkResponse> getMyBookmarks() {
        Member loginMember = authUtil.getLoginMember();
        return bookMarkRepository.findAllByMember(loginMember).stream()
                .map(BookMarkResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long bookmarkId) {
        Member loginMember = authUtil.getLoginMember();
        BookMark bookMark = bookMarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOKMARK_NOT_FOUND));

        if (!bookMark.getMember().equals(loginMember)) {
            throw new EntityNotFoundException(ErrorCode.UNAUTHORIZED_BOOKMARK_ACCESS);
        }

        bookMarkRepository.delete(bookMark);
    }
}