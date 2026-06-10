package com.amonteiro.todelete.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.amonteiro.todelete.data.remote.WeatherEntity
import com.amonteiro.todelete.di.apiFakeModule
import com.amonteiro.todelete.di.viewModelModule
import com.amonteiro.todelete.presentation.ui.MyError
import com.amonteiro.todelete.presentation.ui.WeatherGallery
import com.amonteiro.todelete.presentation.ui.theme.A26_04_ambientit_kotlinTheme
import com.amonteiro.todelete.presentation.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.viewmodel.koinViewModel
import todelete.shared.generated.resources.Res
import todelete.shared.generated.resources.bt_load
import todelete.shared.generated.resources.compose_multiplatform
import todelete.shared.generated.resources.error

@Preview(showBackground = true, showSystemUi = true)
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, locale = "fr"
)
@Composable
fun SearchScreenPreview() {
    //Il faut remplacer NomVotreAppliTheme par le thème de votre application
    //Utilisé par exemple dans MainActivity.kt sous setContent {...}
    A26_04_ambientit_kotlinTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            KoinApplicationPreview(application = {
                //androidContext(context) uniquement si coté Android avec Context
                modules(viewModelModule, apiFakeModule)
            }) {
                val mainViewModel: MainViewModel = koinViewModel<MainViewModel>()
                mainViewModel.loadFakeData(true, "Une erreur")
                SearchScreen(
                    modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel,
                )
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, locale = "fr",
    name = "No data"
)
@Composable
fun SearchScreenNoDataPreview() {
    //Il faut remplacer NomVotreAppliTheme par le thème de votre application
    //Utilisé par exemple dans MainActivity.kt sous setContent {...}
    A26_04_ambientit_kotlinTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            KoinApplicationPreview(application = {
                //androidContext(context) uniquement si coté Android avec Context
                modules(viewModelModule, apiFakeModule)
            }) {
                val mainViewModel: MainViewModel = koinViewModel<MainViewModel>()
                //mainViewModel.runInProgress.value = false
                SearchScreen(
                    modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel,
                )
            }
        }
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    onRowPictureClick: (Int) -> Unit = {}
) {


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //var searchText by remember { mutableStateOf("") }

        val list = mainViewModel.dataList.collectAsStateWithLifecycle().value
        //.filter {            it.name.contains(searchText, true)        }
        val searchText by mainViewModel.searchText.collectAsStateWithLifecycle()
        val errorMessage by mainViewModel.errorMessage.collectAsStateWithLifecycle()
        val runInProgress by mainViewModel.runInProgress.collectAsStateWithLifecycle()



        SearchBar(
            searchText = searchText,
            onSearchAction = { mainViewModel.loadWeathers() }) {
            mainViewModel.updateSearchText(it)
        }

        MyError(errorMessage = errorMessage)

        AnimatedVisibility(runInProgress) {
            CircularProgressIndicator()
        }

        WeatherGallery(modifier = Modifier.weight(1f), list, onRowPictureClick)


        Row {
            Button(
                onClick = { mainViewModel.updateSearchText("") },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Clear")
            }

            Button(
                onClick = { mainViewModel.loadWeathers(searchText) },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(Res.string.bt_load))
            }
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchAction: () -> Unit,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = searchText, //Valeur affichée
        onValueChange = onValueChange, //Nouveau texte entrée
        leadingIcon = { //Image d'icône
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        },

        singleLine = true,
        label = { //Texte d'aide qui se déplace
            Text("Enter text")
            //Pour aller le chercher dans string.xml, R de votre package com.nom.projet
            //Text(stringResource(R.string.placeholder_search))
        },
        //placeholder = { //Texte d'aide qui disparait
        //Text("Recherche")
        //},

        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), // Définir le bouton "Entrée" comme action de recherche
        keyboardActions = KeyboardActions(onSearch = { onSearchAction() }), // Déclenche l'action définie
        //Comment le composant doit se placer
        modifier = modifier
            .fillMaxWidth() // Prend toute la largeur
            .heightIn(min = 56.dp) //Hauteur minimum
    )
}


@Composable //Composable affichant 1 élément
fun PictureRowItem(
    modifier: Modifier = Modifier, data: WeatherEntity,

    onRowPictureClick: (Int) -> Unit = {}
) {

    var expended by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onTertiary)
            .fillMaxWidth()
    ) {

//Permission Internet nécessaire
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
            modifier = Modifier
                .heightIn(max = 100.dp)
                .widthIn(max = 100.dp)
                .clickable {
                    onRowPictureClick(data.id)
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expended = !expended
                }) {
            Text(
                text = data.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                text = if (expended) data.getResume() else data.getResume().take(20) + "...",
                fontSize = 14.sp,
                modifier = Modifier.animateContentSize()
            )

        }


    }

}