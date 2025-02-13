package com.pinup.domain.place.entity;

import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {

    NEAR("가까운 순"),
    LATEST("최신 순"),
    STAR_HIGH("별점 높은 순"),
    STAR_LOW("별점 낮은 순")

    ;

    private final String description;

    public static SortType getSortType(String sort) {
        for (SortType sortType : SortType.values()) {
            if (sortType.name().equalsIgnoreCase(sort)) {
                return sortType;
            }
        }
        throw new EntityNotFoundException(ErrorCode.PLACE_SORT_NOT_FOUND);
    }
}
