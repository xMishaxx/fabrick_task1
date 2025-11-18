package com.example.fabrick_task1.client;

import com.example.fabrick_task1.config.NasaApiProperties;
import com.example.fabrick_task1.exception.NasaApiException;
import com.example.fabrick_task1.model.NasaAsteroidResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NasaApiClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private NasaApiProperties nasaApiProperties;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private NasaApiClient nasaApiClient;

    private static final String BASE_URL = "https://api.nasa.gov/neo/rest/v1/neo";
    private static final String API_KEY = "test-api-key";
    private static final int ASTEROID_ID = 3542519;

    @BeforeEach
    void setUp() {
        when(nasaApiProperties.getBaseUrl()).thenReturn(BASE_URL);
        when(nasaApiProperties.getApiKey()).thenReturn(API_KEY);
    }

    @Test
    void getAsteroidData_WithValidId_ShouldReturnAsteroidResponse() {
        NasaAsteroidResponse expectedResponse = new NasaAsteroidResponse();
        expectedResponse.setId("3542519");
        expectedResponse.setName("(2010 PK9)");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenReturn(expectedResponse);

        NasaAsteroidResponse result = nasaApiClient.getAsteroidData(ASTEROID_ID);

        assertNotNull(result);
        assertEquals("3542519", result.getId());
        assertEquals("(2010 PK9)", result.getName());

        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(anyString());
        verify(requestHeadersUriSpec).retrieve();
        verify(responseSpec).body(NasaAsteroidResponse.class);
    }

    @Test
    void getAsteroidData_WhenNotFound_ShouldThrowNasaApiExceptionWithNotFound() {
        HttpClientErrorException notFoundException = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                null
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenThrow(notFoundException);

        NasaApiException exception = assertThrows(NasaApiException.class, () -> {
            nasaApiClient.getAsteroidData(ASTEROID_ID);
        });

        assertEquals("Asteroid not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertNotNull(exception.getCause());
    }

    @Test
    void getAsteroidData_WhenTooManyRequests_ShouldThrowNasaApiExceptionWithTooManyRequests() {
        HttpClientErrorException tooManyRequestsException = HttpClientErrorException.create(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too Many Requests",
                null,
                null,
                null
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenThrow(tooManyRequestsException);

        NasaApiException exception = assertThrows(NasaApiException.class, () -> {
            nasaApiClient.getAsteroidData(ASTEROID_ID);
        });

        assertEquals("Service rate limit exceeded", exception.getMessage());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getHttpStatus());
        assertNotNull(exception.getCause());
    }

    @Test
    void getAsteroidData_WhenBadRequest_ShouldThrowNasaApiExceptionWithBadGateway() {
        HttpClientErrorException badRequestException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                null,
                null
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenThrow(badRequestException);

        NasaApiException exception = assertThrows(NasaApiException.class, () -> {
            nasaApiClient.getAsteroidData(ASTEROID_ID);
        });

        assertEquals("Unable to retrieve asteroid data", exception.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getHttpStatus());
        assertNotNull(exception.getCause());
    }

    @Test
    void getAsteroidData_WhenServerError_ShouldThrowNasaApiExceptionWithBadGateway() {
        HttpServerErrorException serverErrorException = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null,
                null,
                null
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenThrow(serverErrorException);

        NasaApiException exception = assertThrows(NasaApiException.class, () -> {
            nasaApiClient.getAsteroidData(ASTEROID_ID);
        });

        assertEquals("Unable to retrieve asteroid data", exception.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getHttpStatus());
        assertNotNull(exception.getCause());
    }

    @Test
    void getAsteroidData_WhenServiceUnavailable_ShouldThrowNasaApiExceptionWithBadGateway() {
        HttpServerErrorException serviceUnavailableException = HttpServerErrorException.create(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service Unavailable",
                null,
                null,
                null
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenThrow(serviceUnavailableException);

        NasaApiException exception = assertThrows(NasaApiException.class, () -> {
            nasaApiClient.getAsteroidData(ASTEROID_ID);
        });

        assertEquals("Unable to retrieve asteroid data", exception.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getHttpStatus());
        assertNotNull(exception.getCause());
    }

    @Test
    void getAsteroidData_WhenUnexpectedException_ShouldThrowNasaApiExceptionWithBadGateway() {
        RuntimeException unexpectedException = new RuntimeException("Unexpected error");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenThrow(unexpectedException);

        NasaApiException exception = assertThrows(NasaApiException.class, () -> {
            nasaApiClient.getAsteroidData(ASTEROID_ID);
        });

        assertEquals("Unable to retrieve asteroid data", exception.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getHttpStatus());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof RuntimeException);
    }


    @Test
    void getAsteroidData_WhenNullResponse_ShouldReturnNull() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NasaAsteroidResponse.class)).thenReturn(null);

        NasaAsteroidResponse result = nasaApiClient.getAsteroidData(ASTEROID_ID);

        assertNull(result);
    }
}