package com.jaylen.serialportlibrary.util

object PortUtil {
    /**
     * String 转byte[]
     *
     * @param string
     * @return
     */
    @JvmStatic
    fun stringToBytes(string: String?): ByteArray {
        if (string == null || string == "") {
            return ByteArray(0)
        }
        val size = string.length / 2
        val data = ByteArray(size)
        for (i in 0 until size) {
            val str = string.substring(i * 2, i * 2 + 2)
            data[i] = Integer.parseInt(str, 16).toByte()
        }
        return data
    }

    /**
     * byte[] 转 String
     *
     * @return
     */
    @JvmStatic
    fun byteToHexString(bytes: ByteArray?): String {
        if (bytes == null || bytes.isEmpty()) {
            return ""
        }
        val size = bytes.size
        val sb = StringBuffer(size)
        for (i in 0 until size) {
            val va = bytes[i].toInt() and 0xFF
            var temp = Integer.toHexString(va)
            if (temp.length < 2) {
                temp = "0$temp"
            } else if (temp.length > 2) {
                temp = temp.substring(0, 2)
            }
            sb.append(temp.toUpperCase())
        }
        return sb.toString()
    }


    /**
     * 双直接byte转 int。 高位在前
     *
     * @param hight
     * @param low
     * @return
     */
    @JvmStatic
    fun bytesToInt(hight: Byte, low: Byte): Int {
        val h = hight.toInt() and 0xFF shl 8
        val l = low.toInt() and 0xFF
        return h or l and 0xFFFF
    }

    /**
     * byte转Int 无符号
     */
    @JvmStatic
    fun byteToIntUnsigned(byte:Byte):Int{
        return (byte.toInt() and 0xFF)
    }
}