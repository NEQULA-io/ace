package com.ace.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.ace.app.ui.theme.*

@Composable
fun AceBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AceBackgroundDark)
    ) {
        // Atmospheric purple glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            /*
            // Central purple glow bloom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AceGlowPurple.copy(alpha = 0.15f),
                        AceGlowPurple.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.25f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.25f),
                radius = size.width * 0.7f
            )
            
            // Secondary glow near middle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AceGlowPurple.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.45f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.45f),
                radius = size.width * 0.5f
            )
            */

            // Draw constellation network
            drawConstellationNetwork(this)
            
            // Draw particles
            drawParticles(this)
        }
        
        content()
    }
}

private fun drawConstellationNetwork(drawScope: DrawScope) {
    with(drawScope) {
        val nodes = listOf(
            // Cluster 1 - upper left, 4 stars, angular
            Offset(size.width * 0.18f, size.height * 0.06f),
            Offset(size.width * 0.26f, size.height * 0.10f),
            Offset(size.width * 0.23f, size.height * 0.16f),
            Offset(size.width * 0.30f, size.height * 0.20f),

            // Cluster 2 - upper right, 5 stars, bigger spread
            Offset(size.width * 0.72f, size.height * 0.05f),
            Offset(size.width * 0.80f, size.height * 0.09f),
            Offset(size.width * 0.86f, size.height * 0.07f),
            Offset(size.width * 0.83f, size.height * 0.14f),
            Offset(size.width * 0.90f, size.height * 0.18f),

            // Cluster 3 - left side, mid height, only 3 stars, tight
            Offset(size.width * 0.06f, size.height * 0.42f),
            Offset(size.width * 0.11f, size.height * 0.46f),
            Offset(size.width * 0.09f, size.height * 0.52f),

            // Cluster 4 - far right, single line of 3, offset lower than cluster 3
            Offset(size.width * 0.90f, size.height * 0.55f),
            Offset(size.width * 0.85f, size.height * 0.60f),
            Offset(size.width * 0.92f, size.height * 0.64f),

            // Cluster 5 - bottom left corner, small, below button row
            Offset(size.width * 0.05f, size.height * 0.90f),
            Offset(size.width * 0.10f, size.height * 0.94f),

            // Cluster 6 - bottom right corner, small, asymmetric count vs cluster 5
            Offset(size.width * 0.93f, size.height * 0.88f),
            Offset(size.width * 0.88f, size.height * 0.92f),
            Offset(size.width * 0.95f, size.height * 0.95f)
        )

        val connections = listOf(
            // Cluster 1
            0 to 1, 1 to 2, 2 to 3,
            // Cluster 2
            4 to 5, 5 to 6, 5 to 7, 7 to 8,
            // Cluster 3
            9 to 10, 10 to 11,
            // Cluster 4
            12 to 13, 13 to 14,
            // Cluster 5
            15 to 16,
            // Cluster 6
            17 to 18, 18 to 19
        )
        
        // Draw connection lines
        connections.forEach { (startIdx, endIdx) ->
            if (startIdx < nodes.size && endIdx < nodes.size) {
                val start = nodes[startIdx]
                val end = nodes[endIdx]
                
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AceNetworkLine.copy(alpha = 0.7f),
                            AceNetworkLine.copy(alpha = 0.5f),
                            AceNetworkLine.copy(alpha = 0.7f)
                        ),
                        start = start,
                        end = end
                    ),
                    start = start,
                    end = end,
                    strokeWidth = 1.5f
                )
            }
        }
        
        // Draw nodes
        nodes.forEach { node ->
            // Outer glow — bigger radius, soft radial gradient like particles
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    radius = 12f
                ),
                radius = 12f,
                center = node
            )

            // Node point — bright and bigger
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = node
            )
        }
    }
}

private fun drawParticles(drawScope: DrawScope) {
    with(drawScope) {
        val particles = listOf(
            // Scattered around screen
            Triple(size.width * 0.25f, size.height * 0.30f, 1.5f),
            Triple(size.width * 0.65f, size.height * 0.35f, 1.2f),
            Triple(size.width * 0.40f, size.height * 0.55f, 1.0f),
            Triple(size.width * 0.72f, size.height * 0.62f, 1.3f),
            Triple(size.width * 0.30f, size.height * 0.70f, 1.1f),
            Triple(size.width * 0.88f, size.height * 0.45f, 1.4f),
            Triple(size.width * 0.15f, size.height * 0.25f, 1.0f),
            Triple(size.width * 0.55f, size.height * 0.20f, 1.2f)
        )
        
        particles.forEach { (x, y, size) ->
            // Soft glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AceParticleWhite.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    radius = size * 3f
                ),
                center = Offset(x, y),
                radius = size * 3f
            )
            
            // Particle dot
            drawCircle(
                color = AceParticleWhite.copy(alpha = 0.6f),
                radius = size,
                center = Offset(x, y)
            )
        }
    }
}
