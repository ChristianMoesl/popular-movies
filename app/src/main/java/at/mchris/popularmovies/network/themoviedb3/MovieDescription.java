package at.mchris.popularmovies.network.themoviedb3;


import java.util.List;

/**
 * Simple POD object which holds all the
 * movie info needed.
 */
public class MovieDescription {
    public String posterPath;
    public boolean adult;
    public String overview;
    public String releaseDate;
    public List<Integer> genreIds;
    public int id;
    public String originalTitle;
    public String originalLanguage;
    public String title;
    public String backdropPath;
    public float popularity;
    public int voteCount;
    public boolean video;
    public float voteAverage;

    public boolean hasPoster() {
        return posterPath != null;
    }
}