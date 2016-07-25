package at.mchris.popularmovies;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import at.mchris.popularmovies.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements OverviewFragment.OnMovieSelectedListener,
                    FragmentManager.OnBackStackChangedListener {
    /**
     * A tag for logging purpose.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * A data binding instance for this activity.
     */
    private ActivityMainBinding binding;

    /**
     * The current displayed movie top list.
     */
    private String movieTopList;

    /**
     * Saves the persistent state of this activity.
     *
     * @param savedInstanceState The state to be saved.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        movieTopList = savedInstanceState.getString(null);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Restores the persistent state of this activity.
     *
     * @param savedInstanceState The state to be restored.
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putString(null, movieTopList);
    }

    /**
     * Creates all the view items for this activity.
     *
     * @param savedInstanceState The state of the main activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Setup the spinner in the toolbar, which is used to select a movie top list type.
        final ArrayAdapter<CharSequence> sortTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.movie_sort_types, android.R.layout.simple_spinner_item);
        sortTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerToolbarMain.setOnItemSelectedListener(onMovieTopListSelected);
        binding.spinnerToolbarMain.setAdapter(sortTypeAdapter);
        movieTopList = (String)sortTypeAdapter.getItem(0);

        setSupportActionBar(binding.toolbarMain);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);

        // Starts the default fragment on initial creation.
        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .add(R.id.activity_main_fragment, OverviewFragment.newInstance(movieTopList))
                    .commit();
        }

        // Update the home button visibility.
        onBackStackChanged();
    }

    /**
     * Implementation of the {@link at.mchris.popularmovies.OverviewFragment.OnMovieSelectedListener}
     * listener. Opens the detail fragment of the selected movie.
     *
     * @param uri The URI of the selected movie.
     */
    @Override
    public void onMovieSelected(Uri uri) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.activity_main_fragment, DetailFragment.newInstance(uri))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Notifies the overview fragment if the movie top list selection in the toolbar spinner
     * has changed.
     */
    private final AdapterView.OnItemSelectedListener onMovieTopListSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            String selected = (String) binding.spinnerToolbarMain.getSelectedItem();

            if (!movieTopList.equals(selected)) {
                movieTopList = selected;

                Fragment fragment =
                        getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment);

                if (fragment instanceof OverviewFragment) {
                    OverviewFragment overview = (OverviewFragment)fragment;
                    overview.setMovieTopList(movieTopList);
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    };

    /**
     * Updates the presence of the back button in the app toolbar.
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        onForegroundFragmentChanged();
    }

    /**
     * @param item The selected menu item.
     * @return True if the action has been processed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                onForegroundFragmentChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Will be called whenever the foreground fragment changes.
     *
     * Handles the visibility of the toolbar spinner, used to select the movie top list.
     */
    private void onForegroundFragmentChanged() {

        final Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_fragment);

        if (fragment instanceof OverviewFragment) {
            binding.spinnerToolbarMain.setVisibility(View.VISIBLE);
        } else if  (fragment instanceof  DetailFragment) {
            binding.spinnerToolbarMain.setVisibility(View.GONE);
        }
    }
}
