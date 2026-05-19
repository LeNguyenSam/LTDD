package com.example.ckapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── BỘ MÀU CHUNG ─────────────────────────────────────────────────────────────
val BgDark      = Color(0xFF0A0E17)
val BgPage      = Color(0xFF0A0E17)           // Background chính
val BgCard      = Color(0xFF111827)           // Card background
val BgRaised    = Color(0xFF1F2937)
val BgStroke    = Color(0xFF1E2A3A)           // Border

val Teal        = Color(0xFF00D4AA)
val TealDim     = Color(0xFF00A885)
val TextPri     = Color(0xFFE8EDF5)
val TextSec     = Color(0xFF8A96A8)
val TextMuted   = Color(0xFF64748B)

val GreenSafe   = Color(0xFF00C853)
val CyanAcc     = Color(0xFF00E5FF)
val AmberWarn   = Color(0xFFFFC107)
val RedDanger   = Color(0xFFFF5C5C)

val PurpleAcc   = Color(0xFFBA68C8)
val OrangeAcc   = Color(0xFFFF9800)

val BorderColor = Color(0xFF1E2A3A)

// Component Gauge dùng chung
@Composable
fun ArcGauge(
    fraction: Float,
    label: String,
    value: String,
    color: Color,
    size: androidx.compose.ui.unit.Dp = 100.dp
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = 7.dp.toPx()
            val pad = sw / 2
            val arcSize = Size(this.size.width - sw, this.size.height - sw)

            drawArc(
                color = BgRaised,
                startAngle = 150f, sweepAngle = 240f, useCenter = false,
                style = Stroke(sw, cap = StrokeCap.Round),
                topLeft = Offset(pad, pad), size = arcSize
            )
            if (fraction > 0f) {
                drawArc(
                    color = color,
                    startAngle = 150f, sweepAngle = 240f * fraction, useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round),
                    topLeft = Offset(pad, pad), size = arcSize
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPri)
            Text(text = label, fontSize = 10.sp, color = TextSec)
        }
    }
}