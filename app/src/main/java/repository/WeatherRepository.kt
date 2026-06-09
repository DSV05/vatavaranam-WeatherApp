package com.example.vatavaranam.repository

import com.example.vatavaranam.RetrofitInstance

class WeatherRepository {

    suspend fun getWeather(
        city: String,
        apiKey: String
    ) = RetrofitInstance.api.getWeather(
        city,
        apiKey
    )

    suspend fun getForecast(
        city: String,
        apiKey: String
    ) = RetrofitInstance.api.getForecast(
        city,
        apiKey
    )

    suspend fun getWeatherByLocation(
        lat: Double,
        lon: Double,
        apiKey: String
    ) = RetrofitInstance.api.getWeatherByLocation(
        lat,
        lon,
        apiKey
    )

    suspend fun getForecastByLocation(
        lat: Double,
        lon: Double,
        apiKey: String
    ) = RetrofitInstance.api.getForecastByLocation(
        lat,
        lon,
        apiKey
    )
}