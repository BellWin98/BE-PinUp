package com.pinup.domain.place.repository;

import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.repository.querydsl.PlaceRepositoryQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryQueryDsl {
    Optional<Place> findByKakaoMapId(String kakaoMapId);
}
