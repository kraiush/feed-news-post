package com.faang.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "bing-spell-checking", url = "${post-corrector.url}")
public interface BingSpellCheckingClient {

    @PostMapping
    ResponseEntity<String> makeTextCorrect(@RequestHeader Map<String, String> headers,
                                           @RequestParam("mode") String mode, String body);
}
