package com.pinup.domain.place.repository.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import com.pinup.domain.place.entity.PlaceDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PlaceDocumentCustomRepositoryImpl implements PlaceDocumentCustomRepository{

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;
    private static final String PLACE_INDEX = "places";

    @Override
    public List<PlaceDocument> findAutoCompleteSuggestion(String keyword, int size) {
        try {
            SearchResponse<PlaceDocument> response = elasticsearchClient.search(
                    s -> autoCompleteSuggestionQuery(s, keyword, size), PlaceDocument.class
            );

            return response.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            log.error("Elasticsearch Error");
        }

        return null;
    }

/*    @Override
    public List<PlaceDocument> findByKeyword(String keyword) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("keywords", keyword).boost(3.0f));
        Query query = new StringQuery(boolQuery.toString());
        SearchHits<PlaceDocument> searchHits = elasticsearchOperations.search(query, PlaceDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }*/

    private ObjectBuilder<SearchRequest> autoCompleteSuggestionQuery(
            SearchRequest.Builder builder, String keyword, int size
    ) {
        Query matchPhraseQuery = MatchPhraseQuery.of(mp ->
                mp.field("placeName")
                        .query(keyword)
                        .slop(1)
                        .boost(3.0f))._toQuery();
        Query matchNoriQuery = MatchQuery.of(m ->
                m.field("placeName.nori")
                        .query(keyword)
                        .operator(Operator.And)
                        .boost(2.0f)
                        .fuzziness("1"))._toQuery();
        Query matchNgramQuery = MatchQuery.of(m ->
                m.field("placeName.ngram")
                        .query(keyword))._toQuery();
        Query boolQuery = BoolQuery.of(b ->
                b.should(matchPhraseQuery)
                        .should(matchNoriQuery)
                        .should(matchNgramQuery)
                        .minimumShouldMatch("1"))._toQuery();

        return builder.index(PLACE_INDEX).size(size).query(boolQuery);
    }
}
