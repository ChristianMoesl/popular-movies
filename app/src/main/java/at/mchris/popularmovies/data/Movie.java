package at.mchris.popularmovies.data;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.Nullable;

import at.mchris.popularmovies.R;
import at.mchris.popularmovies.network.themoviedb3.*;

/**
 * The view model of a movie.
 */
public class Movie {

    public final ObservableInt posterHeight = new ObservableInt(1);
    public final ObservableInt posterWidth = new ObservableInt(1);
    public final ObservableField<String> posterUrl = new ObservableField<>();
    public final ObservableField<String> overview = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> userRating = new ObservableField<>();
    public final ObservableField<String> releaseDate = new ObservableField<>();

    private final Context context;

    private boolean isIdValid = false;

    private long id;

    @Nullable
    private final String posterPath;

    private final at.mchris.popularmovies.data.Configuration configuration;

    public static Movie createFromDescription(Context context, MovieDescription movieDescription,
                                              at.mchris.popularmovies.data.Configuration configuration) {
        return new Movie(context,
                        movieDescription.getTitle(),
                        movieDescription.getOverview(),
                        movieDescription.getVoteAverage(),
                        movieDescription.getReleaseDate(),
                        movieDescription.getPosterPath(),
                        configuration);
    }

    public Movie(Context context, String title, String overview, float voteAverage,
                 @Nullable String releaseDate, @Nullable String posterPath,
                 at.mchris.popularmovies.data.Configuration configuration) {
        this.context = context;
        this.posterPath = posterPath;
        this.configuration = configuration;

        this.overview.set(overview);
        this.title.set(title);
        this.userRating.set(Float.toString(voteAverage));

        if (releaseDate != null) {

            if (releaseDate.length() < 9) {

                throw new IllegalArgumentException("The date has to be in a YYYY-MM-DD format, but has: "
                                                + releaseDate);

            } else if (isFormatNeeded(releaseDate)) {

                this.releaseDate.set(convertDate(releaseDate));

            } else {

                this.releaseDate.set(releaseDate);
            }
        } else {

            this.releaseDate.set(context.getString(R.string.unknown));
        }
    }

    public long getId() {
        if (!isIdValid) {
            throw new IllegalAccessError("Id can't be read in invalid state");
        }
        return id;
    }

    public void setId(long id) {
        isIdValid = true;
        this.id = id;
    }

    public void setPosterSize(int targetWidth, int targetHeigth, double quality) {

        if (quality <= 0) {
            throw new IllegalArgumentException("The quality has to be bigger than 0");
        }

        posterWidth.set(targetWidth);
        posterHeight.set(targetHeigth);

        int scaledWidth = (int)(targetWidth * quality);
        int scaledHeight = (int)(targetHeigth * quality);

        posterUrl.set(configuration.buildImageUrl(
                context.getString(R.string.the_movie_db_api_key),
                this, scaledWidth, scaledHeight));
    }

    @Nullable
    public String getPosterPath() {
        return posterPath;
    }

    private boolean isFormatNeeded(String date) {
        return date.charAt(4) == '-' && date.charAt(7) == '-';
    }

    /**
     * @param date A date string in this format: YYYY-MM-DD.
     * @return The converted DD.MM.YYYY string.
     */
    private String convertDate(String date) {

        final String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        if (month.charAt(0) == '0') {
            month = month.substring(1);
        }
        String day = date.substring(8, 10);
        if (day.charAt(0) == '0') {
            day = day.substring(1);
        }

        return day + '.' + month + '.' + year;
    }

    public String getTitle() {
        return title.get();
    }

}
