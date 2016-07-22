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
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    private static final String MOVIE_URI_KEY = "uri";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final double POSTER_SCALE_FACTOR = 1.0;

    private Uri movieUri;
    private FragmentDetailBinding binding;

    public DetailFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uri The uri of the movie.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(Uri uri) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_URI_KEY, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieUri = getArguments().getParcelable(MOVIE_URI_KEY);
        }
    }

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

    private void updatePosterSize(Movie movie) {
        final Resources res =  getResources();
        int width = (int) res.getDimension(R.dimen.detail_activity_image_width);
        int height = (int) res.getDimension(R.dimen.detail_activity_image_height);

        binding.getMovie().setPosterSize(width, height, POSTER_SCALE_FACTOR);

        Picasso.with(this.getContext())
                .load(movie.posterUrl.get())
                .centerCrop()
                .resize(width, height)
                .into(binding.imageViewDetail);
    }

}
