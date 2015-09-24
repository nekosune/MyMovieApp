package com.nekokittygames.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nekokittygames.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Katrina on 01/09/2015.
 */
class MovieAdapter extends CursorAdapter {
    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView=(ImageView)view.findViewById(R.id.imageButton);
        }
    }

    public MovieAdapter(Context context, Cursor c,int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view=LayoutInflater.from(context).inflate(R.layout.gridlist_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder= (ViewHolder) view.getTag();

        Picasso.with(context).load(Utilities.getPosterUrl(cursor.getString(MainScreenFragment.COL_POSTER))).into(holder.imageView);
        holder.imageView.setContentDescription(cursor.getString(MainScreenFragment.COL_TITLE));
    }
}
