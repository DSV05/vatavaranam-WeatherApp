package com.example.vatavaranam

import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vatavaranam.ui.theme.VatavaranamTheme
import com.example.vatavaranam.viewmodel.ErrorType
import com.example.vatavaranam.viewmodel.WeatherState
import com.example.vatavaranam.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationServices

// ─── Color Palette ───────────────────────────────────────────────────────────
private val BgDeep      = Color(0xFF060B1A)
private val BgMid       = Color(0xFF0D1530)
private val BgCard      = Color(0xFF141B38)
private val AccentBlue  = Color(0xFF4A90E2)
private val AccentCyan  = Color(0xFF64D4F5)
private val AccentRed   = Color(0xFFFF6B6B)
private val AccentAmber = Color(0xFFFFD166)
private val GlassWhite  = Color.White.copy(alpha = 0.07f)
private val GlassBorder = Color.White.copy(alpha = 0.12f)
private val TextPrimary = Color(0xFFF0F4FF)
private val TextSecond  = Color(0xFF8A9BC5)
private val TextHint    = Color(0xFF4A5680)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VatavaranamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDeep
                ) {
                    WeatherScreen()
                }
            }
        }
    }
}

// ─── Main Screen ─────────────────────────────────────────────────────────────
@Composable
fun WeatherScreen() {
    var city by remember { mutableStateOf("") }
    val viewModel: WeatherViewModel = viewModel()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.getWeatherByLocation(location.latitude, location.longitude, BuildConfig.WEATHER_API_KEY)
                    viewModel.getForecastByLocation(location.latitude, location.longitude, BuildConfig.WEATHER_API_KEY)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.getWeatherByLocation(location.latitude, location.longitude, BuildConfig.WEATHER_API_KEY)
                    viewModel.getForecastByLocation(location.latitude, location.longitude, BuildConfig.WEATHER_API_KEY)
                } else {
                    println("LOCATION IS NULL")
                }
            }
        }
    }

    val weatherState     = viewModel.weatherState
    val weatherInfo      = viewModel.weatherInfo
    val weatherCondition = viewModel.weatherCondition
    val forecastList     = viewModel.forecastList
    val isLoading        = viewModel.isLoading
    val humidity         = viewModel.humidity
    val windSpeed        = viewModel.windSpeed
    val feelsLike        = viewModel.feelsLike
    val cityName         = viewModel.cityName
    val temperature      = viewModel.temperature

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgDeep, BgMid, BgDeep)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Header ──────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vatavaranam",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Real-time weather",
                            fontSize = 13.sp,
                            color = TextSecond
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // ── Search Bar ──────────────────────────────────────────────────
            item {
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            placeholder = { Text("Search city...", color = TextHint, fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = TextSecond,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (city.isNotBlank()) {
                                        viewModel.getWeather(city, BuildConfig.WEATHER_API_KEY)
                                        viewModel.getForecast(city, BuildConfig.WEATHER_API_KEY)
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue.copy(alpha = 0.6f),
                                unfocusedBorderColor = GlassBorder,
                                cursorColor = AccentCyan,
                                focusedContainerColor = GlassWhite,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        )
                        FilledTonalButton(
                            onClick = {
                                if (city.isNotBlank()) {
                                    viewModel.getWeather(city, BuildConfig.WEATHER_API_KEY)
                                    viewModel.getForecast(city, BuildConfig.WEATHER_API_KEY)
                                    focusManager.clearFocus()
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AccentBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Go", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // ── Error Screen ─────────────────────────────────────────────────
            if (weatherState is WeatherState.Error) {
                item {
                    val error = weatherState as WeatherState.Error
                    ErrorCard(
                        errorType = error.type,
                        message = error.message,
                        onRetry = {
                            if (city.isNotBlank()) {
                                viewModel.getWeather(city, BuildConfig.WEATHER_API_KEY)
                                viewModel.getForecast(city, BuildConfig.WEATHER_API_KEY)
                            } else {
                                val fusedLocationClient =
                                    LocationServices.getFusedLocationProviderClient(context)
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                        if (location != null) {
                                            viewModel.getWeatherByLocation(location.latitude, location.longitude, BuildConfig.WEATHER_API_KEY)
                                            viewModel.getForecastByLocation(location.latitude, location.longitude, BuildConfig.WEATHER_API_KEY)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // ── Current Weather Card ─────────────────────────────────────────
            if (weatherState is WeatherState.Success || weatherState is WeatherState.Loading) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF1A2050), Color(0xFF0D1535))
                                    )
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 40.dp, y = (-40).dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(AccentBlue.copy(alpha = 0.15f), Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(80.dp)
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = AccentCyan,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(vertical = 24.dp)
                                    )
                                } else {
                                    WeatherIcon(weatherCondition, size = 80.dp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = weatherCondition.replaceFirstChar { it.uppercase() }.ifEmpty { "—" },
                                        fontSize = 13.sp,
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = weatherInfo.ifEmpty { "Search a city or use GPS" },
                                        fontSize = 13.sp,
                                        color = TextSecond,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (temperature == "—") "—" else temperature,
                                        fontSize = 64.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        lineHeight = 64.sp
                                    )

                                    // Min / Max from next 24h
                                    val next24 = forecastList.take(8)
                                    val todayMax = next24.maxOfOrNull { it.main.temp_max }
                                    val todayMin = next24.minOfOrNull { it.main.temp_min }
                                    if (todayMax != null && todayMin != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "↑ ${todayMax.toInt()}°",
                                                fontSize = 14.sp,
                                                color = AccentAmber,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "↓ ${todayMin.toInt()}°",
                                                fontSize = 14.sp,
                                                color = AccentCyan,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))
                                    HorizontalDivider(
                                        color = Color.White.copy(alpha = 0.08f),
                                        thickness = 1.dp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    WeatherStatRow(Icons.Default.LocationOn,  "City",       cityName)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    WeatherStatRow(Icons.Default.WaterDrop,   "Humidity",   humidity)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    WeatherStatRow(Icons.Default.Air,         "Wind",       windSpeed)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    WeatherStatRow(Icons.Default.Thermostat,  "Feels like", feelsLike)
                                }
                            }
                        }
                    }
                }
            }

            // ── Hourly Forecast ──────────────────────────────────────────────
            if (forecastList.isNotEmpty() && weatherState is WeatherState.Success) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Today's Forecast",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(forecastList.take(8)) { item ->
                            HourlyCard(item)
                        }
                    }
                }
            }

            // ── 5-Day Forecast ───────────────────────────────────────────────
            if (weatherState is WeatherState.Success) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "5-Day Forecast",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                val filtered = forecastList
                    .filter { it.dt_txt.contains("12:00:00") }
                    .take(5)

                items(filtered) { item ->
                    ForecastCard(item)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// ─── Error Card ───────────────────────────────────────────────────────────────
@Composable
fun ErrorCard(errorType: ErrorType, message: String, onRetry: () -> Unit) {
    val (icon, iconTint, title) = when (errorType) {
        ErrorType.NO_INTERNET   -> Triple(Icons.Default.WifiOff,     AccentRed,   "No Internet")
        ErrorType.CITY_NOT_FOUND -> Triple(Icons.Default.SearchOff,  AccentAmber, "City Not Found")
        ErrorType.UNKNOWN       -> Triple(Icons.Default.ErrorOutline, AccentRed,   "Something Went Wrong")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1A2050), Color(0xFF0D1535))
                )
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextSecond,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            FilledTonalButton(
                onClick = onRetry,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Reusable Components ─────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun WeatherStatRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecond,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun HourlyCard(item: ForecastItem) {
    val condition = item.weather[0].main
    val time = try {
        item.dt_txt.split(" ")[1].substring(0, 5)
    } catch (e: Exception) {
        "--:--"
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = time, fontSize = 12.sp, color = TextSecond, fontWeight = FontWeight.Medium)
        WeatherIcon(condition, size = 28.dp)
        Text(text = "${item.main.temp.toInt()}°", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun ForecastCard(item: ForecastItem) {
    val condition = item.weather[0].main
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeatherIcon(condition, size = 32.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatForecastDate(item.dt_txt),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(text = condition, fontSize = 12.sp, color = AccentCyan)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${item.main.temp_max.toInt()}°",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${item.main.temp_min.toInt()}°",
                fontSize = 14.sp,
                color = TextSecond
            )
        }
    }
}

// ─── Weather Icon ─────────────────────────────────────────────────────────────
@Composable
fun WeatherIcon(condition: String, size: androidx.compose.ui.unit.Dp = 72.dp) {
    val (icon, tint) = when (condition.lowercase()) {
        "clear"        -> Icons.Default.WbSunny      to Color(0xFFFFD166)
        "clouds"       -> Icons.Default.Cloud         to Color(0xFFAEC6E8)
        "rain",
        "drizzle"      -> Icons.Default.WaterDrop     to AccentCyan
        "thunderstorm" -> Icons.Default.Thunderstorm  to Color(0xFFB388FF)
        "snow"         -> Icons.Default.AcUnit        to Color(0xFFE0F7FA)
        "mist",
        "fog",
        "haze"         -> Icons.Default.Dehaze        to Color(0xFF90A4AE)
        else           -> Icons.Default.WbSunny      to Color(0xFFFFD166)
    }
    Icon(
        imageVector = icon,
        contentDescription = condition,
        tint = tint,
        modifier = Modifier.size(size)
    )
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
fun formatForecastDate(dtTxt: String): String {
    return try {
        val parts = dtTxt.split(" ")[0].split("-")
        val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val month = months[parts[1].toInt() - 1]
        val day = parts[2].toInt()
        "$month $day"
    } catch (e: Exception) {
        dtTxt.take(10)
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF060B1A)
@Composable
fun WeatherScreenPreview() {
    VatavaranamTheme {
        WeatherScreen()
    }
}