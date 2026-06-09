package com.example.vatavaranam

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val name: String
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val main: String,
    val description: String
)

data class Wind(
    val speed: Double
)