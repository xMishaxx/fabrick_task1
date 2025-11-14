package com.example.fabrick_task1.exception;

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

import java.util.Map;

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

        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentTypeMismatch(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertTrue(((String) body.get("message")).contains("dateParam"));
        assertEquals("/api/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("fromDate cannot be after toDate");

        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("fromDate cannot be after toDate", body.get("message"));
        assertEquals("/api/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleNasaApiException_ShouldReturnProvidedHttpStatus() {
        NasaApiException exception = new NasaApiException("NASA API returned error: 503", HttpStatus.SERVICE_UNAVAILABLE);

        ResponseEntity<Object> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), body.get("status"));
        assertEquals("Service Unavailable", body.get("error"));
        assertEquals("NASA API returned error: 503", body.get("message"));
        assertEquals("/api/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleNasaApiException_WithBadGateway_ShouldReturnBadGateway() {
        NasaApiException exception = new NasaApiException("Unexpected error while fetching asteroid data", HttpStatus.BAD_GATEWAY);

        ResponseEntity<Object> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.BAD_GATEWAY.value(), body.get("status"));
        assertEquals("Bad Gateway", body.get("error"));
        assertTrue(((String) body.get("message")).contains("Unexpected error"));
        assertEquals("/api/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleNasaApiException_WithNullHttpStatus_ShouldReturnBadGateway() {
        NasaApiException exception = new NasaApiException("Error with null status", null);

        ResponseEntity<Object> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.BAD_GATEWAY.value(), body.get("status"));
        assertEquals("Bad Gateway", body.get("error"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<Object> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertEquals("/api/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleIllegalArgumentException_WithComplexMessage_ShouldReturnBadRequest() {
        String complexMessage = "Multiple validation errors: fromDate is invalid, toDate is invalid";
        IllegalArgumentException exception = new IllegalArgumentException(complexMessage);

        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(complexMessage, body.get("message"));
    }
}