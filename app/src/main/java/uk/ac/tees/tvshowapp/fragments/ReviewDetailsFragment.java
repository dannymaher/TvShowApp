package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.Review;

public class ReviewDetailsFragment extends Fragment {
    public static final String ARG_REVIEW ="review";
    private Review review;

    public ReviewDetailsFragment() {
    }

    public static ReviewDetailsFragment newInstance(Review review){
        ReviewDetailsFragment fragment = new ReviewDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REVIEW, review);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            review = (Review) getArguments().getSerializable(ARG_REVIEW);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_details,container,false);
        TextView reviewText = view.findViewById(R.id.review_text);
        if(!review.getContent().equals(null)){
            reviewText.setText(review.getContent());
        }
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
