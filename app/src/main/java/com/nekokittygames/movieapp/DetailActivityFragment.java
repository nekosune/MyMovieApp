package com.nekokittygames.movieapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_detail, container, false);



        MovieDetails movieDetails=getActivity().getIntent().getParcelableExtra("data");
        TextView textView=(TextView)view.findViewById(R.id.detail_title);
        textView.setText(movieDetails.title);

        RatingBar ratingBar=(RatingBar)view.findViewById(R.id.detail_rating);
        ratingBar.setRating(movieDetails.vote_average / 2);

        TextView synopsisView=(TextView)view.findViewById(R.id.detail_synopsis);
        synopsisView.setText(movieDetails.overview);

        TextView dateView=(TextView)view.findViewById(R.id.detail_date);
        dateView.setText(movieDetails.release_date);
        ImageView imageView= (ImageView) view.findViewById(R.id.detail_poster);
        Picasso.with(getContext()).load(Utilities.getPosterUrl(movieDetails.poster_path)).into(imageView);
        imageView.setContentDescription(movieDetails.title);
        return view;
    }
}
