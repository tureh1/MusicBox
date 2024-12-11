package onetomany.Song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DeezerService {
    private static final String DEEZER_TRACK_URL = "https://api.deezer.com/track/";
    private static final String DEEZER_SEARCH_URL = "https://api.deezer.com/search";
    private static final int MAX_RANDOM_SONGS = 100;

    @Autowired
    private SongRepository songRepository;

    // Fetch 100 random songs using Deezer Search API (including cover)
    public void fetchAndSaveRandomSongs() {
        RestTemplate restTemplate = new RestTemplate();
        Set<String> addedSongs = new HashSet<>(); // To track unique songs

        while (addedSongs.size() < MAX_RANDOM_SONGS) {
            // Generate a random query (e.g., a random letter or digit)
            String randomQuery = getRandomQuery();

            // Build the URL for the search API
            String url = UriComponentsBuilder.fromHttpUrl(DEEZER_SEARCH_URL)
                    .queryParam("q", randomQuery)
                    .queryParam("limit", 10) // Fetch 10 results per query
                    .toUriString();

            try {
                // Fetch songs using the Deezer API
                DeezerSearchResponse response = restTemplate.getForObject(url, DeezerSearchResponse.class);
                if (response != null && response.getData() != null) {
                    for (DeezerSearchResponse.Track track : response.getData()) {
                        String songKey = track.getTitle() + "-" + track.getArtist().getName();
                        if (!addedSongs.contains(songKey)) {
                            addedSongs.add(songKey);

                            // Check if the song is already in the database
                            if (!songRepository.findByTitleAndArtist(track.getTitle(), track.getArtist().getName()).isPresent()) {
                                Song song = new Song(track.getTitle(), track.getArtist().getName());

                                // Set the cover image URL with the highest quality available
                                String highQualityCover = track.getAlbum().getCoverXl();
                                if (highQualityCover == null) {
                                    highQualityCover = track.getAlbum().getCoverBig();
                                }
                                if (highQualityCover == null) {
                                    highQualityCover = track.getAlbum().getCover();
                                }
                                if (highQualityCover == null) {
                                    highQualityCover = "https://defaultcoverurl.com";  // Use a default cover if not available
                                }
                                song.setCover(highQualityCover);

                                song.setAverageRating(0.0); // Initialize with no ratings
                                songRepository.save(song); // Save to the database
                            }
                        }

                        // Stop if we've reached the target number of unique songs
                        if (addedSongs.size() >= MAX_RANDOM_SONGS) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch random songs from Deezer", e);
            }
        }
    }

    private String getRandomQuery() {
        // Generate a random single-character query (letter or digit)
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        int randomIndex = (int) (Math.random() * chars.length());
        return String.valueOf(chars.charAt(randomIndex));
    }

    public void fetchAndSaveChartSongs() {
    }

    public Song fetchSongFromDeezer(int deezerTrackId) {
        return null;
    }

    // DTO for Deezer Search API response
    static class DeezerSearchResponse {
        private List<Track> data;

        public List<Track> getData() {
            return data;
        }

        public void setData(List<Track> data) {
            this.data = data;
        }

        static class Track {
            private String title;
            private Artist artist;
            private Album album;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Artist getArtist() {
                return artist;
            }

            public void setArtist(Artist artist) {
                this.artist = artist;
            }

            public Album getAlbum() {
                return album;
            }

            public void setAlbum(Album album) {
                this.album = album;
            }
        }

        static class Artist {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        static class Album {
            private String cover;
            private String cover_big;
            private String cover_xl;

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getCoverBig() {
                return cover_big;
            }

            public void setCoverBig(String cover_big) {
                this.cover_big = cover_big;
            }

            public String getCoverXl() {
                return cover_xl;
            }

            public void setCoverXl(String cover_xl) {
                this.cover_xl = cover_xl;
            }
        }
    }
}
