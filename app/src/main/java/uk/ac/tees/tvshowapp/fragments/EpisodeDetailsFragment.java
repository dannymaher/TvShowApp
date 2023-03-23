package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.enums.EpisodeImageSize;
import uk.ac.tees.tvshowapp.util.CalendarEvents;

public class EpisodeDetailsFragment extends Fragment {
    public static final String ARG_EPISODE = "episode";

    private String tvShowName;
    private Episode episode;

    public EpisodeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EpisodeDetailsFragmentArgs args = EpisodeDetailsFragmentArgs.fromBundle(getArguments());
        tvShowName = args.getTvShowName();
        episode = args.getEpisode();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.calendar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.add_to_calendar);
        menuItem.setOnMenuItemClickListener(item -> CalendarEvents.insertEpisodeEvent(tvShowName, episode, getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_episode_details, container, false);

        TextView episodeName = view.findViewById(R.id.episode_name);
        episodeName.setText(episode.getName());
        TextView episodeNumber = view.findViewById(R.id.episode_number);
        episodeNumber.setText("Episode " + episode.getEpisodeNumber());
        TextView episodeDate = view.findViewById(R.id.episode_date);
        episodeDate.setText(episode.getAirDateFormatted());
        TextView episodeOverview = view.findViewById(R.id.episode_overview);
        episodeOverview.setText(episode.getOverview());
        ImageView episodeImage = view.findViewById(R.id.episode_image);
        Picasso.get()
                .load(episode.getStill(EpisodeImageSize.original))
                .into(episodeImage);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
