package at.mchris.popularmovies.network.themoviedb3;

/**
 * A Option which specifies the order of a movie list.
 */
public enum SortOption {
    DESCENDING("desc"),
    ASCENDING("asc");

    private final String text;
    SortOption(final String text) { this.text = text; }
    @Override public String toString() { return text; }
}