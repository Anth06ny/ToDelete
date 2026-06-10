package com.amonteiro.todelete.data.remote

import com.amonteiro.todelete.BuildConfig
import com.amonteiro.todelete.di.initKoin
import com.amonteiro.todelete.domaine.WeatherAPII
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

//Suspend sera expliqué dans le chapitre des coroutines
suspend fun main() {


    val ktorWeatherApi = initKoin {}.get<KtorWeatherApi>()

    val res = ktorWeatherApi.loadWeathers("Nice").joinToString(separator = "\n") {
        it.getResume()
    }

    print(res)

}

class KtorWeatherApi(val client: HttpClient) : WeatherAPII {
    companion object {
        private const val API_URL =
            "https://api.openweathermap.org/data/2.5/find?appid=${BuildConfig.WEATHER_API_KEY}&units=metric&lang=fr"
    }
    //Création et réglage du client


    //GET Le JSON reçu sera parser en List<WeatherDTO>,
    //Crash si le JSON ne correspond pas
    override suspend fun loadWeathers(cityName: String): List<WeatherEntity> {

        if (cityName.length <= 3) {
            throw Exception("Il faut 3 caractères minimum")
        }

        val response = client.get(API_URL + "&q=" + cityName)
        if (!response.status.isSuccess()) {
            throw Exception("Erreur API: ${response.status} - ${response.bodyAsText()}")
        }

        return response.body<WeatherAPIResult>().list.onEach {
            it.weather.forEach {
                it.icon = "https://openweathermap.org/img/wn/${it.icon}@4x.png"
            }
        }
    }

    suspend fun loadWeathers(lat: Double, lng: Double): List<WeatherEntity> {
        val response = client.get(API_URL + "&lat=$lat&lon=$lng")
        if (!response.status.isSuccess()) {
            throw Exception("Erreur API: ${response.status} - ${response.bodyAsText()}")
        }

        return response.body<WeatherAPIResult>().list.onEach {
            it.weather.forEach {
                it.icon = "https://openweathermap.org/img/wn/${it.icon}@4x.png"
            }
        }
    }

    fun loadWeathersWithFlow(vararg cities: String) = flow<WeatherEntity> {
        cities.forEach { cityName ->
            val list = loadWeathers(cityName)
            list.forEach {
                emit(it)
            }
            delay(1000)
        }
    }

    //Ferme le Client mais celui ci ne sera plus utilisable. Uniquement pour le main
    fun close() = client.close()

    //Avancés : Exemple avec Flow
    //fun loadWeathersFlow() = flow<List<WeatherDTO>> {
    //    emit(client.get(API_URL).body())
    //}
}

/* -------------------------------- */
// WEATHER
/* -------------------------------- */
@Serializable
data class WeatherAPIResult(val list: List<WeatherEntity>)

@Serializable
data class WeatherEntity(
    val id: Int, val name: String, var main: TempEntity,
    var weather: List<DescriptionEntity>,
    var wind: WindEntity
) {

    fun getResume() = """
            Il fait ${main.temp}° à $name (id=$id) avec un vent de ${wind.speed} m/s
            -Description : ${weather.firstOrNull()?.description ?: "-"}
            -Icône : ${weather.firstOrNull()?.icon ?: "-"}
        """.trimIndent()

}

@Serializable
data class TempEntity(var temp: Double)

@Serializable
data class DescriptionEntity(var description: String, var icon: String)

@Serializable
data class WindEntity(var speed: Double)