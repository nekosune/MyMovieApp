package com.nekokittygames.movieapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.nekokittygames.movieapp.MovieDetails;
import com.nekokittygames.movieapp.R;
import com.nekokittygames.movieapp.Utilities;
import com.nekokittygames.movieapp.data.MovieContract;
import com.nekokittygames.movieapp.network.MovieDbService;
import com.nekokittygames.movieapp.network.MovieResult;
import com.nekokittygames.movieapp.network.Result;
import com.nekokittygames.movieapp.network.Result_;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Katrina on 28/09/2015.
 */
public class MovieAppSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieAppSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private MovieDbService mMovieService;
    private Retrofit mRetrofit;

    public MovieAppSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mRetrofit=new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();
        mMovieService=mRetrofit.create(MovieDbService.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Response<MovieResult> result;
        try {
            result = mMovieService.getResults("popularity.desc", getContext().getString(R.string.movie_api_key)).execute();
        }
        catch (IOException e)
        {
            Log.e(getClass().getSimpleName(), "Crashed grabbing results", e);
            return;
        }
        if(!result.isSuccess()) {
            Log.e(getClass().getSimpleName(),"Error Found: "+result.code());
            return;
        }
        MovieResult res=result.body();
        List<ContentValues> movieValues=new ArrayList<>();
        List<ContentValues> movieVideos=new ArrayList<>();
        List<ContentValues> movieReviews=new ArrayList<>();
        for(MovieDetails details: res.results)
        {
            Response<com.nekokittygames.movieapp.network.MovieDetails> detailResponse;
            try {
                detailResponse = mMovieService.getDetails(Integer.toString(details.id),getContext().getString(R.string.movie_api_key)).execute();

            }
            catch (IOException e)
            {
                Log.e(getClass().getSimpleName(),"Crashed getting results",e);
                return;
            }
            if(!detailResponse.isSuccess())
            {
                Log.e(getClass().getSimpleName(),"Error found: "+result.errorBody());
                return;
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
            Picasso.with(getContext()).load(Utilities.getPosterUrl(movieDet.getPosterPath())).fetch();
            movieValues.add(value);

            for(Result_ vres:movieDet.getVideos().getResults())
            {
                if(vres.getType().equalsIgnoreCase("Trailer") && vres.getSite().equalsIgnoreCase("YouTube"))
                {
                    ContentValues vvalue=new ContentValues();
                    vvalue.put(MovieContract.YoutubeEntry.YOUTUBE_MOVIE_ID,movieDet.getId());
                    vvalue.put(MovieContract.YoutubeEntry.YOUTUBE_KEY,vres.getKey());
                    Picasso.with(getContext()).load(Utilities.getYoutubeURL(vres.getKey())).fetch();
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
        }
        if(movieValues.size()>0) {
            ContentValues[] mv=new ContentValues[movieValues.size()];
            movieValues.toArray(mv);
            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, mv);
        }

        if(movieVideos.size()>0) {
            ContentValues[] mv=new ContentValues[movieVideos.size()];
            movieVideos.toArray(mv);
            getContext().getContentResolver().bulkInsert(MovieContract.YoutubeEntry.CONTENT_URI, mv);
        }
        if(movieReviews.size()>0) {
            ContentValues[] mv=new ContentValues[movieReviews.size()];
            movieReviews.toArray(mv);
            getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, mv);
        }


        return;
    }
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieAppSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
