package com.amonteiro.todelete

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.amonteiro.todelete.presentation.AppNavigation
import com.amonteiro.todelete.presentation.ui.theme.A26_04_ambientit_kotlinTheme

@Composable
@Preview
fun App() {
    A26_04_ambientit_kotlinTheme {
        AppNavigation()
    }
}