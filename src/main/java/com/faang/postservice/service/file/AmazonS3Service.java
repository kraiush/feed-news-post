package com.faang.postservice.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface AmazonS3Service {

    String uploadFile(MultipartFile file, Long userId);

    InputStream downloadFile(String key, Long userId);

    void deleteFile(String key, Long userId);
}
