package uk.ac.tees.tvshowapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.Crew;

/**
 * adapter for displaying lists of crew
 */
public class CrewListAdapter extends RecyclerView.Adapter<CrewListAdapter.PersonViewHolder> {
    private List<Crew> crew;
    private OnCrewSelectedListener selectedListener;

    public CrewListAdapter(List<Crew> people, OnCrewSelectedListener selectedListener) {
        this.crew = people;
        this.selectedListener = selectedListener;
    }

    @NonNull
    @Override
    public CrewListAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cast, parent, false);
        CrewListAdapter.PersonViewHolder vh = new CrewListAdapter.PersonViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CrewListAdapter.PersonViewHolder holder, final int position) {
        holder.personName.setText(crew.get(position).getName());
        holder.personImage.setOnClickListener(v -> selectedListener.onCrewSelected(crew.get(position)));

        if (crew.get(position).getProfile() != null) {
            Picasso.get()
                    .load(crew.get(position).getProfile())
                    .placeholder(R.drawable.image_loading_full)
                    .fit()
                    .centerCrop()
                    .into(holder.personImage);
        } else {
            holder.personImage.setImageDrawable(ContextCompat.getDrawable(holder.personImage.getContext(), R.drawable.image_loading_full));
        }
    }

    @Override
    public int getItemCount() {
        return crew.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        ImageView personImage;
        TextView personName;

        PersonViewHolder(View v) {
            super(v);
            personImage = v.findViewById(R.id.person_image);
            personName = v.findViewById(R.id.person_name);

            TextView personRole = v.findViewById(R.id.person_role);
            personRole.setVisibility(View.INVISIBLE);
            personRole.setTextSize(0);
        }
    }

    public interface OnCrewSelectedListener {
        void onCrewSelected(Crew crew);
    }
}
