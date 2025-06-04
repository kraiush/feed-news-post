package com.faang.postservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@EnableRetry
@EnableFeignClients(basePackages = "com.faang.postservice.client")
@ConfigurationPropertiesScan("com.faang.postservice.config")
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Post Service",
                version = "1.0.0",
                description = "OpenApi documentation for Post Service of FeedNews System",
                contact = @Contact(
                        name = "Alexander Kraiushkin",
                        email = "akraiushkin@gmail.com"
                )
        )
)
public class AppPostService {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AppPostService.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
