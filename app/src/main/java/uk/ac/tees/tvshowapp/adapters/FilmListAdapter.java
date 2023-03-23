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
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

/**
 * recyclerview adapter for list of films with posters
 */
public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.FilmViewHolder> {
    private List<Film> films;
    private onFilmSelectedListener selectedListener;

    private int layoutId = R.layout.item_tvshow;
    private PosterImageSize posterImageSize = PosterImageSize.w500;

    /**
     * @param films          the films to display
     * @param selectedListener the listener to call when an film is selected
     * @param smallCards       true to display small, fixed size, cards with padding. false for normal size.
     */
    public FilmListAdapter(List<Film> films, FilmListAdapter.onFilmSelectedListener selectedListener, boolean smallCards) {
        this.films = films;
        this.selectedListener = selectedListener;

        if (smallCards) {
            layoutId = R.layout.item_tvshow_padding;
            posterImageSize = PosterImageSize.w342;
        }
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImage;
        public TextView filmTitle;

        public FilmViewHolder(View v) {
            super(v);
            posterImage = v.findViewById(R.id.show_poster);
            filmTitle = v.findViewById(R.id.show_title);
        }
    }

    @Override
    public FilmListAdapter.FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        FilmListAdapter.FilmViewHolder vh = new FilmListAdapter.FilmViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FilmListAdapter.FilmViewHolder holder, final int position) {
        holder.posterImage.setOnClickListener(v -> selectedListener.onFilmSelected(films.get(position)));

        if(films.get(position).getPosterPath() != null){
            holder.filmTitle.setVisibility(View.GONE);
            Picasso.get()
                    .load(films.get(position).getPoster(posterImageSize))
                    .placeholder(R.drawable.image_loading_full)
                    .fit()
                    .centerCrop()
                    .into(holder.posterImage);
        }else{
            holder.filmTitle.setVisibility(View.VISIBLE);
            holder.filmTitle.setText(films.get(position).getTitle());
            holder.posterImage.setImageDrawable(ContextCompat.getDrawable(holder.posterImage.getContext(), R.drawable.image_loading_full));
        }
    }

    public interface onFilmSelectedListener {
        void onFilmSelected(Film film);
    }

    @Override
    public int getItemCount() {
        return films.size();
    }
}
