package com.amonteiro.todelete.domaine.fakeData

import com.amonteiro.todelete.data.remote.DescriptionEntity
import com.amonteiro.todelete.data.remote.TempEntity
import com.amonteiro.todelete.data.remote.WeatherEntity
import com.amonteiro.todelete.data.remote.WindEntity
import com.amonteiro.todelete.domaine.WeatherAPII

class WeatherFakeAPI : WeatherAPII {
    override suspend fun loadWeathers(cityName: String): List<WeatherEntity> {
       return  listOf(
            WeatherEntity(
                id = 1,
                name = "Paris",
                main = TempEntity(temp = 18.5),
                weather = listOf(
                    DescriptionEntity(description = "ciel dégagé", icon = "https://picsum.photos/200")
                ),
                wind = WindEntity(speed = 5.0)
            ),
            WeatherEntity(
                id = 2,
                name = "Toulouse",
                main = TempEntity(temp = 22.3),
                weather = listOf(
                    DescriptionEntity(description = "partiellement nuageux", icon = "https://picsum.photos/201")
                ),
                wind = WindEntity(speed = 3.2)
            ),
            WeatherEntity(
                id = 3,
                name = "Toulon",
                main = TempEntity(temp = 25.1),
                weather = listOf(
                    DescriptionEntity(description = "ensoleillé", icon = "https://picsum.photos/202")
                ),
                wind = WindEntity(speed = 6.7)
            ),
            WeatherEntity(
                id = 4,
                name = "Lyon",
                main = TempEntity(temp = 19.8),
                weather = listOf(
                    DescriptionEntity(description = "pluie légère", icon = "https://picsum.photos/203")
                ),
                wind = WindEntity(speed = 4.5)
            )
        ).shuffled() //shuffled() pour avoir un ordre différent à chaque appel
    }
}