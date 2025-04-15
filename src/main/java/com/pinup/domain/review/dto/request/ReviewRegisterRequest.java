package com.pinup.domain.review.dto.request;

import com.pinup.domain.place.dto.request.PlaceRequest;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ReviewRegisterRequest {

    @Valid
    private ReviewRequest reviewRequest;

    @Valid
    private PlaceRequest placeRequest;
}
