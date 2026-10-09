package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;

import java.util.List;

public interface MoodService {

    List<Mood> getAllMoods();

    Mood getMoodByName(String name);
}