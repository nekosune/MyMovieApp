package com.nekokittygames.movieapp;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by Katrina on 02/09/2015.
 */
public class Utilities {

    public static String getPosterUrl(String input)
    {
        return "http://image.tmdb.org/t/p/w185/"+input;
    }
    public static String getSortPreference(Context c)
    {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(c.getString(R.string.pref_sort),c.getString(R.string.pref_sort_popular));
    }

    public static String getYoutubeURL(String key)
    {
        return "http://img.youtube.com/vi/"+key+"/0.jpg";
    }

    public static Uri getYoutubeLink(String key) {
        return Uri.parse("https://www.youtube.com/watch?v="+key);
    }
}
