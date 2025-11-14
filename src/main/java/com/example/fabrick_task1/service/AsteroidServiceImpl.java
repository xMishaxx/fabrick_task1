package com.example.fabrick_task1.service;

import com.example.fabrick_task1.config.NasaApiProperties;
import com.example.fabrick_task1.exception.NasaApiException;
import com.example.fabrick_task1.model.AsteroidPath;
import com.example.fabrick_task1.model.CloseApproachData;
import com.example.fabrick_task1.model.NasaAsteroidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsteroidServiceImpl implements AsteroidService {

    private final RestTemplate restTemplate;
    private final NasaApiProperties nasaApiProperties;

    @Override
    public List<AsteroidPath> getAsteroidPaths(int asteroidId, LocalDate fromDate, LocalDate toDate) {
        log.info("Fetching asteroid data from NASA API for asteroidId: {}", asteroidId);

        String url = UriComponentsBuilder.fromUriString(nasaApiProperties.getBaseUrl() + "/{asteroidId}")
                .queryParam("api_key", nasaApiProperties.getApiKey())
                .buildAndExpand(asteroidId)
                .toUriString();

        log.debug("NASA API URL: {}", url);

        try {
            NasaAsteroidResponse response = restTemplate.getForObject(url, NasaAsteroidResponse.class);

            if (response == null || response.getCloseApproachData() == null || response.getCloseApproachData().isEmpty()) {
                log.warn("No close approach data found for asteroidId: {}", asteroidId);
                return new ArrayList<>();
            }

            log.info("Retrieved {} close approach records", response.getCloseApproachData().size());

            List<CloseApproachData> sortedApproaches = response.getCloseApproachData().stream()
                    .sorted(Comparator.comparing(CloseApproachData::getCloseApproachDate))
                    .collect(Collectors.toList());

            List<AsteroidPath> paths = buildPaths(sortedApproaches);

            List<AsteroidPath> filteredPaths = paths.stream()
                    .filter(path -> isInDateRange(path, fromDate, toDate))
                    .collect(Collectors.toList());

            log.info("Found {} paths, filtered to {} within date range {} to {}",
                    paths.size(), filteredPaths.size(), fromDate, toDate);

            return filteredPaths;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("NASA API error for asteroidId {}: {} - {}", asteroidId, e.getStatusCode(), e.getMessage());
            throw new NasaApiException("NASA API returned error: " + e.getStatusCode().value(), HttpStatus.valueOf(e.getStatusCode().value()), e);
        } catch (Exception e) {
            log.error("Unexpected error calling NASA API for asteroidId: {}", asteroidId, e);
            throw new NasaApiException("Unexpected error while fetching asteroid data: " + e.getMessage(), HttpStatus.BAD_GATEWAY, e);
        }
    }

    private List<AsteroidPath> buildPaths(List<CloseApproachData> sortedApproaches) {
        List<AsteroidPath> paths = new ArrayList<>();

        for (int i = 0; i < sortedApproaches.size() - 1; i++) {
            CloseApproachData current = sortedApproaches.get(i);
            CloseApproachData next = sortedApproaches.get(i + 1);

            if (!current.getOrbitingBody().equals(next.getOrbitingBody())) {
                AsteroidPath path = new AsteroidPath();
                path.setFromPlanet(current.getOrbitingBody());
                path.setToPlanet(next.getOrbitingBody());
                path.setFromDate(current.getCloseApproachDate());
                path.setToDate(next.getCloseApproachDate());
                paths.add(path);

                log.debug("Created path: {} -> {} ({} to {})",
                        path.getFromPlanet(), path.getToPlanet(),
                        path.getFromDate(), path.getToDate());
            }
        }

        return paths;
    }

    private boolean isInDateRange(AsteroidPath path, LocalDate fromDate, LocalDate toDate) {
        return !path.getFromDate().isBefore(fromDate) && !path.getToDate().isAfter(toDate);
    }
}
