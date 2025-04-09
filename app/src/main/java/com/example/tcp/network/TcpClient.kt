package com.example.tcp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException

/**
 * TCP客户端类，用于处理TCP连接、数据发送和接收
 */
class TcpClient {
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null
    private var isConnected = false

    /**
     * 连接到指定的服务器和端口
     * @param serverIp 服务器IP地址
     * @param serverPort 服务器端口
     * @return 连接是否成功
     */
    suspend fun connect(serverIp: String, serverPort: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // 关闭之前的连接
            disconnect()
            
            // 创建新的Socket连接
            socket = Socket(serverIp, serverPort)
            reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
            writer = PrintWriter(socket?.getOutputStream(), true)
            isConnected = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        try {
            writer?.close()
            reader?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            writer = null
            reader = null
            socket = null
            isConnected = false
        }
    }

    /**
     * 发送消息到服务器
     * @param message 要发送的消息
     * @return 发送是否成功
     */
    suspend fun sendMessage(message: String): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected || writer == null) return@withContext false
        
        return@withContext try {
            writer?.println(message)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 接收来自服务器的消息
     * @return 接收到的消息，如果出错则返回null
     */
    suspend fun receiveMessage(): String? = withContext(Dispatchers.IO) {
        if (!isConnected || reader == null) return@withContext null
        
        return@withContext try {
            // 检查是否有可读数据
            if (socket?.getInputStream()?.available() ?: 0 > 0) {
                // 创建缓冲区读取数据
                val buffer = ByteArray(1024)
                val bytesRead = socket?.getInputStream()?.read(buffer) ?: -1
                
                if (bytesRead > 0) {
                    // 将读取的字节转换为字符串
                    String(buffer, 0, bytesRead)
                } else {
                    // 没有读取到数据或连接已关闭
                    null
                }
            } else {
                // 没有可读数据，返回null但不阻塞
                null
            }
        } catch (e: SocketException) {
            // 连接已关闭
            disconnect()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 检查是否已连接
     * @return 是否已连接
     */
    fun isConnected(): Boolean {
        return isConnected && socket?.isConnected == true && !socket?.isClosed!!
    }
}