package com.faang.postservice.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.faang.postservice.validation.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.faang.postservice.config.file.AmazonS3Properties;
import com.faang.postservice.exception.FileStorageException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 amazonS3;
    private final AmazonS3Properties amazonS3Properties;
    private final UserValidator userValidator;

    @Override
    public String uploadFile(MultipartFile file, Long userId) {
        userValidator.existUser(userId);
        String key = generateUniqueKey();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(amazonS3Properties.getBucket(), key, file.getInputStream(), metadata);
            log.info("Successfully uploaded file: {} ",  file.getOriginalFilename());
        } catch (IOException ex) {
            log.error("error [{}] occurred while uploading file: {} ", ex.getMessage(), file.getOriginalFilename());
            throw new FileStorageException("Could not upload file");
        }
        return key;
    }

    @Override
    public InputStream downloadFile(String key, Long userId) {
        userValidator.existUser(userId);
        try {
            return amazonS3.getObject(amazonS3Properties.getBucket(), key).getObjectContent();
        } catch (Exception ex) {
            log.error("error [{}] occurred while file download", ex.getMessage());
            throw new FileStorageException("Error: Could not download file");
        }
    }

    @Override
    public void deleteFile(String key, Long userId) {
        userValidator.existUser(userId);
        try {
            amazonS3.deleteObject(amazonS3Properties.getBucket(), key);
            log.info("Successfully delete file");
        } catch (Exception ex) {
            log.error("error [{}] occurred while file removing", ex.getMessage());
            throw new FileStorageException("Error: Could not delete file");
        }
    }

    private String generateUniqueKey() {
        return UUID.randomUUID().toString();
    }
}
