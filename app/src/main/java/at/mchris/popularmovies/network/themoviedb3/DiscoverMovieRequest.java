package at.mchris.popularmovies.network.themoviedb3;

import com.android.volley.Response;

/**
 * A /discover/movie request to get a list of movies filtered in
 * a specific way.
 */
public class DiscoverMovieRequest extends MovieDbRequest<DiscoverMovieAnswer> {

    protected static final String DISCOVER_MOVIE_PATH = "/discover/movie";
    protected static final String SORT_PARAM_KEY = "sort_by";

    protected static final String PAGE_PARAM_KEY = "page";
    public static final int PAGE_PARAM_MAX = 1000;
    public static final int PAGE_PARAM_MIN = 1;

    public DiscoverMovieRequest(String apiKey, Response.Listener<DiscoverMovieAnswer> listener) {
        super(apiKey, DISCOVER_MOVIE_PATH, DiscoverMovieAnswer.class, listener);
    }

    public DiscoverMovieRequest setDiscoverOption(DiscoverOption discoverOption, SortOption sortOption) {
        getBuilder().putParameter(SORT_PARAM_KEY,
                discoverOption.toString() + '.' + sortOption);
        return this;
    }

    public DiscoverMovieRequest setPage(int page) {

        if (page < PAGE_PARAM_MIN || page > PAGE_PARAM_MAX) {
            throw new IllegalArgumentException("pages has to be between "
                    + PAGE_PARAM_MIN + " and " + PAGE_PARAM_MAX);
        }

        getBuilder().putParameter(PAGE_PARAM_KEY, Integer.toString(page));
        return this;
    }
}
