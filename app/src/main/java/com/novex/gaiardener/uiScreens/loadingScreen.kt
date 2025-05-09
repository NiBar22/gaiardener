package com.novex.gaiardener.uiScreens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import org.json.JSONObject

@Composable
fun LoadingScreen(
    onDismiss: () -> Unit,
    plantName: String,
    onResult: (ph: Float, humedad: Float, temperatura: Float) -> Unit
) {
    val context = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }
    var progress by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Fragmento dentro de LaunchedEffect:
        BluetoothManager.sendMessage("SCAN")
        BluetoothManager.listenForResponseOnce { message ->
            when {
                message.startsWith("PROGRESS:") -> {
                    val value = message.removePrefix("PROGRESS:").trim().toIntOrNull()
                    if (value != null && value in 1..5) {
                        handler.post {
                            progress = value
                        }
                    }
                }

                message.startsWith("DATA:") -> {
                    val json = message.substringAfter("DATA:").trim()
                    try {
                        val data = JSONObject(json)

                        val humedad = data.optDouble("humedad", Double.NaN).toFloat()
                        val temperatura = data.optDouble("temperatura", Double.NaN).toFloat()
                        val ph = data.optDouble("ph", Double.NaN).toFloat()

                        Log.i("Bluetooth", "Parsed -> humedad=$humedad, temperatura=$temperatura, ph=$ph")

                        if (humedad.isNaN() || temperatura.isNaN() || ph.isNaN()) {
                            handler.post {
                                Toast.makeText(
                                    context,
                                    "❌ Error al procesar los datos.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                            }
                        } else {
                            handler.post {
                                onResult(ph, humedad, temperatura)
                            }
                        }
                    } catch (e: Exception) {
                        handler.post {
                            Toast.makeText(
                                context,
                                "❌ Formato de datos incorrecto.",
                                Toast.LENGTH_LONG
                            ).show()
                            onDismiss()
                        }
                    }
                }
            }
        }

    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFB53B))
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = {
            Text("Leyendo sensores de $plantName")
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Obteniendo 5 lecturas estables...")
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(progress = progress / 5f)
                Spacer(modifier = Modifier.height(10.dp))
                Text("$progress/5 completadas")
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}
