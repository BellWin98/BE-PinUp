package com.pinup.domain.article.controller;


import com.pinup.domain.article.dto.request.ArticleCreateRequest;
import com.pinup.domain.article.dto.response.ArticleResponse;
import com.pinup.global.response.ResultCode;
import com.pinup.global.response.ResultResponse;
import com.pinup.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResultResponse> createArticle(
            ArticleCreateRequest articleCreateRequest,
            @RequestParam(value = "multipartFiles") MultipartFile multipartFile
    ) {
        ArticleResponse result = articleService.create(articleCreateRequest, multipartFile);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.CREATE_ARTICLE_SUCCESS, result));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getArticleById(@PathVariable("id") Long id) {
        ArticleResponse result = articleService.findById(id);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_ARTICLE_DETAIL_SUCCESS, result));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getArticles(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        List<ArticleResponse> articles = articleService.getArticles(page, size);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_ARTICLE_LIST_SUCCESS, articles));
    }
}
