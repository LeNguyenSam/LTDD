package com.example.ckapp.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ─────────────────────────────────────────────────────────────
val BgPage    = Color(0xFF0A0E17)
val BgCard    = Color(0xFF111827)
val BgRaised  = Color(0xFF1C2333)
val BgStroke  = Color(0xFF1E2D40)
val CyanAcc   = Color(0xFF00D4FF)
val GreenSafe = Color(0xFF00E5A0)
val AmberWarn = Color(0xFFFFB020)
val RedDanger = Color(0xFFFF4040)
val PurpleAcc = Color(0xFFB388FF)
val OrangeAcc = Color(0xFFFF8C42)
val TextPri   = Color(0xFFF0F4FF)
val TextMuted = Color(0xFF5A6A80)

// ── Shared helpers ────────────────────────────────────────────────────────────

fun Modifier.topGlow(color: Color, alpha: Float = 0.55f): Modifier =
    this.drawBehind {
        drawLine(
            color       = color.copy(alpha = alpha),
            start       = Offset(20f, 0f),
            end         = Offset(size.width - 20f, 0f),
            strokeWidth = 1.5f,
            cap         = StrokeCap.Round
        )
    }

@Composable
fun ChipLabel(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Bold,
        letterSpacing = 1.4.sp,
        color         = TextMuted,
        fontFamily    = FontFamily.Monospace
    )
}

// ── SystemStatusCard ──────────────────────────────────────────────────────────

@Composable
fun SystemStatusCard(status: String) {

    val accent by animateColorAsState(
        targetValue   = when (status) {
            "WARNING" -> AmberWarn
            "DANGER"  -> RedDanger
            else      -> GreenSafe
        },
        animationSpec = tween(600),
        label         = "accent"
    )
    val breathe = rememberInfiniteTransition(label = "breathe")
    val scale by breathe.animateFloat(
        initialValue  = 0.82f,
        targetValue   = 1.18f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseInOut), RepeatMode.Reverse),
        label         = "sc"
    )
    val alpha by breathe.animateFloat(
        initialValue  = 0.25f,
        targetValue   = 0.85f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseInOut), RepeatMode.Reverse),
        label         = "al"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(accent.copy(alpha = 0.13f), BgCard)))
            .border(1.dp, accent.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
            .topGlow(accent)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                ChipLabel("trạng thái hệ thống")
                Spacer(Modifier.height(8.dp))
                Text(
                    text          = when (status) {
                        "WARNING" -> "CẢNH BÁO"
                        "DANGER"  -> "NGUY HIỂM"
                        else      -> "AN TOÀN"
                    },
                    fontSize      = 28.sp,
                    fontWeight    = FontWeight.Black,
                    color         = accent,
                    fontFamily    = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = when (status) {
                        "WARNING" -> "Nồng độ khí ở mức cao"
                        "DANGER"  -> "Yêu cầu xử lý ngay!"
                        else      -> "Mọi thông số bình thường"
                    },
                    fontSize = 12.sp,
                    color    = TextMuted
                )
            }
            Box(
                modifier        = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size((64 * scale).dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = alpha * 0.13f))
                )
                Box(
                    Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(accent, accent.copy(alpha = 0.45f))))
                )
            }
        }
    }
}

// ── GasCard ───────────────────────────────────────────────────────────────────

@Composable
fun GasCard(gas: Int) {

    val fraction = (gas / 1000f).coerceIn(0f, 1f)
    val trackColor by animateColorAsState(
        targetValue   = when {
            gas < 300 -> GreenSafe
            gas < 600 -> AmberWarn
            else      -> RedDanger
        },
        animationSpec = tween(600),
        label         = "gc"
    )
    val animFrac by animateFloatAsState(
        targetValue   = fraction,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "gf"
    )
    val badge = when {
        gas < 300 -> "BÌNH THƯỜNG"
        gas < 600 -> "CAO"
        else      -> "NGUY HIỂM"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BgStroke, RoundedCornerShape(20.dp))
            .topGlow(trackColor, 0.45f)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                ChipLabel("cảm biến khí gas")
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text       = "$gas",
                        fontSize   = 44.sp,
                        fontWeight = FontWeight.Black,
                        color      = TextPri,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 44.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text     = "ppm",
                        fontSize = 14.sp,
                        color    = TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BgRaised)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(animFrac)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Brush.horizontalGradient(listOf(GreenSafe, trackColor)))
                    )
                }
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("0", "300", "600", "1000").forEach {
                        Text(it, fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                    }
                }
            }
            Spacer(Modifier.width(18.dp))
            ArcGauge(fraction = animFrac, color = trackColor, size = 80.dp, label = badge)
        }
    }
}

// ── FlameCard ─────────────────────────────────────────────────────────────────

