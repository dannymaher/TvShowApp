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
import uk.ac.tees.tvshowapp.tmdb.model.Cast;
import uk.ac.tees.tvshowapp.tmdb.model.enums.ProfileImageSize;

/**
 * adapter for displaying lists of cast
 */
public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.PersonViewHolder> {
    private List<Cast> cast;
    private onCastSelectedListener selectedListener;

    public CastListAdapter(List<Cast> people, onCastSelectedListener selectedListener){
        this.cast = people;
        this.selectedListener = selectedListener;
    }

    @NonNull
    @Override
    public CastListAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cast, parent, false);
        CastListAdapter.PersonViewHolder vh = new CastListAdapter.PersonViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CastListAdapter.PersonViewHolder holder, final int position) {
        holder.personName.setText(cast.get(position).getName());
        holder.personRole.setText(cast.get(position).getCharacter());
        holder.personImage.setOnClickListener(v -> selectedListener.onCastSelected(cast.get(position)));

        if (cast.get(position).getProfilePath() != null){
            Picasso.get()
                    .load(cast.get(position).getProfile(ProfileImageSize.w185))
                    .placeholder(R.drawable.image_loading_full)
                    .fit()
                    .centerCrop()
                    .into(holder.personImage);
        }else {
            holder.personImage.setImageDrawable(ContextCompat.getDrawable(holder.personImage.getContext(), R.drawable.image_loading_full));
        }
    }

    @Override
    public int getItemCount() {
        return cast.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder{
        public ImageView personImage;
        public TextView personName;
        public TextView personRole;

        public PersonViewHolder(View v){
            super(v);
            personImage = v.findViewById(R.id.person_image);
            personName = v.findViewById(R.id.person_name);
            personRole = v.findViewById(R.id.person_role);
        }
    }

    public interface onCastSelectedListener {
        void onCastSelected(Cast cast);
    }
}
