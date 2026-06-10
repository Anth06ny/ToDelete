package com.amonteiro.todelete.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.amonteiro.todelete.presentation.ui.screens.DetailScreen
import com.amonteiro.todelete.presentation.ui.screens.SearchScreen
import com.amonteiro.todelete.presentation.viewmodel.MainViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

class Routes {
    @Serializable
    data object SearchRoute

    //les paramètres ne peuvent être que des types de base(String, Int, Double...)
    @Serializable
    data class DetailRoute(val id: Int)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navHostController: NavHostController = rememberNavController()
    //viewModel() en dehors de NavHost lie à l'Activité donc partagé entre les écrans
    //viewModel() dans le NavHost lié à la stack d'écran. Une instance par stack d'écran
    val mainViewModel: MainViewModel = koinViewModel<MainViewModel>()

    //On observe la back stack comme un State pour que la TopAppBar se recompose à chaque navigation
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Mon titre") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                //Icone à gauche
                navigationIcon = {
                    //On lit currentBackStackEntry pour déclencher la recomposition,
                    //puis on vérifie previousBackStackEntry pour savoir si on peut revenir
                    if (currentBackStackEntry != null && navHostController.previousBackStackEntry != null) {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }

    ) { innerPadding ->

        //Import version avec Composable
        NavHost(
            navController = navHostController,
            startDestination = Routes.SearchRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            //Route 1 vers notre SearchScreen
            composable<Routes.SearchRoute> {

                //Si créé ici, il sera propre à cet instance de l'écran
                //val mainViewModel : MainViewModel = viewModel()

                //on peut passer le navHostController à un écran s'il déclenche des navigations
                SearchScreen(
                    mainViewModel = mainViewModel,
                    onRowPictureClick = { idWeatherEntity: Int -> navHostController.navigate(Routes.DetailRoute(idWeatherEntity)) }
                )
            }

            //Route 2 vers un écran de détail
            composable<Routes.DetailRoute> {
                val detailRoute = it.toRoute<Routes.DetailRoute>()
                val weatherEntity = mainViewModel.dataList.collectAsStateWithLifecycle().value.first { it.id == detailRoute.id }

                DetailScreen(
                    data = weatherEntity,
                    onBackClick = {
                        navHostController.popBackStack()
                    }
                )
            }
        }
    }
}