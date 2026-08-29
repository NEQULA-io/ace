package com.ace.app.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ace.app.download.DownloadState
import com.ace.app.download.ModelDownloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ModelDownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val downloader = ModelDownloader()

    private fun modelFile(): File =
        File(getApplication<Application>().filesDir, "gemma-3n-e4b.gguf")

    // Seed progress at 1f if the file is already on disk (dev convenience / resumed installs)
    private val _downloadProgress = MutableStateFlow(if (modelFile().exists()) 1f else 0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress

    private val _isModelReady = MutableStateFlow(modelFile().exists())
    val isModelReady: StateFlow<Boolean> = _isModelReady

    fun startDownload() {
        viewModelScope.launch {
            val destFile = modelFile()
            downloader.downloadModel(destFile).collect { state ->
                when (state) {
                    is DownloadState.Progress -> {
                        _downloadProgress.value = state.bytesDownloaded.toFloat() / state.totalBytes
                    }
                    is DownloadState.Success -> {
                        _downloadProgress.value = 1f
                        _isModelReady.value = true
                    }
                    is DownloadState.Error -> {
                        // TODO: expose via a separate error StateFlow if needed
                    }
                }
            }
        }
    }
}