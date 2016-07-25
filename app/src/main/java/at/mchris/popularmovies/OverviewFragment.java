package at.mchris.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.android.volley.Response;

import java.util.List;

import at.mchris.popularmovies.data.Movie;
import at.mchris.popularmovies.data.MovieContentUtils;
import at.mchris.popularmovies.data.MovieContract;
import at.mchris.popularmovies.databinding.FragmentOverviewBinding;
import at.mchris.popularmovies.network.themoviedb3.Configuration;
import at.mchris.popularmovies.network.themoviedb3.ConfigurationRequest;
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

    /**
     * A tag used for logging purpose.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * The number of columns with movie images.
     */
    private static final int NUMBER_OF_COLUMNS = 2;

    /**
     * The amount of cached movie views in the recycler view.
     */
    private static final int ITEM_VIEW_CACHE = 20;

    /**
     * The data binding instance for this fragment.
     */
    private FragmentOverviewBinding binding;

    /**
     * The adapter which holds all movies and movie views.
     */
    private MovieAdapter movieAdapter;

    /**
     * A reference to the parent activity, which has to implement
     * {@link OverviewFragment.OnMovieSelectedListener}.
     */
    private OnMovieSelectedListener movieSelectedListener;

    /**
     * A instance of the network utils to start simple HTTP requests.
     */
    private NetworkUtils network;

    /**
     * The current selected movie top list.
     */
    private String movieTopList;

    /**
     * The movie db configuration used to build image URLs.
     */
    @Nullable
    private at.mchris.popularmovies.data.Configuration configuration;

    /**
     * Determines if the initial setup is done.
     */
    private boolean isInitialSetupDone = false;

    public OverviewFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OverviewFragment.
     */
    public static OverviewFragment newInstance(String movieSelectionType) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(null, movieSelectionType);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Restores all persistent state of this fragment.
     *
     * @param savedInstanceState The saved state to be restored.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieTopList = getArguments().getString(null);
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false);

        network = NetworkUtils.getInstance(getContext());

        // Setup the adapter for the recycler view to display all the movies.
        movieAdapter = new MovieAdapter(this.getContext());
        movieAdapter.setOnItemClickListener(onMovieClickedListener);

        // Setup the recycler view for all the thumbnails to display.
        binding.recyclerMovies.setLayoutManager(new GridLayoutManager(this.getContext(), NUMBER_OF_COLUMNS));
        binding.recyclerMovies.setAdapter(movieAdapter);
        binding.recyclerMovies.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerMovies.setItemViewCacheSize(ITEM_VIEW_CACHE);

        final ArrayAdapter<CharSequence> sortTypeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.movie_sort_types, android.R.layout.simple_spinner_item);
        sortTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Fetch data from the database only if the app isn't initially started.
        // The data has to be fetched from the network on initial app start to ensure up 2 date data.
        if (savedInstanceState != null) {
            try {
                configuration = MovieContentUtils.getConfiguration(this.getContext());
                movieAdapter.setMovies(MovieContentUtils.getAllMovies(this.getContext()));
            } catch (Exception e) {
                Log.v(LOG_TAG, e.getMessage());
            }
        }

        binding.recyclerMovies.getViewTreeObserver().addOnGlobalLayoutListener(onFinishedDrawing);

        return binding.getRoot();
    }

    /**
     * Checks if the activity is compatible and saves a interface reference.
     *
     * @param context The activity, which uses this fragment.
     */
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

    /**
     * Deletes the reference of the attached activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        movieSelectedListener = null;
    }

    /**
     * Sets a new movie top list. Triggers network requests to fetch all the
     * data to be displayed.
     *
     * @param movieTopList The movie top list to be set.
     */
    public void setMovieTopList(String movieTopList) {

        if (movieTopList == null) {
            throw new IllegalArgumentException("movieTopList cannot be null.");
        }

        boolean movieListChanged = !this.movieTopList.equals(movieTopList);

        if (movieListChanged) {
            this.movieTopList = movieTopList;
        }

        if (configuration == null || movieAdapter.getMovies().size() == 0) {
            fetchConfiguration();
        } else if (movieListChanged) {
            fetchAllMovies();
        }
    }

    /**
     * Starts to fetch the images after the view is drawn.
     */
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

    /**
     * Fetches all the movie thumbnails with the right size for the display.
     */
    private void updatePosterSize() {

        final int targetWidth = binding.recyclerMovies.getLayoutManager().getWidth() / NUMBER_OF_COLUMNS;
        final int targetHeight = (int)(targetWidth * Info.POSTER_ASPECT_RATIO);
        final double quality = 0.5;

        for (Movie movie : movieAdapter.getMovies()) {
            movie.setPosterSize(targetWidth, targetHeight, quality);
        }

        movieAdapter.notifyDataSetChanged();
    }

    /**
     * Notifies the activity if a movie is clicked.
     */
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

    /**
     * Fetches the movie db configuration.
     */
    private void fetchConfiguration() {
        network.addToRequestQueue(new ConfigurationRequest(
                getString(R.string.the_movie_db_api_key),
                onConfigurationReceived).build());
    }

    /**
     * Saves the received movie db configuration and starts the image fetching task.
     */
    private final Response.Listener<Configuration> onConfigurationReceived =
    new Response.Listener<Configuration>() {
        @Override
        public void onResponse(Configuration response) {

            configuration = new at.mchris.popularmovies.data.Configuration(
                    response.getBaseUrl(), response.getPosterSizes());
            fetchAllMovies();

        }
    };

    /**
     * Fetches a movie top list from the movie db web service.
     */
    private void fetchAllMovies() {

        if (movieTopList.equals(getString(R.string.most_popular))) {

            network.addToRequestQueue(new MovieTopListRequest(
                    getString(R.string.the_movie_db_api_key),
                    MovieTopList.POPULAR,
                    onMoviesReceived).build());

        } else if (movieTopList.equals(getString(R.string.highest_rated))) {

            network.addToRequestQueue(new MovieTopListRequest(
                    getString(R.string.the_movie_db_api_key),
                    MovieTopList.TOP_RATED,
                    onMoviesReceived).build());

        } else {
            throw new IllegalStateException("Unkown movie selection type: " + movieTopList);
        }
    }

    /**
     * Saves all received movies and creates the corresponding view models.
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
