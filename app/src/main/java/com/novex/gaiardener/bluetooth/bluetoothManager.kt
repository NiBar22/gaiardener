package com.novex.gaiardener.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.compose.runtime.mutableStateOf
import java.io.IOException
import java.util.UUID

object BluetoothManager {
    var isConnected = mutableStateOf(false)
    var connectedDevice: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null

    fun connectToDevice(device: BluetoothDevice) {
        if (connectedDevice == device && isConnected.value) {
            return // Si ya está conectado, no hacer nada
        }

        try {
            val uuid = device.uuids?.firstOrNull()?.uuid ?: return
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()

            connectedDevice = device
            isConnected.value = true
        } catch (e: IOException) {
            e.printStackTrace()
            isConnected.value = false
            connectedDevice = null
        }
    }

    fun disconnectDevice() {
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bluetoothSocket = null
        connectedDevice = null
        isConnected.value = false
    }
}
