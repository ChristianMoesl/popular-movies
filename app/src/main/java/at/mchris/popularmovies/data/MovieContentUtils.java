package at.mchris.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContentResolverCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import at.mchris.popularmovies.data.MovieContract.*;

/**
 * Created by chris_000 on 18.07.2016.
 */
public class MovieContentUtils {

    private static final String LOG_TAG = MovieContentUtils.class.getSimpleName();

    public static Configuration getConfiguration(Context context) {
        return getConfiguration(context, false, 0);
    }

    public static Configuration getConfigurationbyId(Context context, long id) {
        return getConfiguration(context, true, id);
    }

    private static Configuration getConfiguration(Context context, boolean useId, long configId) {
        final ContentResolver resolver = context.getContentResolver();

        String baseUrl = null;

        Cursor cr = ContentResolverCompat.query(resolver,
                ConfigurationEntry.CONTENT_URI,
                null,
                useId ? ConfigurationEntry._ID + " = ?" : null,
                useId ? new String[]{Long.toString(configId)} : null,
                ConfigurationEntry.COLUMN_TIME_STAMP + " DESC LIMIT 1",
                null);

        if (cr != null) {
            if (cr.moveToFirst()) {
                baseUrl = cr.getString(cr.getColumnIndex(ConfigurationEntry.COLUMN_BASE_URL));
                configId = cr.getLong(cr.getColumnIndex(ConfigurationEntry._ID));
            }
            cr.close();
        }

        List<String> sizes = null;

        cr = ContentResolverCompat.query(resolver,
                PosterSizeEntry.CONTENT_URI,
                null,
                PosterSizeEntry.COLUMN_CONFIG_KEY + " = ?",
                new String[]{Long.toString(configId)},
                null,
                null);

        if (cr != null) {

            if (cr.moveToFirst()) {

                sizes = new ArrayList<>(cr.getCount());
                do {
                    sizes.add(cr.getString(cr.getColumnIndex(PosterSizeEntry.COLUMN_POSTER_SIZE)));
                } while (cr.moveToNext());

            }
            cr.close();
        }

        if (baseUrl == null || sizes == null) {
            throw new Resources.NotFoundException("Configuration not found");

        }
        Configuration config = new Configuration(baseUrl, sizes);
        config.setId(configId);
        return config;
    }

    public static void insertMovieDataSet(Context context, List<Movie> movies, Configuration configuration) {

        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(ConfigurationEntry.COLUMN_BASE_URL, configuration.getBaseUrl());
        values.put(ConfigurationEntry.COLUMN_TIME_STAMP, getDateTime());

        Uri uri = resolver.insert(ConfigurationEntry.CONTENT_URI, values);
        long configId = ContentUris.parseId(uri);
        configuration.setId(configId);

        for (final String size : configuration.getPosterSizes()) {

            values.clear();
            values.put(PosterSizeEntry.COLUMN_CONFIG_KEY, configId);
            values.put(PosterSizeEntry.COLUMN_POSTER_SIZE, size);
            values.put(PosterSizeEntry.COLUMN_TIME_STAMP, getDateTime());

            resolver.insert(PosterSizeEntry.CONTENT_URI, values);
        }

        for (Movie movie : movies) {

            values.clear();
            values.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(MovieEntry.COLUMN_USER_RATING, movie.userRating.get());
            values.put(MovieEntry.COLUMN_TITLE, movie.title.get());
            values.put(MovieEntry.COLUMN_OVERVIEW, movie.overview.get());
            values.put(MovieEntry.COLUMN_TIME_STAMP, getDateTime());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, normalizeDate(movie.releaseDate.get()));
            values.put(MovieEntry.COLUMN_CONFIG_KEY, configId);

            uri = resolver.insert(MovieEntry.CONTENT_URI, values);
            movie.setId(ContentUris.parseId(uri));
        }

        Log.v(LOG_TAG, "Inserted " + movies.size() + " movies");
    }

    public static void deleteAllMovieDataSets(Context context) {

        int affected = context.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        Log.v(LOG_TAG, "Deleted " + affected + " movies");

        affected = context.getContentResolver().delete(
                PosterSizeEntry.CONTENT_URI,
                null,
                null
        );

        Log.v(LOG_TAG, "Deleted " + affected + " poster sizes");

         affected = context.getContentResolver().delete(
                ConfigurationEntry.CONTENT_URI,
                null,
                null
        );
        Log.v(LOG_TAG, "Deleted " + affected + " configurations");
    }

    public static Movie getMovieById(Context context, long id) {

        final ContentResolver resolver = context.getContentResolver();
        Movie movie = null;

        Cursor cr = ContentResolverCompat.query(resolver,
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry._ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null);

        if (cr != null) {

            if (cr.moveToFirst()) {

                long configId = cr.getLong(cr.getColumnIndex(MovieEntry.COLUMN_CONFIG_KEY));

                Configuration configuration = getConfigurationbyId(context, configId);

                movie = new Movie(
                        context,
                        cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_TITLE)),
                        cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)),
                        cr.getFloat(cr.getColumnIndex(MovieEntry.COLUMN_USER_RATING)),
                        cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)),
                        cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)),
                        configuration);

                movie.setId(cr.getLong(cr.getColumnIndex(MovieEntry._ID)));
            }

            cr.close();
        }

        if (movie == null) {
            throw new IllegalStateException("Movie not found");
        }
        return movie;
    }

    public static List<Movie> getAllMovies(Context context) {

        final ContentResolver resolver = context.getContentResolver();
        List<Movie> movies = null;

        Cursor cr = ContentResolverCompat.query(resolver,
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null);

        if (cr != null) {

            if (cr.moveToFirst()) {

                long configId = cr.getLong(cr.getColumnIndex(MovieEntry.COLUMN_CONFIG_KEY));

                Configuration configuration = getConfigurationbyId(context, configId);
                movies = new ArrayList<>(cr.getCount());
                do {
                    Movie movie = new Movie(
                            context,
                            cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_TITLE)),
                            cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)),
                            cr.getFloat(cr.getColumnIndex(MovieEntry.COLUMN_USER_RATING)),
                            cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)),
                            cr.getString(cr.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)),
                            configuration);

                    movie.setId(cr.getLong(cr.getColumnIndex(MovieEntry._ID)));

                    movies.add(movie);

                } while (cr.moveToNext());

            }
            cr.close();
        }

        if (movies == null) {
            throw new IllegalStateException("Movies not found");
        }
        return movies;
    }

    private static String normalizeDate(String date) {

        if (date == null) {
            return date;
        }

        String[] t = date.split("\\.");
        StringBuilder sb = new StringBuilder(5);
        sb.append(t[2]).append('-');

        if (t[1].length() < 2) {
            sb.append("0" + t[1]);
        } else {
            sb.append(t[1]);
        }

        sb.append('-');

        if (t[2].length() < 2) {
            sb.append("0" + t[2]);
        } else {
            sb.append(t[2]);
        }

        Log.v("Date", "Date normalized: " + sb.toString());

        return sb.toString();
    }

    private static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        return sdf.format(Calendar.getInstance().getTime());
    }
}
