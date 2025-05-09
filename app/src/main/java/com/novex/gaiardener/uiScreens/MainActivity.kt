package com.novex.gaiardener.uiScreens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.novex.gaiardener.data.database.AppDatabase
import com.novex.gaiardener.data.repository.PlantRepository
import com.novex.gaiardener.ui.theme.GaiardenerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import com.novex.gaiardener.uiScreens.navigation.NavGraph


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = 0xFF7BA05B.toInt()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Creamos un CoroutineScope para la base de datos
        val applicationScope = CoroutineScope(SupervisorJob())

        // Obtén la base de datos con el scope
        val database = AppDatabase.getDatabase(this, applicationScope)
        val plantDao = database.plantDao()

        // Inicializa el repositorio con el DAO
        val repository = PlantRepository(plantDao)

        setContent {
            val navController = rememberNavController()
            NavGraph(navController, repository)
        }
    }
}
