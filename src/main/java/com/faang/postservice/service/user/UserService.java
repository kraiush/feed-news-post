package com.faang.postservice.service.user;

import com.faang.postservice.client.UserServiceClient;
import com.faang.postservice.dto.user.UserDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public UserDto getUser(Long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (Exception ex) {
            String errMessage = String.format("<User> with Id: %d is not found in DB", userId);
            log.info(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<Long> getSubscriberIds(Long userId) {
        try {
            return userServiceClient.getAuthorIdsSubscribedByUser(userId);
        } catch (Exception ex) {
            String errMessage = String.format("Not all provided <User> with Id: %d followers registered in DB", userId);
            log.info(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<Long> getFollowerIds(Long userId) {
        try {
            return userServiceClient.getFollowerIdsOfUser(userId);
        } catch (Exception ex) {
            String errMessage = String.format("Not all provided <User> with Id: %d subscribed to registered in DB", userId);
            log.info(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
    }
}