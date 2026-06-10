package com.amonteiro.todelete.presentation.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.amonteiro.todelete.data.remote.DescriptionEntity
import com.amonteiro.todelete.data.remote.TempEntity
import com.amonteiro.todelete.data.remote.WeatherEntity
import com.amonteiro.todelete.data.remote.WindEntity
import com.amonteiro.todelete.presentation.ui.theme.A26_04_ambientit_kotlinTheme
import org.jetbrains.compose.resources.painterResource
import todelete.shared.generated.resources.Res
import todelete.shared.generated.resources.compose_multiplatform
import todelete.shared.generated.resources.error

@Preview(showBackground = true, showSystemUi = true)
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL
)
@Composable
fun DetailScreenPreview() {
    A26_04_ambientit_kotlinTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            DetailScreen(
                modifier = Modifier.padding(innerPadding),
                //jeu de donnée pour la Preview
                data = WeatherEntity(
                    id = 2,
                    name = "Toulouse",
                    main = TempEntity(temp = 22.3),
                    weather = listOf(
                        DescriptionEntity(description = "partiellement nuageux", icon = "https://picsum.photos/201")
                    ),
                    wind = WindEntity(speed = 3.2)
                )
            )
        }
    }
}

@Composable //id du WeatherEntity à afficher
fun DetailScreen(
    modifier: Modifier = Modifier,
    data: WeatherEntity,
    onBackClick: () -> Unit = {},
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = data.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )

        AsyncImage(
            model = data.weather.firstOrNull()?.icon,
            //Pour aller le chercher dans string.xml R de votre package com.nom.projet
            //contentDescription = getString(R.string.picture_of_cat),
            //En dur
            contentDescription = "une photo de chat",
            contentScale = ContentScale.FillWidth,

            //Pour toto.png. Si besoin de choisir l'import pour la classe R, c'est celle de votre package
            //Image d'échec de chargement qui sera utilisé par la preview
            error = painterResource(Res.drawable.error),
            //Image d'attente.
            placeholder = painterResource(Res.drawable.compose_multiplatform),
            onError = { println(it) },
            modifier = Modifier.fillMaxWidth().weight(3f)
        )

        Text(
            text = data.getResume(),
            fontSize = 14.sp,
            modifier = Modifier.animateContentSize().weight(2f)
        )

        Button(
            onClick = { onBackClick() },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowLeft,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Retour")
        }
    }

}