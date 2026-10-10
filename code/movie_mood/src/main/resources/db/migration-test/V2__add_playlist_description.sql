-- TEST ONLY: Add description to Playlist
ALTER TABLE flyway_test_v2."Playlist"
ADD COLUMN description varchar(500);