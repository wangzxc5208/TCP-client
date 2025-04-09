package com.example.tcp.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TCP连接状态管理类，用于管理TCP连接状态和消息
 */
class TcpConnectionState {
    // 连接状态
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // 连接地址
    private val _serverAddress = MutableStateFlow("")
    val serverAddress: StateFlow<String> = _serverAddress.asStateFlow()

    // 连接端口
    private val _serverPort = MutableStateFlow(0)
    val serverPort: StateFlow<Int> = _serverPort.asStateFlow()

    // 接收到的消息列表
    private val _receivedMessages = MutableStateFlow<List<String>>(emptyList())
    val receivedMessages: StateFlow<List<String>> = _receivedMessages.asStateFlow()

    // 发送的消息列表
    private val _sentMessages = MutableStateFlow<List<String>>(emptyList())
    val sentMessages: StateFlow<List<String>> = _sentMessages.asStateFlow()

    // 连接状态消息
    private val _statusMessage = MutableStateFlow("未连接")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    /**
     * 更新连接状态
     */
    fun updateConnectionState(connected: Boolean) {
        _isConnected.value = connected
        _statusMessage.value = if (connected) "已连接到 ${_serverAddress.value}:${_serverPort.value}" else "未连接"
    }

    /**
     * 设置服务器地址和端口
     */
    fun setServerInfo(address: String, port: Int) {
        _serverAddress.value = address
        _serverPort.value = port
    }

    /**
     * 添加接收到的消息
     */
    fun addReceivedMessage(message: String) {
        _receivedMessages.value = _receivedMessages.value + message
    }

    /**
     * 添加发送的消息
     */
    fun addSentMessage(message: String) {
        _sentMessages.value = _sentMessages.value + message
    }

    /**
     * 更新状态消息
     */
    fun updateStatusMessage(message: String) {
        _statusMessage.value = message
    }

    /**
     * 清除消息历史
     */
    fun clearMessages() {
        _receivedMessages.value = emptyList()
        _sentMessages.value = emptyList()
    }
}