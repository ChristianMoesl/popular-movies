package at.mchris.popularmovies.network.themoviedb3;

import com.android.volley.Response;

/**
 * A request to get a moviedb configuration object, which
 * is necessary for fetching images.
 */
public class ConfigurationRequest extends MovieDbRequest<Configuration> {

    protected static final String CONFIG_PATH = "/configuration";

    public ConfigurationRequest(String apiKey, Response.Listener<Configuration> listener) {
        super(apiKey, CONFIG_PATH, Configuration.class, listener);
    }
}
