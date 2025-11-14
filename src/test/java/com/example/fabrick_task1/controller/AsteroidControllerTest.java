package com.example.fabrick_task1.controller;

import com.example.fabrick_task1.model.AsteroidPath;
import com.example.fabrick_task1.service.AsteroidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsteroidControllerTest {

    @Mock
    private AsteroidService asteroidService;

    @InjectMocks
    private AsteroidController asteroidController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAsteroidPaths_WithValidDateRange_ShouldReturnOk() {
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();
        List<AsteroidPath> expectedPaths = new ArrayList<>();

        when(asteroidService.getAsteroidPaths(eq(asteroidId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expectedPaths);

        ResponseEntity<List<AsteroidPath>> response = asteroidController.getAsteroidPaths(asteroidId, fromDate, toDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAsteroidPaths_WithFromDateAfterToDate_ShouldThrowException() {
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().minusDays(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            asteroidController.getAsteroidPaths(asteroidId, fromDate, toDate);
        });

        assertEquals("fromDate cannot be after toDate", exception.getMessage());
    }

    @Test
    void getAsteroidPaths_WithNullDates_ShouldUseDefaults() {
        // Arrange
        int asteroidId = 123;
        List<AsteroidPath> expectedPaths = new ArrayList<>();

        when(asteroidService.getAsteroidPaths(eq(asteroidId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expectedPaths);

        ResponseEntity<List<AsteroidPath>> response = asteroidController.getAsteroidPaths(asteroidId, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAsteroidPaths_WithSameFromAndToDate_ShouldReturnOk() {
        // Arrange
        int asteroidId = 123;
        LocalDate sameDate = LocalDate.now().minusDays(5);
        List<AsteroidPath> expectedPaths = new ArrayList<>();

        when(asteroidService.getAsteroidPaths(eq(asteroidId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expectedPaths);

        ResponseEntity<List<AsteroidPath>> response = asteroidController.getAsteroidPaths(asteroidId, sameDate, sameDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}