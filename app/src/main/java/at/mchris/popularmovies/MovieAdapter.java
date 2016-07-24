package at.mchris.popularmovies;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import at.mchris.popularmovies.data.Movie;
import at.mchris.popularmovies.databinding.GridItemMovieBinding;

/**
 * A simple adapter to manage all the movie view models.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    /**
     * A event if an child item of the movie adapter is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter<?> recyclerView, View view, int position, long id);
    }

    /**
     * A placeholder for the movie thumbnail.
     */
    private final BitmapDrawable placeholder;

    /**
     * The context, where this adapter lives in.
     */
    private final Context context;

    /**
     * A list of all view models to display.
     */
    private List<Movie> movies = new ArrayList<>();

    /**
     * The subscribed listener of the item-clicked event.
     */
    private OnItemClickListener onItemClickListener;

    public MovieAdapter(Context context) {
        this.context = context;
        final Bitmap bm = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888);
        placeholder = new BitmapDrawable(context.getResources(), bm);
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getMovies() { return movies; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        GridItemMovieBinding binding = DataBindingUtil.inflate(
                                LayoutInflater.from(context),
                                R.layout.grid_item_movie, parent, false);

        return new MovieAdapter.ViewHolder(binding);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void removeOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (this.onItemClickListener == onItemClickListener) {
            this.onItemClickListener = null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindConnection(movies.get(position));
    }

    @Override
    public long getItemId(int i) {
        return movies.get(i).getId();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @BindingAdapter({"url"})
    public static void setUrl(ImageView view, String url) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.updatePoster();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final GridItemMovieBinding binding;

        public ViewHolder(GridItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.gridItemMovieImage.setOnClickListener(onClick);
            binding.gridItemMovieImage.setTag(this);
        }

        public void bindConnection(Movie movie) {
            binding.setMovie(movie);
            updatePoster();
        }

        private void updatePoster() {

            Movie movie = binding.getMovie();

            Picasso.with(context.getApplicationContext())
                    .load(movie.posterUrl.get())
                    .centerCrop()
                    .placeholder(placeholder)
                    .resize(movie.posterWidth.get(), movie.posterHeight.get())
                    .into(binding.gridItemMovieImage);
        }

        private final View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(MovieAdapter.this,
                            view, movies.indexOf(binding.getMovie()), binding.getMovie().getId());
                }
            }
        };
    }
}
