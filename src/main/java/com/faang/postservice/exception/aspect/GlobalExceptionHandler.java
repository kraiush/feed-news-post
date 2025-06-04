package com.faang.postservice.exception.aspect;

import com.faang.postservice.exception.DataValidationException;
import com.faang.postservice.exception.NotAccessException;
import com.faang.postservice.exception.ResourceAlreadyExistsException;
import com.faang.postservice.exception.ResourceNotFoundException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.ConnectException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Invalid argument: {} " + ex.getMessage());
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMismatchTypeException(MethodArgumentTypeMismatchException ex) {
        log.error("Mismatch argument type: {} " + ex);
        return new ErrorResponse("Mismatch Argument Exception Type - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataFormatException.class)
    public ErrorResponse handleDataFormatException(DataFormatException ex) {
        log.error("Data format error: {} " + ex);
        return new ErrorResponse("Data Format Error - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataValidationException.class)
    public ErrorResponse handleDataValidationException(DataValidationException ex, HttpServletRequest webRequest) {
        log.error("Data validation error: {} ", ex.getMessage());
        return new ErrorResponse("Data Validation Error - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest webRequest) {
        log.error("Access denied: {} " + ex);
        return new ErrorResponse("Access Denied Exception - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ErrorResponse handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex,
                                                              HttpServletRequest webRequest) {
        log.error("Resource Already Exists: {} " + ex);
        return new ErrorResponse("Resource Already Exists - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotAccessException.class)
    public ErrorResponse handleNoAccessException(NotAccessException ex, HttpServletRequest webRequest) {
        log.error("Access denied: {} ", ex.getMessage());
        return new ErrorResponse("Access denied - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex,
                                                         HttpServletRequest webRequest) {
        log.error("Failed to find the requested resource: {} " + ex);
        return new ErrorResponse("Resource Not Found Exception - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleNotFoundEntityException(EntityNotFoundException ex,
                                                       HttpServletRequest webRequest) {
        log.error(String.valueOf(ex));
        return new ErrorResponse("Not Found Entity Exception - " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException ex) {
        String message;
        if (ex.getCause() instanceof ConnectException) {
            message = "Connection to external service failed";
        } else {
            message = ex.getMessage();
        }
        log.error("FeignException occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(message)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public final ResponseEntity<Map<String, List<String>>> handleGeneralExceptions(Exception ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildError(HttpServletRequest webRequest, Exception e, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(webRequest.getRequestURI())
                .status(status)
                .message(e.getMessage())
                .build();
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
