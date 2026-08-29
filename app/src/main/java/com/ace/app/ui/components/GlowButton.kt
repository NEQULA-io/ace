package com.ace.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ace.app.ui.theme.*

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    glowIntensity: Float = 0.6f,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(56.dp)
    ) {
        // Outer glow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF9ACB).copy(alpha = glowIntensity * 0.35f),
                            Color(0xFFD9E9FF).copy(alpha = glowIntensity * 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        )
        
        // Middle glow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .blur(8.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AceGlowPurple.copy(alpha = glowIntensity * 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
        )
        
        // Button surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF9ACB).copy(alpha = 0.85f),
                            Color(0xFFFFD6EA).copy(alpha = 0.95f),
                            Color(0xFFD9E9FF).copy(alpha = 0.85f),
                            Color(0xFFFF9ACB).copy(alpha = 0.75f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AceSurfaceDark.copy(alpha = 0.9f),
                            AceSurfaceDark.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .clip(RoundedCornerShape(28.dp))
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = AcePurple)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = AceTextWhite
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = AceTextWhite
                )
            }
        }
    }
}

@Composable
fun CircularAuthOption(
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(72.dp)
        ) {
            // Glow layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(12.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF9ACB).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(36.dp)
                    )
            )
            
            // Circle surface
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF9ACB).copy(alpha = 0.75f),
                                Color(0xFFFFD6EA).copy(alpha = 0.85f),
                                Color(0xFFD9E9FF).copy(alpha = 0.7f),
                                Color(0xFFFF9ACB).copy(alpha = 0.65f)
                            )
                    ),
                        shape = RoundedCornerShape(36.dp)
                    )
                    .background(
                        color = AceSurfaceDark.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(36.dp)
                    )
                    .clip(RoundedCornerShape(36.dp))
                    .clickable(
                        onClick = onClick,
                        enabled = enabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = AcePurple)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    modifier = Modifier.size(32.dp),
                    tint = AceLavender
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = AceTextGray
        )
    }
}
