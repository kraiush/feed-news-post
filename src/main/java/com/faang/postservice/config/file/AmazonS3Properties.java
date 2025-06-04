package com.faang.postservice.config.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.data.s3")
public class AmazonS3Properties {

    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucket;
    private int targetWidth;
    private int targetHeight;
    private int maxFilesAmount;
}
