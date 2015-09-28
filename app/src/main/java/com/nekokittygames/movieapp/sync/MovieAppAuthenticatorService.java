package com.nekokittygames.movieapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Katrina on 28/09/2015.
 */
public class MovieAppAuthenticatorService extends Service {

    private MovieAppAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator=new MovieAppAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
     return mAuthenticator.getIBinder();
    }
}
