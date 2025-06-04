package com.faang.postservice.exception.aspect;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.faang.postservice.validation.utils.DateTimePattern.DATE_TIME_PATTERN;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private String url;
    private HttpStatus status;
    private String message;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
