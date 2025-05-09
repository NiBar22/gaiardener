package com.novex.gaiardener.uiScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.novex.gaiardener.R
import com.novex.gaiardener.viewModel.PlantViewModel
import com.novex.gaiardener.uiScreens.components.CircularIcon
import com.novex.gaiardener.uiScreens.components.getDrawableResource
import com.novex.gaiardener.uiScreens.LoadingScreen
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState


@Composable
fun PlantDetailScreen(navController: NavController, plantId: Int, plantViewModel: PlantViewModel) {
    var showScanDialog by remember { mutableStateOf(false) }
    val selectedPlant by plantViewModel.selectedPlant.collectAsState()

    if (showScanDialog && selectedPlant != null) {
        LoadingScreen(
            onDismiss = { showScanDialog = false },
            plantName = selectedPlant!!.nombre
        ) { ph, humedad, temperatura ->
            showScanDialog = false
            navController.navigate("scanResult/${plantId}/$ph/$humedad/$temperatura/0")
        }
    }

    LaunchedEffect(plantId) {
        plantViewModel.getPlantById(plantId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7BA05B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                CircularIcon(
                    iconResId = R.drawable.ic_back,
                    iconColor = Color.White,
                    bubbleColor = Color(0xFFCFB53B),
                    size = 50,
                    iconSize = 24,
                    onClick = { navController.popBackStack() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedPlant == null) {
                CircularProgressIndicator(color = Color.White)
            } else {
                val plant = selectedPlant!!
                val imageRes = getDrawableResource(plant.imagenes.firstOrNull())

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = plant.nombre.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Imagen de ${plant.nombre}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = plant.nombreCientifico,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = plant.datosGenerales ?: "Información no disponible",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (!plant.datosCuriosos.isNullOrEmpty()) {
                                Text(
                                    text = "🌱 Datos Curiosos",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7BA05B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plant.datosCuriosos,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!plant.recomendaciones.isNullOrEmpty()) {
                                Text(
                                    text = "💡 Recomendaciones",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7BA05B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plant.recomendaciones,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = { showScanDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFB53B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                ) {
                    Text(text = "ESCANEAR", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
