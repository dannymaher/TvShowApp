package uk.ac.tees.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

/**
 * adapter for displaying a list of seasons
 */
public class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.SeasonViewHolder> {
    private List<Season> seasons;
    private OnSeasonSelectedListener selectedListener;

    public static class SeasonViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImage;
        public TextView seasonName;
        public TextView seasonOverview;
        public CardView cardView;

        public SeasonViewHolder(View v) {
            super(v);
            posterImage = v.findViewById(R.id.season_poster);
            seasonName = v.findViewById(R.id.season_name);
            seasonOverview = v.findViewById(R.id.season_overview);
            cardView = v.findViewById(R.id.season_card);
        }
    }

    public SeasonListAdapter(List<Season> seasons, OnSeasonSelectedListener selectedListener) {
        this.seasons = seasons;
        this.selectedListener = selectedListener;
    }

    @Override
    public SeasonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_season_padding, parent, false);
        SeasonViewHolder vh = new SeasonViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SeasonViewHolder holder, final int position) {
        final Season season = seasons.get(position);

        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedListener.onSeasonSelected(season);
            }
        });

        Picasso.get()
                .load(season.getPosterPath(PosterImageSize.w342))
                .placeholder(R.drawable.image_loading_full)
                .fit()
                .centerCrop()
                .into(holder.posterImage);

        holder.seasonName.setText(season.getName());
        holder.seasonOverview.setText(season.getOverview());
    }

    public interface OnSeasonSelectedListener {
        void onSeasonSelected(Season season);
    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }
}
