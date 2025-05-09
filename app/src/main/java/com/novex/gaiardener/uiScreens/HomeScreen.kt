package com.novex.gaiardener.uiScreens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.novex.gaiardener.R
import com.novex.gaiardener.bluetooth.BluetoothManager
import com.novex.gaiardener.bluetooth.BluetoothPopupDialog
import com.novex.gaiardener.uiScreens.components.CircularIcon

class BluetoothViewModel : androidx.lifecycle.ViewModel() {
    var isConnected by mutableStateOf(false)
    var connectedDevice by mutableStateOf<String?>(null)
}

@Composable
fun HomeScreen(navController: NavController, bluetoothViewModel: BluetoothViewModel = viewModel()) {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var showBluetoothDialog by remember { mutableStateOf(false) }

    val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION // NECESARIO para escaneo real
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            showBluetoothDialog = true
        } else {
            Toast.makeText(context, "Permisos de Bluetooth y ubicación requeridos", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkBluetoothPermissions(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) return true
        return bluetoothPermissions.all {
            androidx.core.content.ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    // 🔁 Intentar reconexión automática
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("bluetooth_prefs", Context.MODE_PRIVATE)
        val lastAddress = prefs.getString("last_device_address", null)

        if (lastAddress != null && !bluetoothViewModel.isConnected) {
            val device = bluetoothAdapter?.bondedDevices?.find { it.address == lastAddress }
            if (device != null) {
                BluetoothManager.connectToDevice(context, device)
                bluetoothViewModel.connectedDevice = device.name ?: device.address
                bluetoothViewModel.isConnected = true
                Toast.makeText(context, "Reconectado a ${device.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showBluetoothDialog) {
        BluetoothPopupDialog(
            onDismiss = { showBluetoothDialog = false },
            onDeviceSelected = { device ->
                bluetoothAdapter?.cancelDiscovery()
                BluetoothManager.connectToDevice(context, device)
                bluetoothViewModel.connectedDevice = device.name ?: device.address
                bluetoothViewModel.isConnected = true
                Toast.makeText(context, "Conectado a ${device.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF7BA05B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Text(text = "¡BIENVENIDO!", fontSize = 32.sp, color = Color.White)

            Spacer(modifier = Modifier.height(30.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                CircularIcon(
                    iconResId = R.drawable.ic_jardinero,
                    bubbleColor = Color(0xFFCFB53B),
                    size = 270,
                    iconSize = 230,
                    isPngIcon = true
                )

                CircularIcon(
                    iconResId = R.drawable.ic_plant,
                    iconColor = if (bluetoothViewModel.isConnected) Color(0xFF7BA05B) else Color(0xFFDC493A),
                    bubbleColor = Color(0xFFCFB53B),
                    isPngIcon = false,
                    size = 80,
                    iconSize = 60,
                    modifier = Modifier.offset(x = (-120).dp, y = (-90).dp)
                )

                CircularIcon(
                    iconResId = R.drawable.ic_bluetooth,
                    iconColor = Color.White,
                    bubbleColor = Color(0xFFCFB53B),
                    isPngIcon = false,
                    size = 80,
                    iconSize = 60,
                    modifier = Modifier.offset(x = (120).dp, y = (-90).dp),
                    onClick = {
                        if (!checkBluetoothPermissions()) {
                            permissionLauncher.launch(bluetoothPermissions)
                            return@CircularIcon
                        }

                        if (bluetoothAdapter == null) {
                            Toast.makeText(context, "Bluetooth no soportado", Toast.LENGTH_SHORT).show()
                            return@CircularIcon
                        }

                        if (!bluetoothAdapter.isEnabled) {
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            context.startActivity(enableBtIntent)
                        } else {
                            showBluetoothDialog = true
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val sent = BluetoothManager.sendMessage("PING")
                    if (sent) {
                        BluetoothManager.listenForResponseOnce { response ->
                            println("📡 Respuesta Bluetooth: $response")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFB53B))
            ) {
                Text("Probar Conexión", color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = if (bluetoothViewModel.isConnected)
                    "¡Genial! Conectado a ${bluetoothViewModel.connectedDevice}, selecciona una opción para continuar"
                else
                    "¡Haz clic en el icono de Bluetooth para añadir tu Gaiardener y escanear una planta.",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    { navController.navigate("plantSelectionScreen") },
                    enabled = bluetoothViewModel.isConnected,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFB53B))
                ) {
                    Text("Nueva Planta", color = Color.White)
                }

                Button(
                    onClick = { /* Acción de Mis Plantas */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFB53B))
                ) {
                    Text("Mis Plantas", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (bluetoothViewModel.isConnected)
                    "Conectado a ${bluetoothViewModel.connectedDevice}"
                else
                    "Sin conexión",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
