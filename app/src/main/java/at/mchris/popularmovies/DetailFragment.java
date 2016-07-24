package at.mchris.popularmovies;

import android.content.ContentUris;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import at.mchris.popularmovies.data.Movie;
import at.mchris.popularmovies.data.MovieContentUtils;
import at.mchris.popularmovies.databinding.FragmentDetailBinding;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    /**
     * Key for uri serialization.
     */
    private static final String MOVIE_URI_KEY = "uri";

    /**
     * Tag used for logging.
     */
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    /**
     * The poster quality. (A lower quality gives faster download speed)
     */
    private static final double POSTER_QUALITY = 1.0;

    /**
     * The uri of the movie to show.
     */
    private Uri movieUri;

    /**
     * The data binding instance for this fragment.
     */
    private FragmentDetailBinding binding;

    public DetailFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uri The URI of the movie.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(Uri uri) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_URI_KEY, uri);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Restores all persistent state of this fragment.
     *
     * @param savedInstanceState The saved instance state of this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieUri = getArguments().getParcelable(MOVIE_URI_KEY);
        }
    }

    /**
     * Creates all views and the data binding for this fragment.
     *
     * @param inflater The layout inflater, which should be used.
     * @param container The container, where this fragment is embedded in.
     * @param savedInstanceState The persistent state of this fragment.
     * @return The created view to display.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        long movieId = ContentUris.parseId(movieUri);
        try {
            Movie movie = MovieContentUtils.getMovieById(this.getContext(), movieId);
            binding.setMovie(movie);
            updatePosterSize(movie);

        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return binding.getRoot();
    }

    /**
     * Updates the poster size and loads a image with a fitting size into the
     * image view.
     *
     * @param movie The movie to be updated.
     */
    private void updatePosterSize(Movie movie) {
        final Resources res =  getResources();
        int width = (int) res.getDimension(R.dimen.detail_activity_image_width);
        int height = (int) res.getDimension(R.dimen.detail_activity_image_height);

        binding.getMovie().setPosterSize(width, height, POSTER_QUALITY);

        Picasso.with(this.getContext())
                .load(movie.posterUrl.get())
                .centerCrop()
                .resize(width, height)
                .into(binding.imageViewDetail);
    }

}
