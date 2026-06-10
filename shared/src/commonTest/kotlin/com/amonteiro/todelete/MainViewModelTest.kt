package com.amonteiro.todelete

import com.amonteiro.todelete.di.apiModule
import com.amonteiro.todelete.di.initKoin
import com.amonteiro.todelete.di.viewModelModule
import com.amonteiro.todelete.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class MainViewModelTest {


    @Test
    fun loadWeathers() = runTest(timeout = 30.seconds) {

        val koin = startKoin {
            modules(apiModule, viewModelModule)
        }.koin

        //Pas de mock : vraie injection Koin et vraie requête HTTP derrière
        val viewModel = koin.get<MainViewModel>()

        assertFalse(viewModel.runInProgress.value)

        viewModel.loadWeathers("Toulouse")

        assertTrue(viewModel.runInProgress.value)

        //first de flow attend la 1er réponse qui correspond au prédicat
        viewModel.runInProgress.first { !it }

        assertFalse(viewModel.runInProgress.value)
        assertTrue(viewModel.errorMessage.value.isEmpty(), "errorMessage=${viewModel.errorMessage.value}")
        assertTrue(viewModel.dataList.value.isNotEmpty(), "dataList doit être remplie")
    }

    @AfterTest
    fun tearDown() = stopKoin() //évite "KoinApplication has already been started" si plusieurs tests
}
