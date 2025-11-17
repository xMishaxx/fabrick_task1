package com.example.fabrick_task1.controller;

import com.example.fabrick_task1.model.AsteroidPath;
import com.example.fabrick_task1.model.ErrorResponse;
import com.example.fabrick_task1.service.AsteroidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Asteroid API", description = "API for retrieving asteroid path information from NASA")
public class AsteroidController {

    private final AsteroidService asteroidService;

    @Autowired
    public AsteroidController(AsteroidService asteroidService) {
        this.asteroidService = asteroidService;
    }


    @Operation(
            summary = "Get asteroid paths",
            description = "Retrieves the list of paths for a specific asteroid between two dates. " +
                    "If fromDate is not provided, defaults to 100 years ago. " +
                    "If toDate is not provided, defaults to today."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved asteroid paths",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AsteroidPath.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid parameters (e.g., fromDate after toDate, invalid date format)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Asteroid with the specified ID does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests - NASA API rate limit exceeded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Bad Gateway - Error communicating with NASA API",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/asteroids/{asteroidId}/paths")
    public ResponseEntity<List<AsteroidPath>> getAsteroidPaths(
            @Parameter(description = "The unique identifier of the asteroid", required = true, example = "3542519")
            @PathVariable int asteroidId,
            @Parameter(description = "Start date for path search (format: yyyy-MM-dd). Defaults to 100 years ago if not provided.", example = "2000-01-01")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "End date for path search (format: yyyy-MM-dd). Defaults to today if not provided.", example = "2025-12-31")
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
    }
}
