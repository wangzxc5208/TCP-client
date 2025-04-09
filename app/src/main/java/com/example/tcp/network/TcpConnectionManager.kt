package com.example.tcp.network

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow

/**
 * TCP连接管理器，负责协调UI状态和网络操作
 */
class TcpConnectionManager {
    private val tcpClient = TcpClient()
    private val connectionState = TcpConnectionState()
    private var messageReceiveJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 暴露状态流给UI层
    val isConnected: StateFlow<Boolean> = connectionState.isConnected
    val serverAddress: StateFlow<String> = connectionState.serverAddress
    val serverPort: StateFlow<Int> = connectionState.serverPort
    val receivedMessages: StateFlow<List<String>> = connectionState.receivedMessages
    val sentMessages: StateFlow<List<String>> = connectionState.sentMessages
    val statusMessage: StateFlow<String> = connectionState.statusMessage

    /**
     * 连接到服务器
     * @param serverIp 服务器IP地址
     * @param serverPort 服务器端口
     */
    fun connect(serverIp: String, serverPort: Int) {
        connectionState.setServerInfo(serverIp, serverPort)
        connectionState.updateStatusMessage("正在连接到 $serverIp:$serverPort...")
        
        coroutineScope.launch {
            val connected = tcpClient.connect(serverIp, serverPort)
            connectionState.updateConnectionState(connected)
            
            if (connected) {
                startMessageReceiving()
            }
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        coroutineScope.launch {
            stopMessageReceiving()
            tcpClient.disconnect()
            connectionState.updateConnectionState(false)
            connectionState.updateStatusMessage("已断开连接")
        }
    }

    /**
     * 发送消息
     * @param message 要发送的消息
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return
        
        coroutineScope.launch {
            connectionState.updateStatusMessage("正在发送消息...")
            val success = tcpClient.sendMessage(message)
            
            if (success) {
                connectionState.addSentMessage(message)
                connectionState.updateStatusMessage("消息已发送")
            } else {
                connectionState.updateStatusMessage("消息发送失败")
            }
        }
    }

    /**
     * 开始接收消息
     */
    private fun startMessageReceiving() {
        // 取消之前的接收任务
        stopMessageReceiving()
        
        // 启动新的接收任务
        messageReceiveJob = coroutineScope.launch {
            connectionState.updateStatusMessage("已连接，正在监听消息...")
            
            try {
                while (isActive && tcpClient.isConnected()) {
                    val message = tcpClient.receiveMessage()
                    if (message != null && message.isNotEmpty()) {
                        connectionState.addReceivedMessage(message)
                        connectionState.updateStatusMessage("收到新消息")
                    } else if (!tcpClient.isConnected()) {
                        // 连接已断开
                        break
                    }
                    
                    // 适当延迟，避免CPU占用过高
                    delay(50) 
                }
            } catch (e: Exception) {
                connectionState.updateStatusMessage("接收消息时出错: ${e.message}")
                e.printStackTrace()
            } finally {
                // 如果循环结束，检查连接状态
                if (!tcpClient.isConnected()) {
                    tcpClient.disconnect()
                    connectionState.updateConnectionState(false)
                    connectionState.updateStatusMessage("连接已断开")
                }
            }
        }
    }

    /**
     * 停止接收消息
     */
    private fun stopMessageReceiving() {
        messageReceiveJob?.cancel()
        messageReceiveJob = null
    }

    /**
     * 清除消息历史
     */
    fun clearMessages() {
        connectionState.clearMessages()
    }

    /**
     * 释放资源
     */
    fun release() {
        disconnect()
        coroutineScope.cancel()
    }
}