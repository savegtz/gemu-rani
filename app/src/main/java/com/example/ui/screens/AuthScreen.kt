package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onRegisterSuccess: (fullName: String, username: String, emailOrPhone: String, password: String, mkoa: String) -> Unit,
    onLoginSuccess: (emailOrPhone: String, password: String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onOpenAdmin: () -> Unit = {}
) {
    var isRegisterMode by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    val mikoaList = listOf(
        "Dar es Salaam 🌊",
        "Arusha 🏔️",
        "Mwanza 🐟",
        "Zanzibar 🏝️",
        "Dodoma 🏛️",
        "Mbeya ⛰️",
        "Tanga 🌴",
        "Kilimanjaro ❄️",
        "Morogoro 🌿",
        "Nairobi / East Africa 🌍"
    )
    var selectedMkoa by remember { mutableStateOf(mikoaList[0]) }
    var isMkoaMenuExpanded by remember { mutableStateOf(false) }
    
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        errorMessage = null
        if (emailOrPhone.trim().isEmpty()) {
            errorMessage = "Tafadhali weka namba ya simu au barua pepe!"
            return
        }
        if (password.length < 4) {
            errorMessage = "Nenosiri lazima liwe na angalau tarakimu 4!"
            return
        }

        if (isRegisterMode) {
            if (fullName.trim().isEmpty()) {
                errorMessage = "Tafadhali weka jina lako kamili!"
                return
            }
            val userTag = if (username.trim().isNotEmpty()) username.trim() else fullName.trim().replace(" ", "_")
            SoundEngine.playPowerUp()
            onRegisterSuccess(fullName, userTag, emailOrPhone, password, selectedMkoa.substringBefore(" "))
        } else {
            SoundEngine.playPowerUp()
            onLoginSuccess(emailOrPhone, password)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgMain)
            .testTag("auth_screen")
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            TanzaniteBlue.copy(alpha = 0.25f),
                            DarkBgMain,
                            DarkBgMain
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Game Logo & Banner
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        Brush.linearGradient(listOf(BrightAmber, NeonGold)),
                        CircleShape
                    )
                    .border(3.dp, ElectricCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🏃💨", fontSize = 34.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BONGO RUNNER 🇹🇿",
                    color = NeonGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Mbio za Mitaani Tanzania & Afrika Mashariki",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Tab Switcher (Jisajili vs Ingia)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBgCard, RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isRegisterMode) NeonGold else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            isRegisterMode = true
                            errorMessage = null
                            SoundEngine.playMenuClick()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Jisajili (Sign Up)",
                        color = if (isRegisterMode) DarkBgMain else TextSecondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (!isRegisterMode) NeonGold else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            isRegisterMode = false
                            errorMessage = null
                            SoundEngine.playMenuClick()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ingia (Sign In)",
                        color = if (!isRegisterMode) DarkBgMain else TextSecondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }

            // Error message banner
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CrimsonFire.copy(alpha = 0.2f),
                        border = ButtonDefaults.outlinedButtonBorder(true),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CrimsonFire, modifier = Modifier.size(18.dp))
                            Text(text = msg, color = Color(0xFFFCA5A5), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Input Fields Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBgCard),
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Full Name (Only for registration)
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Jina Kamili (Full Name)", color = TextSecondary, fontSize = 12.sp) },
                            placeholder = { Text("Mfano: Juma Bakari", color = TextMuted, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonGold)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("full_name_input")
                        )

                        // Username / Gamertag
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Jina la Mchezo / Gamertag (Hiari)", color = TextSecondary, fontSize = 12.sp) },
                            placeholder = { Text("Mfano: Juma_Speed", color = TextMuted, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Tag, contentDescription = null, tint = ElectricCyan)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input")
                        )
                    }

                    // Phone Number or Email
                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("Namba ya Simu au Email", color = TextSecondary, fontSize = 12.sp) },
                        placeholder = { Text("0712345678 au juma@gmail.com", color = TextMuted, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = BrightAmber)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = if (isRegisterMode) ImeAction.Next else ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrightAmber,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_or_phone_input")
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Nenosiri (Password)", color = TextSecondary, fontSize = 12.sp) },
                        placeholder = { Text("Weka nenosiri lako", color = TextMuted, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ZanzibarTurquoise)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Onyesha nenosiri",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            validateAndSubmit()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZanzibarTurquoise,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    // Mkoa / Region Dropdown (For Registration)
                    if (isRegisterMode) {
                        ExposedDropdownMenuBox(
                            expanded = isMkoaMenuExpanded,
                            onExpandedChange = { isMkoaMenuExpanded = !isMkoaMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedMkoa,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Mkoa / Eneo Lako", color = TextSecondary, fontSize = 12.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMkoaMenuExpanded) },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AfricanEmerald)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AfricanEmerald,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isMkoaMenuExpanded,
                                onDismissRequest = { isMkoaMenuExpanded = false },
                                modifier = Modifier.background(DarkBgCardElevated)
                            ) {
                                mikoaList.forEach { mkoa ->
                                    DropdownMenuItem(
                                        text = { Text(text = mkoa, color = TextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            selectedMkoa = mkoa
                                            isMkoaMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    validateAndSubmit()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_submit_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isRegisterMode) "JISAJILI & ANZA KUCHEZA 🏃" else "INGIA KWENYE MCHEZO 🔑",
                        color = DarkBgMain,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Quick Play / Guest Option
            OutlinedButton(
                onClick = {
                    SoundEngine.playMenuClick()
                    onContinueAsGuest()
                },
                border = ButtonDefaults.outlinedButtonBorder(true),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("guest_play_button")
            ) {
                Text(
                    text = "Cheza kama Mgeni (Guest Mode) ⚡",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Direct Admin Dashboard Entrance
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkBgCardElevated,
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        SoundEngine.playMenuClick()
                        onOpenAdmin()
                    }
                    .testTag("auth_admin_panel_button")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "👑", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PANELI YA UTENGENEZAJI (ADMIN DASHBOARD)",
                        color = NeonGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
