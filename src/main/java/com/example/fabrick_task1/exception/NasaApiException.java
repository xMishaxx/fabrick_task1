package com.example.fabrick_task1.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NasaApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    public NasaApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public NasaApiException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}