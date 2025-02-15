package com.pinup.domain.article.service;

import com.pinup.domain.article.dto.request.ArticleCreateRequest;
import com.pinup.domain.article.dto.response.ArticleResponse;
import com.pinup.domain.article.entity.Article;
import com.pinup.domain.member.entity.Member;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.pinup.global.config.s3.S3Service;
import com.pinup.domain.article.repository.ArticleRepository;
import com.pinup.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final String FILE_TYPE = "articles";

    private final S3Service s3Service;

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest articleCreateRequest, MultipartFile multipartFile) {
        Member currentMember = getCurrentMember();
        String uploadedFileUrl = s3Service.uploadFile(FILE_TYPE, multipartFile);

        Article newArticle = Article.builder()
                .imageUrl(uploadedFileUrl)
                .title(articleCreateRequest.getArticleTitle())
                .author(currentMember)
                .build();
        articleRepository.save(newArticle);

        return ArticleResponse.from(newArticle);
    }

    @Transactional(readOnly = true)
    public ArticleResponse findById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        return ArticleResponse.from(article);
    }

    private Member getCurrentMember() {
        String socialId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Article> articlePage = articleRepository.findAllByOrderByCreatedAtDesc(pageable);

        return articlePage.stream()
                .map(ArticleResponse::from)
                .collect(Collectors.toList());
    }
}

