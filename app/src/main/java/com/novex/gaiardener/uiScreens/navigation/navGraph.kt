package com.novex.gaiardener.uiScreens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.novex.gaiardener.viewModel.PlantViewModel
import com.novex.gaiardener.viewModel.PlantViewModelFactory
import com.novex.gaiardener.data.repository.PlantRepository


@Composable
fun NavGraph(navController: NavHostController, repository: PlantRepository) {
    val factory = PlantViewModelFactory(repository)
    val plantViewModel: PlantViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "homeScreen"
    ) {
        composable("homeScreen") { HomeScreen(navController) }
        composable("plantSelectionScreen") { PlantSelectionScreen(navController, plantViewModel) }

        composable(
            "plant_detail/{plantId}",
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: -1
            PlantDetailScreen(navController, plantId, plantViewModel)
        }
    }
}
