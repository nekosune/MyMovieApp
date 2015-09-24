package com.nekokittygames.movieapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by Katrina on 14/09/2015.
 */
public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.nekokittygames.movieapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_YOUTUBE = "youtube";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_FAVORITE = "favorite";


    /**
     * Inner class that defines the movie table
     */
    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final Uri FAVORITE_URI=CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }


        /**
         * Movie ID
         */
        public static final String MOVIE_ID="movie_id";
        /**
         * Movie table title
         */
        public static final String TABLE_NAME="movie";

        /**
         * Title of the movie
         */
        public static final String MOVIE_TITLE="title";

        /**
         * Syopsis of the movie
         */
        public static final String MOVIE_SYNOPSIS="synopsis";

        /**
         * Date the movie was released
         */
        public static final String MOVIE_RELEASE_DATE="release_date";

        /**
         * Rating the movie has
         */
        public static final String MOVIE_RATING="rating";

        /**
         * Popularity of the movie
         */
        public static final String MOVIE_POPULARITY="popularity";

        /**
         * Partial URL to poster path
         */
        public static final String MOVIE_POSTER="poster";

        /**
         * IMDB ID for the movie
         */
        public static final String MOVIE_IMDB_ID="imdb_id";

        /**
         * Runtime of the movie
         */
        public static final String MOVIE_RUNTIME="runtime";


        /**
         * Website of the movie
         */
        public static final String MOVIE_WEBSITE="website";

    }

    public static final class YoutubeEntry implements  BaseColumns
    {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_YOUTUBE).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_YOUTUBE;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_YOUTUBE;

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        /**
         * Table name
         */
        public static final String TABLE_NAME="youtube";

        /**
         * ID of movie this video belongs to
         */
        public static final String YOUTUBE_MOVIE_ID="movie_id";
        /**
         * Name of the video
         */
        public static final String YOUTUBE_NAME="name";
        /**
         * Key of the video
         */
        public static final String YOUTUBE_KEY="key";
    }

    public static final class ReviewEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_REVIEW;

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildMovieUro(long movieID) {
            return CONTENT_URI.buildUpon().appendPath("movie").appendPath(Long.toString(movieID)).build();
        }
        /**
         * Table name
         */
        public static final String TABLE_NAME="review";

        /**
         * ID of the movie this review belongs to
         */
        public static final String REVIEW_MOVIE_ID="movie_id";

        /**
         * Author of the review
         */
        public static final String REVIEW_AUTHOR="author";

        /**
         * Content of the review
         */
        public static final String REVIEW_CONTENT="content";

        /**
         * URL of the review
         */
        public static final String REVIEW_URL="url";
    }


    public static final class FavoriteEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_FAVORITE;

        public static Uri buildUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static final String TABLE_NAME="favorite";

        public static final String FAVORITE_MOVIE_ID="movie_id";
    }
}
