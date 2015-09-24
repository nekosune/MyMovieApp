package com.nekokittygames.movieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState==null)
        {
            Bundle args=new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());
            DetailActivityFragment fragment=new DetailActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,fragment).commit();
        }
    }


}
