package at.mchris.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

import org.junit.Test;

import static at.mchris.popularmovies.data.MovieContract.MovieEntry.*;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * Created by chris_000 on 18.07.2016.
 */
//@RunWith(AndroidJUnit4.class)
//@LargeTest
public class TestDb extends ProviderTestCase2<MovieProvider> {

    public TestDb() {
        super(MovieProvider.class, MovieContract.CONTENT_AUTHORITY);
    }

    @Test
    public void testSample() {

        ContentResolver resolver = getMockContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_OVERVIEW, "Overview text");
        contentValues.put(COLUMN_TITLE, "Title text");
        contentValues.put(COLUMN_POSTER_PATH, "Poster uri");
        contentValues.put(COLUMN_RELEASE_DATE, "date");
        contentValues.put(COLUMN_USER_RATING, 3.4);
        contentValues.put(COLUMN_TIME_STAMP, "2016-04-05 22:15:15");

        resolver.insert(MovieContract.MovieEntry.CONTENT_URI,
                    contentValues);

        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);

        assertTrue(cursor != null);
        cursor.moveToFirst();

        assertTrue(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)).equals("Overview text"));
        assertTrue(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)).equals("Title text"));
        assertTrue(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)).equals("Poster uri"));
        assertTrue(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)).equals("date"));
        assertTrue(cursor.getFloat(cursor.getColumnIndex(COLUMN_USER_RATING)) == (float)3.4);

        assertFalse(cursor.moveToNext());
        cursor.close();
    }

}
