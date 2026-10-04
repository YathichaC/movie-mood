package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.exception.MoodNotFoundException;
import com.example.movie_mood.service.MoodService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MoodServiceImpl implements MoodService {

    @Override
    public List<Mood> getAllMoods() {
        return Arrays.asList(Mood.values());
    }

    @Override
    public Mood getMoodByName(String name) {
        if (name == null || name.isBlank()) {
            throw new MoodNotFoundException(name);
        }

        try {
            return Mood.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new MoodNotFoundException(name);
        }
    }
}