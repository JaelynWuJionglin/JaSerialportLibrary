package com.jaserialportlibrary.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jaylen.serialportlibrary.port.PortBean
import com.jaylen.serialportlibrary.port.PortManager
import com.jaylen.serialportlibrary.port.SerialPortCallback

class MainActivity : AppCompatActivity(), SerialPortCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //打开串口
        PortManager.instance
            .addPortBean(PortBean("/dev/ttyS1", 9600, 0))
            .openPort(this)

        //发送数据，指定串口
        PortManager.instance.sendDataAt("/dev/ttyS1", byteArrayOf(0,0,0,0,0))

        //发送数据，所有打开的串口
        PortManager.instance.sendDataAll(byteArrayOf(0,0,0,0,0))
    }

    override fun onPortMessage(address: String, message: ByteArray) {

    }

    override fun onPortOpenSuccess() {

    }

    override fun onPortOpenFail() {

    }

    override fun onPortClose() {

    }

    override fun onDestroy() {
        super.onDestroy()
        PortManager.instance.closePort()
    }
}