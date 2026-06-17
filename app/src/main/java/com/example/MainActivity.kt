package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import android.net.Uri
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// High-fidelity "Sophisticated Dark" cryptographic color palette
object CryptorThemeColors {
    val BackgroundDark = Color(0xFF1C1B1F)    // bg-[#1C1B1F]
    val ContainerDark = Color(0xFF49454F)     // bg-[#49454F]
    val ResultContainer = Color(0xFF2B2930)   // bg-[#2B2930]
    val TextPrimary = Color(0xFFE6E1E5)       // text-[#E6E1E5]
    val TextSecondary = Color(0xFFCAC4D0)     // text-[#CAC4D0]
    val LightPurple = Color(0xFFD0BCFF)       // bg-[#D0BCFF]
    val DeepPurple = Color(0xFF381E72)        // bg-[#381E72]
    val ErrorRed = Color(0xFFF2B8B5)          // Material Design 3 Dark Mode Error
    val ErrorContainer = Color(0xFF601410)
}

enum class CryptoMode {
    ENCRYPT, DECRYPT
}

class CryptoViewModel : ViewModel() {
    private val _currentMode = MutableStateFlow(CryptoMode.ENCRYPT)
    val currentMode: StateFlow<CryptoMode> = _currentMode.asStateFlow()

    private val _plainText = MutableStateFlow("")
    val plainText: StateFlow<String> = _plainText.asStateFlow()

    private val _cipherText = MutableStateFlow("")
    val cipherText: StateFlow<String> = _cipherText.asStateFlow()

    private val _cryptoKey = MutableStateFlow("")
    val cryptoKey: StateFlow<String> = _cryptoKey.asStateFlow()

    private val _isKeyVisible = MutableStateFlow(false)
    val isKeyVisible: StateFlow<Boolean> = _isKeyVisible.asStateFlow()

    private val _resultText = MutableStateFlow("")
    val resultText: StateFlow<String> = _resultText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _lastOperationTime = MutableStateFlow<String?>(null)
    val lastOperationTime: StateFlow<String?> = _lastOperationTime.asStateFlow()

    fun setMode(mode: CryptoMode) {
        _currentMode.value = mode
        _resultText.value = ""
        _errorMessage.value = null
    }

    fun updatePlainText(text: String) {
        _plainText.value = text
        _errorMessage.value = null
    }

    fun updateCipherText(text: String) {
        _cipherText.value = text
        _errorMessage.value = null
    }

    fun updateCryptoKey(key: String) {
        _cryptoKey.value = key
        _errorMessage.value = null
    }

    fun toggleKeyVisibility() {
        _isKeyVisible.value = !_isKeyVisible.value
    }

    fun executeOperation() {
        _errorMessage.value = null
        _resultText.value = ""

        val key = _cryptoKey.value
        if (key.isEmpty()) {
            _errorMessage.value = "⚠️ کلمه عبور (کلید) نمی‌تواند خالی باشد."
            return
        }

        if (_currentMode.value == CryptoMode.ENCRYPT) {
            val textToEncrypt = _plainText.value
            if (textToEncrypt.isEmpty()) {
                _errorMessage.value = "⚠️ لطفا متن اصلی برای رمزگذاری را وارد کنید."
                return
            }
            try {
                val encrypted = CryptoUtils.encrypt(textToEncrypt, key)
                _resultText.value = encrypted
                _lastOperationTime.value = "آخرین رمزگذاری: الان"
            } catch (e: Exception) {
                _errorMessage.value = "❌ خطا در حین رمزگذاری: ${e.localizedMessage}"
            }
        } else {
            val base64Text = _cipherText.value.trim()
            if (base64Text.isEmpty()) {
                _errorMessage.value = "⚠️ لطفا متن رمزگذاری شده (Base64) را وارد کنید."
                return
            }
            try {
                val decrypted = CryptoUtils.decrypt(base64Text, key)
                _resultText.value = decrypted
                _lastOperationTime.value = "آخرین رمزگشایی: الان"
            } catch (e: Exception) {
                _errorMessage.value = "❌ خطا: کلمه عبور نادرست است یا محتوا معتبر نیست."
            }
        }
    }

