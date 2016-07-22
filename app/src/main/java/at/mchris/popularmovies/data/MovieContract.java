package at.mchris.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by chris_000 on 18.07.2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "at.mchris.popularmovies.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = PATH_MOVIE;
        public static final String COLUMN_CONFIG_KEY = "configuration_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TIME_STAMP = "time_stamp";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final String PATH_CONFIGURATION = "configuration";

    public static final class ConfigurationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONFIGURATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONFIGURATION;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONFIGURATION;

        public static final String TABLE_NAME = PATH_CONFIGURATION;
        public static final String COLUMN_BASE_URL = "base_url";
        public static final String COLUMN_TIME_STAMP = "time_stamp";

        public static Uri buildConfigurationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final String PATH_POSTER_SIZES = "poster_sizes";

    public static final class PosterSizeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POSTER_SIZES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTER_SIZES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTER_SIZES;

        public static final String TABLE_NAME = PATH_POSTER_SIZES;
        public static final String COLUMN_CONFIG_KEY = "configuration_id";
        public static final String COLUMN_POSTER_SIZE = "poster_size";
        public static final String COLUMN_TIME_STAMP = "time_stamp";

        public static Uri buildPosterSizeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static String normalizeDate(String dateString) {

        final String from = "dd.mm.yyyy";
        final String to = "yyyy-mm-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(from, Locale.GERMAN);
        sdf.applyLocalizedPattern(to);
        final String result = sdf.format(dateString);

        Log.i("MovieContract", result);

        return result;
    }
}
