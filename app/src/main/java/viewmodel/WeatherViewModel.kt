package com.example.vatavaranam.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vatavaranam.ForecastItem
import com.example.vatavaranam.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.net.SocketTimeoutException

sealed class WeatherState {
    object Idle : WeatherState()
    object Loading : WeatherState()
    object Success : WeatherState()
    data class Error(val type: ErrorType, val message: String) : WeatherState()
}

enum class ErrorType {
    NO_INTERNET,
    CITY_NOT_FOUND,
    UNKNOWN
}

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    var weatherState by mutableStateOf<WeatherState>(WeatherState.Idle)
        private set

    var weatherInfo by mutableStateOf("")
        private set

    var weatherCondition by mutableStateOf("")
        private set

    var forecastList by mutableStateOf<List<ForecastItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var humidity by mutableStateOf("—")
        private set

    var windSpeed by mutableStateOf("—")
        private set

    var feelsLike by mutableStateOf("—")
        private set

    var cityName by mutableStateOf("—")
        private set

    var temperature by mutableStateOf("—")
        private set

    var currentLat by mutableStateOf(0.0)
    var currentLon by mutableStateOf(0.0)

    fun getWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                weatherState = WeatherState.Loading
                weatherInfo = ""

                val response = repository.getWeather(city, apiKey)

                if (response.isSuccessful) {
                    isLoading = false
                    val data = response.body()
                    weatherCondition = data?.weather?.get(0)?.main ?: ""
                    cityName         = data?.name ?: "—"
                    temperature      = "${data?.main?.temp?.toInt()} °C"
                    humidity         = "${data?.main?.humidity} %"
                    windSpeed        = "${data?.wind?.speed?.toInt()} km/h"
                    feelsLike        = "${data?.main?.feels_like?.toInt()} °C"
                    weatherInfo      = data?.weather?.get(0)?.description
                        ?.replaceFirstChar { it.uppercase() } ?: ""
                    weatherState     = WeatherState.Success
                } else if (response.code() == 404) {
                    isLoading = false
                    weatherState = WeatherState.Error(
                        ErrorType.CITY_NOT_FOUND,
                        "City \"$city\" not found.\nCheck the spelling and try again."
                    )
                } else {
                    isLoading = false
                    weatherState = WeatherState.Error(
                        ErrorType.UNKNOWN,
                        "Something went wrong.\nPlease try again."
                    )
                }

            } catch (e: UnknownHostException) {
                isLoading = false
                weatherState = WeatherState.Error(
                    ErrorType.NO_INTERNET,
                    "No internet connection.\nConnect to Wi-Fi or mobile data and try again."
                )
            } catch (e: SocketTimeoutException) {
                isLoading = false
                weatherState = WeatherState.Error(
                    ErrorType.NO_INTERNET,
                    "Connection timed out.\nCheck your internet and try again."
                )
            } catch (e: Exception) {
                isLoading = false
                weatherState = WeatherState.Error(
                    ErrorType.UNKNOWN,
                    e.message ?: "Unknown error occurred."
                )
            }
        }
    }

    fun getWeatherByLocation(lat: Double, lon: Double, apiKey: String) {
        currentLat = lat
        currentLon = lon

        viewModelScope.launch {
            try {
                isLoading = true
                weatherState = WeatherState.Loading
                weatherInfo = ""

                val response = repository.getWeatherByLocation(lat, lon, apiKey)

                if (response.isSuccessful) {
                    isLoading = false
                    val data = response.body()
                    weatherCondition = data?.weather?.get(0)?.main ?: ""
                    cityName         = data?.name ?: "—"
                    temperature      = "${data?.main?.temp?.toInt()} °C"
                    humidity         = "${data?.main?.humidity} %"
                    windSpeed        = "${data?.wind?.speed?.toInt()} km/h"
                    feelsLike        = "${data?.main?.feels_like?.toInt()} °C"
                    weatherInfo      = data?.weather?.get(0)?.description
                        ?.replaceFirstChar { it.uppercase() } ?: ""
                    weatherState     = WeatherState.Success
                } else {
                    isLoading = false
                    weatherState = WeatherState.Error(
                        ErrorType.UNKNOWN,
                        "Could not get weather for your location.\nPlease try again."
                    )
                }

            } catch (e: UnknownHostException) {
                isLoading = false
                weatherState = WeatherState.Error(
                    ErrorType.NO_INTERNET,
                    "No internet connection.\nConnect to Wi-Fi or mobile data and try again."
                )
            } catch (e: SocketTimeoutException) {
                isLoading = false
                weatherState = WeatherState.Error(
                    ErrorType.NO_INTERNET,
                    "Connection timed out.\nCheck your internet and try again."
                )
            } catch (e: Exception) {
                isLoading = false
                weatherState = WeatherState.Error(
                    ErrorType.UNKNOWN,
                    e.message ?: "Unknown error occurred."
                )
            }
        }
    }

    fun getForecast(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getForecast(city, apiKey)
                if (response.isSuccessful) {
                    forecastList = response.body()?.list ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getForecastByLocation(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getForecastByLocation(lat, lon, apiKey)
                if (response.isSuccessful) {
                    forecastList = response.body()?.list ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}