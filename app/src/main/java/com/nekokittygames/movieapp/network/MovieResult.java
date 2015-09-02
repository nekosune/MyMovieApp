package com.nekokittygames.movieapp.network;

import com.nekokittygames.movieapp.MovieDetails;

import java.util.List;

/**
 * Created by Katrina on 02/09/2015.
 */
public class MovieResult {

    public List<MovieDetails> results;
    public int page;
    public int total_pages;
    public int total_results;
}
