package com.faang.postservice.validation.user;

import com.faang.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserServiceClient userServiceClient;
    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void existUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (Exception ex) {
            String errMessage = String.format("<User> with Id: %d is not found in DB", userId);
            log.info(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
    }
}