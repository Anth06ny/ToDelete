package com.amonteiro.todelete

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.amonteiro.todelete.di.initKoin

fun main() = application {

    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "ToDelete",
    ) {
        App()
    }
}