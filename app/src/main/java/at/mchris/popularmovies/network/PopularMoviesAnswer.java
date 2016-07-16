package at.mchris.popularmovies.network;

import java.util.List;

/**
 * Created by chris_000 on 16.07.2016.
 */
public class PopularMoviesAnswer {
    public int page;
    public List<Movie> results;
    public int totalResults;
    public int totalPages;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Page: ")
                .append(page)
                .append("  Total results: ")
                .append(totalResults)
                .append("  Total pages: ")
                .append(totalPages)
                .toString();
    }
}
