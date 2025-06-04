package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.cache.PostCache;
import com.faang.postservice.service.FeedNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WatsNewsController {

    private final FeedNewsService service;
    private final UserContext userContext;

    @GetMapping("/feednews")
    public List<PostCache> getNewsWire() {
        var userId = userContext.getUserId();
        return service.gePersonalNews(userId);
    }

    @GetMapping("/heater")
    public List<PostCache> getAllFeedNewsFromScratch() {
        var userId = userContext.getUserId();
        return service.heatFeedNews(userId);
    }
}
