package com.ace.app.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

import com.ace.app.R


// ============================================================
// COLORS
// ============================================================

private val AceBackground = Color(0xFF03020A)
private val AceInputBackground = Color(0xFF0B0815)
private val AcePurple = Color(0xFF8C52FF)
private val AceLightPurple = Color(0xFFB99AFF)
private val AceWhite = Color(0xFFF8F7FF)
private val AceGray = Color(0xFF9C98B0)


// ============================================================
// CHAT MESSAGE
// ============================================================

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)


// ============================================================
// HOME SCREEN
// ============================================================

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    // --------------------------------------------------------
    // TEXT INPUT
    // --------------------------------------------------------

    var inputText by remember {
        mutableStateOf("")
    }

    // --------------------------------------------------------
    // CHAT MESSAGES
    // --------------------------------------------------------

    var messages by remember {
        mutableStateOf(emptyList<ChatMessage>())
    }

    // --------------------------------------------------------
    // WORK MODE
    // --------------------------------------------------------

    var workModeEnabled by remember {
        mutableStateOf(false)
    }

    var showWorkModeDialog by remember {
        mutableStateOf(false)
    }

    // --------------------------------------------------------
    // PROFILE
    // --------------------------------------------------------

    var showProfileDialog by remember {
        mutableStateOf(false)
    }

    // --------------------------------------------------------
    // ATTACHMENT MENU
    // --------------------------------------------------------

    var showAttachmentMenu by remember {
        mutableStateOf(false)
    }

    // --------------------------------------------------------
    // MICROPHONE
    // --------------------------------------------------------

    var isListening by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // FILE PICKER
    // ========================================================

    val filePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                Toast.makeText(
                    context,
                    "File selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ========================================================
    // IMAGE PICKER
    // ========================================================

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                Toast.makeText(
                    context,
                    "Image selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ========================================================
    // CAMERA
    // ========================================================

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {

                Toast.makeText(
                    context,
                    "Photo captured",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ========================================================
    // MICROPHONE PERMISSION
    // ========================================================

    val microphonePermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                startAceSpeechRecognition(
                    context = context,

                    onResult = { result ->
                        inputText = result
                    },

                    onListeningChanged = { listening ->
                        isListening = listening
                    }
                )

            } else {

                Toast.makeText(
                    context,
                    "Microphone permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ========================================================
    // START MICROPHONE
    // ========================================================

    fun startMicrophone() {

        val permission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            )

        if (
            permission ==
            PackageManager.PERMISSION_GRANTED
        ) {

            startAceSpeechRecognition(
                context = context,

                onResult = { result ->
                    inputText = result
                },

                onListeningChanged = { listening ->
                    isListening = listening
                }
            )

        } else {

            microphonePermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }


    // ========================================================
    // SEND MESSAGE
    // ========================================================

    fun sendMessage() {

        val text = inputText.trim()

        if (text.isEmpty()) {
            return
        }

        messages = messages + ChatMessage(
            text = text,
            isUser = true
        )

        // Temporary ACE response
        messages = messages + ChatMessage(
            text = "ACE received your message.",
            isUser = false
        )

        inputText = ""

        showAttachmentMenu = false
    }


    // ========================================================
    // MAIN SCREEN
    // ========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AceBackground)
    ) {

        // ----------------------------------------------------
        // BACKGROUND GLOW
        // ----------------------------------------------------

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AcePurple.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        radius = 1000f
                    )
                )
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {


            // =================================================
            // TOP BAR
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 20.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // ------------------------------------------------
                // ACE TITLE
                // ------------------------------------------------

                Text(
                    text = "ACE",

                    color = AceWhite,

                    fontSize = 23.sp,

                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.weight(1f)
                )


                // ------------------------------------------------
                // WORK MODE
                // ------------------------------------------------

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(30.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = AcePurple,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickable {

                            workModeEnabled =
                                !workModeEnabled

                            showWorkModeDialog = true
                        }
                        .padding(
                            horizontal = 18.dp,
                            vertical = 10.dp
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (workModeEnabled)
                                "Work Mode ✓"
                            else
                                "Work Mode",

                        color = AceLightPurple,

                        fontSize = 14.sp
                    )
                }


                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                // ------------------------------------------------
                // PROFILE
                // ------------------------------------------------

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = AcePurple,
                            shape = CircleShape
                        )
                        .clickable {
                            showProfileDialog = true
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "○",

                        color = AceWhite,

                        fontSize = 27.sp
                    )
                }
            }


            // =================================================
            // HEADER DIVIDER
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        AcePurple.copy(alpha = 0.25f)
                    )
            )


            // =================================================
            // CENTER CONTENT
            // =================================================

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                if (messages.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        // ----------------------------------------
                        // ACE LOGO
                        // ----------------------------------------

                        Image(
                            painter =
                                painterResource(
                                    id = R.drawable.ace_logo
                                ),

                            contentDescription =
                                "ACE Logo",

                            contentScale =
                                ContentScale.Crop,

                            modifier =
                                Modifier.size(300.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.height(20.dp)
                        )


                        Text(
                            text =
                                "How can I help you today?",

                            color =
                                AceWhite,

                            fontSize =
                                23.sp,

                            fontWeight =
                                FontWeight.Medium
                        )


                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )


                        Text(
                            text =
                                "Ask ACE to explain, create, analyze or help you work.",

                            color =
                                AceGray,

                            fontSize =
                                14.sp
                        )
                    }

                } else {

                    // ----------------------------------------
                    // CHAT
                    // ----------------------------------------

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),

                        contentPadding =
                            PaddingValues(18.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(messages) { message ->

                            ChatBubble(
                                message = message
                            )
                        }
                    }
                }
            }


            // =================================================
            // ATTACHMENT OPTIONS
            // =================================================

            if (showAttachmentMenu) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 8.dp
                        ),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    SmallActionButton(
                        text = "Camera"
                    ) {

                        cameraLauncher.launch(null)
                    }


                    SmallActionButton(
                        text = "File"
                    ) {

                        filePickerLauncher.launch(
                            arrayOf("*/*")
                        )
                    }


                    SmallActionButton(
                        text = "Image"
                    ) {

                        imagePickerLauncher.launch(
                            "image/*"
                        )
                    }
                }
            }


            // =================================================
            // INPUT BAR
            // =================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 18.dp,
                        end = 18.dp,
                        top = 8.dp,
                        bottom = 14.dp
                    )
                    .height(60.dp)
                    .clip(
                        RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 1.5.dp,

                        color =
                            if (isListening)
                                AceLightPurple
                            else
                                AcePurple,

                        shape =
                            RoundedCornerShape(32.dp)
                    )
                    .background(
                        AceInputBackground.copy(
                            alpha = 0.85f
                        )
                    )
                    .padding(
                        horizontal = 7.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // =================================================
                // PLUS BUTTON
                // =================================================

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable {

                            showAttachmentMenu =
                                !showAttachmentMenu
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "+",

                        color = AceWhite,

                        fontSize = 30.sp
                    )
                }


                // =================================================
                // BASIC TEXT FIELD
                // =================================================

                BasicTextField(
                    value = inputText,

                    onValueChange = {
                        inputText = it
                    },

                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = 10.dp
                        ),

                    singleLine = true,

                    textStyle = TextStyle(
                        color = AceWhite,
                        fontSize = 16.sp
                    ),

                    // ---------------------------------------------
                    // KEYBOARD SEND BUTTON
                    // ---------------------------------------------

                    keyboardOptions =
                        KeyboardOptions(
                            imeAction =
                                ImeAction.Send
                        ),

                    keyboardActions =
                        KeyboardActions(
                            onSend = {

                                sendMessage()
                            }
                        ),

                    decorationBox = {
                        innerTextField ->

                        if (inputText.isEmpty()) {

                            Text(
                                text =
                                    if (isListening)
                                        "Listening..."
                                    else
                                        "Ask ACE anything...",

                                color =
                                    if (isListening)
                                        AceLightPurple
                                    else
                                        AceGray,

                                fontSize =
                                    16.sp
                            )
                        }

                        innerTextField()
                    }
                )


                // =================================================
                // SEND BUTTON (shown when there's text typed)
                // =================================================

                if (inputText.isNotBlank()) {

                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(AcePurple)
                            .clickable {

                                sendMessage()
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "➤",

                            color = AceWhite,

                            fontSize = 20.sp
                        )
                    }

                } else {

                    // =================================================
                    // MIC ICON (shown when input is empty)
                    // =================================================

                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .clickable {

                                startMicrophone()
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Image(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable.mic_icon
                                ),

                            contentDescription =
                                "Voice input",

                            contentScale =
                                ContentScale.Fit,

                            modifier =
                                Modifier.size(
                                    if (isListening)
                                        31.dp
                                    else
                                        27.dp
                                )
                        )
                    }
                }
            }
        }
    }


    // ========================================================
    // WORK MODE DIALOG
    // ========================================================

    if (showWorkModeDialog) {

        AlertDialog(
            onDismissRequest = {

                showWorkModeDialog =
                    false
            },

            title = {

                Text(
                    text = "Work Mode"
                )
            },

            text = {

                Text(
                    text =
                        if (workModeEnabled)
                            "Work Mode is enabled."
                        else
                            "Work Mode is disabled."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showWorkModeDialog =
                            false
                    }
                ) {

                    Text(
                        text = "OK"
                    )
                }
            }
        )
    }


    // ========================================================
    // PROFILE DIALOG
    // ========================================================

    if (showProfileDialog) {

        AlertDialog(
            onDismissRequest = {

                showProfileDialog =
                    false
            },

            title = {

                Text(
                    text = "ACE Profile"
                )
            },

            text = {

                Text(
                    text =
                        "Profile settings will be available here."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showProfileDialog =
                            false
                    }
                ) {

                    Text(
                        text = "Close"
                    )
                }
            }
        )
    }
}


