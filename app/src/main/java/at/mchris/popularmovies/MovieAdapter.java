package at.mchris.popularmovies;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
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
    private BitmapDrawable placeholder;

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
    }

    /**
     * Updates the list of movies to be displayed. "onDataSetChanged" has to be called to apply
     * the changes.
     *
     * @param movies The list of movies to be set.
     */
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    /**
     * @return The current set list of movies.
     */
    public List<Movie> getMovies() {
        return movies;
    }

    /**
     * Creates and initializes the view holder with it's data binding.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        GridItemMovieBinding binding = DataBindingUtil.inflate(
                                LayoutInflater.from(context),
                                R.layout.grid_item_movie, parent, false);

        return new MovieAdapter.ViewHolder(binding);
    }

    /**
     * @param onItemClickListener The listener to be called if an child item is clicked.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * @param onItemClickListener The listener to be removed.
     */
    public void removeOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (this.onItemClickListener == onItemClickListener) {
            this.onItemClickListener = null;
        }
    }

    /**
     * Binds an existing view holder to a new movie.
     *
     * @param holder The view holder to be used.
     * @param position The position of the item to bind to.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindMovie(movies.get(position));
    }

    /**
     * Returns the unique ID of the underlying child item.
     *
     * @param i The position of the child item to be used.
     * @return The unique ID of the underlying child item.
     */
    @Override
    public long getItemId(int i) {
        return movies.get(i).getId();
    }

    /**
     * @return The total amount of items in this movie adapter.
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * Updates the poster every time when the movie poster url is changing.
     *
     * @param view The view to be updated.
     * @param url The url which has changed.
     */
    @BindingAdapter({"url"})
    public static void setUrl(ImageView view, String url) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.updatePoster();
    }

    /**
     * The underlying view holder which binds to an child item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The data binding instance, which is created once.
         */
        private final GridItemMovieBinding binding;

        /**
         * Basic initialization of the view holder. It has to be bound to a data set
         * for full function.
         *
         * @param binding The data binding instance to be used.
         */
        public ViewHolder(GridItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.gridItemMovieImage.setOnClickListener(onClick);
            binding.gridItemMovieImage.setTag(this);
        }

        /**
         * Binds a new data set to this view holder.
         *
         * @param movie The movie to be set.
         */
        public void bindMovie(Movie movie) {
            binding.setMovie(movie);
            updatePoster();
        }

        /**
         * Updates the poster image in the corresponding image view with the specified
         * poster url.
         */
        private void updatePoster() {

            Movie movie = binding.getMovie();

            updatePlaceholder(movie.posterWidth.get(), movie.posterHeight.get());

            Picasso.with(context.getApplicationContext())
                    .load(movie.posterUrl.get())
                    .centerCrop()
                    .resize(movie.posterWidth.get(), movie.posterHeight.get())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(placeholder)
                    .into(binding.gridItemMovieImage);
        }

        private void updatePlaceholder(int width, int height) {
            if (placeholder == null
                    || placeholder.getBitmap().getWidth() != width
                        || placeholder.getBitmap().getHeight() != height) {
                final Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                placeholder = new BitmapDrawable(context.getResources(), bm);
            }
        }

        /**
         * Notifies an listener if a movie item is clicked.
         */
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
