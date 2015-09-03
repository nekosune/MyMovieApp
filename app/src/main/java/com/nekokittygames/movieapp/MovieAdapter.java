package com.nekokittygames.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Katrina on 01/09/2015.
 */
class MovieAdapter extends ArrayAdapter<MovieDetails> {


    public MovieAdapter(Context context, ArrayList<MovieDetails> objects) {
        super(context, R.layout.gridlist_item, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView==null)
        {
            view= LayoutInflater.from(getContext()).inflate(R.layout.gridlist_item,parent,false);
        }
        else
        {
            view=convertView;
        }

        MovieDetails detail= getItem(position);
        ImageView button= (ImageView) view.findViewById(R.id.imageButton);
        Picasso.with(getContext()).load(Utilities.getPosterUrl(detail.poster_path)).into(button);
        button.setContentDescription(detail.title);

        return view;
    }
}
