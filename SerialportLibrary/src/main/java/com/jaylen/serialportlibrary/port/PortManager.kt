package com.jaylen.serialportlibrary.port

import android.util.Log
import com.android.serialport.SerialPort
import java.io.IOException
import java.util.concurrent.ThreadPoolExecutor

/**
 * 串口通信
 */
class PortManager {
    private var serialPort: SerialPort = SerialPort()
    private var callback: SerialPortCallback? = null
    private val portBeanList = arrayListOf<PortBean>()
    private val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutorUtil.cachedThreadPool()
    private val sendPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutorUtil.cachedThreadPool(1)
    private val threadList = ArrayList<SerialPortThread>()

    /**
     * 单例
     * @return
     */
    companion object {
        @JvmStatic
        val instance: PortManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PortManager()
        }
    }

    /**
     * 设置波特率
     * @param portBean 需要打开的串口
     */
    fun addPortBean(portBean: PortBean): PortManager {
        for (bean in portBeanList) {
            if (bean.deviceAdr == portBean.deviceAdr) {
                bean.baudRate = portBean.baudRate
                bean.flags = portBean.flags
                return instance
            }
        }
        portBeanList.add(portBean)
        return instance
    }

    /**
     * 开启串口
     * @param callback
     */
    fun openPort(callback: SerialPortCallback) {
        this.callback = callback
        if (portBeanList.isEmpty()) {
            return
        }
        startThreadAndOpenPort()
    }

    /**
     * 关闭串口
     */
    fun closePort() {
        if (threadList.isNotEmpty()) {
            for (thread in threadList) {
                thread.closePort()
            }
            threadList.clear()
        }
        portBeanList.clear()
        callback?.onPortClose()
        // 此处不一定能shutdown线程池
        // 需要在MainActivity的onDestroy()中使用exitProcess(0),完全退出app
//        threadPoolExecutor.shutdownNow()
//        sendPoolExecutor.shutdownNow()
    }

    /**
     * 所有需要打开的串口是否打开
     */
    fun isAllPortOpen(): Boolean {
        if (threadList.isEmpty()) {
            Log.e("PortManager", "Error! threadList.isEmpty()!")
            return false
        }

        for (thread in threadList) {
            if (!thread.isPortOpen) {
                return false
            }
        }

        return true
    }

    /**
     * 指定串口是否打开
     */
    fun isPortOpen(dev: String): Boolean {
        if (threadList.isEmpty()) {
            return false
        }
        for (thread in threadList) {
            if (thread.portBean.deviceAdr == dev) {
                return thread.isPortOpen

            }
        }
        return false
    }

    /**
     * 向所有打开的串口发送数据
     */
    fun sendDataAll(bytes: ByteArray): Boolean {
        if (threadList.isEmpty() || bytes.isEmpty()) {
            return false
        }

        for (thread in threadList) {
            sendData(thread, bytes)
        }

        return true
    }

    /**
     * 向指定串口发送数据
     * @param dev
     * @param bytes
     * @return
     */
    fun sendDataAt(dev: String, bytes: ByteArray): Boolean {
        if (threadList.isEmpty() || bytes.isEmpty() || dev == "") {
            return false
        }
        for (thread in threadList) {
            if (thread.portBean.deviceAdr == dev) {
                return sendData(thread, bytes)

            }
        }
        return false
    }

    /**
     * 发送数据
     */
    private fun sendData(thread: SerialPortThread, bytes: ByteArray): Boolean {
        var boolean = false
        sendPoolExecutor.execute {
            try {
                thread.outputStream?.write(bytes)
                thread.outputStream?.flush()
                boolean = true
            } catch (e: IOException) {
                e.printStackTrace()

            }
        }
        return boolean
    }

    /**
     * 启动线程，打开串口，开始任务
     */
    private fun startThreadAndOpenPort() {
        for (portBean in portBeanList) {
            if (!threadPoolExecutor.isShutdown) {
                val thread = SerialPortThread(serialPort, portBean, callback)
                threadPoolExecutor.execute(thread)
                threadList.add(thread)
                thread.openPort()
            } else {
                Log.e("PortManager", "Error! startThread() threadPoolExecutor.isShutdown!")
            }
        }
    }
}
