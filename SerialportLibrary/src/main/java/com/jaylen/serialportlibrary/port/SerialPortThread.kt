package com.jaylen.serialportlibrary.port

import android.util.Log
import com.android.serialport.SerialPort

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * 串口数据接收线程
 */
class SerialPortThread(
    private val serialPort: SerialPort,
    val portBean: PortBean,
    private val callback: SerialPortCallback?
) : Thread() {
    private val tag = "SerialPortThread"
    private var isRun = true
    private var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var isPortOpen = false

    fun openPort() {
        Log.d(tag, "openPort()  $portBean")
        if (portBean.deviceAdr != "" && portBean.baudRate > 0) {
            try {
                if (serialPort.openPort(portBean.deviceAdr, portBean.baudRate, portBean.flags)) {
                    inputStream = serialPort.inputStream
                    outputStream = serialPort.outputStream
                    Log.i(tag, "成功打开串口: ${portBean.deviceAdr}")
                    isPortOpen = true
                    callback?.onPortOpenSuccess()
                } else {
                    isPortOpen = false
                    callback?.onPortOpenFail()
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                isPortOpen = false
                callback?.onPortOpenFail()
            }
        } else {
            isPortOpen = false
            callback?.onPortOpenFail()
        }
    }

    fun closePort() {
        isRun = false
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream = null
            outputStream = null
        }

        serialPort.close()
        isPortOpen = false
        Log.e(tag, "${portBean.deviceAdr} --- serialPort.close()")
    }

    /**
     * 读取串口数据
     */
    private fun readData() {
        try {
            while (isRun) {
                try {
                    val bytes = ByteArray(1024)
                    val readLen = inputStream?.read(bytes) ?: 0
                    if (readLen > 0) {
                        val data = ByteArray(readLen)
                        System.arraycopy(bytes, 0, data, 0, readLen)
                        callback?.onPortMessage(portBean.deviceAdr, data)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Log.e(tag, "run: 串口关闭！")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        readData()
    }
}