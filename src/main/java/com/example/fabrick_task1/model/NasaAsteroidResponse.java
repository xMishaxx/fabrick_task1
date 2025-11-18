package com.example.fabrick_task1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NasaAsteroidResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("close_approach_data")
    private List<CloseApproachData> closeApproachData;
}