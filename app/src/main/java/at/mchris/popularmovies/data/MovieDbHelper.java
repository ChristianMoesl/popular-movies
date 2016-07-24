package at.mchris.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteClosable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Closeable;

import static at.mchris.popularmovies.data.MovieContract.*;

/**
 * The helper to access the movie db SQLite database.
 */
public class MovieDbHelper extends SQLiteOpenHelper implements Closeable {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + "("
                + MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieEntry.COLUMN_CONFIG_KEY + " INTEGER NOT NULL, "
                + MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_RELEASE_DATE + " DATE, "
                + MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, "
                + MovieEntry.COLUMN_TIME_STAMP + " DATETIME NOT NULL, "
                + "FOREIGN KEY (" + MovieEntry.COLUMN_CONFIG_KEY + ") REFERENCES "
                        + ConfigurationEntry.TABLE_NAME + "(" + ConfigurationEntry._ID + "))";

        final String SQL_CREATE_CONFIGURATION_TABLE =
                "CREATE TABLE " + ConfigurationEntry.TABLE_NAME + "("
                + ConfigurationEntry._ID + " INTEGER PRIMARY KEY, "
                + ConfigurationEntry.COLUMN_BASE_URL + " TEXT NOT NULL, "
                + ConfigurationEntry.COLUMN_TIME_STAMP + " DATETIME NOT NULL)";

        final String SQL_CREATE_POSTER_SIZES_TABLE =
                "CREATE TABLE " + PosterSizeEntry.TABLE_NAME + "("
                + PosterSizeEntry._ID + " INTEGER PRIMARY KEY, "
                + PosterSizeEntry.COLUMN_CONFIG_KEY + " INTEGER NOT NULL, "
                + PosterSizeEntry.COLUMN_POSTER_SIZE + " TEXT NOT NULL, "
                + PosterSizeEntry.COLUMN_TIME_STAMP + " DATETIME NOT NULL, "
                + "FOREIGN KEY (" + PosterSizeEntry.COLUMN_CONFIG_KEY + ") REFERENCES "
                    + ConfigurationEntry.TABLE_NAME + "(" + ConfigurationEntry._ID + "))";

        sqLiteDatabase.execSQL(SQL_CREATE_CONFIGURATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POSTER_SIZES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        final String SQL_DROP = "DROP TABLE IF EXISTS ";

        sqLiteDatabase.execSQL(SQL_DROP + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(SQL_DROP + PosterSizeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(SQL_DROP + ConfigurationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
