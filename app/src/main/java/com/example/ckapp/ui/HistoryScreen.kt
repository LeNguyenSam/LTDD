package com.example.ckapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ckapp.model.SensorData

@Composable
fun HistoryScreen() {

    val history = listOf(
        SensorData(58,  false, 21.0, 55.0, 1012.0, 280,  1.2, "9:00"),
        SensorData(56,  false, 25.1, 60.0, 1013.0, 340,  3.2, "9:05"),
        SensorData(120, false, 27.3, 58.0, 1012.5, 400,  4.1, "9:10"),
        SensorData(443, false, 31.5, 52.0, 1011.8, 520,  5.8, "9:15"),
        SensorData(690, true,  35.0, 48.0, 1010.2, 600,  7.5, "9:20")
    )

    // Summary stats
    val avgGas     = history.map { it.gas }.average()
    val maxGas     = history.maxOf { it.gas }
    val avgTemp    = history.map { it.temperature }.average()
    val dangerCount = history.count { it.flame || it.gas >= 600 }
    val warnCount  = history.count { !it.flame && it.gas in 300..599 }

    LazyColumn(
        modifier            = Modifier.fillMaxSize().background(BgPage),
        contentPadding      = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // Header
        item {
            Text("Lịch sử", fontSize = 30.sp, fontWeight = FontWeight.Black,
                color = TextPri, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(2.dp))
            Text("${history.size} bản ghi · hôm nay", fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(14.dp))
        }

        // Summary stats row
        item {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                SummaryChip("TB Gas",   "${avgGas.toInt()} ppm", CyanAcc,   Modifier.weight(1f))
                SummaryChip("Max Gas",  "$maxGas ppm",           AmberWarn, Modifier.weight(1f))
                SummaryChip("TB Temp",  "${"%.1f".format(avgTemp)}°C", GreenSafe, Modifier.weight(1f))
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                SummaryChip("⚠ Cảnh báo", "$warnCount lần",   AmberWarn, Modifier.weight(1f))
                SummaryChip("🔴 Nguy hiểm","$dangerCount lần", RedDanger, Modifier.weight(1f))
                SummaryChip("✅ An toàn", "${history.size - warnCount - dangerCount} lần", GreenSafe, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }

        // Section title
        item {
            Text("CHI TIẾT BẢN GHI", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                color = TextMuted, letterSpacing = 1.5.sp, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(4.dp))
        }

        items(items = history) { data ->
            HistoryRow(data)
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column {
            Text(label, fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = color, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun HistoryRow(data: SensorData) {

    val dotColor = when {
        data.flame      -> RedDanger
        data.gas >= 600 -> RedDanger
        data.gas >= 300 -> AmberWarn
        else            -> GreenSafe
    }
    val badgeLabel = when {
        data.flame      -> "NGUY HIỂM"
        data.gas >= 600 -> "NGUY HIỂM"
        data.gas >= 300 -> "CẢNH BÁO"
        else            -> "AN TOÀN"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .border(1.dp, BgStroke, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            // Top row: time + badge
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.width(3.dp).height(36.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(dotColor.copy(alpha = 0.8f))
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(data.time, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = TextPri, fontFamily = FontFamily.Monospace)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp))
                        .background(dotColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(badgeLabel, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold,
                        color = dotColor, letterSpacing = 0.5.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = BgStroke, thickness = 1.dp)
            Spacer(Modifier.height(10.dp))

            // Metrics grid — 3 per row
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                HistMetric("GAS",  "${data.gas}",         "ppm", if (data.gas >= 300) dotColor else TextPri)
                HistMetric("TEMP", "${data.temperature}", "°C",  TextPri)
                HistMetric("ẨM",   "${data.humidity}",    "%",   CyanAcc)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                HistMetric("ÁP",   "${data.pressure}",   "hPa", PurpleAcc)
                HistMetric("SÁNG", "${data.light}",       "lux", OrangeAcc)
                HistMetric("UV",   "${data.uvIndex}",     "UV",  if (data.uvIndex >= 6) AmberWarn else GreenSafe)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (data.flame) RedDanger.copy(0.1f) else GreenSafe.copy(0.07f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("LỬA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted,
                    fontFamily = FontFamily.Monospace)
                Text(
                    text       = if (data.flame) "PHÁT HIỆN 🔥" else "KHÔNG PHÁT HIỆN ✓",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (data.flame) RedDanger else GreenSafe,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun HistMetric(label: String, value: String, unit: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextMuted,
            letterSpacing = 0.8.sp, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                color = valueColor, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.width(2.dp))
            Text(unit, fontSize = 9.sp, color = TextMuted, modifier = Modifier.padding(bottom = 2.dp))
        }
    }
}