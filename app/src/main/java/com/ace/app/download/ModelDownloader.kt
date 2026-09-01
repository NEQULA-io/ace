package com.ace.app.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

sealed class DownloadState {
    data class Progress(val bytesDownloaded: Long, val totalBytes: Long) : DownloadState()
    data class Success(val file: File) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

class ModelDownloader {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val fileId = "1Y2F6RNFAJs6SovHZGvZe7fXf-EdLiiRH"
    private val url = "https://drive.usercontent.google.com/download?id=$fileId&export=download&confirm=t"

    fun downloadModel(destinationFile: File): Flow<DownloadState> = flow {
        val existingBytes = if (destinationFile.exists()) destinationFile.length() else 0L

        val requestBuilder = Request.Builder().url(url)
        if (existingBytes > 0) {
            requestBuilder.addHeader("Range", "bytes=$existingBytes-")
        }

        try {
            client.newCall(requestBuilder.build()).execute().use { response ->
                if (!response.isSuccessful && response.code != 206) {
                    emit(DownloadState.Error("Server error: ${response.code}"))
                    return@flow
                }

                // Google Drive returns an HTML error/quota page instead of the
                // actual file when the download link is rate-limited. Detect
                // that here before writing anything to disk.
                val contentType = response.header("Content-Type") ?: ""
                if (contentType.contains("text/html", ignoreCase = true)) {
                    emit(DownloadState.Error("Google Drive quota exceeded or returned an error page. Try again later."))
                    return@flow
                }

                val contentLength = response.body?.contentLength() ?: -1L
                val totalBytes = if (existingBytes > 0) existingBytes + contentLength else contentLength

                val raf = RandomAccessFile(destinationFile, "rw")
                raf.seek(existingBytes)

                var bytesDownloaded = existingBytes
                response.body?.byteStream()?.use { input ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        raf.write(buffer, 0, bytesRead)
                        bytesDownloaded += bytesRead
                        emit(DownloadState.Progress(bytesDownloaded, totalBytes))
                    }
                }
                raf.close()

                emit(DownloadState.Success(destinationFile))
            }
        } catch (e: Exception) {
            emit(DownloadState.Error(e.message ?: "Download failed"))
        }
    }.flowOn(Dispatchers.IO)
}