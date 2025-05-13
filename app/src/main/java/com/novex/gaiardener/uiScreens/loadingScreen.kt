package com.novex.gaiardener.uiScreens

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novex.gaiardener.bluetooth.BluetoothManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LoadingScreen(
    onDismiss: () -> Unit,
    plantName: String,
    onDataReceived: (ph: Float, humedad: Float, temperatura: Float) -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(0) }

    val animatedDots by rememberInfiniteTransition().animateValue(
        initialValue = "",
        targetValue = "...",
        typeConverter = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.length.toFloat()) },
            convertFromVector = { ".".repeat(it.value.toInt().coerceIn(0, 3)) }
        ),
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        )
    )

    val handler = remember { Handler(Looper.getMainLooper()) }

    DisposableEffect(Unit) {
        BluetoothManager.sendMessage("SCAN")

        BluetoothManager.listenForResponseOnce { message ->
            when {
                message.startsWith("PROGRESS:") -> {
                    val value = message.removePrefix("PROGRESS:").trim().toIntOrNull()
                    if (value != null) {
                        handler.post {
                            progress = value.coerceIn(0, 5)
                        }
                    }
                }

                message.startsWith("DATA:") -> {
                    try {
                        val json = message.removePrefix("DATA:").trim()
                        val valores = Regex("\"(\\w+)\":([0-9.]+)").findAll(json)
                            .associate { it.groupValues[1] to it.groupValues[2].toFloat() }

                        val humedad = valores["humedad"] ?: throw Exception("humedad no encontrada")
                        val temperatura = valores["temperatura"] ?: throw Exception("temperatura no encontrada")
                        val ph = valores["ph"] ?: throw Exception("ph no encontrado")

                        handler.post {
                            onDataReceived(ph, humedad, temperatura)
                        }
                    } catch (e: Exception) {
                        handler.post {
                            Toast.makeText(context, "❌ Error al procesar las lecturas del sensor.", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                }
            }
        }

        onDispose {
            handler.removeCallbacksAndMessages(null)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7BA05B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            LoadingWave()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Escaneando el estado de tu $plantName",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estamos leyendo los sensores de tu planta$animatedDots",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoadingWave() {
    val infiniteTransition = rememberInfiniteTransition()
    val delays = listOf(0, 150, 300)
    val bars = delays.map { delay ->
        infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = 40f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(50.dp)
    ) {
        bars.forEach { heightAnim ->
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(heightAnim.value.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFCFB53B))
            )
        }
    }
}
