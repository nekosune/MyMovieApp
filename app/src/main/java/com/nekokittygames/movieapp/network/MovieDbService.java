package com.nekokittygames.movieapp.network;



import com.nekokittygames.movieapp.*;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Katrina on 02/09/2015.
 */
public interface MovieDbService {

    @GET("3/discover/movie")
    Call<MovieResult> getResults(@Query("sort_by")String sort,@Query("api_key")String key);

    @GET("3/movie/{id}?append_to_response=reviews,videos")
    Call<com.nekokittygames.movieapp.network.MovieDetails> getDetails(@Path("id") String id,@Query("api_key")String key);
}
