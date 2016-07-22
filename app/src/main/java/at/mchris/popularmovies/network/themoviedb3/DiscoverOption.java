package at.mchris.popularmovies.network.themoviedb3;

/**
 * Option to specify the movies fetch.
 */
public enum DiscoverOption {
    POPULAR("popular"),
    RELEASE_DATE("release_date"),
    REVENUE("revenue"),
    PRIMARY_RELEASE_DATE("primary_release_date"),
    ORIGINAL_TITLE("original_title"),
    VOTE_AVERAGE("vote_average"),
    VOTE_COUNT("vote_count");

    private final String text;
    DiscoverOption(final String text) { this.text = text; }
    @Override public String toString() { return text; }
}