package com.example.weatherwave

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city : String,
        @Query("appid") appid : String,
        @Query("units") units : String
    ) : Call<WeatherData>
}