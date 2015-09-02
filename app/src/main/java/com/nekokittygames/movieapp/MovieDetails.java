package com.nekokittygames.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Katrina on 01/09/2015.
 */
public class MovieDetails implements Parcelable{

    public String poster_path;
    public String title;
    public String overview;
    public float vote_average;
    public String release_date;

    public MovieDetails()
    {

    }

    protected MovieDetails(Parcel in) {
        poster_path = in.readString();
        title = in.readString();
        overview = in.readString();
        vote_average = in.readFloat();
        release_date = in.readString();
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeFloat(vote_average);
        dest.writeString(release_date);
    }
}
