package at.mchris.popularmovies.network.themoviedb3;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Utils for simplified json de- serialization.
 */
public class JsonUtils {

    public static <T> T fromJson(JSONObject json, Class<T> clazz) {

        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(json.toString(), clazz);
    }
}
