package com.novex.gaiardener.uiScreens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.novex.gaiardener.data.repository.PlantRepository
import com.novex.gaiardener.uiScreens.*
import com.novex.gaiardener.viewModel.PlantViewModel
import com.novex.gaiardener.viewModel.PlantViewModelFactory

@Composable
fun NavGraph(navController: NavHostController, repository: PlantRepository) {
    val factory = PlantViewModelFactory(repository)
    val plantViewModel: PlantViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "homeScreen"
    ) {
        composable("homeScreen") {
            HomeScreen(navController)
        }

        composable("plantSelectionScreen") {
            PlantSelectionScreen(navController, plantViewModel)
        }

        composable(
            "plant_detail/{plantId}",
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: -1
            PlantDetailScreen(navController, plantId, plantViewModel)
        }

        // ✅ Ruta a resultados del escaneo
        composable(
            "scanResult/{plantId}/{ph}/{humedad}/{temperatura}/{luz}",
            arguments = listOf(
                navArgument("plantId") { type = NavType.IntType },
                navArgument("ph") { type = NavType.FloatType },
                navArgument("humedad") { type = NavType.FloatType },
                navArgument("temperatura") { type = NavType.FloatType },
                navArgument("luz") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: -1
            val ph = backStackEntry.arguments?.getFloat("ph") ?: 0f
            val humedad = backStackEntry.arguments?.getFloat("humedad") ?: 0f
            val temperatura = backStackEntry.arguments?.getFloat("temperatura") ?: 0f
            val luz = backStackEntry.arguments?.getFloat("luz") ?: 0f

            androidx.compose.runtime.LaunchedEffect(plantId) {
                plantViewModel.getPlantById(plantId)
            }

            val plant = plantViewModel.selectedPlant.collectAsState().value

            if (plant != null) {
                ScanResultScreen(
                    navController = navController,
                    plant = plant,
                    humedad = humedad,
                    temperatura = temperatura,
                    luz = luz,
                    ph = ph
                )
            } else {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
    }
}
