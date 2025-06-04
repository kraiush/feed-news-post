package com.faang.postservice.service.corrector.post;

import com.faang.postservice.service.post.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationPostsScheduler {

    private final PostServiceImpl postService;

    @Scheduled(cron = "${scheduler.moderator.post.cron}")
    public void doPostModeration() {
        log.info("ModerationPostsScheduler() is starting at: {}", LocalDateTime.now());
        postService.doPostModeration();
    }
}

