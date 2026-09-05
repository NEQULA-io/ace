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

    // Real Gemma 3N E4B Q4_K_M file is ~4.2GB. Reject anything under 1GB as
    // invalid/corrupt/incomplete (e.g. an HTML error page saved by mistake).
    private val minimumValidBytes = 1_000_000_000L

    private fun modelFile(): File =
    File(getApplication<Application>().getExternalMediaDirs()[0], "gemma-3n-e4b.gguf")

    private fun isValidModelFile(file: File): Boolean =
        file.exists() && file.length() >= minimumValidBytes

    private val _downloadProgress = MutableStateFlow(if (isValidModelFile(modelFile())) 1f else 0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress

    private val _isModelReady = MutableStateFlow(isValidModelFile(modelFile()))
    val isModelReady: StateFlow<Boolean> = _isModelReady

    private val _downloadError = MutableStateFlow<String?>(null)
    val downloadError: StateFlow<String?> = _downloadError

    fun startDownload() {
        _downloadError.value = null

        viewModelScope.launch {
            val destFile = modelFile()
            downloader.downloadModel(destFile).collect { state ->
                when (state) {
                    is DownloadState.Progress -> {
                        _downloadProgress.value = state.bytesDownloaded.toFloat() / state.totalBytes
                    }
                    is DownloadState.Success -> {
                        if (isValidModelFile(destFile)) {
                            _downloadProgress.value = 1f
                            _isModelReady.value = true
                        } else {
                            destFile.delete()
                            _downloadProgress.value = 0f
                            _isModelReady.value = false
                            _downloadError.value = "Downloaded file was invalid. Please try again."
                        }
                    }
                    is DownloadState.Error -> {
                        _downloadProgress.value = 0f
                        _downloadError.value = state.message
                    }
                }
            }
        }
    }
}