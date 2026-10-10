
-- Movie Mood initial schema (V1)
-- For a NEW, EMPTY PostgreSQL schema only.
-- Existing production schema must use Flyway baseline at version 1.

CREATE TABLE "Genre" (
    genre_id varchar(255) NOT NULL,
    genre_name varchar(255),
    CONSTRAINT "Genre_pkey" PRIMARY KEY (genre_id)
);

CREATE TABLE "User" (
    user_id uuid NOT NULL DEFAULT gen_random_uuid(),
    username varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    CONSTRAINT "User_pkey" PRIMARY KEY (user_id),
    CONSTRAINT "User_email_key" UNIQUE (email),
    CONSTRAINT "User_username_key" UNIQUE (username)
);

CREATE TABLE "Playlist" (
    playlist_id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    playlist_name varchar(255) NOT NULL,
    CONSTRAINT "Playlist_pkey" PRIMARY KEY (playlist_id),
    CONSTRAINT "Playlist_user_id_fkey"
        FOREIGN KEY (user_id)
        REFERENCES "User" (user_id),
    CONSTRAINT playlist_user_name_unique
        UNIQUE (user_id, playlist_name)
);

CREATE TABLE "PlaylistDetail" (
    playlist_id uuid NOT NULL,
    detail varchar(255),
    cover_image_path varchar(255),
    CONSTRAINT playlistdetail_pkey PRIMARY KEY (playlist_id),
    CONSTRAINT playlistdetail_playlist_id_fkey
        FOREIGN KEY (playlist_id)
        REFERENCES "Playlist" (playlist_id)
        ON DELETE CASCADE
);

CREATE TABLE "Movielist" (
    playlist_id uuid NOT NULL,
    tmdb_movie_id varchar(255),
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    CONSTRAINT "Movielist_pkey" PRIMARY KEY (id),
    CONSTRAINT "Movielist_playlist_id_fkey"
        FOREIGN KEY (playlist_id)
        REFERENCES "Playlist" (playlist_id),
    CONSTRAINT movielist_playlist_movie_unique
        UNIQUE (playlist_id, tmdb_movie_id)
);

CREATE TABLE "UserDislikedGenre" (
    user_id uuid NOT NULL,
    genre_id varchar(255) NOT NULL,
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    CONSTRAINT "UserDislikedGenre_pkey" PRIMARY KEY (id),
    CONSTRAINT "UserDislikedGenre_genre_id_fkey"
        FOREIGN KEY (genre_id)
        REFERENCES "Genre" (genre_id),
    CONSTRAINT "UserDislikedGenre_user_id_fkey"
        FOREIGN KEY (user_id)
        REFERENCES "User" (user_id),
    CONSTRAINT user_disliked_genre_unique
        UNIQUE (user_id, genre_id)
);

CREATE TABLE "WatchHistory" (
    history_id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    tmdb_movie_id varchar(255),
    CONSTRAINT "WatchHistory_pkey" PRIMARY KEY (history_id),
    CONSTRAINT "WatchHistory_user_id_fkey"
        FOREIGN KEY (user_id)
        REFERENCES "User" (user_id),
    CONSTRAINT uk_watch_history_user_movie
        UNIQUE (user_id, tmdb_movie_id),
    CONSTRAINT watchhistory_user_movie_unique
        UNIQUE (user_id, tmdb_movie_id)
);

CREATE TABLE password_reset_tokens (
    id uuid NOT NULL,
    "expiryDate" timestamptz NOT NULL,
    token varchar(255) NOT NULL,
    used boolean NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id),
    CONSTRAINT "FKilj6ujfvxwp7qlc9ek2sjwn47"
        FOREIGN KEY (user_id)
        REFERENCES "User" (user_id),
    CONSTRAINT "UK5jotgkaljjir7q0a2kaxe33q0"
        UNIQUE (user_id),
    CONSTRAINT "UK8xw75m01fscbbpylx1xkej3rt"
        UNIQUE (token)
);
