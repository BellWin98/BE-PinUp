package com.pinup.global.common;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchAnalyzer {

    private final ElasticsearchClient elasticsearchClient;
    private static final String INDEX_NAME = "places";
    private static final int MIN_WORD_LENGTH = 2;
    private static final int MIN_FREQUENCY = 2;
    private static final int MAX_KEYWORDS_PER_REVIEW = 10;

    public Set<String> extractKeywords(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }
        try {
            AnalyzeResponse response = elasticsearchClient.indices().analyze(a -> a
                    .index(INDEX_NAME)
                    .analyzer("my-nori-analyzer")
                    .text(text)
            );
            Map<String, Long> tokenFrequency = response.tokens().stream()
                    .filter(this::isValidKeywordToken)
                    .collect(Collectors.groupingBy(
                            AnalyzeToken::token,
                            Collectors.counting()
                    ));

            return tokenFrequency.entrySet().stream()
//                    .filter(entry -> entry.getValue() >= MIN_FREQUENCY)
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(MAX_KEYWORDS_PER_REVIEW)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IOException e) {
            log.error("키워드 추출 중 오류 발생", e);
            return Collections.emptySet();
        }
    }

    private boolean isValidKeywordToken(AnalyzeToken token) {
        String term = token.token();
        if (term.matches("\\d+")) {
            return false;
        }
        if (term.length() < MIN_WORD_LENGTH) {
            return false;
        }
        return true;
//        String type = token.type();
//
//        return type != null && (isNoun(token) || isAdjective(token) || isVerb(token));
    }

    private boolean isNoun(AnalyzeToken token) {
        String type = token.type();
        return type != null && (type.contains("NNG") || type.contains("NNP"));
    }

    private boolean isAdjective(AnalyzeToken token) {
        String type = token.type();
        return type != null && type.contains("VA");
    }

    private boolean isVerb(AnalyzeToken token) {
        String type = token.type();
        return type != null && type.contains("VV");
    }
}
