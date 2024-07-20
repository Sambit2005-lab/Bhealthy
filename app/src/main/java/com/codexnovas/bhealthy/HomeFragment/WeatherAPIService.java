package com.codexnovas.bhealthy.HomeFragment;

import com.codexnovas.bhealthy.HomeFragment.WeatherAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIService {
    @GET("current.json")
    Call<WeatherAPIResponse> getCurrentWeather(@Query("key") String apiKey, @Query("q") String location);
}
