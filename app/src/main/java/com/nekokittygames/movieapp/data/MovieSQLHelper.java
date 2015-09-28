package com.nekokittygames.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nekokittygames.movieapp.data.MovieContract.MovieEntry;
import com.nekokittygames.movieapp.data.MovieContract.YoutubeEntry;
import com.nekokittygames.movieapp.data.MovieContract.ReviewEntry;
import com.nekokittygames.movieapp.data.MovieContract.FavoriteEntry;
/**
 * Created by Katrina on 14/09/2015.
 */
class MovieSQLHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=2;
    private static final String DATABASE_NAME="movies.db";
    public MovieSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE="CREATE TABLE " + MovieEntry.TABLE_NAME + " ("+
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.MOVIE_ID + " INTEGER NOT NULL,"+
                MovieEntry.MOVIE_IMDB_ID + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_RATING + " REAL NOT NULL, " +
                MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_RUNTIME + " INTEGER NOT NULL, " +
                MovieEntry.MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.MOVIE_WEBSITE + " TEXT ," +
                " UNIQUE ("+MovieEntry.MOVIE_ID+") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_YOUTUBE_TABLE="CREATE TABLE " + YoutubeEntry.TABLE_NAME + " (" +
                YoutubeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                YoutubeEntry.YOUTUBE_MOVIE_ID + " INTEGER NOT NULL ," +
                YoutubeEntry.YOUTUBE_NAME + " TEXT NOT NULL, " +
                YoutubeEntry.YOUTUBE_KEY + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + YoutubeEntry.YOUTUBE_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " ("+MovieEntry.MOVIE_ID+")"+
                " UNIQUE ("+YoutubeEntry.YOUTUBE_KEY+") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_YOUTUBE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE="CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewEntry.REVIEW_MOVIE_ID + " INTEGER NOT NULL ," +
                ReviewEntry.REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.REVIEW_URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + ReviewEntry.REVIEW_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " ("+MovieEntry.MOVIE_ID+")"+
                " UNIQUE ("+ReviewEntry.REVIEW_CONTENT+") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_FAVORITE_TABLE="CREATE TABLE " + FavoriteEntry.TABLE_NAME+ " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.FAVORITE_MOVIE_ID + " INTEGER NOT NULL ," +
                " FOREIGN KEY (" + FavoriteEntry.FAVORITE_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " ("+MovieEntry._ID+")," +
                " UNIQUE ("+FavoriteEntry.FAVORITE_MOVIE_ID+") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+YoutubeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ReviewEntry.TABLE_NAME);

        // In the future if I change the DB format, this will be dealt with in the upgraded version
        db.execSQL("DROP TABLE IF EXISTS "+FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
