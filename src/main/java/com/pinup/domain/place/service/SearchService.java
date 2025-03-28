package com.pinup.domain.place.service;

import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.dto.request.SearchRequestDto;
import com.pinup.domain.place.dto.response.PlaceResponseDto;
import com.pinup.domain.place.dto.response.SearchResponseDto;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceDocument;
import com.pinup.domain.place.repository.elasticsearch.PlaceDocumentRepository;
import com.pinup.domain.place.repository.PlaceRepository;
import com.pinup.domain.review.repository.ReviewRepository;
import com.pinup.global.common.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final AuthUtil authUtil;
    private final PlaceRepository placeRepository;
    private final PlaceDocumentRepository placeDocumentRepository;
    private final ReviewRepository reviewRepository;
    private final FriendShipRepository friendShipRepository;

    @Transactional(readOnly = true)
    public List<PlaceResponseDto> searchPlacesByKeyword(SearchRequestDto requestDto) {
        Member loginMember = authUtil.getLoginMember();
        List<Long> friendIds = getFriendList(loginMember);
        List<Long> targetIds = new ArrayList<>(friendIds);
        targetIds.add(loginMember.getId());
        List<PlaceDocument> placeDocuments = placeDocumentRepository.findByKeyword(requestDto.getKeyword());
        List<Place> filteredPlaces = placeRepository.findPlacesByKeywordAndTargetIds(
                requestDto.getKeyword(),
                targetIds,
                requestDto.getCurrLat(),
                requestDto.getCurrLng(),
                requestDto.getRadius()
        );

        if (filteredPlaces.isEmpty()) {
            return Collections.emptyList();
        }

        return filteredPlaces.stream()
                .map(place -> PlaceResponseDto.of(place, targetIds))
                .toList();
    }

    private List<Long> getFriendList(Member member) {
        return friendShipRepository.findAllByMember(member).stream()
                .map(friendShip -> friendShip.getFriend().getId())
                .toList();
    }

    private SearchResponseDto createEmptyResponse(int page, int size) {
        return new SearchResponseDto(
                Collections.emptyList(),
                SearchResponseDto.PaginationDto.builder()
                        .page(page)
                        .size(size)
                        .totalElements(0L)
                        .totalPages(0)
                        .build());
    }
}
