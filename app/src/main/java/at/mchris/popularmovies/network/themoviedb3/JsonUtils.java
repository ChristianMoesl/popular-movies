package at.mchris.popularmovies.network.themoviedb3;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Created by chris_000 on 18.07.2016.
 */
public class JsonUtils {

    public static <T> T fromJson(JSONObject json, Class<T> clazz) {
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(json.toString(), clazz);
    }
}
