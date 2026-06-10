package com.amonteiro.todelete.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amonteiro.todelete.data.remote.WeatherEntity
import com.amonteiro.todelete.presentation.ui.screens.PictureRowItem

@Composable
actual fun WeatherGallery(
    modifier: Modifier,
    list: List<WeatherEntity>,
    onPictureClick: (Int) -> Unit
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier
    ) {
        items(list.size) {
            PictureRowItem(data = list[it], onRowPictureClick = onPictureClick)
        }
    }
}

