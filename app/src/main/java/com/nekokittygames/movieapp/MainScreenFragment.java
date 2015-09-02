package com.nekokittygames.movieapp;

import android.os.AsyncTask;
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
import android.widget.Toast;

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

    private MovieAdapter mAdapater;
    protected Retrofit mRetrofit;
    protected MovieDbService mMovieService;
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
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GridView view= (GridView) inflater.inflate(R.layout.fragment_main_screen, container, false);



        mAdapater=new MovieAdapter(getContext(),R.layout.gridlist_item,new ArrayList<MovieDetails>());

        view.setAdapter(mAdapater);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetails details = (MovieDetails) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "I am loading the movie " + details.title, Toast.LENGTH_SHORT).show();
            }
        });
        mRetrofit=new Retrofit.Builder().baseUrl("http://api.themoviedb.org/").addConverterFactory(GsonConverterFactory.create()).build();
        mMovieService=mRetrofit.create(MovieDbService.class);

        return view;
    }

    public class FetchMovieDetails extends AsyncTask<Void, Void, List<MovieDetails>>
    {

        @Override
        protected List<MovieDetails> doInBackground(Void... params) {
            MovieResult result=null;
            try {
                Response<MovieResult> resp = mMovieService.getResults("popularity.desc", getString(R.string.movie_api_key)).execute();
                result=resp.body();
            }
            catch (IOException e)
            {
                Log.e(getClass().getSimpleName(),"Crashed grabbing results",e);
            }

            return result.results;
        }

        @Override
        protected void onPostExecute(List<MovieDetails> result) {
            if(result!=null) {
                mAdapater.clear();
                mAdapater.addAll(result);
            }
        }
    }
}
