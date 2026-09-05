package com.ace.app.ui.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ace.app.ui.components.AceBackground
import com.ace.app.ui.components.GlowButton
import com.ace.app.ui.theme.AceTextWhite

@Composable
fun ModelDownloadScreen(
    onSignOutClick: () -> Unit,
    viewModel: ModelDownloadViewModel = viewModel()
) {
    val progress by viewModel.downloadProgress.collectAsState()
    val error by viewModel.downloadError.collectAsState()
    val isDownloading = progress > 0f && progress < 1f

    AceBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ACE LOCAL LLM\nDOWNLOAD REQUIRED",
                    style = MaterialTheme.typography.titleLarge,
                    color = AceTextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (isDownloading) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AceTextWhite,
                        textAlign = TextAlign.Center
                    )
                } else {
                    GlowButton(
                        text = if (progress >= 1f) "DOWNLOADED" else "DOWNLOAD NOW",
                        onClick = {
                            if (progress < 1f) {
                                viewModel.startDownload()
                            }
                        },
                        glowIntensity = 0.55f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color(0xFFFF6B6B),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.labelMedium,
                    color = AceTextWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { onSignOutClick() }
                )
            }
        }
    }
}