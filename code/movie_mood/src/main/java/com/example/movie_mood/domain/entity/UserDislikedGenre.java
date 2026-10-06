package com.example.movie_mood.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_disliked_genres",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "genre_id"}
                )
        }
)
public class UserDislikedGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public UserDislikedGenre() {
    }

    public UserDislikedGenre(User user, Genre genre) {
        this.user = user;
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}