package com.amonteiro.todelete.domaine

import com.amonteiro.todelete.data.remote.WeatherEntity

interface WeatherAPII {
    suspend fun loadWeathers(cityName: String): List<WeatherEntity>
}