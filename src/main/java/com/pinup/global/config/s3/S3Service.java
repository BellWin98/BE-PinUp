package com.pinup.global.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pinup.global.common.image.util.ImageValidator;
import com.pinup.global.exception.*;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;
    private final ImageValidator imageValidator;

    public S3FileInfo uploadFile(String fileType, MultipartFile file) {
        imageValidator.validate(file);
        String originalFilename = null;
        String uploadFileName = null;
        String uploadFileUrl;
        if (file.getOriginalFilename() != null) {
            originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            uploadFileName = UUID.randomUUID() + extension;
        }
        String uploadFilePath = fileType + "/" + getFolderName();
        String key = uploadFilePath + "/" + uploadFileName;
        try (InputStream inputStream = file.getInputStream()){
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());
            // S3에 폴더 및 파일 업로드
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata));
            uploadFileUrl = getFileUrl(key);

            return new S3FileInfo(key, uploadFileUrl, originalFilename);
        } catch (IOException e){
            throw new FileProcessingException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public String getFileUrl(String key) {
        return amazonS3Client.getUrl(bucket, key).toString();
    }

    private String getFolderName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String str = sdf.format(date);

        return str.replace("-", "/");
    }

    /*public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.startsWith("https://lh3.googleusercontent.com")) {
            return;
        }
        try {
            String key = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
            amazonS3Client.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException e) {
            log.error("파일 삭제 실패: Amazon S3 서비스 에러", e);
            throw new FileProcessingException(ErrorCode.FILE_DELETE_ERROR);
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            throw new FileProcessingException(ErrorCode.FILE_DELETE_ERROR);
        }
    }*/

    public void deleteFile(String imageKey) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, imageKey);
        amazonS3Client.deleteObject(deleteObjectRequest);
        log.info("Image deleted successfully. Key: {}", imageKey);
    }

    public void deleteFiles(List<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            return;
        }
        for (String imageKey : imageKeys) {
            deleteFile(imageKey);
        }

        log.info("Deleted {} images from S3", imageKeys.size());
    }

    @Async("imageProcessingExecutor")
    public void deleteFilesAsync(List<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            return;
        }
        log.info("Starting async deletion of {} images", imageKeys.size());
        deleteFiles(imageKeys);
        log.info("Completed async deletion of images");
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            int startIndex = fileUrl.indexOf("amazonaws.com/") + "amazonaws.com/".length();
            String path = fileUrl.substring(startIndex);
            if (path.startsWith(bucket + "/")) {
                path = path.substring(bucket.length() + 1);
            }
            log.info("Original URL: {}", fileUrl);
            log.info("Extracted key: {}", path);

            return path;
        } catch (StringIndexOutOfBoundsException e) {
            log.error("잘못된 파일 URL 형식: {}", fileUrl, e);
            throw new FileProcessingException(ErrorCode.FILE_EXTENSION_INVALID);
        }
    }

    public record S3FileInfo(String fileKey, String fileUrl, String originFilename) {

    }
}
