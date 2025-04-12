package com.pinup.global.common.image.controller;

import com.pinup.global.common.image.service.ImageService;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "이미지 업로드 API", description = "이미지 단건/다건 업로드")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 단건 업로드", description = "type: profiles, reviews 중 택1")
    @PostMapping(value = "/{type}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> uploadImage(
            @PathVariable String type,
            @RequestPart MultipartFile image
    ) {
        String imageUrl = imageService.uploadImage(type, image);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.UPLOAD_IMAGE_SUCCESS, imageUrl));
    }

    @Operation(summary = "이미지 다건 업로드", description = "type: profiles, reviews 중 택1")
    @PostMapping(value = "/{type}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> uploadImages(
            @PathVariable String type,
            @RequestPart List<MultipartFile> images
    ) {
        List<String> imageUrls = imageService.uploadImages(type, images);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.UPLOAD_IMAGE_SUCCESS, imageUrls));
    }
}
