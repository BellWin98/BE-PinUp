package com.pinup.domain.review.event;

import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceDocument;
import com.pinup.domain.place.repository.elasticsearch.PlaceDocumentRepository;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.ElasticsearchAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {

    private final ElasticsearchAnalyzer analyzer;
    private final PlaceDocumentRepository placeDocumentRepository;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReviewCreatedEvent(Review review) {
        try {
            log.info("리뷰 생성 이벤트 수신: 리뷰 ID={}", review.getId());
            Set<String> newKeywords = analyzer.extractKeywords(review.getContent());
            Place place = review.getPlace();
            PlaceDocument existingDocument = placeDocumentRepository.findById(place.getId()).orElse(null);
            if (existingDocument != null) {
                newKeywords.addAll(existingDocument.getKeywords());
                existingDocument.updateKeywords(newKeywords);
                placeDocumentRepository.save(existingDocument);
            } else {
                PlaceDocument newDocument = PlaceDocument.builder()
                        .id(place.getId())
                        .placeName(place.getName())
                        .keywords(newKeywords)
                        .build();
                placeDocumentRepository.save(newDocument);
            }
        } catch (Exception e) {
            log.error("키워드 추출 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
