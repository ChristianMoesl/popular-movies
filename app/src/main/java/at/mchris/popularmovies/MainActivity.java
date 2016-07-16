package at.mchris.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import at.mchris.popularmovies.network.HttpJsonQuery;
import at.mchris.popularmovies.network.Movie;
import at.mchris.popularmovies.network.PopularMoviesAnswer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayAdapter<String> adapter;
    private String[] testData = new String[]{
            "First item",
            "Second item"
    };

    private static final String POPULAR_MOVIES = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc";
    private static final String API_KEY = "api_key=0c172f2ca16cdd51a9edecebcff7d693";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<>(getBaseContext(),
                R.layout.grid_item_movie,
                testData);

        GridView grid = (GridView) findViewById(R.id.grid_view_movies);
        grid.setAdapter(adapter);

        Query q = new Query();
        try {
            q.execute();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private class Query extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            PopularMoviesAnswer pop = new HttpJsonQuery<>(
                    POPULAR_MOVIES + "&" + API_KEY,
                    PopularMoviesAnswer.class).execute();

            Log.i(TAG, pop.toString());

            for (Movie movie : pop.results) {
                Log.i(TAG, "Movie " + movie.title);
            }

            return null;
        }
    }
}
