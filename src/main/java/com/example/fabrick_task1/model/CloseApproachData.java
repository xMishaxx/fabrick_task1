package com.example.fabrick_task1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CloseApproachData {

    @JsonProperty("close_approach_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate closeApproachDate;

    @JsonProperty("orbiting_body")
    private String orbitingBody;
}
