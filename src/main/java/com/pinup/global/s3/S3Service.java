package com.pinup.global.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pinup.exception.FileDeleteErrorException;
import com.pinup.exception.FileExtensionInvalidException;
import com.pinup.exception.FileUploadErrorException;
import com.pinup.exception.InvalidFileUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    @Autowired
    public S3Service(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String uploadFile(String fileType, MultipartFile multipartFile) {

        String uploadFileName = "";
        String uploadFileUrl;

        if (multipartFile.getOriginalFilename() != null) {
            String originalFilename = multipartFile.getOriginalFilename();
            uploadFileName = getUuidFileName(originalFilename);
        }

        try (InputStream inputStream = multipartFile.getInputStream()){

            String uploadFilePath = fileType + "/" + getFolderName();
            String key = uploadFilePath + "/" + uploadFileName;

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            // S3에 폴더 및 파일 업로드
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata));
            uploadFileUrl = getFileUrl(key);

        } catch (IOException e){
            throw new FileUploadErrorException();
        }
        return uploadFileUrl;
    }

    public String getFileUrl(String key) {
        return amazonS3Client.getUrl(bucket, key).toString();
    }

    private String getUuidFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        validateFileFormat(extension);

        return UUID.randomUUID() + "." + extension;
    }

    private void validateFileFormat(String extension) {
        List<String> fileValidate = new ArrayList<>();
        fileValidate.add("jpg");
        fileValidate.add("jpeg");
        fileValidate.add("png");
        fileValidate.add("gif");

        if (!fileValidate.contains(extension)) {
            throw new FileExtensionInvalidException();
        }
    }

    private String getFolderName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String str = sdf.format(date);

        return str.replace("-", "/");
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.startsWith("https://lh3.googleusercontent.com")) {
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            System.out.println(key);
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);
            amazonS3Client.deleteObject(deleteObjectRequest);

        } catch (AmazonServiceException e) {
            log.error("파일 삭제 실패: Amazon S3 서비스 에러", e);
            throw new FileDeleteErrorException();
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            throw new FileDeleteErrorException();
        }
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
            throw new InvalidFileUrlException();
        }
    }
}
