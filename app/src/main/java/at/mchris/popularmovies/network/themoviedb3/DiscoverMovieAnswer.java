package at.mchris.popularmovies.network.themoviedb3;

import java.util.List;

/**
 * Answer on a /discover/move request.
 */
public class DiscoverMovieAnswer {
    private int page;
    private List<MovieDescription> results;
    private int totalResults;
    private int totalPages;

    public List<MovieDescription> getMovieDescriptions() {
        return results;
    }
}
