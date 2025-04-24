package com.pinup.global.common.image.repository;

import com.pinup.global.common.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageUrl(String imageUrl);
    List<Image> findByImageUrlIn(Collection<String> imageUrls);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.imageKey IN :imageKeys")
    void deleteByImageKeys(List<String> imageKeys);
}
