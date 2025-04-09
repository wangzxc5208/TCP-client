package com.example.tcp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tcp.network.TcpConnectionManager

/**
 * 取件码验证页面
 * 用户可以输入取件码并发送到服务器进行验证
 * 作为应用的主页面
 */
@Composable
fun PickupCodeScreen(
    tcpConnectionManager: TcpConnectionManager,
    onNavigateToTcpClient: () -> Unit, // 导航到TCP客户端页面
    modifier: Modifier = Modifier
) {
    // 取件码状态，最多4位数字
    var pickupCode by remember { mutableStateOf("") }
    // 连接状态
    val isConnected by tcpConnectionManager.isConnected.collectAsState()
    // 状态消息
    val statusMessage by tcpConnectionManager.statusMessage.collectAsState()
    
    // 创建渐变背景色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
            .padding(16.dp)
    ) {
        // 顶部栏 - 标题和TCP客户端按钮
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.size(48.dp))
            Text(
                text = "取件码验证",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // TCP客户端按钮移至右上角并缩小
            IconButton(
                onClick = onNavigateToTcpClient,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "TCP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 取件码图标 - 增加视觉效果
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(45.dp))
                .clip(RoundedCornerShape(45.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // 这里应该替换为合适的图标
                contentDescription = "取件码",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(45.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 提示文本
        Text(
            text = "请输入取件码",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "验证取件码后，可以打开快递柜取出您的包裹",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 取件码输入框 - 美化样式
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            // 显示4个数字框
            for (i in 0 until 4) {
                val isActive = i < pickupCode.length
                val boxColor = if (isActive) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(
                            elevation = if (isActive) 6.dp else 2.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(boxColor)
                        .border(
                            width = 2.dp,
                            color = if (isActive) MaterialTheme.colorScheme.primary 
                                   else Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    if (isActive) {
                        Text(
                            text = pickupCode[i].toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 数字键盘
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 数字键盘行 1-3
            for (row in 0 until 3) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (col in 1..3) {
                        val number = row * 3 + col
                        NumberKey(
                            number = number.toString(),
                            onClick = {
                                if (pickupCode.length < 4) {
                                    pickupCode += number.toString()
                                }
                            }
                        )
                    }
                }
            }
            
            // 最后一行：清除、0、删除
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 清除按钮
                NumberKey(
                    number = "清除",
                    onClick = { pickupCode = "" }
                )
                
                // 数字0
                NumberKey(
                    number = "0",
                    onClick = {
                        if (pickupCode.length < 4) {
                            pickupCode += "0"
                        }
                    }
                )
                
                // 删除按钮
                NumberKey(
                    number = "删除",
                    onClick = {
                        if (pickupCode.isNotEmpty()) {
                            pickupCode = pickupCode.dropLast(1)
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 删除TCP客户端页面导航按钮（已移至右上角）
        // 删除状态消息（根据需求）
        
        // 确认按钮 - 放大并美化
        Button(
            onClick = {
                if (pickupCode.length == 4 && isConnected) {
                    // 发送取件码到服务器
                    tcpConnectionManager.sendMessage("PICKUP:$pickupCode")
                }
            },
            enabled = pickupCode.length == 4 && isConnected,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp), // 增加高度
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp), // 更圆润的按钮
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp, // 添加阴影
                pressedElevation = 8.dp
            )
        ) {
            Text(
                "确认",
                fontSize = 20.sp, // 更大的字体
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 数字键盘按键
 */
@Composable
fun NumberKey(
    number: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 为数字键和功能键使用不同的颜色
    val (backgroundColor, textColor) = when {
        number == "清除" -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        number == "删除" -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        else -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(76.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(38.dp))
            .clip(RoundedCornerShape(38.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
    ) {
        Text(
            text = number,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}