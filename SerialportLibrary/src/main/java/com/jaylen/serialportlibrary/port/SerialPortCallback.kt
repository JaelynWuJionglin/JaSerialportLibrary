package com.jaylen.serialportlibrary.port

/**
 * 串口数据回调
 */
interface SerialPortCallback {

    /**
     * 服务端收到消息
     * @param message 客户端发送过来的数据
     */
    fun onPortMessage(address: String, message: ByteArray)

    fun onPortOpenSuccess(address: String)

    fun onPortOpenFail(address: String)

    fun onPortClose(address: String)
}
