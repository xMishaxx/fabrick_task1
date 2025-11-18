package com.example.fabrick_task1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response model returned when an error occurs")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:45")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type or reason phrase", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "fromDate cannot be after toDate")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/fabrick/v1.0/asteroids/3542519/paths")
    private String path;
}