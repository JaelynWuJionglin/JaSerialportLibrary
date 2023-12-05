package com.jaylen.serialportlibrary.port

class PortBean(

    /**
     * 串口地址
     */
    var deviceAdr: String = "",

    /**
     * 波特率
     */
    var baudRate: Int = 0,

    /**
     * flags
     */
    var flags: Int = 0
) {

    override fun toString(): String {
        return "PortBean: deviceAdr='$deviceAdr', baudRate=$baudRate, flags=$flags"
    }
}