package com.faang.postservice.service.corrector.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.faang.postservice.config.spellCheck.BingSpellCheckConfig;
import com.faang.postservice.model.Post;
import com.faang.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCorrectorService {

    private final PostRepository postRepository;
    private final TextCorrection textCorrector;
    private final BingSpellCheckConfig bingSpellCheckConfig;
    private final ThreadPoolTaskExecutor bingSpellAsyncExecutor;

    @Scheduled(cron = "${scheduler.moderator.post-corrector.cron}")
    public void correctPostsScheduled() throws JsonProcessingException {
        log.info("Post correction job is started");
        correctUnpublishedPosts();
        log.info("Post correction job is ended");
    }

    public void correctUnpublishedPosts() {
        List<Post> readyToPublish = postRepository.findNotPublished();

        int rateLimit = bingSpellCheckConfig.getRateLimitPerSecond();
        AtomicInteger requestCount = new AtomicInteger();
        bingSpellAsyncExecutor.submit(() -> {
            for (Post toPublish : readyToPublish) {
                String content = toPublish.getContent();
                Post post = toPublish;
                try {
                    if (requestCount.get() >= rateLimit) {
                        Thread.sleep(1000);
                        requestCount.set(0);
                    }
                    post.setContent(textCorrector.getCorrectText(content).get());
                    requestCount.getAndIncrement();
                    post.setCorrected(true);
                    log.info("Drafts posts was corrected successfully postId: {}", toPublish.getId());
                } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            postRepository.saveAll(readyToPublish);
        });
    }
}
