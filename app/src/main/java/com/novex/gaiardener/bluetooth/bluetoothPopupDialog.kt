package com.novex.gaiardener.bluetooth


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun BluetoothPopupDialog(
    onDismiss: () -> Unit,
    onDeviceSelected: (BluetoothDevice) -> Unit
) {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val isDiscovering = remember { mutableStateOf(false) }
    val forceReload = remember { mutableStateOf(false) }

    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (discoveredDevices.none { it.address == device.address }) {
                            discoveredDevices.add(it)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(forceReload.value) {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }

        discoveredDevices.clear()

        bluetoothAdapter.bondedDevices?.forEach { bondedDevice ->
            if (discoveredDevices.none { it.address == bondedDevice.address }) {
                discoveredDevices.add(bondedDevice)
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        isDiscovering.value = true
        bluetoothAdapter.startDiscovery()
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isDiscovering.value) bluetoothAdapter.cancelDiscovery()
            context.unregisterReceiver(receiver)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                forceReload.value = !forceReload.value
            }) {
                Text("🔄 Escanear nuevamente")
            }
        },
        title = { Text("Selecciona un dispositivo") },
        text = {
            Column {
                if (discoveredDevices.isEmpty()) {
                    Text("Buscando dispositivos...\nAsegúrate de que el Bluetooth y la ubicación estén activados.")
                } else {
                    discoveredDevices.forEach { device ->
                        TextButton(onClick = {
                            bluetoothAdapter.cancelDiscovery()
                            onDeviceSelected(device)
                            onDismiss()
                        }) {
                            Text(device.name ?: device.address)
                        }
                    }
                }
            }
        }
    )
}
