package com.pinup.global.exception;

import com.pinup.global.response.ErrorCode;
import com.pinup.global.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

import static com.pinup.global.response.ErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class: {}, Status: {}, Message: {}";

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getParameterName());
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        ErrorResponse response = ErrorResponse.of(NO_RESOURCE_FOUND);
        logError(e, response);

        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getConstraintViolations());
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getBindingResult());
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getBindingResult());
        logError(e, response);
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException e) {
        ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getRequestPartName());
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMissingRequestCookieException(
            MissingRequestCookieException e) {
        ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getCookieName());
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        ErrorResponse response = ErrorResponse.of(e);
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorResponse response = ErrorResponse.of(HTTP_MESSAGE_NOT_READABLE);
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpRequestMethodNotSupportedException e) {
        List<ErrorResponse.CustomFieldError> errors = new ArrayList<>();
        errors.add(new ErrorResponse.CustomFieldError(
                "HTTP METHOD", e.getMethod(), METHOD_NOT_ALLOWED.getMessage()));
        ErrorResponse response = ErrorResponse.of(HTTP_HEADER_INVALID, errors);
        logError(e, response);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
        logError(e, response);

        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {

        ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
        logError(e, response);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception e, ErrorResponse response) {
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), response.getStatus(), response.getMessage());
    }
}
