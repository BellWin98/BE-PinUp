package com.pinup.global.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pinup.global.exception.PinUpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pinup.global.exception.PinUpException.*;

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

    /**
     * S3 파일 업로드
     */
    public String uploadFile(String fileType, MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFileUrl = "";

        if (originalFilename != null) {
            String uploadFileName = getUuidFileName(originalFilename);
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
                log.error("파일 업로드 실패", e);
            }
        }
        return uploadFileUrl;
    }

    /**
     * S3 저장소에서 파일 경로 가져오기
     */
    public String getFileUrl(String key) {
        return amazonS3Client.getUrl(bucket, key).toString();
    }

    /**
     * UUID 파일명 생성
     */
    private String getUuidFileName(String fileName) {

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        validateFileFormat(extension);

        return UUID.randomUUID() + "." + extension;
    }

    /**
     * 파일 형식 유효성 검사
     */
    private void validateFileFormat(String extension) {

        List<String> fileValidate = new ArrayList<>();
        fileValidate.add("jpg");
        fileValidate.add("jpeg");
        fileValidate.add("png");
        fileValidate.add("JPG");
        fileValidate.add("JPEG");
        fileValidate.add("PNG");

        if (!fileValidate.contains(extension)) {
            throw FILE_EXTENSION_INVALID;
        }
    }

    /**
     * 폴더 이름에 이미지가 삽입된 날짜(연/월/일) 추가
     */
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
            throw FILE_DELETE_ERROR;
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            throw FILE_DELETE_ERROR;
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
            throw INVALID_FILE_URL;
        }
    }
}
