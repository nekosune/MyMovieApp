package com.nekokittygames.movieapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Katrina on 28/09/2015.
 */
public class MovieAppSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MovieAppSyncAdapter sMovieAppSyncAdapter = null;


    @Override
    public void onCreate() {
        Log.d("MovieAppSyncService", "onCreate - MovieAppSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieAppSyncAdapter == null) {
                sMovieAppSyncAdapter= new MovieAppSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sMovieAppSyncAdapter.getSyncAdapterBinder();
    }
}
