package com.pinup.domain.bookmark.controller;

import com.pinup.domain.bookmark.dto.request.BookMarkCreateRequest;
import com.pinup.domain.bookmark.dto.response.BookMarkResponse;
import com.pinup.domain.bookmark.service.BookMarkService;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Tag(name = "북마크 관련 API", description = "북마크 등록, 조회, 삭제")
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @PostMapping
    @Operation(summary = "북마크 등록 API", description = "장소를 북마크에 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "북마크 등록에 성공하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = Long.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> create(
            @Valid @RequestBody BookMarkCreateRequest request
    ) {
        Long bookmarkId = bookMarkService.create(request.getKakaoPlaceId());
        return ResponseEntity.ok(ResultResponse.of(ResultCode.CREATE_BOOKMARK_SUCCESS, bookmarkId));
    }

    @GetMapping("/my")
    @Operation(summary = "내 북마크 목록 조회 API", description = "로그인한 사용자의 북마크 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 북마크 목록 조회에 성공하였습니다.",
                    content = {
                            @Content(schema = @Schema(implementation = BookMarkResponse.class))
                    }
            )
    })
    public ResponseEntity<ResultResponse> getMyBookmarks() {
        List<BookMarkResponse> bookmarks = bookMarkService.getMyBookmarks();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MY_BOOKMARK_SUCCESS, bookmarks));
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "북마크 삭제 API", description = "특정 북마크를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "북마크 삭제에 성공하였습니다."
            )
    })
    public ResponseEntity<ResultResponse> delete(
            @Schema(description = "DB에 등록된 북마크 고유 ID", example = "1")
            @PathVariable Long bookmarkId
    ) {
        bookMarkService.delete(bookmarkId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_BOOKMARK_SUCCESS));
    }
}