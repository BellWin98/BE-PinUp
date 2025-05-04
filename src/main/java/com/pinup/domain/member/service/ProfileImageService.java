package com.pinup.domain.member.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.global.config.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final S3Service s3Service;

    public void saveProfileImage(Member member, String profileImageUrl) {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            member.updateProfileImage(profileImageUrl);
        }
    }

    public void updateProfileImage(Member member, String newProfileImageUrl) {
        String prevProfileImageUrl = "";
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
            prevProfileImageUrl = member.getProfileImageUrl();
        }
        String prevProfileImageKey = s3Service.extractKeyFromUrl(prevProfileImageUrl);
        if (newProfileImageUrl != null && !newProfileImageUrl.isEmpty()) {
            if (!prevProfileImageUrl.equals(newProfileImageUrl)) {
                s3Service.deleteFile(prevProfileImageKey);
                member.updateProfileImage(newProfileImageUrl);
            }
        } else {
            if (!prevProfileImageUrl.isEmpty()) {
                s3Service.deleteFile(prevProfileImageKey);
                member.removeProfileImage();
            }
        }
    }

    public void deleteProfileImage(Member member) {
        String prevProfileImageKey = s3Service.extractKeyFromUrl(member.getProfileImageUrl());
        s3Service.deleteFile(prevProfileImageKey);
    }
}
