package com.pinup.global.common.image.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.common.image.entity.Image;
import com.pinup.global.common.image.repository.ImageRepository;
import com.pinup.global.config.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final AuthUtil authUtil;
    private final S3Service s3Service;

    @Transactional
    public String uploadImage(String imageType, MultipartFile image) {
        S3Service.S3FileInfo s3FileInfo = s3Service.uploadFile(imageType, image);
        Image savedImage = imageRepository.save(new Image(s3FileInfo));

        return savedImage.getImageUrl();
    }

    @Transactional
    public List<String> uploadImages(String imageType, List<MultipartFile> images) {
        Member loginMember = authUtil.getLoginMember();
        log.info("{}: tries to upload images", loginMember.getNickname());
        List<String> uploadedImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            S3Service.S3FileInfo s3FileInfo = s3Service.uploadFile(imageType, image);
            Image savedImage = imageRepository.save(new Image(s3FileInfo));
            uploadedImageUrls.add(savedImage.getImageUrl());
        }

        return uploadedImageUrls;
    }
}
