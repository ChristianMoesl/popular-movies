package at.mchris.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import java.util.List;

import at.mchris.popularmovies.data.Movie;
import at.mchris.popularmovies.data.MovieContentUtils;
import at.mchris.popularmovies.data.MovieContract;
import at.mchris.popularmovies.databinding.FragmentOverviewBinding;
import at.mchris.popularmovies.network.themoviedb3.Configuration;
import at.mchris.popularmovies.network.themoviedb3.ConfigurationRequest;
import at.mchris.popularmovies.network.themoviedb3.DiscoverOption;
import at.mchris.popularmovies.network.themoviedb3.Info;
import at.mchris.popularmovies.network.themoviedb3.MovieDescription;
import at.mchris.popularmovies.network.themoviedb3.MovieTopList;
import at.mchris.popularmovies.network.themoviedb3.MovieTopListAnswer;
import at.mchris.popularmovies.network.themoviedb3.MovieTopListRequest;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnMovieSelectedListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    /**
     * Signals the parent activity, that a movie is clicked by the user.
     */
    public interface OnMovieSelectedListener {
        void onMovieSelected(Uri movieUri);
    }

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_OF_COLUMNS = 2;
    private static final int ITEM_VIEW_CACHE = 20;

    private FragmentOverviewBinding binding;
    private MovieAdapter movieAdapter;
    private OnMovieSelectedListener movieSelectedListener;

    private RequestQueue requestQueue;
    private String movieSelectionType;
    private at.mchris.popularmovies.data.Configuration configuration;
    private boolean isInitialSetupDone = false;

    public OverviewFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OverviewFragment.
     */
    public static OverviewFragment newInstance() {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false);

        requestQueue = NetworkUtils.getInstance(this.getActivity().getApplicationContext())
                .getRequestQueue();

        movieSelectionType = getString(R.string.most_popular);

        movieAdapter = new MovieAdapter(this.getContext());
        movieAdapter.setOnItemClickListener(onMovieClickedListener);

        binding.recyclerMovies.setLayoutManager(new GridLayoutManager(this.getContext(), NUMBER_OF_COLUMNS));
        binding.recyclerMovies.setAdapter(movieAdapter);
        binding.recyclerMovies.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerMovies.setItemViewCacheSize(ITEM_VIEW_CACHE);

        final ArrayAdapter<CharSequence> sortTypeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.movie_sort_types, android.R.layout.simple_spinner_item);
        sortTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        try {
            configuration = MovieContentUtils.getConfiguration(this.getContext());
            movieAdapter.setMovies(MovieContentUtils.getAllMovies(this.getContext()));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        binding.recyclerMovies.getViewTreeObserver().addOnGlobalLayoutListener(onFinishedDrawing);

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieSelectedListener) {
            movieSelectedListener = (OnMovieSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnMovieSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        movieSelectedListener = null;
    }


    public void setMovieSetType(String movieSelectionType) {
        this.movieSelectionType = movieSelectionType;
        fetchAllMovies();
    }

    private final ViewTreeObserver.OnGlobalLayoutListener onFinishedDrawing =
            new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (isInitialSetupDone) {
                return;
            }

            isInitialSetupDone = true;

            if (configuration == null || movieAdapter.getMovies().size() == 0) {
                fetchConfiguration();
            } else {
                updatePosterSize();
            }
        }
    };

    private void updatePosterSize() {

        final int targetWidth = binding.recyclerMovies.getLayoutManager().getWidth() / NUMBER_OF_COLUMNS;
        final int targetHeight = (int)(targetWidth * Info.POSTER_ASPECT_RATIO);
        final double quality = 0.5;

        for (Movie movie : movieAdapter.getMovies()) {
            movie.setPosterSize(targetWidth, targetHeight, quality);
        }

        movieAdapter.notifyDataSetChanged();
    }

    private final MovieAdapter.OnItemClickListener onMovieClickedListener =
            new MovieAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(RecyclerView.Adapter<?> recycler, View view, int i, long l) {
            MovieAdapter movieAdapter = (MovieAdapter)recycler;
            Movie movie = movieAdapter.getMovies().get(i);

            Uri movieUri = MovieContract.MovieEntry.buildMovieUri(movie.getId());
            movieSelectedListener.onMovieSelected(movieUri);
        }
    };

    private DiscoverOption convertFromSpinnerText(String text) {
        if (getString(R.string.most_popular).equals(text)) {
            return DiscoverOption.POPULAR;
        } else if (getString(R.string.highest_rated).equals(text)) {
            return DiscoverOption.VOTE_AVERAGE;
        }
        throw new IllegalArgumentException("text is not a discovery option");
    }



    private void fetchConfiguration() {
        requestQueue.add(new ConfigurationRequest(
                getString(R.string.the_movie_db_api_key),
                onConfigurationReceived).build());
    }

    private final Response.Listener<Configuration> onConfigurationReceived =
            new Response.Listener<Configuration>() {
                @Override
                public void onResponse(Configuration response) {

                    configuration = new at.mchris.popularmovies.data.Configuration(
                            response.getBaseUrl(), response.getPosterSizes());
                    fetchAllMovies();

                }
            };

    private void fetchAllMovies() {

        if (movieSelectionType.equals(getString(R.string.most_popular))) {

            requestQueue.add(new MovieTopListRequest(
                    getString(R.string.the_movie_db_api_key),
                    MovieTopList.POPULAR,
                    onMoviesReceived).build());

        } else if (movieSelectionType.equals(getString(R.string.highest_rated))) {

            requestQueue.add(new MovieTopListRequest(
                    getString(R.string.the_movie_db_api_key),
                    MovieTopList.TOP_RATED,
                    onMoviesReceived).build());

        } else {
            throw new IllegalStateException("Unkown movie selection type: " + movieSelectionType);
        }
    }

    /**
     *
     */
    private final Response.Listener<MovieTopListAnswer> onMoviesReceived =
            new Response.Listener<MovieTopListAnswer>() {
        @Override
        public void onResponse(MovieTopListAnswer response) {

            MovieContentUtils.deleteAllMovieDataSets(OverviewFragment.this.getContext());
            final List<Movie> movies = movieAdapter.getMovies();
            movies.clear();

            for (MovieDescription movieDesc : response.getMovieDescriptions()) {

                if (movieDesc.hasPoster()) {
                    Movie movie = Movie.createFromDescription(getContext(), movieDesc, configuration);
                    movies.add(movie);
                }

            }
            MovieContentUtils.insertMovieDataSet(OverviewFragment.this.getContext(), movies, configuration);
            updatePosterSize();
        }
    };
}
