package com.novex.gaiardener.uiScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.novex.gaiardener.data.entities.Plant

@Composable
fun ScanResultScreen(
    navController: NavController,
    plant: Plant,
    humedad: Float,
    temperatura: Float,
    luz: Float,
    ph: Float
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val imageName = plant.imagenes.firstOrNull() ?: "default_image"
    val imageResId = getImageResourceId(imageName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Regresar"
                )
            }
        }

        Text(
            text = plant.nombre,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = plant.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Text(
                    text = plant.datosGenerales,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnalysisCard("Humedad", humedad, plant.rangoHumedad)
            AnalysisCard("Temperatura", temperatura, plant.rangoTemperatura)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnalysisCard("Luz", luz, plant.rangoLuzSolar)
            AnalysisCard("pH", ph, plant.rangoPh)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAD2))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌱 Recomendaciones:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF7BA05B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ajusta las condiciones para que coincidan con los valores ideales. Pronto implementaremos consejos personalizados.",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun getImageResourceId(imageName: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(
        imageName.substringBeforeLast('.'),
        "drawable",
        context.packageName
    )
}

@Composable
fun AnalysisCard(label: String, value: Float, rangoStr: String) {
    val parts = rangoStr.split("-").mapNotNull { it.trim().toFloatOrNull() }

    if (parts.size < 2) {
        Card(
            modifier = Modifier
                .width(160.dp)
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "🌿 $label", fontWeight = FontWeight.Bold, color = Color(0xFF7BA05B))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Rango inválido: \"$rangoStr\"", color = Color.Red)
            }
        }
        return
    }

    val (min, max) = parts
    val color = when {
        value < min -> Color(0xFFE57373)
        value > max -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "🌿 $label", fontWeight = FontWeight.Bold, color = Color(0xFF7BA05B))
            Spacer(modifier = Modifier.height(6.dp))
            Text("Leído: $value", fontSize = 14.sp, color = color)
            Text("Ideal: $min - $max", fontSize = 12.sp)
        }
    }
}
