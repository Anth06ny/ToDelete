package com.amonteiro.todelete.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amonteiro.todelete.data.remote.DescriptionEntity
import com.amonteiro.todelete.data.remote.KtorWeatherApi
import com.amonteiro.todelete.data.remote.TempEntity
import com.amonteiro.todelete.data.remote.WeatherEntity
import com.amonteiro.todelete.data.remote.WindEntity
import com.amonteiro.todelete.di.initKoin
import com.amonteiro.todelete.domaine.WeatherAPII
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

suspend fun main() {


    val viewModel = initKoin {}.get<MainViewModel>()
    viewModel.loadWeathers("Pantin")
    while (viewModel.runInProgress.value) {
        println("Attente....")
        delay(500)
    }

//    //Affichage de la liste (qui doit être remplie) contenue dans la donnée observable
    println("List : ${viewModel.dataList.value}")
    println("ErrorMessage : ${viewModel.errorMessage.value}")

}

class MainViewModel(val ktorWeatherApi: WeatherAPII) : ViewModel() {
    //MutableStateFlow est une donnée observable
    val dataList = MutableStateFlow(emptyList<WeatherEntity>())
//    val dataList = _dataList.asStateFlow()
//    val dataList2 : StateFlow<List<WeatherEntity>> = _dataList

    var runInProgress = MutableStateFlow(false)

    val errorMessage = MutableStateFlow("")
    private val _searchText = MutableStateFlow("Toulouse")
    val searchText = _searchText.asStateFlow()

    init {
        //loadWeathers()
    }

//    init {//Création d'un jeu de donnée au démarrage
//        println("Instanciation de MainViewModel")
//        loadFakeData()
//    }

    fun loadWeathers(cityName: String = searchText.value) {
        runInProgress.value = true
        errorMessage.value = ""

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataList.value = ktorWeatherApi.loadWeathers(cityName)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = e.message ?: "Une erreur est survenue"
            } finally {
                runInProgress.value = false
            }
        }
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun loadFakeData(runInProgress: Boolean = false, errorMessage: String = "") {
        this.runInProgress.value = runInProgress
        this.errorMessage.value = errorMessage
        dataList.value = listOf(
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
