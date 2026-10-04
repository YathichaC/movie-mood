package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.dto.response.MoodResponse;
import com.example.movie_mood.mapper.MoodMapper;
import com.example.movie_mood.service.MoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
@Tag(name = "Mood", description = "Mood management APIs")
public class MoodController {

    private final MoodService moodService;
    private final MoodMapper moodMapper;

    public MoodController(MoodService moodService, MoodMapper moodMapper) {
        this.moodService = moodService;
        this.moodMapper = moodMapper;
    }

    @GetMapping
    @Operation(
            summary = "Get all moods",
            description = "Returns all moods supported by MovieMood"
    )
    public List<MoodResponse> getAllMoods() {
        return moodService.getAllMoods()
                .stream()
                .map(moodMapper::toResponse)
                .toList();
    }

    @GetMapping("/{moodName}")
    @Operation(
            summary = "Get mood by name",
            description = "Returns a supported mood by name"
    )
    public MoodResponse getMoodByName(@PathVariable String moodName) {
        Mood mood = moodService.getMoodByName(moodName);
        return moodMapper.toResponse(mood);
    }
}