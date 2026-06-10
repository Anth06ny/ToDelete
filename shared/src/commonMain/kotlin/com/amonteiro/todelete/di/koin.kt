package com.amonteiro.todelete.di

import com.amonteiro.todelete.data.remote.KtorWeatherApi
import com.amonteiro.todelete.domaine.WeatherAPII
import com.amonteiro.todelete.presentation.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

//Si besoin du contexte, pour le passer en paramètre au lancement de Koin
fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(apiModule, viewModelModule)
    }.koin

// Version pour iOS et Desktop
fun initKoin() {
    initKoin {}
}

val apiModule = module {

    single {
        HttpClient {
            install(Logging) {
                //(import io.ktor.client.plugins.logging.Logger)
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.INFO  // TRACE, HEADERS, BODY, etc.
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
            }
            //engine { proxy = ProxyBuilder.http("monproxy:1234") }
        }
    }

    single<WeatherAPII> {
        KtorWeatherApi(get())
    }
    //singleOf(::KtorWeatherApi)
}

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}