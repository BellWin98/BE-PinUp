package com.pinup.enums;

import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {

    ALL("전체", ""),
    RESTAURANT("음식점", "FD6"),
    CAFE("카페", "CE7"),

    ;

    private final String description;
    private final String code;

    public static PlaceCategory getCategory(String category) {
        for (PlaceCategory placeCategory : PlaceCategory.values()) {
            if (placeCategory.name().equalsIgnoreCase(category)) {
                return placeCategory;
            }
        }
        throw new EntityNotFoundException(ErrorCode.PLACE_CATEGORY_NOT_FOUND);
    }
}
