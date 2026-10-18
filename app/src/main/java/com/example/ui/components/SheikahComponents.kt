package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CookingEffect
import com.example.ui.theme.*

@Composable
fun SheikahCard(
    modifier: Modifier = Modifier,
    borderColor: Color = SheikahCyan.copy(alpha = 0.45f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SheikahSlateCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            content()
        }
    }
}

@Composable
fun EffectBadge(
    effect: CookingEffect,
    modifier: Modifier = Modifier,
    level: Int = 0
) {
    val bg = Color(effect.colorHex)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, bg)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val levelIndicator = if (level > 0) " Lv.$level" else ""
            Text(
                text = "${effect.displayName}$levelIndicator",
                color = bg,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HeartsDisplay(
    hearts: Float,
    bonusHearts: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val totalWhole = hearts.toInt()
        val hasHalf = (hearts - totalWhole) >= 0.5f

        val displayHearts = minOf(totalWhole, 15)
        for (i in 0 until displayHearts) {
            Text("❤️", fontSize = 14.sp)
        }
        if (hasHalf && displayHearts < 15) {
            Text("💔", fontSize = 14.sp)
        }
        if (totalWhole > 15) {
            Text("+${totalWhole - 15}", color = HeartRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        if (bonusHearts > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            for (i in 0 until minOf(bonusHearts, 5)) {
                Text("💛", fontSize = 14.sp)
            }
            if (bonusHearts > 5) {
                Text("+$bonusHearts", color = ExtraHeartYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StaminaWheelMeter(
    wheels: Float = 3.0f,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val wholeWheels = wheels.toInt()
        val partial = wheels - wholeWheels

        for (i in 0 until minOf(wholeWheels, 3)) {
            SingleWheelCanvas(fillProgress = 1.0f, isExtra = false)
        }
        if (partial > 0f && wholeWheels < 3) {
            SingleWheelCanvas(fillProgress = partial, isExtra = false)
        }
    }
}

@Composable
private fun SingleWheelCanvas(
    fillProgress: Float,
    isExtra: Boolean,
    modifier: Modifier = Modifier.size(24.dp)
) {
    val color = if (isExtra) ExtraStaminaYellow else StaminaGreen
    Canvas(modifier = modifier) {
        // Track circle
        drawArc(
            color = Color(0xFF1E3757),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        // Fill circle
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * fillProgress,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun GlowingPulseBorder(
    color: Color = SheikahCyan,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .border(1.5.dp, color.copy(alpha = alpha), RoundedCornerShape(12.dp))
    ) {
        content()
    }
}