    fun clearAll() {
        _plainText.value = ""
        _cipherText.value = ""
        _cryptoKey.value = ""
        _resultText.value = ""
        _errorMessage.value = null
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Enforce RTL Layout Direction explicitly for a polished Persian user experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = CryptorThemeColors.BackgroundDark
                    ) { innerPadding ->
                        CryptoAppScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CryptoAppScreen(
    modifier: Modifier = Modifier,
    viewModel: CryptoViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val currentMode by viewModel.currentMode.collectAsState()
    val plainText by viewModel.plainText.collectAsState()
    val cipherText by viewModel.cipherText.collectAsState()
    val cryptoKey by viewModel.cryptoKey.collectAsState()
    val isKeyVisible by viewModel.isKeyVisible.collectAsState()
    val resultText by viewModel.resultText.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var copyClicked by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sophisticated Dark Header matching HTML structure
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar badge "C"
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(CryptorThemeColors.LightPurple, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        color = CryptorThemeColors.DeepPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Column {
                    Text(
                        text = "رمزنگار سیفرگارد",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = CryptorThemeColors.TextPrimary
                    )
                    Text(
                        text = "AES-256 SECURE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CryptorThemeColors.LightPurple,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Cleanup icon (Icon of Clear/Delete)
            IconButton(
                onClick = {
                    viewModel.clearAll()
                    Toast.makeText(context, "تمام فیلدها پاکسازی شدند 🧹", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(CryptorThemeColors.ContainerDark, RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "پاکسازی",
                    tint = CryptorThemeColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Custom High-End Segmented Control tab using Sophisticated Dark accents
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(CryptorThemeColors.ContainerDark, RoundedCornerShape(16.dp))
                .border(1.dp, CryptorThemeColors.ContainerDark, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            val encryptTabModifier = if (currentMode == CryptoMode.ENCRYPT) {
                Modifier.background(CryptorThemeColors.LightPurple, RoundedCornerShape(12.dp))
            } else {
                Modifier.background(Color.Transparent)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(encryptTabModifier)
                    .clickable { viewModel.setMode(CryptoMode.ENCRYPT) }
                    .padding(vertical = 12.dp)
                    .testTag("enc_tab_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (currentMode == CryptoMode.ENCRYPT) CryptorThemeColors.DeepPurple else CryptorThemeColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "رمزگذاری (کد)",
                        color = if (currentMode == CryptoMode.ENCRYPT) CryptorThemeColors.DeepPurple else CryptorThemeColors.TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            val decryptTabModifier = if (currentMode == CryptoMode.DECRYPT) {
                Modifier.background(CryptorThemeColors.LightPurple, RoundedCornerShape(12.dp))
            } else {
                Modifier.background(Color.Transparent)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(decryptTabModifier)
                    .clickable { viewModel.setMode(CryptoMode.DECRYPT) }
                    .padding(vertical = 12.dp)
                    .testTag("dec_tab_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (currentMode == CryptoMode.DECRYPT) CryptorThemeColors.DeepPurple else CryptorThemeColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "رمزگشایی (دیکد)",
                        color = if (currentMode == CryptoMode.DECRYPT) CryptorThemeColors.DeepPurple else CryptorThemeColors.TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Main Sophisticated Container Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = CryptorThemeColors.BackgroundDark,
            border = BorderStroke(1.dp, CryptorThemeColors.ContainerDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Label
                Text(
                    text = if (currentMode == CryptoMode.ENCRYPT) "متن ورودی (INPUT TEXT)" else "متن رمزنگاری شده (CIPHER TEXT)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CryptorThemeColors.LightPurple,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input field wrapped inside simulated Glass box with round 16dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CryptorThemeColors.ContainerDark, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentMode == CryptoMode.ENCRYPT) "پیام خام:" else "پیام با کدگذاری base64:",
                                fontSize = 11.sp,
                                color = CryptorThemeColors.TextSecondary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentMode == CryptoMode.DECRYPT) {
                                    TextButton(
                                        onClick = {
                                            val clipData = clipboardManager.getText()
                                            if (clipData != null) {
                                                viewModel.updateCipherText(clipData.text)
                                                Toast.makeText(context, "جای‌گذاری شد 📥", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "حافظه کلیپ‌بورد سیستم خالی است", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Text(
                                            text = "[جای‌گذاری از کلیپ‌بورد]",
                                            color = CryptorThemeColors.LightPurple,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                val hasContent = if (currentMode == CryptoMode.ENCRYPT) plainText.isNotEmpty() else cipherText.isNotEmpty()
                                if (hasContent) {
                                    IconButton(
                                        onClick = {
                                            if (currentMode == CryptoMode.ENCRYPT) viewModel.updatePlainText("")
                                            else viewModel.updateCipherText("")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "پاک کردن",
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Real input text field
                        OutlinedTextField(
                            value = if (currentMode == CryptoMode.ENCRYPT) plainText else cipherText,
                            onValueChange = {
                                if (currentMode == CryptoMode.ENCRYPT) viewModel.updatePlainText(it)
                                else viewModel.updateCipherText(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("input_text_field"),
                            placeholder = {
                                Text(
                                    text = if (currentMode == CryptoMode.ENCRYPT) {
                                        "پیام خود را جهت رمزنگاری امن بنویسید یا بچسبانید..."
                                    } else {
                                        "پیام رمزنگاری شده قبلی را برای رمزگشایی اینجا وارد کنید..."
                                    },
                                    color = CryptorThemeColors.TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = CryptorThemeColors.TextPrimary,
                                unfocusedTextColor = CryptorThemeColors.TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Secret Key Label
                Text(
                    text = "کلید سری (SECRET KEY)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CryptorThemeColors.LightPurple,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Secret key bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CryptorThemeColors.ContainerDark, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "کلید",
                            tint = CryptorThemeColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = cryptoKey,
                            onValueChange = { viewModel.updateCryptoKey(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("key_text_field"),
                            placeholder = {
                                Text(
                                    text = "رمز عبور دلخواه برای قفل کردن...",
                                    color = CryptorThemeColors.TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            },
                            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = CryptorThemeColors.TextPrimary,
                                unfocusedTextColor = CryptorThemeColors.TextPrimary
                            )
                        )
                        
                        TextButton(onClick = { viewModel.toggleKeyVisibility() }) {
                            Text(
                                text = if (isKeyVisible) "مخفی" else "نمایش",
                                color = CryptorThemeColors.TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Password strength hint
                if (cryptoKey.isNotEmpty() && cryptoKey.length < 5) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 پیشنهاد: کلیدهای تصادفی یا طولانی‌تر، سطح حریم خصوصی را تضمین می‌کنند.",
                        color = CryptorThemeColors.LightPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }

        // Active Error Panel
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CryptorThemeColors.ErrorContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CryptorThemeColors.ErrorRed)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "خطا",
                            tint = CryptorThemeColors.ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = CryptorThemeColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row matching CSS design format
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Encrypt Button style: bg-[#D0BCFF] text-[#381E72] rounded-full
            Button(
                onClick = {
                    viewModel.setMode(CryptoMode.ENCRYPT)
                    viewModel.executeOperation()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("encrypt_action_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptorThemeColors.LightPurple,
                    contentColor = CryptorThemeColors.DeepPurple
                ),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "قفل",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "رمز کردن متن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Decrypt Button style: bg-[#381E72] text-[#D0BCFF] border border-[#D0BCFF] rounded-full
            Button(
                onClick = {
                    viewModel.setMode(CryptoMode.DECRYPT)
                    viewModel.executeOperation()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("decrypt_action_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptorThemeColors.DeepPurple,
                    contentColor = CryptorThemeColors.LightPurple
                ),
                border = BorderStroke(1.dp, CryptorThemeColors.LightPurple),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "باز کردن",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "رمزگشایی متن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Section - Sophisticated deep box base styled on bg-[#2B2930] with 3xl rounded corner
        AnimatedVisibility(
            visible = resultText.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(400)) + expandVertically(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "خروجی (RESULT)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CryptorThemeColors.LightPurple,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = CryptorThemeColors.ResultContainer,
                    border = BorderStroke(1.dp, CryptorThemeColors.ContainerDark)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "خروجی تولید شده:",
                                fontWeight = FontWeight.Bold,
                                color = CryptorThemeColors.TextPrimary,
                                fontSize = 14.sp
                            )

                            if (copyClicked) {
                                Text(
                                    text = "کپی شد ✅",
                                    color = CryptorThemeColors.LightPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Text display box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CryptorThemeColors.BackgroundDark, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = resultText,
                                color = CryptorThemeColors.TextSecondary,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Left, // Crypotgraphic standard Left aligned
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Floating action design buttons row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Copy button style
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(resultText))
                                    copyClicked = true
                                    coroutineScope.launch {
                                        delay(2000)
                                        copyClicked = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("copy_result_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CryptorThemeColors.ContainerDark,
                                    contentColor = CryptorThemeColors.TextPrimary
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "کپی متن",
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            // Share button style active color
                            Button(
                                onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, resultText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "ارسال به دیگران:")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("share_result_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CryptorThemeColors.LightPurple,
                                    contentColor = CryptorThemeColors.DeepPurple
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "اشتراک‌گذاری",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "اشتراک‌گذاری",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Info guideline cards on bottom
        if (resultText.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CryptorThemeColors.ResultContainer,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CryptorThemeColors.ContainerDark)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "امنیت",
                        tint = CryptorThemeColors.LightPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تمامی پردازش‌های سایفرگارد کاملاً آفلاین بوده و داده‌های شما درون دستگاه باقی می‌مانند.",
                        fontSize = 11.sp,
                        color = CryptorThemeColors.TextSecondary,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sophisticated Dark Bottom Navigation Tab Layout
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CryptorThemeColors.BackgroundDark,
            border = BorderStroke(1.dp, CryptorThemeColors.ContainerDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Section 1: App Version (displays "نسخه ۲" instead of Vault button, located at the right in RTL)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "نسخه برنامه",
                        tint = CryptorThemeColors.LightPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "نسخه ۲",
                        fontSize = 8.sp,
                        color = CryptorThemeColors.LightPurple,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Section 2: Professional branding icons row (GitHub and LinkedIn instead of Keys, located at the left in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Bahram-PAB"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "خطا در باز کردن مرورگر 🌐", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_github),
                                contentDescription = "گیت‌هاب",
                                tint = CryptorThemeColors.TextSecondary,
                                modifier = Modifier.size(18.dp)
                              )
                            Text(
                                text = "گیت‌هاب",
                                fontSize = 8.sp,
                                color = CryptorThemeColors.TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/bahram-pouralibaba-1a992239"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "خطا در باز کردن مرورگر 🌐", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_linkedin),
                                contentDescription = "لینکدین",
                                tint = CryptorThemeColors.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "لینکدین",
                                fontSize = 8.sp,
                                color = CryptorThemeColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Minimalist helpers
@Composable
private fun BoxBorder(hasError: Boolean): BorderStroke {
    return BorderStroke(
        width = 1.dp,
        color = if (hasError) CryptorThemeColors.ErrorRed else CryptorThemeColors.ContainerDark
    )
}

@Preview(showBackground = true)
@Composable
fun CryptoAppPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.background(CryptorThemeColors.BackgroundDark)) {
            CryptoAppScreen()
        }
    }
}
