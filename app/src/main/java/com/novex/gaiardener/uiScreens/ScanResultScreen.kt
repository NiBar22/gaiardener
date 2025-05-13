package com.novex.gaiardener.uiScreens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.novex.gaiardener.R
import com.novex.gaiardener.data.entities.Plant
import com.novex.gaiardener.uiScreens.components.getDrawableResource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import kotlin.math.abs



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
    val imageResId = getDrawableResource(plant.imagenes.firstOrNull())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7BA05B))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Cerrar",
                    tint = Color(0xFF7BA05B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = plant.nombre,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
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
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = plant.datosGenerales,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HumidityCircularCard(humedad, plant.rangoHumedad)
                TemperatureBarCard(temperatura, plant.rangoTemperatura)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconStatusCard("Luz", luz, plant.rangoLuzSolar, R.drawable.ic_light)
                PhRangeCard(ph, plant.rangoPh)
            }
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
fun AnimatedTestCircleCard() {
    val startAnimation = remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (startAnimation.value) 0.7f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "animatedFillCircle"
    ).value

    LaunchedEffect(Unit) {
        startAnimation.value = true
    }

    Card(
        modifier = Modifier
            .padding(top = 24.dp)
            .size(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Test Pecera", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF7BA05B))
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedProgress)
                        .align(Alignment.BottomCenter)
                        .background(Color(0xFF81C784))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("70%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF81C784))
            Text("Ideal: 60 - 80", fontSize = 12.sp, color = Color.Gray)
        }
    }
}




@Composable
fun HumidityCircularCard(value: Float, rangoStr: String) {
    val cleaned = rangoStr.replace(",", ".")
    val parts = cleaned.split("-").mapNotNull { it.trim().toFloatOrNull() }
    if (parts.size != 2) return

    val (min, max) = parts
    val ideal = (min + max) / 2
    val maxDistance = max - min

    val distanceFromIdeal = kotlin.math.abs(value - ideal)
    val normalized = 1f - (distanceFromIdeal / (maxDistance / 2)).coerceIn(0f, 1f)
    val displayProgress = normalized.coerceAtLeast(0.05f)

    val isOutOfRange = value < min || value > max

    val startAnimation = remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (startAnimation.value) displayProgress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "humidityAdjustedProgress"
    ).value

    LaunchedEffect(Unit) {
        startAnimation.value = true
    }

    val color = when {
        isOutOfRange -> Color(0xFFE57373) // rojo
        distanceFromIdeal > (maxDistance * 0.25f) -> Color(0xFFFFB74D) // naranja si se aleja del ideal
        else -> Color(0xFF81C784) // verde si está cerca del ideal
    }

    Card(
        modifier = Modifier.size(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Humedad", fontWeight = FontWeight.Bold, color = Color(0xFF7BA05B), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedProgress)
                        .align(Alignment.BottomCenter)
                        .background(color)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${"%.1f".format(value)}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text("Ideal: %.1f - %.1f".format(min, max), fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TemperatureBarCard(value: Float, rangoStr: String) {
    val cleaned = rangoStr.replace(",", ".")
    val parts = cleaned.split("-").mapNotNull { it.trim().toFloatOrNull() }
    if (parts.size != 2) return

    val (min, max) = parts
    val mid = (min + max) / 2

    val isOutOfRange = value < min || value > max

    // Lógica de progreso:
    val rawProgress = (value / max).coerceIn(0f, 1f)
    val progressInRange = maxOf(rawProgress, 0.75f)
    val displayProgress = if (isOutOfRange) 1f else progressInRange

    val startAnimation = remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (startAnimation.value) displayProgress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "tempBarAnim"
    ).value

    LaunchedEffect(Unit) {
        startAnimation.value = true
    }

    // Lógica de color:
    val color = when {
        isOutOfRange -> Color(0xFFE57373) // rojo
        value > mid -> Color(0xFFFFB74D)  // amarillo
        else -> Color(0xFF81C784)         // verde
    }

    Card(
        modifier = Modifier.size(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Temperatura", fontWeight = FontWeight.Bold, color = Color(0xFF7BA05B), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(80.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedProgress)
                        .align(Alignment.BottomCenter)
                        .background(color, shape = RoundedCornerShape(12.dp))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${"%.1f".format(value)} °C", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text("Ideal: %.1f - %.1f".format(min, max), fontSize = 12.sp, color = Color.Gray)
        }
    }
}


@Composable
fun PhRangeCard(value: Float, rangoStr: String) {
    val cleanedRange = rangoStr.replace(",", ".")
    val parts = cleanedRange.split("-").mapNotNull { it.trim().toFloatOrNull() }
    if (parts.size != 2) return
    val (min, max) = parts

    val ideal = (min + max) / 2
    val maxDistance = max - min
    val distanceFromIdeal = abs(value - ideal)
    val normalized = 1f - (distanceFromIdeal / (maxDistance / 2)).coerceAtMost(1f)

    val isOutOfRange = value < min || value > max
    val displayProgress = if (isOutOfRange) 0.5f else normalized.coerceAtLeast(0.05f)

    val startAnimation = remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (startAnimation.value) displayProgress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "phProgressAnim"
    ).value

    LaunchedEffect(Unit) {
        startAnimation.value = true
    }

    val color = when {
        isOutOfRange -> Color(0xFFE57373)
        distanceFromIdeal > (maxDistance * 0.25f) -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    val textRange = when {
        value < 6.5 -> "Ácido"
        value > 7.5 -> "Básico"
        else -> "Neutro"
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("pH", fontWeight = FontWeight.Bold, color = Color(0xFF7BA05B), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(modifier = Modifier.size(80.dp)) {
                drawArc(
                    color = Color.LightGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 12f)
                )
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 180f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 12f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${"%.2f".format(value)} ($textRange)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text("Ideal: %.1f - %.1f".format(min, max), fontSize = 12.sp, color = Color.Gray)
        }
    }
}


@Composable
fun IconStatusCard(label: String, value: Float, rangoStr: String, iconRes: Int) {
    val cleanedRange = rangoStr.replace(",", ".")
    val parts = cleanedRange.split("-").mapNotNull { it.trim().toFloatOrNull() }
    if (parts.size < 2) return

    val (min, max) = parts
    val color = when {
        value < min -> Color(0xFFE57373)
        value > max -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    Card(
        modifier = Modifier.size(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF7BA05B), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(color)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("Valor: ${"%.2f".format(value)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
            Text("Ideal: %.1f - %.1f".format(min, max), fontSize = 12.sp, color = Color.Gray)
        }
    }
}
