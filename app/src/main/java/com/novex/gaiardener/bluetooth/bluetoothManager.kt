package com.novex.gaiardener.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Executors

object BluetoothManager {
    var isConnected = mutableStateOf(false)
    private var connectedDevice: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    private val executor = Executors.newSingleThreadExecutor()
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var responseCallback: ((String) -> Unit)? = null

    fun connectToDevice(context: Context, device: BluetoothDevice) {
        try {
            val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            if (socket.isConnected) {
                println("⚠️ El socket ya está conectado")
                return
            }

            socket.connect()

            bluetoothSocket = socket
            connectedDevice = device
            outputStream = socket.outputStream
            inputStream = socket.inputStream
            isConnected.value = true

            val prefs = context.getSharedPreferences("bluetooth_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("last_device_address", device.address).apply()

            println("✅ Conectado a ${device.name}")
        } catch (e: IOException) {
            e.printStackTrace()
            isConnected.value = false
        }
    }

    fun sendMessage(message: String): Boolean {
        return try {
            outputStream?.write((message + "\n").toByteArray())
            outputStream?.flush()
            println("📤 Enviado: $message")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun listenForResponseOnce(onMessageReceived: (String) -> Unit) {
        responseCallback = onMessageReceived
        if (bluetoothSocket?.isConnected == true) {
            executor.execute {
                val buffer = ByteArray(1024)
                try {
                    while (true) {
                        val bytes = inputStream?.read(buffer) ?: break
                        if (bytes > 0) {
                            val received = String(buffer, 0, bytes).trim()
                            println("📥 Recibido: $received")
                            responseCallback?.invoke(received)

                            if (received.startsWith("DATA:")) {
                                break
                            }
                        }
                    }
                } catch (e: IOException) {
                    println("❌ Desconectado")
                    isConnected.value = false
                    e.printStackTrace()
                }
            }
        }
    }

    fun disconnect() {
        try {
            bluetoothSocket?.close()
            outputStream = null
            inputStream = null
            isConnected.value = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
