package com.pinup.domain.place.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "places", writeTypeHint = WriteTypeHint.FALSE)
@Setting(settingPath = "elastic/place-setting.json")
@Mapping(mappingPath = "elastic/place-mapping.json")
public class PlaceDocument {

    @Id
    private String id;
    private Set<String> keywords = new HashSet<>();

    @Builder
    public PlaceDocument(String id, Set<String> keywords) {
        this.id = id;
        this.keywords = keywords;
    }

    public void updateKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }
}
