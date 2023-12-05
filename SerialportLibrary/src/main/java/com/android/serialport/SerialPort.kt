package com.android.serialport

import android.util.Log
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class SerialPort {
    private val TAG = "SerialPort"
    private var mFd: FileDescriptor? = null
    private var mFileInputStream: FileInputStream? = null
    private var mFileOutputStream: FileOutputStream? = null

    companion object {
        init {
            try {
                System.loadLibrary("serial_port")
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    /**
     * 串口
     *
     * @param device 串口设备文件
     * @param baudRate 波特率
     * @param dataBits 数据位；默认8,可选值为5~8
     * @param parity 奇偶校验；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     * @param stopBits 停止位；默认1；1:1位停止位；2:2位停止位
     * @param flags 默认0
     */
    private external fun open(
        device: String, baudRate: Int, dataBits: Int, parity: Int, stopBits: Int, flags: Int
    ): FileDescriptor?

    //close
    external fun close()

    /**
     * 获取输入输出流
     */
    val inputStream: InputStream?
        get() = this.mFileInputStream

    val outputStream: OutputStream?
        get() = this.mFileOutputStream


    /**
     * 打开串口
     */
    fun openPort(devPath: String, baudRate: Int, flags: Int): Boolean {
        val device = File(devPath)
        if (!device.canRead() || !device.canWrite()) {
            try {/* Missing read/write permission, trying to chmod the file */
                val su: Process = Runtime.getRuntime().exec("/system/bin/su")
                val cmd = "chmod 666 $devPath \n exit \n"

                su.outputStream.write(cmd.toByteArray())
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    Log.e(TAG, "su.waitFor() != 0 || !device.canRead() || !device.canWrite()")
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        this.mFd = open(devPath, baudRate, 8, 0, 1, flags)

        return if (this.mFd == null) {
            Log.e(TAG, "native open returns null")
            false
        } else {
            this.mFileInputStream = FileInputStream(this.mFd)
            this.mFileOutputStream = FileOutputStream(this.mFd)

            true
        }
    }

}