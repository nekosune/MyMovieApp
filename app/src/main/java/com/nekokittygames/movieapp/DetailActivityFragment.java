package com.nekokittygames.movieapp;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nekokittygames.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    static final String DETAIL_URI = "URI";
    private TextView mTextTitle;
    private RatingBar mRatingBar;
    private TextView mSynopsisView;
    private TextView mDateView;
    private TextView mRuntimeView;
    private ImageView mImageView;
    private TextView mHomepageView;
    private LinearLayout mTrailersView;
    private LinearLayout mReviewsView;
    private String imdbId=null;
    private long movieID=-1;
    static final int DETAIL_LOADER=0;
    static final int YOUTUBE_LOADER=1;
    static final int REVIEW_LOADER=2;
    static final int FAVORITE_LOADER=3;
    private ShareActionProvider mShareActionProvider;
    private boolean mIsFavorite;
    private MenuItem mFavorite;
    private String mYoutubeKey;

    private Uri mUri;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        getLoaderManager().initLoader(FAVORITE_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem item=menu.findItem(R.id.action_share);
        mFavorite=menu.findItem(R.id.action_favorite);
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setMenu();
        if(mYoutubeKey!=null)
        {
            mShareActionProvider.setShareIntent(getShareIntent());
        }
    }

    private void setMenu() {
        if (mIsFavorite)
            mFavorite.setTitle(getString(R.string.action_unfavorite));
        else
            mFavorite.setTitle(getString(R.string.action_favorite));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_imdb)
        {
            if(imdbId!=null)
            {
                String url="http://www.imdb.com/title/"+imdbId;
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        }
        if(id==R.id.action_favorite)
        {
            if(mIsFavorite) {
                getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI, MovieContract.FavoriteEntry.FAVORITE_MOVIE_ID + " = ?", new String[]{Long.toString(movieID)});
                mIsFavorite=false;
            }
            else {
                ContentValues values=new ContentValues();
                values.put(MovieContract.FavoriteEntry.FAVORITE_MOVIE_ID,movieID);
                getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);
                mIsFavorite=true;
            }
            setMenu();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle argumets=getArguments();
        if(argumets!=null)
        {
            mUri=argumets.getParcelable(DETAIL_URI);
        }
       View view= inflater.inflate(R.layout.fragment_detail, container, false);

        mTextTitle=(TextView)view.findViewById(R.id.detail_title);
        mRatingBar=(RatingBar)view.findViewById(R.id.detail_rating);
        mSynopsisView=(TextView)view.findViewById(R.id.detail_synopsis);
        mDateView=(TextView)view.findViewById(R.id.detail_date);
        mRuntimeView=(TextView)view.findViewById(R.id.detail_runtime);
        mImageView= (ImageView) view.findViewById(R.id.detail_poster);
        mHomepageView=(TextView)view.findViewById(R.id.detail_homepage);
        mHomepageView.setMovementMethod(LinkMovementMethod.getInstance());
        mTrailersView=(LinearLayout)view.findViewById(R.id.detail_trailers);
        mReviewsView=(LinearLayout)view.findViewById(R.id.detail_reviews);


        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAIL_LOADER:
                if (mUri != null) {

                    return new CursorLoader(getActivity(), mUri, null, null, null, null);
                }
                return null;
            case YOUTUBE_LOADER:
                return new CursorLoader(getActivity(), MovieContract.YoutubeEntry.buildUri(movieID), null, null, null, null);
            case REVIEW_LOADER:
                return new CursorLoader(getActivity(), MovieContract.ReviewEntry.buildUri(movieID), null, null, null, null);
            case FAVORITE_LOADER:
                if(mUri!=null)
                    return new CursorLoader(getActivity(), MovieContract.FavoriteEntry.CONTENT_URI,null,MovieContract.FavoriteEntry.FAVORITE_MOVIE_ID+" = ?",new String[]{mUri.getLastPathSegment()},null);
        }
        return null;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==DETAIL_LOADER) {
            if (data == null || !data.moveToNext())
                return;
            mTextTitle.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)));
            mRatingBar.setRating(data.getFloat(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_RATING)) / 2);
            mRatingBar.setVisibility(View.VISIBLE);
            mSynopsisView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_SYNOPSIS)));
            mDateView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE)));
            Picasso.with(getContext()).load(Utilities.getPosterUrl(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER)))).into(mImageView);
            mRuntimeView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_RUNTIME)) + " min");
            mImageView.setContentDescription(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)));
            imdbId = data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_IMDB_ID));

            String homepage = data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_WEBSITE));
            if (!homepage.isEmpty()) {
                homepage = "<a href=\"" + homepage + "\">Homepage</a>";
                mHomepageView.setText(Html.fromHtml(homepage));
            }
            movieID=data.getLong(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID));
            getLoaderManager().initLoader(YOUTUBE_LOADER,null,this);
            getLoaderManager().initLoader(REVIEW_LOADER,null,this);
        }
        else if(loader.getId()==YOUTUBE_LOADER)
        {
            while(data.moveToNext()) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.youtube_item, mTrailersView, false);
                        ((TextView) view.findViewById(R.id.youtube_name)).setText(data.getString(data.getColumnIndex(MovieContract.YoutubeEntry.YOUTUBE_NAME)));
                String key=data.getString(data.getColumnIndex(MovieContract.YoutubeEntry.YOUTUBE_KEY));
                Picasso.with(getActivity()).load(Utilities.getYoutubeURL(key)).into((ImageView) view.findViewById(R.id.youtube_picture));
                view.setTag(key);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = (String) v.getTag();

                        if (id != null) {
                            Intent i=new Intent(Intent.ACTION_VIEW).setData(Utilities.getYoutubeLink(id));
                            startActivity(i);
                        }
                    }
                });
                if(mYoutubeKey==null)
                    mYoutubeKey=key;
                mTrailersView.addView(view);
            }
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(getShareIntent());
            }

        }
        else if(loader.getId()==REVIEW_LOADER)
        {
            while(data.moveToNext()) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.rewview_item,mReviewsView,false);
                ((TextView) view.findViewById(R.id.review_author)).setText(data.getString(data.getColumnIndex(MovieContract.ReviewEntry.REVIEW_AUTHOR)));
                ((TextView) view.findViewById(R.id.review_content)).setText(data.getString(data.getColumnIndex(MovieContract.ReviewEntry.REVIEW_CONTENT)));
                view.setTag(data.getString(data.getColumnIndex(MovieContract.ReviewEntry.REVIEW_URL)));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String link = (String) v.getTag();

                        if (link != null) {
                            Intent i=new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
                            startActivity(i);
                        }
                    }
                });
                mReviewsView.addView(view);
            }
        }
        else if(loader.getId()==FAVORITE_LOADER)
        {
            mIsFavorite=data.getCount()!=0;

            if(mFavorite!=null) {
                setMenu();
            }
        }

    }

    private Intent getShareIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Utilities.getYoutubeLink(mYoutubeKey).toString());
        return shareIntent;
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
