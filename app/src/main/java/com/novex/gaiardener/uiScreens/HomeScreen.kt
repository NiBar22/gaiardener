package com.novex.gaiardener.uiScreens

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.novex.gaiardener.R
import com.novex.gaiardener.uiScreens.components.CircularIcon
import com.novex.gaiardener.bluetooth.BluetoothManager

class BluetoothViewModel : ViewModel() {
    var isConnected by mutableStateOf(false)
    var connectedDevice by mutableStateOf<String?>(null)
}

@Composable
fun HomeScreen(navController: NavController, bluetoothViewModel: BluetoothViewModel = viewModel()) {
    val context = LocalContext.current
    var isReceiverRegistered by remember { mutableStateOf(false) }

    val bluetoothReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        bluetoothViewModel.connectedDevice = device?.name
                        bluetoothViewModel.isConnected = true
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        bluetoothViewModel.isConnected = false
                        bluetoothViewModel.connectedDevice = null
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            context.registerReceiver(bluetoothReceiver, filter)
            isReceiverRegistered = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isReceiverRegistered) {
                try {
                    context.unregisterReceiver(bluetoothReceiver)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
                isReceiverRegistered = false
            }
        }
    }

    val bluetoothDevicePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val device: BluetoothDevice? = result.data?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            device?.let {
                BluetoothManager.connectToDevice(it)
                bluetoothViewModel.connectedDevice = it.name
                bluetoothViewModel.isConnected = true
                Toast.makeText(context, "Conectado a ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val bluetoothPermissions = arrayOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(context, "Se requieren permisos de Bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    fun checkBluetoothPermissions(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) return true // No se necesitan permisos

        return bluetoothPermissions.all {
            androidx.core.content.ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
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

                        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                        if (bluetoothAdapter == null) {
                            Toast.makeText(context, "Bluetooth no soportado", Toast.LENGTH_SHORT).show()
                            return@CircularIcon
                        }
                        if (!bluetoothAdapter.isEnabled) {
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            context.startActivity(enableBtIntent)
                        } else {
                            val intent = Intent("android.bluetooth.devicepicker.action.LAUNCH")
                            intent.putExtra("android.bluetooth.devicepicker.extra.NEED_AUTH", false)
                            intent.putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1)
                            intent.putExtra("android.bluetooth.devicepicker.extra.LAUNCH_CONTEXT", 0)
                            bluetoothDevicePickerLauncher.launch(intent)
                        }
                    }

                )
            }


            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = if (bluetoothViewModel.isConnected) "¡Genial! Conectado a ${bluetoothViewModel.connectedDevice}, selecciona una opción para continuar"
                else "¡Parece que aún no estás conectado! Haz click en el icono de Bluetooth para añadir tu Gaiardener y escanear una nueva planta!",
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
                text = if (bluetoothViewModel.isConnected) "Conectado a ${bluetoothViewModel.connectedDevice}" else "Sin conexión",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}
