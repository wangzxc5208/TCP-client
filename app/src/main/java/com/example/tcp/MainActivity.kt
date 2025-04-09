package com.example.tcp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tcp.network.TcpConnectionManager
import com.example.tcp.ui.PickupCodeScreen
import com.example.tcp.ui.TcpClientScreen
import com.example.tcp.ui.theme.TCPTheme

class MainActivity : ComponentActivity() {
    // 创建TCP连接管理器实例
    private val tcpConnectionManager = TcpConnectionManager()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TCPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 使用状态管理简单的页面导航
                    var currentScreen by remember { mutableStateOf(Screen.PickupCode) }
                    
                    when (currentScreen) {
                        Screen.TcpClient -> {
                            TcpClientScreen(
                                tcpConnectionManager = tcpConnectionManager,
                                onNavigateToPickupCode = { currentScreen = Screen.PickupCode },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Screen.PickupCode -> {
                            PickupCodeScreen(
                                tcpConnectionManager = tcpConnectionManager,
                                onNavigateToTcpClient = { currentScreen = Screen.TcpClient },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                    
                    // 当Activity销毁时释放资源
                    DisposableEffect(Unit) {
                        onDispose {
                            tcpConnectionManager.release()
                        }
                    }
                }
            }
        }
    }
}

// 定义应用中的屏幕
enum class Screen {
    TcpClient,
    PickupCode
}