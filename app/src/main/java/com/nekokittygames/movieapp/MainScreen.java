package com.nekokittygames.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nekokittygames.movieapp.data.MovieContract;
import com.nekokittygames.movieapp.sync.MovieAppSyncAdapter;

public class MainScreen extends AppCompatActivity implements MainScreenFragment.Callback{




    protected String mSorting;
    public static final String DETAILFRAGMENT_TAG = "DFTAG";

    boolean mTwoPane=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSorting=Utilities.getSortPreference(this);

        if(findViewById(R.id.movie_detail_container)!=null)
        {
            mTwoPane=true;
            if(savedInstanceState==null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,new DetailActivityFragment(),DETAILFRAGMENT_TAG).commit();
            }
        }
        else
        {
            mTwoPane=false;
        }
        MovieAppSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Utilities.getSortPreference(this).equalsIgnoreCase(mSorting))
        {
            mSorting=Utilities.getSortPreference(this);
            MainScreenFragment frag=(MainScreenFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            frag.onSortingChange();
            if(mTwoPane)
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,new DetailActivityFragment(),DETAILFRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri dataUri) {
        if(mTwoPane)
        {
            Bundle args=new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI,dataUri);
            DetailActivityFragment fragment=new DetailActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,fragment,DETAILFRAGMENT_TAG).commit();
        }
        else
        {
            Intent intent = new Intent(this, DetailActivity.class).setData(dataUri);
            startActivity(intent);
        }
    }
}
