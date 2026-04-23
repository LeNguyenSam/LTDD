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

    // ── Mock sensor state (replace with ViewModel/MQTT later) ─────────────────
    var gas         by remember { mutableStateOf(166) }
    var flame       by remember { mutableStateOf(false) }
    var temperature by remember { mutableStateOf(28.5) }
    var humidity    by remember { mutableStateOf(62.0) }
    var pressure    by remember { mutableStateOf(1013.2) }
    var light       by remember { mutableStateOf(340) }
    var uvIndex     by remember { mutableStateOf(3.2) }

    // Alert threshold state
    var gasThreshold  by remember { mutableStateOf(300f) }
    var showThreshold by remember { mutableStateOf(false) }

    val status = when {
        flame         -> "DANGER"
        gas >= gasThreshold.toInt() && gas < 600 -> "WARNING"
        gas >= 600    -> "DANGER"
        else          -> "SAFE"
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

        // ── Header ────────────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
            Column {
                Text("IoT Monitor", fontSize = 30.sp, fontWeight = FontWeight.Black,
                    color = TextPri, fontFamily = FontFamily.Monospace)
                Text("Giám sát thời gian thực", fontSize = 13.sp, color = TextMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Alert bell
                if (alertActive) {
                    Icon(Icons.Default.NotificationsActive, null,
                        tint = AmberWarn, modifier = Modifier.size(22.dp))
                }
                // Live dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(GreenSafe,
                        androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(5.dp))
                    Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = GreenSafe, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                }
            }
        }

        // ── Alert banner ──────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = alertActive,
            enter   = fadeIn() + slideInVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberWarn.copy(alpha = 0.12f))
                    .border(1.dp, AmberWarn.copy(0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, null,
                        tint = AmberWarn, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text       = if (flame) "⚠ Phát hiện lửa! Sơ tán ngay." else "⚠ Khí gas vượt ngưỡng ${gasThreshold.toInt()} ppm",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = AmberWarn
                    )
                }
            }
        }

        // ── Mini stats row ────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
            MiniStatCard("🌡", "${temperature}°", "Nhiệt độ", Modifier.weight(1f))
            MiniStatCard("💧", "${humidity}%",    "Độ ẩm",    Modifier.weight(1f))
            MiniStatCard("☀️", "UV ${uvIndex}",   "Chỉ số UV", Modifier.weight(1f))
        }

        // ── Main cards ────────────────────────────────────────────────────────
        SystemStatusCard(status)
        GasCard(gas)
        FlameCard(flame)
        TempHumidCard(temperature, humidity)
        EnvCard(pressure, light, uvIndex)

        // ── Alert threshold control ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BgCard)
                .border(1.dp, BgStroke, RoundedCornerShape(20.dp))
                .clickable { showThreshold = !showThreshold }
                .padding(horizontal = 22.dp, vertical = 16.dp)
        ) {
            Column {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    ChipLabel("ngưỡng cảnh báo gas")
                    Text(
                        text     = if (showThreshold) "▲ Thu gọn" else "▼ Chỉnh sửa",
                        fontSize = 11.sp, color = CyanAcc
                    )
                }
                if (showThreshold) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text       = "Ngưỡng: ${gasThreshold.toInt()} ppm",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPri,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value         = gasThreshold,
                        onValueChange = { gasThreshold = it },
                        valueRange    = 100f..900f,
                        steps         = 15,
                        colors        = SliderDefaults.colors(
                            thumbColor        = CyanAcc,
                            activeTrackColor  = CyanAcc,
                            inactiveTrackColor = BgRaised
                        )
                    )
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("100", fontSize = 9.sp, color = TextMuted)
                        Text("900", fontSize = 9.sp, color = TextMuted)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
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
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = TextPri, fontFamily = FontFamily.Monospace)
            Text(label, fontSize = 9.sp, color = TextMuted)
        }
    }
}