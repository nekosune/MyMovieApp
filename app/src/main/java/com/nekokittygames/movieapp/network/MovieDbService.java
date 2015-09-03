package com.nekokittygames.movieapp.network;



import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Katrina on 02/09/2015.
 */
public interface MovieDbService {

    @GET("3/discover/movie")
    Call<MovieResult> getResults(@Query("sort_by")String sort,@Query("api_key")String key);
}
