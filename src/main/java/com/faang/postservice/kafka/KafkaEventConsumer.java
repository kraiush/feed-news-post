package com.faang.postservice.kafka;

import com.faang.postservice.dto.event.EventDomain;
import com.faang.postservice.service.FeedNewsService;
import com.faang.postservice.service.outbox.EventType;
import com.faang.postservice.service.post.PostServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final FeedNewsService feedNewsService;
    private final ObjectMapper objectMapper;
    private final PostServiceImpl postService;

    @KafkaListener(topics = {"${spring.data.kafka.topic.outbox.name}"}, groupId = "${spring.data.kafka.consumer.group-id}")
    public void consumeMessage(@Payload byte[] message, Acknowledgment acknowledgment) {
        try {
            EventDomain event = objectMapper.readValue(message, EventDomain.class);
            log.info("Kafka <> Processes message: EventType <{}> payload <{}>", event.getEventType(), event.getEventPayload());
            acknowledgment.acknowledge();
            List<Long> listIds;
            if (event.getEventType().equals(EventType.PUBLISHED)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long authorId = listIds.get(1);
                if (feedNewsService.cacheFeedNewsIsEmpty()) {
                    feedNewsService.heatFeedNews(authorId);
                }
                postService.cachePost(postId);
                feedNewsService.cacheFeedNewsForUser(postId, authorId);
                postService.cacheUser(0L, authorId);
                List<Long> followerIds = listIds.subList(2, listIds.size());
                feedNewsService.cacheFeedNewsByUserIds(postId, followerIds);
                log.info("PUBLISHED - postId: {}  authorId: {}", postId, authorId);
            }
            if (event.getEventType().equals(EventType.UPDATED)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long authorId = listIds.get(1);
                postService.cachePost(postId);
                postService.cacheUser(0L, authorId);
                List<Long> followerIds = listIds.subList(2, listIds.size());
                feedNewsService.cacheFeedNewsByUserIds(postId, followerIds);
                log.info("POST UPDATED - postId: {}  authorId: {}", postId, authorId);
            }
            if (event.getEventType().equals(EventType.COMMENTED)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long commentId = listIds.get(1);
                feedNewsService.addCommentToPost(postId, commentId);
                log.info("COMMENTED - postId: {}  commentId: {}", postId, commentId);
            }
            if (event.getEventType().equals(EventType.COMMENT_UPDATED)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long commentId = listIds.get(1);
                feedNewsService.updateCommentToPost(postId, commentId);
                log.info("COMMENT UPDATED - postId: {}  commentId: {}", postId, commentId);
            }
            if (event.getEventType().equals(EventType.LIKED_POST)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long userId = listIds.get(2);
                feedNewsService.addLikeToPost(postId, userId);
                log.info("LIKED POST - postId: {} by  userId: {}", postId, userId);
            }
            if (event.getEventType().equals(EventType.LIKED_COMMENT)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long commentId = listIds.get(1);
                long userId = listIds.get(2);
                feedNewsService.addLikeToComment(postId, commentId, userId);
                log.info("LIKED COMMENT - postId: {} commentId: {} by  userId: {}", postId, commentId, userId);
            }
            if (event.getEventType().equals(EventType.VIEWED)) {
                listIds = getListIds(event.getEventPayload());
                long postId = listIds.get(0);
                long userId = listIds.get(1);
                feedNewsService.viewPost(postId, userId);
                log.info("VIEWED POST - postId: {} by  userId: {}", postId, userId);
            }
        } catch (Exception e) {
            log.error("Kafka <> Error while processing message!");
            acknowledgment.acknowledge();
        }
    }

    private List<Long> getListIds(String payLoad) {
        List<Long> listOut = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(payLoad);
        while (matcher.find()) {
            listOut.add(Long.parseLong(matcher.group()));
        }
        return listOut;
    }
}
