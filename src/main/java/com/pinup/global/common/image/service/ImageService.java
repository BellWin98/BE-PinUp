package com.pinup.global.common.image.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.global.common.AuthUtil;
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

    private final AuthUtil authUtil;
    private final S3Service s3Service;

    @Transactional
    public String uploadImage(String imageType, MultipartFile image) {
        return s3Service.uploadFile(imageType, image);
    }

    @Transactional
    public List<String> uploadImages(String imageType, List<MultipartFile> images) {
        Member loginMember = authUtil.getLoginMember();
        log.info("{}: tries to upload images", loginMember.getNickname());
        List<String> uploadedImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            uploadedImageUrls.add(s3Service.uploadFile(imageType, image));
        }

        return uploadedImageUrls;
    }
}
