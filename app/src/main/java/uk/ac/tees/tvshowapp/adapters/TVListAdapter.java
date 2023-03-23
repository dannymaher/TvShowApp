package uk.ac.tees.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

/**
 * recyclerview adapter for list of tv shows with posters
 */
public class TVListAdapter extends RecyclerView.Adapter<TVListAdapter.TvViewHolder> {
    private List<TVShow> tvShows;
    private onTVShowSelectedListener selectedListener;

    private int layoutId = R.layout.item_tvshow;
    private PosterImageSize posterImageSize = PosterImageSize.w500;

    /**
     * @param tvShows          the tv shows to display
     * @param selectedListener the listener to call when an tv show is selected
     * @param smallCards       true to display small, fixed size, cards with padding. false for normal size.
     */
    public TVListAdapter(List<TVShow> tvShows, onTVShowSelectedListener selectedListener, boolean smallCards) {
        this.tvShows = tvShows;
        this.selectedListener = selectedListener;

        if (smallCards) {
            layoutId = R.layout.item_tvshow_padding;
            posterImageSize = PosterImageSize.w342;
        }
    }

    static class TvViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImage;
        public TextView showTitle;

        public TvViewHolder(View v) {
            super(v);
            posterImage = v.findViewById(R.id.show_poster);
            showTitle = v.findViewById(R.id.show_title);

        }
    }

    @Override
    public TvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        TvViewHolder vh = new TvViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TvViewHolder holder, final int position) {
        holder.posterImage.setOnClickListener(v -> selectedListener.onTVShowSelected(tvShows.get(position)));

        if (tvShows.get(position).getPoster(PosterImageSize.w500) != null) {
            holder.showTitle.setVisibility(View.GONE);

            Picasso.get()
                    .load(tvShows.get(position).getPoster(posterImageSize))
                    .placeholder(R.drawable.image_loading_full)
                    .fit()
                    .centerCrop()
                    .into(holder.posterImage);
        } else {
            holder.showTitle.setVisibility(View.VISIBLE);
            holder.showTitle.setText(tvShows.get(position).getName());
            holder.posterImage.setImageDrawable(ContextCompat.getDrawable(holder.posterImage.getContext(), R.drawable.image_loading_full));
        }
    }

    public interface onTVShowSelectedListener {
        void onTVShowSelected(TVShow tvShow);
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }
}
