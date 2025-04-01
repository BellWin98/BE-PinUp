package com.pinup.domain.place.repository.elasticsearch;

import com.pinup.domain.place.entity.PlaceDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceDocumentRepository extends ElasticsearchRepository<PlaceDocument, Long>, PlaceDocumentCustomRepository {
//    @Query("{ \"bool\": { \"should\": [ { \"match\": { \"keywords\": \"?0\" } } ] } }")
//    List<PlaceDocument> findByKeyword(String keyword);
}
