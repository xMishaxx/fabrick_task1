package com.example.fabrick_task1.service;

import com.example.fabrick_task1.config.NasaApiProperties;
import com.example.fabrick_task1.exception.NasaApiException;
import com.example.fabrick_task1.model.AsteroidPath;
import com.example.fabrick_task1.model.CloseApproachData;
import com.example.fabrick_task1.model.NasaAsteroidResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsteroidServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private NasaApiProperties nasaApiProperties;

    @InjectMocks
    private AsteroidServiceImpl asteroidService;

    @BeforeEach
    void setUp() {
        when(nasaApiProperties.getBaseUrl()).thenReturn("https://api.nasa.gov/neo/rest/v1/neo");
        when(nasaApiProperties.getApiKey()).thenReturn("DEMO_KEY");
    }

    @Test
    void getAsteroidPaths_WithValidData_ShouldReturnPaths() {
        // Arrange
        int asteroidId = 3542519;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        NasaAsteroidResponse mockResponse = createMockResponse();
        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAsteroidPaths_WithConsecutiveDifferentPlanets_ShouldCreatePaths() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        NasaAsteroidResponse mockResponse = new NasaAsteroidResponse();
        List<CloseApproachData> approaches = new ArrayList<>();

        CloseApproachData approach1 = new CloseApproachData();
        approach1.setCloseApproachDate(LocalDate.of(2020, 3, 1));
        approach1.setOrbitingBody("Earth");
        approaches.add(approach1);

        CloseApproachData approach2 = new CloseApproachData();
        approach2.setCloseApproachDate(LocalDate.of(2020, 6, 1));
        approach2.setOrbitingBody("Venus");
        approaches.add(approach2);

        CloseApproachData approach3 = new CloseApproachData();
        approach3.setCloseApproachDate(LocalDate.of(2020, 9, 1));
        approach3.setOrbitingBody("Mars");
        approaches.add(approach3);

        mockResponse.setCloseApproachData(approaches);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertEquals(2, result.size());

        AsteroidPath path1 = result.get(0);
        assertEquals("Earth", path1.getFromPlanet());
        assertEquals("Venus", path1.getToPlanet());
        assertEquals(LocalDate.of(2020, 3, 1), path1.getFromDate());
        assertEquals(LocalDate.of(2020, 6, 1), path1.getToDate());

        AsteroidPath path2 = result.get(1);
        assertEquals("Venus", path2.getFromPlanet());
        assertEquals("Mars", path2.getToPlanet());
        assertEquals(LocalDate.of(2020, 6, 1), path2.getFromDate());
        assertEquals(LocalDate.of(2020, 9, 1), path2.getToDate());
    }

    @Test
    void getAsteroidPaths_WithSamePlanetConsecutive_ShouldNotCreatePath() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        NasaAsteroidResponse mockResponse = new NasaAsteroidResponse();
        List<CloseApproachData> approaches = new ArrayList<>();

        CloseApproachData approach1 = new CloseApproachData();
        approach1.setCloseApproachDate(LocalDate.of(2020, 3, 1));
        approach1.setOrbitingBody("Earth");
        approaches.add(approach1);

        CloseApproachData approach2 = new CloseApproachData();
        approach2.setCloseApproachDate(LocalDate.of(2020, 6, 1));
        approach2.setOrbitingBody("Earth");
        approaches.add(approach2);

        mockResponse.setCloseApproachData(approaches);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAsteroidPaths_FilterByDateRange_ShouldReturnOnlyPathsInRange() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 5, 1);
        LocalDate toDate = LocalDate.of(2020, 8, 31);

        NasaAsteroidResponse mockResponse = new NasaAsteroidResponse();
        List<CloseApproachData> approaches = new ArrayList<>();

        // Path outside range (starts before fromDate)
        CloseApproachData approach1 = new CloseApproachData();
        approach1.setCloseApproachDate(LocalDate.of(2020, 2, 1));
        approach1.setOrbitingBody("Earth");
        approaches.add(approach1);

        CloseApproachData approach2 = new CloseApproachData();
        approach2.setCloseApproachDate(LocalDate.of(2020, 4, 1));
        approach2.setOrbitingBody("Venus");
        approaches.add(approach2);

        // Path inside range
        CloseApproachData approach3 = new CloseApproachData();
        approach3.setCloseApproachDate(LocalDate.of(2020, 6, 1));
        approach3.setOrbitingBody("Mars");
        approaches.add(approach3);

        CloseApproachData approach4 = new CloseApproachData();
        approach4.setCloseApproachDate(LocalDate.of(2020, 8, 1));
        approach4.setOrbitingBody("Jupiter");
        approaches.add(approach4);

        // Path outside range (ends after toDate)
        CloseApproachData approach5 = new CloseApproachData();
        approach5.setCloseApproachDate(LocalDate.of(2020, 10, 1));
        approach5.setOrbitingBody("Saturn");
        approaches.add(approach5);

        mockResponse.setCloseApproachData(approaches);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Mars", result.get(0).getFromPlanet());
        assertEquals("Jupiter", result.get(0).getToPlanet());
    }

    @Test
    void getAsteroidPaths_WithNullResponse_ShouldReturnEmptyList() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(null);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAsteroidPaths_WithEmptyCloseApproachData_ShouldReturnEmptyList() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        NasaAsteroidResponse mockResponse = new NasaAsteroidResponse();
        mockResponse.setCloseApproachData(new ArrayList<>());

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAsteroidPaths_WithHttpClientErrorException_ShouldThrowNasaApiException() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Act & Assert
        assertThrows(NasaApiException.class, () -> {
            asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);
        });
    }

    @Test
    void getAsteroidPaths_WithException_ShouldThrowNasaApiException() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenThrow(new RuntimeException("RuntimeException"));

        // Act & Assert
        assertThrows(NasaApiException.class, () -> {
            asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);
        });
    }

    @Test
    void getAsteroidPaths_WithUnsortedDates_ShouldSortAndCreatePaths() {
        // Arrange
        int asteroidId = 123;
        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        NasaAsteroidResponse mockResponse = new NasaAsteroidResponse();
        List<CloseApproachData> approaches = new ArrayList<>();

        // Add in random order
        CloseApproachData approach1 = new CloseApproachData();
        approach1.setCloseApproachDate(LocalDate.of(2020, 9, 1));
        approach1.setOrbitingBody("Mars");
        approaches.add(approach1);

        CloseApproachData approach2 = new CloseApproachData();
        approach2.setCloseApproachDate(LocalDate.of(2020, 3, 1));
        approach2.setOrbitingBody("Earth");
        approaches.add(approach2);

        CloseApproachData approach3 = new CloseApproachData();
        approach3.setCloseApproachDate(LocalDate.of(2020, 6, 1));
        approach3.setOrbitingBody("Venus");
        approaches.add(approach3);

        mockResponse.setCloseApproachData(approaches);

        when(restTemplate.getForObject(anyString(), eq(NasaAsteroidResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, fromDate, toDate);

        // Assert
        assertEquals(2, result.size());
        // Verify sorted order: Earth -> Venus -> Mars
        assertEquals("Earth", result.get(0).getFromPlanet());
        assertEquals("Venus", result.get(0).getToPlanet());
        assertEquals("Venus", result.get(1).getFromPlanet());
        assertEquals("Mars", result.get(1).getToPlanet());
    }

    private NasaAsteroidResponse createMockResponse() {
        NasaAsteroidResponse response = new NasaAsteroidResponse();
        List<CloseApproachData> approaches = new ArrayList<>();

        CloseApproachData approach1 = new CloseApproachData();
        approach1.setCloseApproachDate(LocalDate.of(2020, 3, 15));
        approach1.setOrbitingBody("Earth");
        approaches.add(approach1);

        CloseApproachData approach2 = new CloseApproachData();
        approach2.setCloseApproachDate(LocalDate.of(2020, 6, 20));
        approach2.setOrbitingBody("Venus");
        approaches.add(approach2);

        response.setCloseApproachData(approaches);
        return response;
    }
}