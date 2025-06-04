package com.faang.postservice.service.corrector.comment;

import com.faang.postservice.model.Comment;
import com.faang.postservice.service.comment.CommentServiceImpl;
import com.faang.postservice.service.corrector.ModerationDictionary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentModeratorSchedulerServiceTest {

    @Mock
    CommentServiceImpl commentService;

    @Mock
    ModerationDictionary moderationDictionary;

    @InjectMocks
    CommentModeratorSchedulerService commentModeratorService;

    @Test
    public void testModerateComments() {
        commentModeratorService.setSublistSize(50);
        when(commentService.getUnverifiedComments()).thenReturn(List.of(new Comment()));
        commentModeratorService.moderateComments();
        verify(commentService).getUnverifiedComments();
        verify(moderationDictionary).checkComment(any());
        verify(commentService).saveAll(any());
    }
}