@Composable
fun FlameCard(flame: Boolean) {
    val accent = if (flame) RedDanger else GreenSafe
    val blink  = rememberInfiniteTransition(label = "blink")
    val blinkA by blink.animateFloat(
        initialValue  = 1f,
        targetValue   = if (flame) 0.08f else 1f,
        animationSpec = infiniteRepeatable(
            tween(if (flame) 480 else 2000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "ba"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (flame) Brush.linearGradient(listOf(RedDanger.copy(alpha = 0.14f), BgCard))
                else       Brush.linearGradient(listOf(BgCard, BgCard))
            )
            .border(
                width = 1.dp,
                color = accent.copy(alpha = if (flame) 0.38f else 0.18f),
                shape = RoundedCornerShape(20.dp)
            )
            .topGlow(accent, if (flame) blinkA * 0.7f else 0.35f)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier        = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = if (flame) blinkA * 0.16f else 0.09f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (flame) Icons.Default.LocalFireDepartment
                    else        Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint               = accent.copy(alpha = if (flame) blinkA else 0.85f),
                    modifier           = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                ChipLabel("cảm biến lửa")
                Spacer(Modifier.height(6.dp))
                Text(
                    text       = if (flame) "PHÁT HIỆN LỬA" else "An toàn",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = accent,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text     = if (flame) "Cần xử lý ngay lập tức!" else "Không phát hiện lửa",
                    fontSize = 12.sp,
                    color    = TextMuted
                )
            }
            if (flame) {
                Box(
                    Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(RedDanger.copy(alpha = blinkA))
                )
            }
        }
    }
}

// ── TempHumidCard ─────────────────────────────────────────────────────────────

@Composable
fun TempHumidCard(temperature: Double, humidity: Double) {

    val tempColor = when {
        temperature < 20 -> CyanAcc
        temperature < 35 -> GreenSafe
        temperature < 40 -> AmberWarn
        else             -> RedDanger
    }
    val humidColor = when {
        humidity < 30 -> AmberWarn
        humidity < 70 -> GreenSafe
        else          -> CyanAcc
    }
    val animTemp by animateFloatAsState(
        targetValue   = (temperature / 60.0).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(900),
        label         = "t"
    )
    val animHumid by animateFloatAsState(
        targetValue   = (humidity / 100.0).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(900),
        label         = "h"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BgStroke, RoundedCornerShape(20.dp))
            .topGlow(tempColor, 0.4f)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Column {
            ChipLabel("nhiệt độ & độ ẩm")
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Temperature
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text       = "$temperature",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Black,
                            color      = tempColor,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 32.sp
                        )
                        Text(
                            text     = "°C",
                            fontSize = 14.sp,
                            color    = TextMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    MiniBar(fraction = animTemp, color = tempColor)
                    Spacer(Modifier.height(4.dp))
                    Text("Nhiệt độ", fontSize = 10.sp, color = TextMuted)
                }

                Box(
                    Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(BgStroke)
                        .align(Alignment.CenterVertically)
                )

                // Humidity
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text       = "$humidity",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Black,
                            color      = humidColor,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 32.sp
                        )
                        Text(
                            text     = "%",
                            fontSize = 14.sp,
                            color    = TextMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    MiniBar(fraction = animHumid, color = humidColor)
                    Spacer(Modifier.height(4.dp))
                    Text("Độ ẩm", fontSize = 10.sp, color = TextMuted)
                }
            }
        }
    }
}

// ── EnvCard ───────────────────────────────────────────────────────────────────

@Composable
fun EnvCard(pressure: Double, light: Int, uvIndex: Double) {

    val uvColor = when {
        uvIndex < 3 -> GreenSafe
        uvIndex < 6 -> AmberWarn
        uvIndex < 8 -> OrangeAcc
        else        -> RedDanger
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BgStroke, RoundedCornerShape(20.dp))
            .topGlow(PurpleAcc, 0.35f)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Column {
            ChipLabel("môi trường")
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EnvMetric(label = "Áp suất",  value = "$pressure", unit = "hPa", color = PurpleAcc)
                EnvMetric(label = "Ánh sáng", value = "$light",    unit = "lux", color = OrangeAcc)
                EnvMetric(label = "Chỉ số UV",value = "$uvIndex",  unit = "UV",  color = uvColor)
            }
        }
    }
}

@Composable
private fun EnvMetric(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f))
                .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = value,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Black,
                    color      = color,
                    fontFamily = FontFamily.Monospace
                )
                Text(unit, fontSize = 7.sp, color = TextMuted)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

// ── Shared sub-components ─────────────────────────────────────────────────────

@Composable
fun MiniBar(fraction: Float, color: Color) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(BgRaised)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
    }
}

@Composable
fun ArcGauge(fraction: Float, color: Color, size: Dp, label: String) {
    Box(
        modifier        = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(size)) {
            val sw  = 7.dp.toPx()
            val pad = sw / 2
            val arc = Size(this.size.width - sw, this.size.height - sw)
            // Track
            drawArc(
                color       = BgRaised,
                startAngle  = 150f,
                sweepAngle  = 240f,
                useCenter   = false,
                style       = Stroke(sw, cap = StrokeCap.Round),
                topLeft     = Offset(pad, pad),
                size        = arc
            )
            // Value
            if (fraction > 0f) {
                drawArc(
                    brush      = Brush.sweepGradient(
                        colors = listOf(GreenSafe.copy(alpha = 0.6f), color),
                        center = Offset(this.size.width / 2f, this.size.height / 2f)
                    ),
                    startAngle = 150f,
                    sweepAngle = 240f * fraction,
                    useCenter  = false,
                    style      = Stroke(sw, cap = StrokeCap.Round),
                    topLeft    = Offset(pad, pad),
                    size       = arc
                )
            }
        }
        Text(
            text          = label,
            fontSize      = 7.sp,
            fontWeight    = FontWeight.Bold,
            color         = color,
            letterSpacing = 0.2.sp,
            fontFamily    = FontFamily.Monospace
        )
    }
}