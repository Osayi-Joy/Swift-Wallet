package com.zurum.lanefinance.exceptions;


import com.zurum.lanefinance.dtos.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<AppResponse<?>> handleCustomException(CustomException exception) {
        log.error("custom exception:: msg: {}",exception.getMessage());

        AppResponse<Object> response = AppResponse.builder()
                .isSuccessful(false)
                .message("An error occurred, check message below")
                .result(exception.getMessage())
                .build();

        return ResponseEntity.status(exception.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<AppResponse<Object>> handleHttpMessageNotReadable(final HttpMessageNotReadableException e,
                                                                               final HttpHeaders headers,
                                                                               final HttpStatus status,
                                                                               final WebRequest request) {
        e.printStackTrace();

        AppResponse<Object> response = AppResponse.builder()
                .isSuccessful(false)
                .message("An error occurred, check message below")
                .result(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<AppResponse<Object>> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

        AppResponse<Object> response = AppResponse.builder()
                .isSuccessful(false)
                .message("An error occurred, check message below")
                .result(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({ResourceCreationException.class, ConstraintViolationException.class})
    public ResponseEntity<AppResponse<Object>> resourceConflictException(Exception ex, WebRequest request) {

        AppResponse<Object> response = AppResponse.builder()
                .isSuccessful(false)
                .message("An error occurred, check message below")
                .result(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse<Object>> globalExceptionHandler(Exception ex, WebRequest request) {
        ex.printStackTrace();

        AppResponse<Object> response = AppResponse.builder()
                .isSuccessful(false)
                .message("An error occurred, check message below")
                .result(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<AppResponse<Object>> handleGlobalExceptions(MethodArgumentNotValidException ex,
                                                                         WebRequest request) {
        String[] errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);

        AppResponse<Object> response = AppResponse.builder()
                .isSuccessful(false)
                .message("An error occurred, check message below")
                .result(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}