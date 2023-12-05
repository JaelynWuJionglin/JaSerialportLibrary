package com.jaylen.serialportlibrary.port

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 线程池创建管理类
 */
object ThreadPoolExecutorUtil {
    private const val TAG = "ThreadPoolExecutorUtil"
    /**
     * Java虚拟机可用的处理器数
     */
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    /**
     * 核心线程数量大小
     */
    private val corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4))
    /**
     * 线程池最大容纳线程数
     */
    private val maximumPoolSize = CPU_COUNT * 2 + 1
    /**
     * 线程空闲后的存活时长
     */
    private const val keepAliveTime = 0L

    /**
     * 创建一个设备较优大小的线程池
     * corePoolSize: 该线程池中核心线程的数量。
     * maximumPoolSize：该线程池中最大线程数量。(区别于corePoolSize)
     * keepAliveTime: 是非核心线程空闲时要等待下一个任务到来的时间
     * unit:上面时间属性的单位
     * workQueue:任务队列
     * threadFactory:线程工厂，可用于设置线程名字等等，一般无须设置该参数。
     * @return
     */
    fun cachedThreadPool(): ThreadPoolExecutor {
        /*Log.e(TAG,"CPU_COUNT ------------> " + CPU_COUNT);
        Log.e(TAG,"corePoolSize ------------> " + corePoolSize);
        Log.e(TAG,"maximumPoolSize ------------> " + maximumPoolSize);*/

        return ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(100)
        )
    }

    /**
     * 创建一个制定大小的线程池
     */
    fun cachedThreadPool(size:Int): ThreadPoolExecutor {
        /*Log.e(TAG,"CPU_COUNT ------------> " + CPU_COUNT);
        Log.e(TAG,"corePoolSize ------------> " + corePoolSize);
        Log.e(TAG,"maximumPoolSize ------------> " + maximumPoolSize);*/

        return ThreadPoolExecutor(
            size,
            size,
            keepAliveTime,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(100)
        )
    }
}
