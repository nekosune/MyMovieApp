package com.nekokittygames.movieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nekokittygames.movieapp.network.MovieDbService;
import com.nekokittygames.movieapp.network.MovieResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment {
    private String mSort;
    private MovieAdapter mAdapter;
    private Retrofit mRetrofit;
    private MovieDbService mMovieService;
    private ArrayList<MovieDetails> mArray;

    private final static String RESULTS="results";
    private final static String DATA_BUNDLE="data";

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
        new FetchMovieDetails().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mArray.size()==0)
            updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESULTS,mArray);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GridView view= (GridView) inflater.inflate(R.layout.fragment_main_screen, container, false);

        mArray= new ArrayList<>();
        if(savedInstanceState!=null && savedInstanceState.containsKey(RESULTS))
        {
            mArray=savedInstanceState.getParcelableArrayList(RESULTS);
        }


        mAdapter =new MovieAdapter(getContext(), mArray);

        view.setAdapter(mAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),DetailActivity.class);
                intent.putExtra(DATA_BUNDLE, (MovieDetails)parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });
        mRetrofit=new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();
        mMovieService=mRetrofit.create(MovieDbService.class);

        return view;
    }

    private class FetchMovieDetails extends AsyncTask<Void, Void, List<MovieDetails>>
    {

        @Override
        protected List<MovieDetails> doInBackground(Void... params) {
            Response<MovieResult> result;
            try {
                result = mMovieService.getResults(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.pref_sort),getString(R.string.pref_sort_default)), getString(R.string.movie_api_key)).execute();
            }
            catch (IOException e)
            {
                Log.e(getClass().getSimpleName(),"Crashed grabbing results",e);
                return null;
            }
            if(!result.isSuccess()) {
                Log.e(getClass().getSimpleName(),"Error Found: "+result.code());
                return null;
            }
            return result.body().results;
        }


        @Override
        protected void onPostExecute(List<MovieDetails> result) {
            if(result!=null) {
                mAdapter.clear();
                mAdapter.addAll(result);
            }
        }
    }
}
