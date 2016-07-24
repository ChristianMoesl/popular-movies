package at.mchris.popularmovies.network.themoviedb3;


import android.support.annotation.Nullable;

import java.util.List;

/**
 * Simple POD object which holds all the
 * movie info needed.
 */
public class MovieDescription {

    @Nullable
    private String posterPath;

    private boolean adult;

    private String overview;

    @Nullable
    private String releaseDate;

    private List<Integer> genreIds;

    private int id;

    private String originalTitle;

    private String originalLanguage;

    private String title;

    private String backdropPath;

    private float popularity;

    private int voteCount;

    private boolean video;

    private float voteAverage;

    @Nullable
    public String getPosterPath() {
        return posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    @Nullable
    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public boolean hasPoster() {
        return posterPath != null;
    }
}