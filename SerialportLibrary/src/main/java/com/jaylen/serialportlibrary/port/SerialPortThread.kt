package com.jaylen.serialportlibrary.port

import com.android.serialport.SerialPort
import com.jaylen.serialportlibrary.log.LOGUtils

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
    private var isRun = true
    private var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var isPortOpen = false

    fun openPort() {
        LOGUtils.d("openPort()  $portBean")
        if (portBean.deviceAdr != "" && portBean.baudRate > 0) {
            try {
                if (serialPort.openPort(portBean.deviceAdr, portBean.baudRate, portBean.flags)) {
                    inputStream = serialPort.inputStream
                    outputStream = serialPort.outputStream
                    LOGUtils.i("成功打开串口: ${portBean.deviceAdr}")
                    isPortOpen = true
                    callback?.onPortOpenSuccess(portBean.deviceAdr)
                } else {
                    isPortOpen = false
                    callback?.onPortOpenFail(portBean.deviceAdr)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                isPortOpen = false
                callback?.onPortOpenFail(portBean.deviceAdr)
            }
        } else {
            isPortOpen = false
            callback?.onPortOpenFail(portBean.deviceAdr)
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
        LOGUtils.e("${portBean.deviceAdr} --- serialPort.close()")
        callback?.onPortClose(portBean.deviceAdr)
    }

    /**
     * 读取串口数据
     */
    private fun readData() {
        try {
            var count = 0
            while (count == 0) {
                count = inputStream?.available() ?: 0
            }

            LOGUtils.v("readData: count:$count")

            if (count > 0) {
                val bytes = ByteArray(count)
                var readCount = 0
                while (readCount < count) {
                    readCount += inputStream?.read(bytes, readCount, count - readCount) ?: 0
                }
                callback?.onPortMessage(portBean.deviceAdr, bytes)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            LOGUtils.w(e.toString())
            return
        }
    }

    override fun run() {
        super.run()
        while (!isInterrupted && isRun) {
            while (isRun) {
                readData()
            }
            LOGUtils.w("run: 串口read线程异常！")
        }
        LOGUtils.e("--------  run: 串口read线程中断！ --------")
    }
}
