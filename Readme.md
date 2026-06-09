# 🌦️ Vatavaranam - Weather App

<div align="center">

### Real-Time Weather Forecasting Application

Built with **Kotlin**, **Jetpack Compose**, and **OpenWeather API**

![Android](https://img.shields.io/badge/Android-34+-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange)

</div>

---

## 📖 About

**Vatavaranam** is a modern Android weather application that provides real-time weather information, hourly forecasts, and 5-day weather predictions through a clean and intuitive user interface.

The application allows users to search for any city and instantly receive accurate weather information powered by a weather API.

---

## sc

### Home Screen

<p align="center">
  <img src="screenshots/home.png" width="300" alt="Home Screen">
</p>

### Forecast Screen

<p align="center">
  <img src="screenshots/forecast.png" width="300" alt="Forecast Screen">
</p>

---

## ✨ Features

- 🌍 Search weather by city name
- 🌡️ Real-time temperature updates
- ☁️ Current weather conditions
- 💧 Humidity information
- 🌬️ Wind speed monitoring
- 🤒 Feels-like temperature
- ⏰ Hourly weather forecast
- 📅 5-Day weather forecast
- 🎨 Modern Material Design UI
- 📱 Responsive Jetpack Compose layouts
- ⚡ Fast API-based weather retrieval

---

## 🏗️ Architecture

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern.

```text
UI (Jetpack Compose)
        │
        ▼
ViewModel
        │
        ▼
Repository
        │
        ▼
Weather API
```

---

## 🛠️ Tech Stack

### Frontend

- Kotlin
- Jetpack Compose
- Material 3

### Architecture

- MVVM Architecture
- State Management

### Networking

- Retrofit
- Gson Converter
- Kotlin Coroutines

### API

- OpenWeather API

---

## 📂 Project Structure

```text
Vatavaranam-WeatherApp
│
├── app
│   ├── data
│   │   ├── api
│   │   ├── model
│   │
│   ├── ui
│   │   ├── screens
│   │   ├── components
│   │
│   ├── viewmodel
│   │
│   └── MainActivity.kt
│
├── screenshots
│   ├── home.png
│   └── forecast.png
│
└── README.md
```

---

## 🚀 Installation

### 1. Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/Vatavaranam-WeatherApp.git
```

### 2. Open in Android Studio

Open the project using Android Studio.

### 3. Configure API Key

Create or update your `local.properties` file:

```properties
WEATHER_API_KEY=YOUR_API_KEY
```

### 4. Sync Gradle

Allow Android Studio to sync dependencies.

### 5. Run Application

Connect a device or start an emulator and run the application.

---

## 🔑 API Setup

This application uses the OpenWeather API.

Get your free API key from:

https://openweathermap.org/api

**Important:** Never commit your API key to GitHub.

Ensure the following files are ignored:

```gitignore
local.properties
*.keystore
```

---

## 🎯 Future Enhancements

- 📍 Current location weather support
- 🌙 Dark / Light theme switch
- 🔔 Weather alerts and notifications
- 🌅 Sunrise and sunset details
- 🌫️ Air Quality Index (AQI)
- 🗺️ Weather map integration
- ❤️ Favorite cities feature
- 📶 Offline caching

---

## 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push your branch
5. Open a Pull Request

---

## 👨‍💻 Developer

### Siddivinayak Doppalapudi

Computer Science Engineering (AI) Student

- Android Development
- Data Analytics
- Machine Learning
- Artificial Intelligence

---

## ⭐ Show Your Support

If you like this project, consider giving it a ⭐ on GitHub.

---

## 📜 License

This project is licensed under the MIT License.

Feel free to use, modify, and distribute this project.