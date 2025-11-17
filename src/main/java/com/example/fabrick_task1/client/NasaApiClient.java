package com.example.fabrick_task1.client;

import com.example.fabrick_task1.config.NasaApiProperties;
import com.example.fabrick_task1.exception.NasaApiException;
import com.example.fabrick_task1.model.NasaAsteroidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class NasaApiClient {

    private final RestTemplate restTemplate;
    private final NasaApiProperties nasaApiProperties;

    @Cacheable(value = "asteroidData", key = "#asteroidId")
    public NasaAsteroidResponse getAsteroidData(int asteroidId) {
        log.info("Fetching asteroid data from NASA API for asteroidId: {} (cache miss)", asteroidId);

        String url = UriComponentsBuilder.fromUriString(nasaApiProperties.getBaseUrl() + "/{asteroidId}")
                .queryParam("api_key", nasaApiProperties.getApiKey())
                .buildAndExpand(asteroidId)
                .toUriString();

        log.debug("NASA API URL: {}", url);

        try {
            NasaAsteroidResponse response = restTemplate.getForObject(url, NasaAsteroidResponse.class);
            log.info("Successfully fetched data from NASA API for asteroidId: {}", asteroidId);
            return response;

        } catch (HttpClientErrorException e) {
            log.error("NASA API client error for asteroidId {}: {} - {}", asteroidId, e.getStatusCode(), e.getMessage(), e);

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NasaApiException("Asteroid not found", HttpStatus.NOT_FOUND, e);
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new NasaApiException("Service rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS, e);
            } else {
                throw new NasaApiException("Unable to retrieve asteroid data", HttpStatus.BAD_GATEWAY, e);
            }
        } catch (HttpServerErrorException e) {
            log.error("NASA API server error for asteroidId {}: {} - {}", asteroidId, e.getStatusCode(), e.getMessage(), e);
            throw new NasaApiException("Unable to retrieve asteroid data", HttpStatus.BAD_GATEWAY, e);
        } catch (Exception e) {
            log.error("Unexpected error calling NASA API for asteroidId: {}", asteroidId, e);
            throw new NasaApiException("Unable to retrieve asteroid data", HttpStatus.BAD_GATEWAY, e);
        }
    }
}