// ============================================================
// SPEECH RECOGNITION
// ============================================================

fun startAceSpeechRecognition(
    context: Context,

    onResult: (String) -> Unit,

    onListeningChanged: (Boolean) -> Unit
) {

    if (
        !SpeechRecognizer
            .isRecognitionAvailable(context)
    ) {

        Toast.makeText(
            context,
            "Speech recognition is not available",
            Toast.LENGTH_SHORT
        ).show()

        return
    }


    val speechRecognizer =
        SpeechRecognizer
            .createSpeechRecognizer(context)


    val speechIntent =
        Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,

                RecognizerIntent
                    .LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "en-US"
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )
        }


    speechRecognizer.setRecognitionListener(

        object : RecognitionListener {

            override fun onReadyForSpeech(
                params: Bundle?
            ) {

                onListeningChanged(true)
            }


            override fun onBeginningOfSpeech() {

                onListeningChanged(true)
            }


            override fun onRmsChanged(
                rmsdB: Float
            ) {
            }


            override fun onBufferReceived(
                buffer: ByteArray?
            ) {
            }


            override fun onEndOfSpeech() {

                onListeningChanged(false)
            }


            override fun onError(
                error: Int
            ) {

                onListeningChanged(false)

                speechRecognizer.destroy()
            }


            override fun onResults(
                results: Bundle?
            ) {

                val matches =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                if (!matches.isNullOrEmpty()) {

                    onResult(
                        matches[0]
                    )
                }

                onListeningChanged(false)

                speechRecognizer.destroy()
            }


            override fun onPartialResults(
                partialResults: Bundle?
            ) {

                val matches =
                    partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                if (!matches.isNullOrEmpty()) {

                    onResult(
                        matches[0]
                    )
                }
            }


            override fun onEvent(
                eventType: Int,

                params: Bundle?
            ) {
            }
        }
    )


    speechRecognizer.startListening(
        speechIntent
    )
}


// ============================================================
// CHAT BUBBLE
// ============================================================

@Composable
fun ChatBubble(
    message: ChatMessage
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            if (message.isUser)
                Arrangement.End
            else
                Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .widthIn(
                    max = 300.dp
                )
                .clip(
                    RoundedCornerShape(
                        18.dp
                    )
                )
                .background(
                    if (message.isUser)
                        AcePurple.copy(
                            alpha = 0.18f
                        )
                    else
                        AceInputBackground
                )
                .border(
                    width = 1.dp,

                    color =
                        AcePurple.copy(
                            alpha = 0.4f
                        ),

                    shape =
                        RoundedCornerShape(
                            18.dp
                        )
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        ) {

            Text(
                text = message.text,

                color = AceWhite,

                fontSize = 15.sp
            )
        }
    }
}


// ============================================================
// SMALL ACTION BUTTON
// ============================================================

@Composable
fun SmallActionButton(
    text: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,

                color = AcePurple,

                shape =
                    RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp,
                vertical = 8.dp
            )
    ) {

        Text(
            text = text,

            color = AceLightPurple,

            fontSize = 12.sp
        )
    }
}