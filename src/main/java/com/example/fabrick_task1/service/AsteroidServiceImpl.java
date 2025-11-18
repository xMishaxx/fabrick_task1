package com.example.fabrick_task1.service;

import com.example.fabrick_task1.client.NasaApiClient;
import com.example.fabrick_task1.model.AsteroidPath;
import com.example.fabrick_task1.model.CloseApproachData;
import com.example.fabrick_task1.model.NasaAsteroidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsteroidServiceImpl implements AsteroidService {

    private final NasaApiClient nasaApiClient;

    @Override
    public List<AsteroidPath> getAsteroidPaths(int asteroidId, LocalDate fromDate, LocalDate toDate) {
        log.info("Processing request for asteroidId: {} with date range {} to {}", asteroidId, fromDate, toDate);

        NasaAsteroidResponse response = nasaApiClient.getAsteroidData(asteroidId);

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
