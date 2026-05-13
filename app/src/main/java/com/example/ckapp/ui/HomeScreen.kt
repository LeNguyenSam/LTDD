package com.example.ckapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {

    var gas         by remember { mutableStateOf(166) }
    var flame       by remember { mutableStateOf(false) }
    var temperature by remember { mutableStateOf(28.5) }
    var humidity    by remember { mutableStateOf(62.0) }
    var pressure    by remember { mutableStateOf(1013.2) }
    var light       by remember { mutableStateOf(340) }
    var uvIndex     by remember { mutableStateOf(3.2) }

    var gasThreshold  by remember { mutableStateOf(300f) }
    var showThreshold by remember { mutableStateOf(false) }

    val status = when {
        flame -> "DANGER"
        gas >= 600 -> "DANGER"
        gas >= gasThreshold.toInt() -> "WARNING"
        else -> "SAFE"
    }

    val alertActive = flame || gas >= gasThreshold.toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Header
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
            Column {
                Text(
                    "IoT Monitor",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPri,
                    fontFamily = FontFamily.Monospace
                )
                Text("Giám sát thời gian thực", fontSize = 13.sp, color = TextMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (alertActive) {
                    Icon(Icons.Default.NotificationsActive, null, tint = AmberWarn, modifier = Modifier.size(22.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(GreenSafe, androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenSafe,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Alert banner
        AnimatedVisibility(visible = alertActive, enter = fadeIn() + slideInVertically()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberWarn.copy(alpha = 0.12f))
                    .border(1.dp, AmberWarn.copy(0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, null, tint = AmberWarn, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (flame) "⚠ Phát hiện lửa! Sơ tán ngay."
                        else "⚠ Khí gas vượt ngưỡng ${gasThreshold.toInt()} ppm",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberWarn
                    )
                }
            }
        }

        // Mini stats
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
            MiniStatCard("🌡", "${temperature}°", "Nhiệt độ", Modifier.weight(1f))
            MiniStatCard("💧", "${humidity}%", "Độ ẩm", Modifier.weight(1f))
            MiniStatCard("☀️", "UV ${uvIndex}", "Chỉ số UV", Modifier.weight(1f))
        }

        SystemStatusCard(status)
        GasCardModern(gas, gasThreshold.toInt())     // Card mới theo ảnh
        FlameCard(flame)
        TempHumidCard(temperature, humidity)
        EnvCard(pressure, light, uvIndex)

        ThresholdControl(gasThreshold, showThreshold, { showThreshold = it }, { gasThreshold = it })

        Spacer(Modifier.height(30.dp))
    }
}

// ==================== GAS CARD MỚI (THEO ẢNH) ====================

@Composable
private fun GasCardModern(gas: Int, threshold: Int) {
    val isDanger = gas >= 600
    val isWarning = gas >= threshold && !isDanger
    val statusColor = when {
        isDanger -> RedDanger
        isWarning -> AmberWarn
        else -> GreenSafe
    }

    val progress = (gas / 1000f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .padding(20.dp)
    ) {
        Column {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, null, tint = Teal, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Cảm biến Khí Gas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPri
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(statusColor, androidx.compose.foundation.shape.CircleShape)
                )
            }

            Text(
                text = "Nồng độ khí gas trong không khí",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Giá trị lớn
            Text(
                text = "$gas ppm",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = TextPri,
                letterSpacing = (-1.5).sp
            )

            Spacer(Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(BgRaised)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(statusColor)
                )
            }

            // Scale labels
            Row(
                Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", fontSize = 11.sp, color = TextMuted)
                Text("300", fontSize = 11.sp, color = if (gas >= 300) AmberWarn else TextMuted)
                Text("600", fontSize = 11.sp, color = if (gas >= 600) RedDanger else TextMuted)
                Text("1000", fontSize = 11.sp, color = TextMuted)
            }

            Spacer(Modifier.height(16.dp))

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = when {
                        isDanger -> "🔴 Nguy hiểm"
                        isWarning -> "⚠ Cảnh báo"
                        else -> "✅ An toàn"
                    },
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ==================== CÁC CARD KHÁC (Giữ nguyên) ====================

@Composable
private fun SystemStatusCard(status: String) {
    val (color, text) = when (status) {
        "DANGER" -> RedDanger to "NGUY HIỂM"
        "WARNING" -> AmberWarn to "CẢNH BÁO"
        else -> GreenSafe to "AN TOÀN"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(2.dp, color.copy(0.3f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Box(Modifier.size(12.dp).background(color, androidx.compose.foundation.shape.CircleShape))
            Spacer(Modifier.width(12.dp))
            Text(text, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun FlameCard(flame: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .padding(20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column {
                Text("Cảm biến Lửa", fontSize = 14.sp, color = TextMuted)
                Text(
                    if (flame) "PHÁT HIỆN" else "KHÔNG",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (flame) RedDanger else GreenSafe
                )
            }
            Text(if (flame) "🔥" else "✓", fontSize = 48.sp)
        }
    }
}

@Composable
private fun TempHumidCard(temperature: Double, humidity: Double) {
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
        Box(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(BgCard).padding(16.dp)) {
            Column {
                Text("Nhiệt độ", color = TextMuted, fontSize = 13.sp)
                Text("${temperature}°C", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPri)
            }
        }
        Box(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(BgCard).padding(16.dp)) {
            Column {
                Text("Độ ẩm", color = TextMuted, fontSize = 13.sp)
                Text("${humidity}%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPri)
            }
        }
    }
}

@Composable
private fun EnvCard(pressure: Double, light: Int, uvIndex: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .padding(20.dp)
    ) {
        Column {
            Text("Môi trường", fontSize = 14.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Column { Text("Áp suất", color = TextMuted, fontSize = 12.sp); Text("${pressure} hPa", fontWeight = FontWeight.Bold, color = TextPri) }
                Column { Text("Ánh sáng", color = TextMuted, fontSize = 12.sp); Text("$light lux", fontWeight = FontWeight.Bold, color = TextPri) }
                Column { Text("UV", color = TextMuted, fontSize = 12.sp); Text("$uvIndex", fontWeight = FontWeight.Bold, color = TextPri) }
            }
        }
    }
}

@Composable
private fun ThresholdControl(
    gasThreshold: Float,
    showThreshold: Boolean,
    onShowChange: (Boolean) -> Unit,
    onValueChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BgStroke, RoundedCornerShape(20.dp))
            .clickable { onShowChange(!showThreshold) }
            .padding(horizontal = 22.dp, vertical = 16.dp)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Ngưỡng cảnh báo Gas", fontSize = 15.sp, color = TextPri)
                Text(if (showThreshold) "▲ Thu gọn" else "▼ Chỉnh sửa", fontSize = 12.sp, color = CyanAcc)
            }
            if (showThreshold) {
                Spacer(Modifier.height(12.dp))
                Text("${gasThreshold.toInt()} ppm", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPri)
                Slider(
                    value = gasThreshold,
                    onValueChange = onValueChange,
                    valueRange = 100f..900f,
                    colors = SliderDefaults.colors(thumbColor = CyanAcc, activeTrackColor = CyanAcc)
                )
            }
        }
    }
}

@Composable
private fun MiniStatCard(emoji: String, value: String, label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, BgStroke, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Column {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPri, fontFamily = FontFamily.Monospace)
            Text(label, fontSize = 9.sp, color = TextMuted)
        }
    }
}