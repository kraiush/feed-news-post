package com.faang.postservice.service.corrector.post;

import com.faang.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModerationPostsSchedulerTest {

    @Mock
    private PostServiceImpl postService;
    @InjectMocks
    private ModerationPostsScheduler moderationPostsScheduler;

    @Test
    void testDoPostModeration() {
        moderationPostsScheduler.doPostModeration();
        verify(postService).doPostModeration();
    }
}