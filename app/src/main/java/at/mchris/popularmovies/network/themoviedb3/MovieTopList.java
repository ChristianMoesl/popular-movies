package at.mchris.popularmovies.network.themoviedb3;

/**
 * Created by chris_000 on 22.07.2016.
 */
public enum MovieTopList {

    LATEST("latest"),
    NOW_PLAYING("now_playing"),
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming");

    private final String path;
    MovieTopList(final String path) { this.path = path; }
    @Override public String toString() { return path; }
}
