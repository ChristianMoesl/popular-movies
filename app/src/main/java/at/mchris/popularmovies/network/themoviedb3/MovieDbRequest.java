package at.mchris.popularmovies.network.themoviedb3;

import com.android.volley.Response;

/**
 * A basic request of the movie db webservice.
 *
 * @param <T> The answer type of the request.
 */
public abstract class MovieDbRequest<T> {

    protected static final String BASE_PATH = "http://api.themoviedb.org/3";

    protected static final String API_PARAM_KEY = "api_key";

    private final JsonRequestBuilder<T> request = new JsonRequestBuilder<>();

    public MovieDbRequest(String apiKey, String subPath, Class<T> clazz, Response.Listener<T> listener) {
        request.setPath(BASE_PATH + subPath)
                .setResponseListener(listener)
                .setClass(clazz)
                .putParameter(API_PARAM_KEY, apiKey);
    }

    protected JsonRequestBuilder<T> getBuilder() {
        return request;
    }

    public MovieDbRequest<T> setErrorListener(Response.ErrorListener errorListener) {
        request.setErrorListener(errorListener);
        return this;
    }

    public MovieDbRequest<T> setApiKey(String apiKey) {
        request.putParameter(API_PARAM_KEY, apiKey);
        return this;
    }

    public JsonRequest<T> build() {
        return request.build();
    }

}
