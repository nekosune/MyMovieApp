package com.nekokittygames.movieapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Katrina on 14/09/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher=buildUriMatcher();

    private MovieSQLHelper mOpenHelper;

    static final int MOVIE=100;
    static final int MOVIE_WITH_ID=101;
    static final int MOVIE_BY_FAVORITES=102;
    static final int FAVORITE=200;
    static final int FAVORITE_BY_MOVIE=201;
    static final int YOUTUBE=300;
    static final int YOUTUBE_WITH_MOVIE=301;
    static final int REVIEW=400;
    static final int REVIEW_WITH_MOVIE=401;

    private static final SQLiteQueryBuilder QueryBuilder;

    static {
        QueryBuilder=new SQLiteQueryBuilder();
    }


    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,MovieContract.PATH_MOVIE,MOVIE);
        matcher.addURI(authority,MovieContract.PATH_MOVIE+"/#",MOVIE_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_MOVIE+"/"+MovieContract.PATH_FAVORITE,MOVIE_BY_FAVORITES);
        matcher.addURI(authority,MovieContract.PATH_YOUTUBE,YOUTUBE);
        matcher.addURI(authority,MovieContract.PATH_YOUTUBE+"/#",YOUTUBE_WITH_MOVIE);
        matcher.addURI(authority,MovieContract.PATH_FAVORITE,FAVORITE);
        matcher.addURI(authority,MovieContract.PATH_FAVORITE+"/#",FAVORITE_BY_MOVIE);
        matcher.addURI(authority,MovieContract.PATH_REVIEW,REVIEW);
        matcher.addURI(authority,MovieContract.PATH_REVIEW+"/#",REVIEW_WITH_MOVIE);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper=new MovieSQLHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        long id;
        switch (sUriMatcher.match(uri))
        {
            case MOVIE:
                QueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                break;
            case YOUTUBE:
                QueryBuilder.setTables(MovieContract.YoutubeEntry.TABLE_NAME);
                break;
            case REVIEW:
                QueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
                break;
            case FAVORITE:
                QueryBuilder.setTables(MovieContract.FavoriteEntry.TABLE_NAME);
                break;
            case FAVORITE_BY_MOVIE:
                QueryBuilder.setTables(MovieContract.FavoriteEntry.TABLE_NAME);
                id=Long.parseLong(uri.getLastPathSegment());
                selection=MovieContract.FavoriteEntry.FAVORITE_MOVIE_ID+ " = ?";
                selectionArgs=new String[] { Long.toString(id)};
                break;
            case MOVIE_WITH_ID:
                QueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                id = Long.parseLong(uri.getLastPathSegment());
                selection = MovieContract.MovieEntry.MOVIE_ID+" = ?";
                selectionArgs = new String[]{Long.toString(id)};
                break;

            case YOUTUBE_WITH_MOVIE:
                QueryBuilder.setTables(MovieContract.YoutubeEntry.TABLE_NAME);
                id = Long.parseLong(uri.getLastPathSegment());
                selection = MovieContract.YoutubeEntry.YOUTUBE_MOVIE_ID+" = ?";
                selectionArgs = new String[]{Long.toString(id)};
                break;

            case REVIEW_WITH_MOVIE:
                QueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
                id = Long.parseLong(uri.getLastPathSegment());
                selection = MovieContract.ReviewEntry.REVIEW_MOVIE_ID+" = ?";
                selectionArgs = new String[]{Long.toString(id)};
                break;
            case MOVIE_BY_FAVORITES:
                QueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN "+MovieContract.FavoriteEntry.TABLE_NAME+" ON "+MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry.MOVIE_ID + " = " + MovieContract.FavoriteEntry.TABLE_NAME+"."+MovieContract.FavoriteEntry.FAVORITE_MOVIE_ID);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        Log.i(getClass().getSimpleName(), QueryBuilder.buildQuery(projection,selection,null,null,null,null));
        retCursor=QueryBuilder.query(mOpenHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match)
        {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_FAVORITES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case YOUTUBE:
                return MovieContract.YoutubeEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_BY_MOVIE:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case YOUTUBE_WITH_MOVIE:
                return MovieContract.YoutubeEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match)
        {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case YOUTUBE: {
                long _id = db.insert(MovieContract.YoutubeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.YoutubeEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavoriteEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch (match)
        {
            case MOVIE:
                rowsDeleted=db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case YOUTUBE:
                rowsDeleted=db.delete(MovieContract.YoutubeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted=db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted=db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }
        if(rowsDeleted!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;


        switch (match)
        {
            case MOVIE:
                rowsUpdated=db.update(MovieContract.MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case YOUTUBE:
                rowsUpdated=db.update(MovieContract.YoutubeEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case REVIEW:
                rowsUpdated=db.update(MovieContract.ReviewEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated=db.update(MovieContract.FavoriteEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }
        if(rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case MOVIE: {
                int returnCount = bulkInsertMethod(MovieContract.MovieEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case YOUTUBE: {
                int returnCount = bulkInsertMethod(MovieContract.YoutubeEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW: {
                int returnCount = bulkInsertMethod(MovieContract.ReviewEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case FAVORITE: {
                int returnCount = bulkInsertMethod(MovieContract.FavoriteEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri,values);

        }
    }

    private int bulkInsertMethod(String tableName,ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int returnCount=0;
        db.beginTransaction();
        try
        {
            for(ContentValues value:values)
            {
                long _id=db.insert(tableName,null,value);
                if(_id!=-1)
                    returnCount++;
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        return returnCount;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
