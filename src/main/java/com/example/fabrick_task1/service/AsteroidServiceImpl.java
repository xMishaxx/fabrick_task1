package com.example.fabrick_task1.service;

import com.example.fabrick_task1.model.AsteroidPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsteroidServiceImpl implements AsteroidService {

    @Override
    public List<AsteroidPath> getAsteroidPaths(int asteroidId, LocalDate fromDate, LocalDate toDate) {
        return List.of();
    }
}
