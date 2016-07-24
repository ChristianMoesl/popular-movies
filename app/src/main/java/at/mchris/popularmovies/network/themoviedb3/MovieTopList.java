package at.mchris.popularmovies.network.themoviedb3;

/**
 * Movie top lists of the movie db webservice.
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
