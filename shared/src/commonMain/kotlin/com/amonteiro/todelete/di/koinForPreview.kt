package com.amonteiro.todelete.di

import com.amonteiro.todelete.domaine.WeatherAPII
import com.amonteiro.todelete.domaine.fakeData.WeatherFakeAPI
import org.koin.dsl.module

val apiFakeModule = module {

    single<WeatherAPII> {
        WeatherFakeAPI()
    }
    //singleOf(::KtorWeatherApi)
}