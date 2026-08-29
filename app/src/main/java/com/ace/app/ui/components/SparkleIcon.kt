package com.ace.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill

/**
 * 4-point star sparkle — small decorative element.
 * Draws a classic 4-pointed star with glow layers.
 */
@Composable
fun SparkleIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE8E4FF),
    glowColor: Color = Color(0x607C3AED)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val outer = minOf(w, h) / 2f
        val inner = outer * 0.2f

        // Draw glow bloom behind
        drawCircle(color = glowColor, radius = outer * 1.4f, center = Offset(cx, cy))

        // 4-point star path
        val path = Path().apply {
            val angles = floatArrayOf(270f, 0f, 90f, 180f)
            angles.forEachIndexed { i, angle ->
                val outerAngle = Math.toRadians(angle.toDouble())
                val innerAngle1 = Math.toRadians((angle + 45).toDouble())
                val innerAngle2 = Math.toRadians((angle - 45).toDouble())
                val ox = cx + (outer * Math.cos(outerAngle)).toFloat()
                val oy = cy + (outer * Math.sin(outerAngle)).toFloat()
                val ix1 = cx + (inner * Math.cos(innerAngle1)).toFloat()
                val iy1 = cy + (inner * Math.sin(innerAngle1)).toFloat()
                val ix2 = cx + (inner * Math.cos(innerAngle2)).toFloat()
                val iy2 = cy + (inner * Math.sin(innerAngle2)).toFloat()
                if (i == 0) moveTo(ox, oy) else lineTo(ox, oy)
                lineTo(ix1, iy1)
                lineTo(ox, oy) // back to tip — no, use different approach
            }
        }

        // Simpler 4-point star via line-segments
        val starPath = Path().apply {
            moveTo(cx, cy - outer)
            lineTo(cx + inner, cy - inner)
            lineTo(cx + outer, cy)
            lineTo(cx + inner, cy + inner)
            lineTo(cx, cy + outer)
            lineTo(cx - inner, cy + inner)
            lineTo(cx - outer, cy)
            lineTo(cx - inner, cy - inner)
            close()
        }

        drawPath(starPath, color = color, style = Fill)
    }
}
