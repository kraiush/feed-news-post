package com.faang.postservice.service.corrector.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.client.BingSpellCheckingClient;
import com.faang.postservice.config.spellCheck.BingSpellCheckConfig;
import com.faang.postservice.dto.post.corrector.FlaggedToken;
import com.faang.postservice.dto.post.corrector.PostCorrectorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TextCorrection {

    private final BingSpellCheckingClient bingSpellCheckingClient;
    private final BingSpellCheckConfig bingSpellCheckConfig;

    @Async("bingSpellAsyncExecutor")
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 1000))
    public CompletableFuture<String> getCorrectText(String text) throws JsonProcessingException {
        String body = "Text=" + text;

        var stringHttpResponse = bingSpellCheckingClient
                .makeTextCorrect(bingSpellCheckConfig.getHeaders(), bingSpellCheckConfig.getMode(), body);

        ObjectMapper objectMapper = new ObjectMapper();
        PostCorrectorDto postCorrectorDto1 = objectMapper
                .readValue(stringHttpResponse.getBody(), PostCorrectorDto.class);

        List<FlaggedToken> flaggedTokens = postCorrectorDto1.getFlaggedTokens();
        for (FlaggedToken flaggedToken : flaggedTokens) {
            String incorrectWord = flaggedToken.token;
            String suggestionWord = flaggedToken.suggestions.get(0).suggestion;
            text = text.replace(incorrectWord, suggestionWord);
        }
        return CompletableFuture.completedFuture(text);
    }
}
