package uk.ac.tees.tvshowapp.adapters;

import android.content.res.Resources;
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
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.enums.ProfileImageSize;

/**
 * adapter for displaying lists of people
 */
public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.PersonViewHolder> {
    private List<Person> people;
    private onPersonSelectedListener selectedListener;

    @NonNull
    @Override
    public PersonListAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tvshow, parent, false);
        PersonListAdapter.PersonViewHolder vh = new PersonListAdapter.PersonViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonListAdapter.PersonViewHolder holder, final int position) {
        Resources resource = holder.itemView.getContext().getResources();
        holder.personName.setText(people.get(position).getName());

        holder.personName.setBackground((resource.getDrawable(R.drawable.cast_text_background)));
        holder.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedListener.onPersonSelected(people.get(position));
            }
        });
        if (people.get(position).getProfilePath() != null){
            Picasso.get()
                    .load(people.get(position).getProfile(ProfileImageSize.h632))
                    .placeholder(R.drawable.image_loading_full)
                    .fit()
                    .centerCrop()
                    .into(holder.posterImage);

        }else {
            holder.posterImage.setImageDrawable(ContextCompat.getDrawable(holder.posterImage.getContext(), R.drawable.image_loading_full));
        }
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder{
        ImageView posterImage;
        TextView personName;

        PersonViewHolder(View v){
            super(v);
            posterImage = v.findViewById(R.id.show_poster);
            personName = v.findViewById(R.id.show_title);
        }


    }
    public PersonListAdapter(List<Person> people, PersonListAdapter.onPersonSelectedListener selectedListener){
        this.people = people;
        this.selectedListener = selectedListener;
    }

    public interface onPersonSelectedListener{
        void onPersonSelected(Person person);
    }
}
