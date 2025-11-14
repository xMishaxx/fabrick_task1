package com.example.fabrick_task1.controller;

import com.example.fabrick_task1.model.AsteroidPath;
import com.example.fabrick_task1.service.AsteroidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fabrick/v1.0")
public class AsteroidController {

    private final AsteroidService asteroidService;

    @Autowired
    public AsteroidController(AsteroidService asteroidService) {
        this.asteroidService = asteroidService;
    }


    @GetMapping("/asteroids/{asteroidId}/paths")
    public ResponseEntity<List<AsteroidPath>> getAsteroidPaths(
            @PathVariable int asteroidId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        log.info("Fetching asteroid paths for asteroidId: {}, fromDate: {}, toDate: {}",
                asteroidId, fromDate, toDate);

        // Apply default values as per requirements
        LocalDate actualFromDate = (fromDate != null) ? fromDate : LocalDate.now().minusYears(100);
        LocalDate actualToDate = (toDate != null) ? toDate : LocalDate.now();

        //Validate input
        validateDateRange(actualFromDate, actualToDate);

        log.info("Using date range - From: {}, To: {}", actualFromDate, actualToDate);

        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, actualFromDate, actualToDate);

        log.info("Result size: {}", result.size());

        return ResponseEntity.ok().body(result);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate cannot be after toDate");
        }

        if (fromDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("fromDate cannot be in the future");
        }

        if (toDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("toDate cannot be in the future");
        }
    }
}
