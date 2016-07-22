package at.mchris.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import at.mchris.popularmovies.data.MovieContract.*;

/**
 * Created by chris_000 on 18.07.2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MovieDbHelper dbHelper;

    static final int MOVIE = 100;
    static final int CONFIGURATION = 200;
    static final int POSTER_SIZES = 300;

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final int match = uriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case MOVIE:
                cursor = dbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CONFIGURATION:
                cursor = dbHelper.getReadableDatabase().query(
                        ConfigurationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case POSTER_SIZES:
                cursor = dbHelper.getReadableDatabase().query(
                        PosterSizeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case CONFIGURATION:
                return ConfigurationEntry.CONTENT_TYPE;
            case POSTER_SIZES:
                return PosterSizeEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        long id;
        Uri returnUri;

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            final int match = uriMatcher.match(uri);

            switch (match) {
                case MOVIE:
                    id = db.insertOrThrow(MovieEntry.TABLE_NAME, null, contentValues);
                    returnUri = MovieEntry.buildMovieUri(id);
                    break;
                case CONFIGURATION:
                    id = db.insert(ConfigurationEntry.TABLE_NAME, null, contentValues);
                    returnUri = ConfigurationEntry.buildConfigurationUri(id);
                    break;
                case POSTER_SIZES:
                    id = db.insert(PosterSizeEntry.TABLE_NAME, null, contentValues);
                    returnUri = PosterSizeEntry.buildPosterSizeUri(id);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {

        int affectedRows = 0;

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            final int match = uriMatcher.match(uri);

            switch (match) {
                case MOVIE:
                    affectedRows = db.delete(MovieEntry.TABLE_NAME, whereClause, whereArgs);
                    break;
                case CONFIGURATION:
                    affectedRows = db.delete(ConfigurationEntry.TABLE_NAME, whereClause, whereArgs);
                    break;
                case POSTER_SIZES:
                    affectedRows = db.delete(PosterSizeEntry.TABLE_NAME, whereClause, whereArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {

        int affectedRows = 0;

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            final int match = uriMatcher.match(uri);

            switch (match) {
                case MOVIE:
                    affectedRows = db.update(MovieEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                    break;
                case CONFIGURATION:
                    affectedRows = db.update(ConfigurationEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                    break;
                case POSTER_SIZES:
                    affectedRows = db.update(PosterSizeEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_CONFIGURATION, CONFIGURATION);
        matcher.addURI(authority, MovieContract.PATH_POSTER_SIZES, POSTER_SIZES);

        return matcher;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieEntry.COLUMN_RELEASE_DATE)) {
            String dateValue = values.getAsString(MovieEntry.COLUMN_RELEASE_DATE);
            values.put(MovieEntry.COLUMN_RELEASE_DATE, MovieContract.normalizeDate(dateValue));
        }
    }
}
