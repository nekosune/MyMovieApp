package com.nekokittygames.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nekokittygames.movieapp.data.MovieContract;
import com.nekokittygames.movieapp.network.MovieDbService;
import com.nekokittygames.movieapp.network.MovieResult;
import com.nekokittygames.movieapp.network.Result;
import com.nekokittygames.movieapp.network.Result_;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Katrina on 14/09/2015.
 */
public class FetchMovieDetails extends AsyncTask<Void, Void, List<MovieDetails>>
{

    private Context mContext;
    private MovieDbService mMovieService;
    private Retrofit mRetrofit;
    public FetchMovieDetails(Context context)
    {
        mContext=context;
        mRetrofit=new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();
        mMovieService=mRetrofit.create(MovieDbService.class);
    }
    @Override
    protected List<MovieDetails> doInBackground(Void... params) {
        Response<MovieResult> result;
        try {
            result = mMovieService.getResults("popularity.desc", mContext.getString(R.string.movie_api_key)).execute();
        }
        catch (IOException e)
        {
            Log.e(getClass().getSimpleName(), "Crashed grabbing results", e);
            return null;
        }
        if(!result.isSuccess()) {
            Log.e(getClass().getSimpleName(),"Error Found: "+result.code());
            return null;
        }
        MovieResult res=result.body();
        List<ContentValues> movieValues=new ArrayList<>();
        List<ContentValues> movieVideos=new ArrayList<>();
        List<ContentValues> movieReviews=new ArrayList<>();
        for(MovieDetails details: res.results)
        {

            Response<com.nekokittygames.movieapp.network.MovieDetails> detailResponse;
            try {
                detailResponse = mMovieService.getDetails(Integer.toString(details.id),mContext.getString(R.string.movie_api_key)).execute();

            }
            catch (IOException e)
            {
                Log.e(getClass().getSimpleName(),"Crashed getting results",e);
                return null;
            }
            if(!detailResponse.isSuccess())
            {
                Log.e(getClass().getSimpleName(),"Error found: "+result.errorBody());
                return null;
            }
            com.nekokittygames.movieapp.network.MovieDetails movieDet=detailResponse.body();
            ContentValues value=new ContentValues();
            value.put(MovieContract.MovieEntry.MOVIE_ID,details.id);
            value.put(MovieContract.MovieEntry.MOVIE_IMDB_ID,movieDet.getImdbId());
            value.put(MovieContract.MovieEntry.MOVIE_POPULARITY,movieDet.getPopularity());
            value.put(MovieContract.MovieEntry.MOVIE_POSTER,movieDet.getPosterPath());
            value.put(MovieContract.MovieEntry.MOVIE_RATING,movieDet.getVoteAverage());
            value.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE,movieDet.getReleaseDate());
            value.put(MovieContract.MovieEntry.MOVIE_RUNTIME,movieDet.getRuntime());
            value.put(MovieContract.MovieEntry.MOVIE_SYNOPSIS,movieDet.getOverview());
            value.put(MovieContract.MovieEntry.MOVIE_TITLE, movieDet.getTitle());
            value.put(MovieContract.MovieEntry.MOVIE_WEBSITE, movieDet.getHomepage());
            movieValues.add(value);

            for(Result_ vres:movieDet.getVideos().getResults())
            {
                if(vres.getType().equalsIgnoreCase("Trailer") && vres.getSite().equalsIgnoreCase("YouTube"))
                {
                    ContentValues vvalue=new ContentValues();
                    vvalue.put(MovieContract.YoutubeEntry.YOUTUBE_MOVIE_ID,movieDet.getId());
                    vvalue.put(MovieContract.YoutubeEntry.YOUTUBE_KEY,vres.getKey());
                    vvalue.put(MovieContract.YoutubeEntry.YOUTUBE_NAME,vres.getName());
                    movieVideos.add(vvalue);
                }
            }

            for(Result rres:movieDet.getReviews().getResults())
            {
                ContentValues rvalues=new ContentValues();
                rvalues.put(MovieContract.ReviewEntry.REVIEW_MOVIE_ID,movieDet.getId());
                rvalues.put(MovieContract.ReviewEntry.REVIEW_AUTHOR,rres.getAuthor());
                rvalues.put(MovieContract.ReviewEntry.REVIEW_CONTENT,rres.getContent());
                rvalues.put(MovieContract.ReviewEntry.REVIEW_URL,rres.getUrl());
                movieReviews.add(rvalues);
            }


            Log.i(getClass().getSimpleName(),movieDet.toString());
        }
        if(movieValues.size()>0) {
            ContentValues[] mv=new ContentValues[movieValues.size()];
            movieValues.toArray(mv);
            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, mv);
        }

        if(movieVideos.size()>0) {
            ContentValues[] mv=new ContentValues[movieVideos.size()];
            movieVideos.toArray(mv);
            mContext.getContentResolver().bulkInsert(MovieContract.YoutubeEntry.CONTENT_URI, mv);
        }
        if(movieReviews.size()>0) {
            ContentValues[] mv=new ContentValues[movieReviews.size()];
            movieReviews.toArray(mv);
            mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, mv);
        }


        return result.body().results;
    }


    @Override
    protected void onPostExecute(List<MovieDetails> result) {
    }
}
