package com.pinup.domain.place.repository.elasticsearch;

import com.pinup.domain.place.entity.PlaceDocument;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlaceDocumentCustomRepositoryImpl implements PlaceDocumentCustomRepository{

    private final ElasticsearchOperations elasticsearchOperations;

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
}
