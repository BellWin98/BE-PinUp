package com.pinup.domain.member.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.entity.ProfileImage;
import com.pinup.global.common.image.entity.Image;
import com.pinup.global.common.image.repository.ImageRepository;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.FileProcessingException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;

    public void saveProfileImage(Member member, String profileImageUrl) {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Image image = imageRepository.findByImageUrl(profileImageUrl)
                    .orElseThrow(() -> new FileProcessingException(ErrorCode.IMAGE_IS_EMPTY));
            member.updateProfileImage(new ProfileImage(image));
        }
    }

    public void updateProfileImage(Member member, String newProfileImageUrl) {
        Image prevImage = member.getProfileImage().getImage();
        String prevProfileImageUrl = prevImage.getImageUrl();
        if (newProfileImageUrl != null && !newProfileImageUrl.isEmpty()) {
            if (!prevProfileImageUrl.equals(newProfileImageUrl)) {
                Image newImage = imageRepository.findByImageUrl(newProfileImageUrl)
                        .orElseThrow(() -> new FileProcessingException(ErrorCode.IMAGE_IS_EMPTY));
                s3Service.deleteFile(prevImage.getImageKey());
                imageRepository.delete(prevImage);
                member.getProfileImage().updateImage(newImage);
            }
        } else {
            if (!prevImage.getImageKey().isEmpty()) {
                s3Service.deleteFile(prevImage.getImageKey());
                imageRepository.delete(prevImage);
                member.removeProfileImage();
            }
        }
    }

    public void deleteProfileImage(Member member) {
        Image image = member.getProfileImage().getImage();
        s3Service.deleteFile(image.getImageKey());
    }
}
