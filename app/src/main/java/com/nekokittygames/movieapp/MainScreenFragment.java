package com.nekokittygames.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.nekokittygames.movieapp.data.MovieContract;
import com.nekokittygames.movieapp.sync.MovieAppSyncAdapter;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dataUri);
    }

    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.MOVIE_POSTER,
            MovieContract.MovieEntry.MOVIE_TITLE,
            MovieContract.MovieEntry.TABLE_NAME + "."+MovieContract.MovieEntry.MOVIE_ID
    };

    static final int COL__ID =0;
    static final int COL_POSTER=1;
    static final int COL_TITLE=2;
    private static final int COL_MOVIE_ID=3;
    private int mPosition = GridView.INVALID_POSITION;
    private MovieAdapter mAdapter;

    private final static String RESULTS="results";
    public MainScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.action_refresh)
        {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        MovieAppSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }


    public void onSortingChange()
    {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GridView view= (GridView) inflater.inflate(R.layout.fragment_main_screen, container, false);

        mAdapter =new MovieAdapter(getActivity(), null,0);

        view.setAdapter(mAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                if (c != null) {
                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildUri(c.getLong(COL_MOVIE_ID)));
                }
                mPosition=position;
            }
        });
        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            mPosition=savedInstanceState.getInt(SELECTED_KEY);
        }
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort=Utilities.getSortPreference(getActivity());
        if(sort.equalsIgnoreCase(getString(R.string.pref_sort_favorite)))
            return new CursorLoader(getActivity(),MovieContract.MovieEntry.FAVORITE_URI,MOVIE_COLUMNS,null,null,null);
        return new CursorLoader(getActivity(),MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS, null, null, sort + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
        if(mPosition!=GridView.INVALID_POSITION)
        {
            if(((GridView)getView()) != null)
                ((GridView)getView()).smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


}
