package com.example.fabrick_task1.service;

import com.example.fabrick_task1.model.AsteroidPath;

import java.time.LocalDate;
import java.util.List;

public interface AsteroidService {

    List<AsteroidPath> getAsteroidPaths(int asteroidId, LocalDate fromDate, LocalDate toDate);

}
