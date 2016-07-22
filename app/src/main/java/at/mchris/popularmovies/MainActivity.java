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
import at.mchris.popularmovies.network.themoviedb3.*;

public class MainActivity extends AppCompatActivity
        implements OverviewFragment.OnMovieSelectedListener,
                    FragmentManager.OnBackStackChangedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private String movieSelectionType;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        movieSelectionType = savedInstanceState.getString(null);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putString(null, movieSelectionType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final ArrayAdapter<CharSequence> sortTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.movie_sort_types, android.R.layout.simple_spinner_item);
        sortTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerToolbarMain.setOnItemSelectedListener(onItemSelected);
        binding.spinnerToolbarMain.setAdapter(sortTypeAdapter);
        movieSelectionType = (String)sortTypeAdapter.getItem(0);

        setSupportActionBar(binding.toolbarMain);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .add(R.id.activity_main_fragment, OverviewFragment.newInstance())
                    .commit();

        }

        // Update the home button visibility.
        onBackStackChanged();
    }

    @Override
    public void onMovieSelected(Uri uri) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .add(R.id.activity_main_fragment, DetailFragment.newInstance(uri))
                .addToBackStack(null)
                .commit();
    }

    private final AdapterView.OnItemSelectedListener onItemSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            String selected = (String) binding.spinnerToolbarMain.getSelectedItem();

            if (!movieSelectionType.equals(selected)) {
                movieSelectionType = selected;

                Fragment fragment =
                        getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment);

                if (fragment instanceof OverviewFragment) {
                    OverviewFragment overview = (OverviewFragment)fragment;
                    overview.setMovieSetType(movieSelectionType);
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    };

    private DiscoverOption convertFromSpinnerText(String text) {
        if (getString(R.string.most_popular).equals(text)) {
            return DiscoverOption.POPULAR;
        } else if (getString(R.string.highest_rated).equals(text)) {
            return DiscoverOption.VOTE_AVERAGE;
        }
        throw new IllegalArgumentException("text is not a discovery option");
    }

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

    // TODO: Check if this is called multiple times...
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
