package com.example.movie_mood.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI movieMoodOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MovieMood API")
                        .description(
                                "REST API for browsing movies and " +
                                "getting mood-based movie recommendations"
                        )
                        .version("1.0.0"));
    }
}