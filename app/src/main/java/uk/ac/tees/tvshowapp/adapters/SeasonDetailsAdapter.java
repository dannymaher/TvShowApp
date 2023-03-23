package uk.ac.tees.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.enums.EpisodeImageSize;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

/**
 * for displaying the details of a season and displaying its episodes
 */
public class SeasonDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Season season;
    private List<Episode> episodes;
    private OnEpisodeSelectedListener selectedListener;

    private static int TYPE_HEADER = 0;
    private static int TYPE_ITEM = 1;

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        public ImageView episodeImage;
        public TextView episodeName;
        public TextView episodeOverview;
        public CardView cardView;

        public EpisodeViewHolder(View v) {
            super(v);
            episodeImage = v.findViewById(R.id.episode_image);
            episodeName = v.findViewById(R.id.episode_name);
            episodeOverview = v.findViewById(R.id.episode_overview);
            cardView = v.findViewById(R.id.episode_card);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImage;
        public TextView seasonName;
        public TextView seasonAirDate;
        public TextView seasonOverview;
        public TextView episodesText;

        public HeaderViewHolder(View v) {
            super(v);
            posterImage = v.findViewById(R.id.season_poster);
            seasonName = v.findViewById(R.id.season_name);
            seasonAirDate = v.findViewById(R.id.season_air_date);
            seasonOverview = v.findViewById(R.id.season_overview);
            episodesText = v.findViewById(R.id.episodes_text);
        }
    }

    public SeasonDetailsAdapter(Season season, OnEpisodeSelectedListener selectedListener) {
        this.season = season;
        this.episodes = season.getEpisodes();
        this.selectedListener = selectedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.season_details_header, parent, false);
            HeaderViewHolder vh = new HeaderViewHolder(v);
            return vh;
        }else{
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_episode_padding, parent, false);
            EpisodeViewHolder vh = new EpisodeViewHolder(v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof HeaderViewHolder){
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            Picasso.get()
                    .load(season.getPosterPath(PosterImageSize.w185))
                    .placeholder(R.drawable.image_loading_full)
                    .into(headerHolder.posterImage);

            headerHolder.seasonName.setText(season.getName());
            headerHolder.seasonAirDate.setText(season.getAirDate());
            headerHolder.seasonOverview.setText(season.getOverview());
            headerHolder.episodesText.setText(season.getEpisodes().size() + " Episodes");
        }else{
            final Episode episode = episodes.get(position - 1);
            EpisodeViewHolder episodeHolder = (EpisodeViewHolder) holder;

            if(episode.hasOverview()){
                episodeHolder.cardView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        selectedListener.onEpisodeSelected(episode);
                    }
                });
            }else{
                episodeHolder.cardView.setOnClickListener(null);
            }

            if(episode.getStill(EpisodeImageSize.w185) != null){
                episodeHolder.episodeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                episodeHolder.episodeImage.setAlpha((float) 1);
                Picasso.get()
                        .load(episode.getStill(EpisodeImageSize.w185))
                        .placeholder(R.drawable.image_loading_full)
                        .fit()
                        .centerCrop()
                        .into(episodeHolder.episodeImage);
            }else{
                episodeHolder.episodeImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                episodeHolder.episodeImage.setAlpha((float) .5);
                episodeHolder.episodeImage.setImageDrawable(ContextCompat.getDrawable(episodeHolder.episodeImage.getContext(), R.drawable.image_error));
            }

            episodeHolder.episodeName.setText(episode.getName());
            episodeHolder.episodeOverview.setText(episode.getOverview());
        }
    }

    public interface OnEpisodeSelectedListener {
        void onEpisodeSelected(Episode episode);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return episodes.size() + 1;
    }
}
