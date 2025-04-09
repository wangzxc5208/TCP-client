package com.example.tcp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tcp.network.TcpConnectionManager
import kotlinx.coroutines.launch

@Composable
fun TcpClientScreen(
    tcpConnectionManager: TcpConnectionManager,
    onNavigateToPickupCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected by tcpConnectionManager.isConnected.collectAsState()
    val statusMessage by tcpConnectionManager.statusMessage.collectAsState()
    val receivedMessages by tcpConnectionManager.receivedMessages.collectAsState()
    val sentMessages by tcpConnectionManager.sentMessages.collectAsState()
    
    var serverIp by remember { mutableStateOf("192.168.4.1") }
    var serverPort by remember { mutableStateOf("8080") }
    var messageToSend by remember { mutableStateOf("") }
    
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // 当消息列表更新时，滚动到底部
    LaunchedEffect(receivedMessages.size, sentMessages.size) {
        if (receivedMessages.isNotEmpty() || sentMessages.isNotEmpty()) {
            listState.animateScrollToItem(receivedMessages.size + sentMessages.size)
        }
    }
    
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // 连接状态和设置
        ConnectionStatusSection(
            isConnected = isConnected,
            statusMessage = statusMessage,
            serverIp = serverIp,
            serverPort = serverPort,
            onServerIpChanged = { serverIp = it },
            onServerPortChanged = { serverPort = it },
            onConnectClick = {
                focusManager.clearFocus()
                if (!isConnected) {
                    tcpConnectionManager.connect(serverIp, serverPort.toIntOrNull() ?: 8080)
                } else {
                    tcpConnectionManager.disconnect()
                }
            },
            onClearClick = {
                tcpConnectionManager.clearMessages()
            },
            onNavigateToPickupCode = onNavigateToPickupCode
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 消息显示区域
        MessageDisplaySection(
            receivedMessages = receivedMessages,
            sentMessages = sentMessages,
            listState = listState,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 消息发送区域
        MessageSendSection(
            messageToSend = messageToSend,
            isConnected = isConnected,
            onMessageChanged = { messageToSend = it },
            onSendClick = {
                if (messageToSend.isNotBlank() && isConnected) {
                    tcpConnectionManager.sendMessage(messageToSend)
                    messageToSend = ""
                    focusManager.clearFocus()
                    
                    // 滚动到底部
                    coroutineScope.launch {
                        listState.animateScrollToItem(receivedMessages.size + sentMessages.size)
                    }
                }
            }
        )
    }
}

@Composable
fun ConnectionStatusSection(
    isConnected: Boolean,
    statusMessage: String,
    serverIp: String,
    serverPort: String,
    onServerIpChanged: (String) -> Unit,
    onServerPortChanged: (String) -> Unit,
    onConnectClick: () -> Unit,
    onClearClick: () -> Unit,
    onNavigateToPickupCode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 顶部栏 - 添加返回按钮
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateToPickupCode,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = "TCP客户端配置",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                // 为了对称添加一个空的Box
                Spacer(modifier = Modifier.size(48.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 状态指示器
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isConnected) Color.Green else Color.Red,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 服务器设置
            Row(modifier = Modifier.fillMaxWidth()) {
                // IP地址输入
                OutlinedTextField(
                    value = serverIp,
                    onValueChange = onServerIpChanged,
                    label = { Text("服务器IP") },
                    singleLine = true,
                    enabled = !isConnected,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(2f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 端口输入
                OutlinedTextField(
                    value = serverPort,
                    onValueChange = onServerPortChanged,
                    label = { Text("端口") },
                    singleLine = true,
                    enabled = !isConnected,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 按钮区域
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onConnectClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isConnected) "断开连接" else "连接")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onClearClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("清除消息")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 取件码验证按钮 - 保留底部按钮，但改为轮廓按钮，与UI风格保持一致
            OutlinedButton(
                onClick = onNavigateToPickupCode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("返回取件码验证")
            }
        }
    }
}

@Composable
fun MessageDisplaySection(
    receivedMessages: List<String>,
    sentMessages: List<String>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    // 合并并排序消息
    val allMessages = remember(receivedMessages, sentMessages) {
        val combined = mutableListOf<Pair<String, Boolean>>() // 消息内容, 是否是接收的消息
        
        // 简单实现，实际应用中可能需要更复杂的时间戳排序
        var receiveIndex = 0
        var sentIndex = 0
        
        while (receiveIndex < receivedMessages.size || sentIndex < sentMessages.size) {
            if (receiveIndex < receivedMessages.size) {
                combined.add(Pair(receivedMessages[receiveIndex], true))
                receiveIndex++
            }
            
            if (sentIndex < sentMessages.size) {
                combined.add(Pair(sentMessages[sentIndex], false))
                sentIndex++
            }
        }
        
        combined
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (allMessages.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "无消息记录",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(state = listState) {
                    items(allMessages) { (message, isReceived) ->
                        MessageItem(
                            message = message,
                            isReceived = isReceived,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: String, isReceived: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (!isReceived) {
            Spacer(modifier = Modifier.weight(0.15f))
        }
        
        Box(
            modifier = Modifier
                .weight(0.85f)
                .border(
                    width = 1.dp,
                    color = if (isReceived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = if (isReceived) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                           else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        if (isReceived) {
            Spacer(modifier = Modifier.weight(0.15f))
        }
    }
}

@Composable
fun MessageSendSection(
    messageToSend: String,
    isConnected: Boolean,
    onMessageChanged: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = messageToSend,
                onValueChange = onMessageChanged,
                label = { Text("输入消息") },
                enabled = isConnected,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    onSendClick()
                    focusManager.clearFocus()
                }),
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = onSendClick,
                enabled = isConnected && messageToSend.isNotBlank()
            ) {
                Text("发送")
            }
        }
    }
}