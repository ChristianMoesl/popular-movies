package at.mchris.popularmovies.network.themoviedb3;

import com.android.volley.Response;

/**
 * Created by chris_000 on 22.07.2016.
 */
public class MovieTopListRequest extends MovieDbRequest<MovieTopListAnswer> {

    protected static final String POPULAR_MOVIE_PATH = "/movie";

    protected static final String PAGE_PARAM_KEY = "page";
    public static final int PAGE_PARAM_MAX = 1000;
    public static final int PAGE_PARAM_MIN = 1;

    public MovieTopListRequest(String apiKey, MovieTopList toplist, Response.Listener<MovieTopListAnswer> listener) {
        super(apiKey, POPULAR_MOVIE_PATH + '/' + toplist, MovieTopListAnswer.class, listener);
    }

    public MovieTopListRequest setPage(int page) {

        if (page < PAGE_PARAM_MIN || page > PAGE_PARAM_MAX) {
            throw new IllegalArgumentException("pages has to be between "
                    + PAGE_PARAM_MIN + " and " + PAGE_PARAM_MAX);
        }

        getBuilder().putParameter(PAGE_PARAM_KEY, Integer.toString(page));
        return this;
    }
}
