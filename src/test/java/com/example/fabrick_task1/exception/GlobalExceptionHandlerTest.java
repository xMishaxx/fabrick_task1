package com.example.fabrick_task1.exception;

import com.example.fabrick_task1.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }


    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "invalidValue",
                String.class,
                "dateParam",
                null,
                new IllegalArgumentException("Invalid format")
        );

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentTypeMismatch(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertTrue(body.getMessage().contains("dateParam"));
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("fromDate cannot be after toDate");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("fromDate cannot be after toDate", body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_ShouldReturnProvidedHttpStatus() {
        NasaApiException exception = new NasaApiException("NASA API returned error: 503", HttpStatus.SERVICE_UNAVAILABLE);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), body.getStatus());
        assertEquals("Service Unavailable", body.getError());
        assertEquals("NASA API returned error: 503", body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_WithBadGateway_ShouldReturnBadGateway() {
        NasaApiException exception = new NasaApiException("Unexpected error while fetching asteroid data", HttpStatus.BAD_GATEWAY);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.BAD_GATEWAY.value(), body.getStatus());
        assertEquals("Bad Gateway", body.getError());
        assertTrue(body.getMessage().contains("Unexpected error"));
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_WithNullHttpStatus_ShouldReturnBadGateway() {
        NasaApiException exception = new NasaApiException("Error with null status", null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.BAD_GATEWAY.value(), body.getStatus());
        assertEquals("Bad Gateway", body.getError());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals("Internal Server Error", body.getError());
        assertEquals("An unexpected error occurred", body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleIllegalArgumentException_WithComplexMessage_ShouldReturnBadRequest() {
        String complexMessage = "Multiple validation errors: fromDate is invalid, toDate is invalid";
        IllegalArgumentException exception = new IllegalArgumentException(complexMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertEquals(complexMessage, body.getMessage());
    }
}