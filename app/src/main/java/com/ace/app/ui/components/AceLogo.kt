package com.ace.app.ui.components

import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ace.app.R

@Composable
fun AceLogo(
    modifier: Modifier = Modifier,
    size: Int = 80
) {
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
        painter = painterResource(id = R.drawable.ace_logo),
        contentDescription = "ACE Logo",
        modifier = Modifier
            .size(size.dp)
            .scale(1.80f)
        )
    }
}