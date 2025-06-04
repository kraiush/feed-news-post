package com.faang.postservice.config.spellCheck;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "post-corrector")
public class BingSpellCheckConfig {

    private String contentType;
    private String xRapidApiKey;
    private String xRapidApiHost;
    private String mode;
    private int rateLimitPerSecond;

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", contentType);
        headers.put("X-RapidAPI-Key", xRapidApiKey);
        headers.put("X-RapidAPI-Host", xRapidApiHost);
        return headers;
    }
}
