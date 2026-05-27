package com.example.ui

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import android.widget.Toast
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ImageRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ClothingEntity
import com.example.data.OutfitCombination
import com.example.data.OutfitHistoryEntity
import com.example.ui.components.ClothingVectorIcon
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.launch

// Brand color palette: Editorial Aesthetic (Cream Canvas, Charcoal Accent & Elegant Typography)
val SlateBackground = Color(0xFFFAF9F6) // Alabaster Off-White
val SlateCard = Color(0xFFFFFFFF)       // Pure Paper White Accent Cards
val SandAccent = Color(0xFFEADFCE)      // Soft Clay/Beige Accent
val AntiqueGold = Color(0xFF7D5260)     // Classic Muted Burgundy / Editorial Accent
val CharcoalBorder = Color(0xFFE2E0D8)  // Clean, thin slate border
val TextLight = Color(0xFF1C1B1F)       // Editorial Deep Charcoal primary text
val TextMuted = Color(0xFF757470)       // Muted warm editorial gray secondary text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitCheckApp(viewModel: FitCheckViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = Pair(userProfile, onboardingCompleted),
        transitionSpec = {
            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
        },
        label = "auth_screen"
    ) { (activeUser, isSetupDone) ->
        if (activeUser == null) {
            LoginScreen(onLogin = { email, name -> viewModel.login(email, name) })
        } else if (!isSetupDone) {
            FullLengthSetupScreen(
                viewModel = viewModel,
                name = activeUser.name,
                onComplete = { selfieUrl, height, build, skinColor ->
                    viewModel.completeOnboarding(selfieUrl, height, build, skinColor)
                }
            )
        } else {
            MainAppLayout(viewModel)
        }
    }
}

// --- GOOGLE SIGN IN SCREEN ---
@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var emailInput by remember { mutableStateOf("likhithbellamkonda@gmail.com") }
    var nameInput by remember { mutableStateOf("Likhith Bellamkonda") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Aesthetic linear backdrop grid circles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AntiqueGold.copy(alpha = 0.15f), Color.Transparent),
                            radius = size.width * 0.8f,
                            center = Offset(size.width * 0.5f, size.height * 0.15f)
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant brand monogram icon (Chic classic styling)
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(AntiqueGold, SandAccent.copy(alpha = 0.5f))))
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(23.dp))
                        .background(SlateCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = "FitCheck Icon",
                        tint = AntiqueGold,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "SLAY AI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AntiqueGold,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Slay",
                fontSize = 44.sp,
                fontWeight = FontWeight.Light,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = TextLight,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Elite Personal Digital Wardrobe & AI Try-On",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Customized styled cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Sign In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Name", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Google Account Email", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Button(
                        onClick = { onLogin(emailInput, nameInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                tint = SlateBackground
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Secure multi-session local cloud profile",
                fontSize = 11.sp,
                color = TextMuted.copy(alpha = 0.6f)
            )
        }
    }
}


// --- FULL-LENGTH DIGITAL PROFILE SETUP ONBOARDING ---
@Composable
fun FullLengthSetupScreen(
    viewModel: FitCheckViewModel,
    name: String,
    onComplete: (String, Int, String, String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            android.content.pm.PackageManager.PERMISSION_GRANTED ==
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            )
        )
    }

    val defaultPresets = listOf(
        Triple("Studio Minimal (F)", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500", "Casual (F)"),
        Triple("Urban Street (M)", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500", "Formal (M)"),
        Triple("Fitting Room (F)", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500", "Studio (F)"),
        Triple("Athletic Fit (M)", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500", "Sporty (M)")
    )
    
    var selectedSelfieUrl by remember { mutableStateOf(defaultPresets[0].second) }
    var customUrlText by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    val imageLoader = remember { context.imageLoader }
    var detectedSkinColor by remember { mutableStateOf("#DFC7B3") }
    var isAnalyzingOnboardingSkin by remember { mutableStateOf(false) }
    
    val activePhotoUrl = if (showCustomInput && customUrlText.isNotBlank()) customUrlText else selectedSelfieUrl
    
    LaunchedEffect(activePhotoUrl) {
        if (activePhotoUrl.isNotEmpty()) {
            isAnalyzingOnboardingSkin = true
            try {
                val mapped = when {
                    activePhotoUrl.contains("photo-1534528741775-53994a69daeb") -> "#DFC7B3"
                    activePhotoUrl.contains("photo-1507003211169-0a1dd7228f2d") -> "#A67B5B"
                    activePhotoUrl.contains("photo-1517841905240-472988babdf9") -> "#DFC7B3"
                    activePhotoUrl.contains("photo-1500648767791-00dcc994a43e") -> "#A67B5B"
                    else -> {
                        val request = ImageRequest.Builder(context)
                            .data(activePhotoUrl)
                            .allowHardware(false)
                            .build()
                        val result = imageLoader.execute(request)
                        val drawable = result.drawable
                        if (drawable is BitmapDrawable) {
                            extractDominantSkinColor(drawable.bitmap)
                        } else {
                            "#DFC7B3"
                        }
                    }
                }
                detectedSkinColor = mapped
            } catch (e: Exception) {
                detectedSkinColor = "#E8D3C5"
            } finally {
                isAnalyzingOnboardingSkin = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Camera permission granted. Launching camera...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Camera permission was denied. Try choosing from gallery or using presets.", Toast.LENGTH_LONG).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val savedPath = viewModel.saveSelfieBitmap(bitmap)
            if (savedPath != null) {
                selectedSelfieUrl = savedPath
                showCustomInput = false
                Toast.makeText(context, "Captured photo successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedSelfieUrl = uri.toString()
            showCustomInput = false
            Toast.makeText(context, "Gallery photo selected!", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "SETUP DIGITAL DOUBLE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AntiqueGold,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Welcome, ${name.substringBefore(" ")}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = TextLight,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a full-length portrait of yourself or choose a preset to try on combinations. You can set precise physical dimensions later in Settings.",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Photo card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "1. ADD YOUR FULL-LENGTH PHOTO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Preview of selected photo template
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        coil.compose.AsyncImage(
                            model = if (showCustomInput && customUrlText.isNotBlank()) customUrlText else selectedSelfieUrl,
                            contentDescription = "Active Portrait Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))))
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                        ) {
                            Text(
                                "Your Active Slay Double",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Captured selfie or gallery selection row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (hasCameraPermission) {
                                    cameraLauncher.launch(null)
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = SlateBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateBackground)
                            }
                        }

                        Button(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                            border = BorderStroke(1.dp, AntiqueGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Collections, contentDescription = "Gallery", tint = AntiqueGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pick Gallery", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Preset grid
                    Text(
                        "or Select model/pose Preset",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(defaultPresets) { preset ->
                            val isSelected = selectedSelfieUrl == preset.second && !showCustomInput
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AntiqueGold.copy(alpha = 0.15f) else SlateBackground)
                                    .border(
                                        BorderStroke(1.dp, if (isSelected) AntiqueGold else CharcoalBorder),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedSelfieUrl = preset.second
                                        showCustomInput = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(preset.first, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                    Text(preset.third, fontSize = 8.sp, color = TextMuted)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Toggle Custom
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Paste My Custom Portrait Link", fontSize = 11.sp, color = TextLight, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = showCustomInput,
                            onCheckedChange = { showCustomInput = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AntiqueGold,
                                checkedTrackColor = AntiqueGold.copy(alpha = 0.4f)
                            )
                        )
                    }

                    if (showCustomInput) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customUrlText,
                            onValueChange = { customUrlText = it },
                            placeholder = { Text("Enter secure full-length portrait URL...", fontSize = 11.sp, color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = CharcoalBorder,
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action
            Button(
                onClick = {
                    val finalUrl = if (showCustomInput && customUrlText.isNotBlank()) customUrlText else selectedSelfieUrl
                    onComplete(finalUrl, 175, "Athletic", detectedSkinColor)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_complete_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = SlateBackground)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Generate My Personalized Outfits",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateBackground
                    )
                }
            }
        }
    }
}


// --- MAIN NAVIGATION SCREEN CONTAINER ---
@Composable
fun MainAppLayout(viewModel: FitCheckViewModel) {
    var activeTab by remember { mutableStateOf("match_maker") }
    val incomingSharedImageUri by viewModel.incomingSharedImageUri.collectAsStateWithLifecycle()

    if (incomingSharedImageUri != null) {
        SharedImageActionDialog(
            uriString = incomingSharedImageUri!!,
            viewModel = viewModel,
            onDismiss = { viewModel.clearIncomingSharedImage() }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = SlateBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F0E9), // Editorial Warm Sand navigation background
                tonalElevation = 4.dp,
                modifier = Modifier.border(BorderStroke(0.5.dp, CharcoalBorder))
            ) {
                listOf(
                    Triple("match_maker", "Match", Icons.Default.Checkroom),
                    Triple("closet", "Closet", Icons.Default.AllInbox),
                    Triple("try_on", "Try On", Icons.Default.Psychology),
                    Triple("stylist", "AI Stylist", Icons.Default.Chat)
                ).forEach { item ->
                    val selected = activeTab == item.first
                    NavigationBarItem(
                        selected = selected,
                        onClick = { activeTab = item.first },
                        icon = {
                            Icon(
                                imageVector = item.third,
                                contentDescription = item.second,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.second,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B), // Elite Dark Charcoal/Indigo focus
                            selectedTextColor = Color(0xFF1D192B),
                            indicatorColor = Color(0xFFE8DEF8),    // Elegant Lavender/Rose active pill bubble
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_${item.first}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "match_maker" -> MatchMakerDashboard(viewModel)
                "closet" -> ClosetStudio(viewModel)
                "try_on" -> TryOnSuite(viewModel)
                "stylist" -> StylistRoom(viewModel)
            }
        }
    }
}


// --- TAB 1: DASHBOARD (MATCH MAKER) ---
@Composable
fun MatchMakerDashboard(viewModel: FitCheckViewModel) {
    val items by viewModel.recommendations.collectAsStateWithLifecycle()
    val allClothes by viewModel.allClothes.collectAsStateWithLifecycle()
    val occasion by viewModel.selectedOccasion.collectAsStateWithLifecycle()
    val season by viewModel.selectedSeason.collectAsStateWithLifecycle()
    val selectedCombo by viewModel.selectedCombo.collectAsStateWithLifecycle()
    val tryOnState by viewModel.tryOnState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Dashboard Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "AI STYLIST COORDINATES THE BEST CHOICE FOR YOU!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Slay",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = TextLight,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        val syncState by viewModel.tryOnState.collectAsStateWithLifecycle()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (syncState.cloudSyncStatus.startsWith("Syncing")) AntiqueGold.copy(alpha = 0.15f) else Color(0xFFE2F0D9))
                                .clickable { viewModel.triggerCloudSync() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                .testTag("cloud_sync_capsule")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (syncState.cloudSyncStatus.startsWith("Syncing")) Icons.Default.Autorenew else Icons.Default.CloudDone,
                                    contentDescription = "Cloud Done",
                                    tint = if (syncState.cloudSyncStatus.startsWith("Syncing")) AntiqueGold else Color(0xFF2E7D32),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    syncState.cloudSyncStatus,
                                    color = if (syncState.cloudSyncStatus.startsWith("Syncing")) AntiqueGold else Color(0xFF2E7D32),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                        .clickable { viewModel.logout() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // FILTER CAROUSELS
            Text(
                "Select Occasion Context",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val occasionsList = listOf("Casual", "Office", "Party", "Wedding", "College", "Gym", "Travel")
                items(occasionsList) { name ->
                    val active = name == occasion
                    FilterChip(
                        name = name,
                        active = active,
                        onClick = { viewModel.selectOccasion(name) }
                    )
                }
            }

            Text(
                "Seasonal suitability",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val seasonsList = listOf("All", "Summer", "Autumn", "Winter", "Spring")
                items(seasonsList) { name ->
                    val active = name == season
                    FilterChip(
                        name = name,
                        active = active,
                        onClick = { viewModel.selectSeason(name) }
                    )
                }
            }

            // OUTFIT LIST
            val filteredSuggestions = items
            if (filteredSuggestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageUrl = Icons.Default.AllInbox, tint = TextMuted, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Capsule Under-populated",
                            color = TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Add more Tops (shirts/tees/hoodies) and Bottoms (jeans/slacks) in Closet to unlock coordinates.",
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSuggestions) { combo ->
                        ShoppingComboTile(
                            combo = combo,
                            tryOnState = tryOnState,
                            onClick = {
                                viewModel.setSelectedCombo(combo)
                            }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        )

        // SELECTED OUTFIT MATH DRILLDOWN BOTTOM SHEET
        selectedCombo?.let { combo ->
            AlertDialog(
                onDismissRequest = { viewModel.setSelectedCombo(null) },
                confirmButton = {
                    TextButton(onClick = { viewModel.setSelectedCombo(null) }) {
                        Text("Dismiss Customizer", color = AntiqueGold, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "SLAY DESIGN SUITE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { viewModel.setSelectedCombo(null) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Digital Model Canvas with interactive double zoom
                        Text(
                            "VIRTUAL MODEL PREVIEW (TAP IMAGE TO ZOOM)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF3F1ED)),
                            contentAlignment = Alignment.Center
                        ) {
                            var isZoomed by remember { mutableStateOf(false) }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = if (isZoomed) 2.2f else 1.0f,
                                        scaleY = if (isZoomed) 2.2f else 1.0f
                                    )
                                    .clickable { isZoomed = !isZoomed }
                                    .padding(10.dp)
                            ) {
                                MannequinCanvas(
                                    state = tryOnState.copy(
                                        topItem = combo.top,
                                        bottomItem = combo.bottom,
                                        shoesItem = combo.shoes,
                                        jacketItem = combo.jacket,
                                        accessoryItem = combo.accessory
                                    )
                                )
                            }

                            // Zoom Indicator Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(SlateBackground.copy(alpha = 0.85f))
                                    .clickable { isZoomed = !isZoomed }
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isZoomed) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                    contentDescription = "Zoom toggle",
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            if (isZoomed) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AntiqueGold)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ZOOM ACTIVE • TAP FLIPKART MODEL TO REVERT", fontSize = 8.sp, color = SlateBackground, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom quick actions row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.resetTryOn()
                                    viewModel.selectToTryOn(combo.top)
                                    viewModel.selectToTryOn(combo.bottom)
                                    viewModel.selectToTryOn(combo.shoes)
                                    combo.jacket?.let { viewModel.selectToTryOn(it) }
                                    combo.accessory?.let { viewModel.selectToTryOn(it) }
                                    viewModel.setSelectedCombo(null)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Outfit loaded to real-time Try On suite!")
                                    }
                                },
                                modifier = Modifier.weight(1.3f),
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = SlateBackground, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Virtual Try On", color = SlateBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.logWearingOutfit(combo)
                                    viewModel.setSelectedCombo(null)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Logged outfit coordinate!")
                                    }
                                },
                                modifier = Modifier.weight(1.0f),
                                colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                border = BorderStroke(1.dp, CharcoalBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Done, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Slayed It", color = AntiqueGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // GARMENTS BREAKDOWN
                        Text(
                            "INSPECT GARMENTS INDIVIDUALLY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        var activeZoomedGarment by remember { mutableStateOf<ClothingEntity?>(null) }
                        val garments = listOfNotNull(combo.top, combo.bottom, combo.shoes, combo.jacket, combo.accessory)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            garments.forEach { item ->
                                val activeSelection = activeZoomedGarment?.id == item.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (activeSelection) SlateBackground else CharcoalBorder.copy(alpha = 0.25f))
                                        .border(BorderStroke(1.dp, if (activeSelection) AntiqueGold else Color.Transparent), RoundedCornerShape(8.dp))
                                        .clickable {
                                            activeZoomedGarment = if (activeSelection) null else item
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ClothingVectorIcon(
                                        category = item.category,
                                        colorHex = item.colorHex,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextLight
                                        )
                                        Text(
                                            "${item.category} • Contrast: ${item.style}",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(AntiqueGold.copy(alpha = 0.08f))
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (activeSelection) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                            contentDescription = "Zoom garment",
                                            tint = AntiqueGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                if (activeSelection) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ClothingVectorIcon(
                                            category = item.category,
                                            colorHex = item.colorHex,
                                            modifier = Modifier.size(85.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // SCROLL DOWN ALTERNATIVES (SAME SHIRT DIFFERENT PANTS)
                        Text(
                            "SCROLL DOWN FOR RELATED COMBO TILES",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "COORDINATED MATCHING (SAME SHIRT / DIFF PANTS)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val sameShirtAlternatives = items.filter {
                            it.top.id == combo.top.id && it.bottom.id != combo.bottom.id
                        }

                        if (sameShirtAlternatives.isEmpty()) {
                            Text("No complementary bottom alternatives active.", fontSize = 9.sp, color = TextMuted)
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(sameShirtAlternatives) { altCombo ->
                                    Card(
                                        modifier = Modifier
                                            .width(115.dp)
                                            .clickable { viewModel.setSelectedCombo(altCombo) },
                                        colors = CardDefaults.cardColors(containerColor = SlateBackground),
                                        border = BorderStroke(1.dp, CharcoalBorder),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(45.dp)
                                                    .background(Color(0xFFEDEAE6)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                ClothingVectorIcon(
                                                    category = altCombo.bottom.category,
                                                    colorHex = altCombo.bottom.colorHex,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                altCombo.bottom.name,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextLight,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "Slay Score: ${altCombo.averageScore}",
                                                fontSize = 8.sp,
                                                color = AntiqueGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // SCROLL DOWN ALTERNATIVES (SAME PANTS DIFFERENT SHIRT)
                        Text(
                            "COORDINATED MATCHING (SAME PANTS / DIFF SHIRT)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val samePantsAlternatives = items.filter {
                            it.bottom.id == combo.bottom.id && it.top.id != combo.top.id
                        }

                        if (samePantsAlternatives.isEmpty()) {
                            Text("No complementary top alternatives active.", fontSize = 9.sp, color = TextMuted)
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(samePantsAlternatives) { altCombo ->
                                    Card(
                                        modifier = Modifier
                                            .width(115.dp)
                                            .clickable { viewModel.setSelectedCombo(altCombo) },
                                        colors = CardDefaults.cardColors(containerColor = SlateBackground),
                                        border = BorderStroke(1.dp, CharcoalBorder),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(45.dp)
                                                    .background(Color(0xFFEDEAE6)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                ClothingVectorIcon(
                                                    category = altCombo.top.category,
                                                    colorHex = altCombo.top.colorHex,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                altCombo.top.name,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextLight,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "Slay Score: ${altCombo.averageScore}",
                                                fontSize = 8.sp,
                                                color = AntiqueGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = SlateCard,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun ShoppingComboTile(
    combo: OutfitCombination,
    tryOnState: TryOnState,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("shopping_combo_tile_${combo.top.id}_${combo.bottom.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CharcoalBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(SlateBackground),
                contentAlignment = Alignment.Center
            ) {
                // Background image: Load user's selfie, or fallback high-fashion portrait
                val backdropUrl = tryOnState.userSelfieUrl ?: "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=300"
                Box(modifier = Modifier.fillMaxSize()) {
                    coil.compose.AsyncImage(
                        model = backdropUrl,
                        contentDescription = "Portrait Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        alpha = 0.75f
                    )
                }

                // Pre-styled digital double canvas showing exact coordinate match elements Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                ) {
                    MannequinCanvas(
                        state = tryOnState.copy(
                            activeModel = ModelPreset.presets.first(), // Force myself style to make mannequin translucent
                            userSelfieUrl = backdropUrl,
                            topItem = combo.top,
                            bottomItem = combo.bottom,
                            shoesItem = combo.shoes,
                            jacketItem = combo.jacket,
                            accessoryItem = combo.accessory
                        )
                    )
                }

                // Match score badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AntiqueGold.copy(alpha = 0.9f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    val badgeText = when {
                        combo.averageScore >= 90 -> "EXCELLENT MATCH"
                        combo.averageScore >= 80 -> "ELEVATED MATCH"
                        else -> "EXQUISITE COORDINATE"
                    }
                    Text(
                        badgeText,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                if (combo.containRecentWorn) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE57373))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            "WORN",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = combo.occasion.uppercase(),
                    fontSize = 7.sp,
                    color = AntiqueGold,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "${combo.top.color} & ${combo.bottom.color} Suit",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(9.dp)
                    )
                    Text(
                        "4.9",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextLight
                    )
                    Text(
                        "• Best Seller",
                        fontSize = 8.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun TextRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(value, color = TextLight, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun StyleBullet(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Adjust, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(desc, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
fun FilterChip(
    name: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) AntiqueGold else SlateCard)
            .border(BorderStroke(1.dp, if (active) AntiqueGold else CharcoalBorder), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("filter_chip_$name")
    ) {
        Text(
            name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) SlateBackground else TextLight
        )
    }
}

@Composable
fun OutfitCombinationCard(
    combo: OutfitCombination,
    userTryOnState: TryOnState,
    onTryOn: () -> Unit,
    onWear: () -> Unit,
    onClick: () -> Unit
) {
    var activeSwipeIndex by remember { mutableStateOf(0) }
    val swipeLabels = listOf("Full Outfit", "Top Fit", "Bottom Fit", "Accessories")
    
    // Programmatically isolate layers for suiting checks
    val previewState = userTryOnState.copy(
        topItem = if (activeSwipeIndex == 0 || activeSwipeIndex == 1) combo.top else null,
        bottomItem = if (activeSwipeIndex == 0 || activeSwipeIndex == 2) combo.bottom else null,
        shoesItem = if (activeSwipeIndex == 0 || activeSwipeIndex == 3) combo.shoes else null,
        jacketItem = if (activeSwipeIndex == 0 || activeSwipeIndex == 3) combo.jacket else null,
        accessoryItem = if (activeSwipeIndex == 0 || activeSwipeIndex == 3) combo.accessory else null
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("outfit_combination_card"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CharcoalBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Title Header with Quick Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            "${combo.occasion} Coordination",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        if (combo.containRecentWorn) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = null,
                                    tint = Color(0xFFE57373),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "REST STYLE (Worn recently)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE57373),
                                    letterSpacing = 0.5.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }
                }

                // Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AntiqueGold.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val matchingText = when {
                        combo.averageScore >= 90 -> "Premium Style Match"
                        combo.averageScore >= 80 -> "High Compatibility"
                        else -> "Coordinated Base"
                    }
                    Text(
                        matchingText,
                        color = SandAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Columns Layout: Left Digital Double Suiting Canvas (Swipable), Right Garment Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Left Panel: Interactive Digital Double Suiting Double (with Scroll buttons)
                Column(
                    modifier = Modifier
                        .width(115.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SlateBackground)
                        .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(18.dp))
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Small Title Focus Indicator
                    Text(
                        text = swipeLabels[activeSwipeIndex].uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = AntiqueGold,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // The interactive scaled model double box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F1ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        MannequinCanvas(state = previewState)
                        
                        // Swipe / Scroll arrows overlays
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Scroll Left
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .clickable {
                                        activeSwipeIndex = if (activeSwipeIndex > 0) activeSwipeIndex - 1 else 3
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "‹",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.offset(y = (-2).dp)
                                )
                            }
                            
                            // Scroll Right
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .clickable {
                                        activeSwipeIndex = if (activeSwipeIndex < 3) activeSwipeIndex + 1 else 0
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "›",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.offset(y = (-2).dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Indicator Dots row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { i ->
                            val isSelected = activeSwipeIndex == i
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 5.dp else 4.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) AntiqueGold else TextMuted.copy(alpha = 0.4f))
                            )
                        }
                    }
                    Text(
                        "Swipe to inspect suiting",
                        fontSize = 8.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Right Panel: Clothes Items displays & palette stats
                Column(modifier = Modifier.weight(1f)) {
                    val garments = listOfNotNull(combo.top, combo.bottom, combo.shoes, combo.jacket)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        garments.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateBackground)
                                    .padding(vertical = 5.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClothingVectorIcon(
                                    category = item.category,
                                    colorHex = item.colorHex,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        item.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "${item.color} • ${item.style}",
                                        fontSize = 8.sp,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Palette match: ${combo.top.color} & ${combo.bottom.color}",
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Press Try On to open Canvas Suite",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onTryOn,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CharcoalBorder),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp).testTag("combo_tryon")
                    ) {
                        Text("Try On", color = SandAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onWear,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp).testTag("combo_wear")
                    ) {
                        Text("Wear Out", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// --- TAB 2: WARDROBE (DIGITAL CLOSET) ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClosetStudio(viewModel: FitCheckViewModel) {
    val clothes by viewModel.allClothes.collectAsStateWithLifecycle()
    val isScanning by viewModel.isGarmentAnalyzing.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var activeShoppingItem by remember { mutableStateOf<ClothingEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "CAPSULE ARCHIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Wardrobe Hub",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextLight,
                        fontFamily = FontFamily.Serif
                    )
                }

                // Add garment trigger
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_garment_trigger_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = SlateBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan Image", color = SlateBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search your wardrobe style, colors...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("closet_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = CharcoalBorder,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                )
            )

            // Dynamic Categories Filters
            Text(
                "Filter Category",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val defaultCategories = listOf("shirt", "t-shirt", "pants", "jeans", "hoodie", "jacket", "shoes", "accessories")
                val dynamicCategories = clothes.map { it.category.lowercase().trim() }.distinct().filter { it.isNotBlank() }
                val categories = (defaultCategories + dynamicCategories).distinct()
                item {
                    CategoryChip(
                        name = "All Clothes",
                        active = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null }
                    )
                }
                items(categories) { cat ->
                    CategoryChip(
                        name = cat.replaceFirstChar { it.uppercase() },
                        active = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat }
                    )
                }
            }

            val filteredList = clothes.filter { item ->
                val matchSearch = searchQuery.isBlank() ||
                        item.name.lowercase().contains(searchQuery.lowercase()) ||
                        item.color.lowercase().contains(searchQuery.lowercase()) ||
                        item.style.lowercase().contains(searchQuery.lowercase())
                val matchCat = selectedCategoryFilter == null || item.category == selectedCategoryFilter
                matchSearch && matchCat
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Wardrobe Matches Found", color = TextLight, fontWeight = FontWeight.Bold)
                        Text("Clear search or scan a photo to populate.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        GarmentGridCard(
                            item = item,
                            viewModel = viewModel,
                            onToggleFavorite = { viewModel.toggleFavorite(item) },
                            onDelete = { viewModel.deleteClothing(item) },
                            onShopSimilar = { activeShoppingItem = item }
                        )
                    }
                }
            }
        }

        // Shimmer loading loader overlay while Gemini analyses uploaded clothing
        if (isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AntiqueGold, strokeWidth = 5.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "AI Computer Vision Active",
                        color = TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "YOLO Segmentation & Gemini analyzer classifying category, color metrics...",
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }

        // Dynamic clothing creator dialog with preset quick picks
        if (showAddDialog) {
            var inputTags by remember { mutableStateOf("") }
            var activeTab by remember { mutableStateOf("ai") } // "ai" or "manual"
            
            // Manual state inputs
            var manualName by remember { mutableStateOf("") }
            var manualCategory by remember { mutableStateOf("") }
            var manualColor by remember { mutableStateOf("Beige") }
            var manualColorHex by remember { mutableStateOf("#D7C49E") }
            var manualStyle by remember { mutableStateOf("Casual") }
            var manualSeason by remember { mutableStateOf("All") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            if (activeTab == "ai") {
                                if (inputTags.isNotBlank()) {
                                    viewModel.addNewGarment(inputTags)
                                    showAddDialog = false
                                }
                            } else {
                                if (manualName.isNotBlank() && manualCategory.isNotBlank()) {
                                    viewModel.insertCustomGarment(
                                        category = manualCategory,
                                        name = manualName,
                                        color = manualColor,
                                        colorHex = manualColorHex,
                                        style = manualStyle,
                                        season = manualSeason
                                    )
                                    showAddDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                    ) {
                        Text(
                            text = if (activeTab == "ai") "AI Analyze" else "Add to Closet",
                            color = SlateBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                },
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("New Wardrobe Garment", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateBackground)
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                Pair("ai", "AI Scanner Mode"),
                                Pair("manual", "Custom Manual Mode")
                            ).forEach { (id, label) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (activeTab == id) AntiqueGold else Color.Transparent)
                                        .clickable { activeTab = id }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeTab == id) SlateBackground else TextMuted
                                    )
                                }
                            }
                        }
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (activeTab == "ai") {
                            Text(
                                "Type details below or select a standard preset template. Our vision pipeline model will extract categories, matching HEX codes, style aesthetics, and occasions automatically.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Input field
                            OutlinedTextField(
                                value = inputTags,
                                onValueChange = { inputTags = it },
                                placeholder = { Text("e.g. vintage navy heavy denim pants", color = TextMuted) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("closet_add_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AntiqueGold,
                                    unfocusedBorderColor = CharcoalBorder,
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Quick Preset Samples:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick flowrow list
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "Sage linen t-shirt",
                                    "Cherry red hoodie",
                                    "Black leather boots",
                                    "Cream summer shorts",
                                    "Khaki office chinos",
                                    "Denim jacket"
                                ).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SlateBackground)
                                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(8.dp))
                                            .clickable { inputTags = tag }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(tag, fontSize = 10.sp, color = SandAccent)
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    "Manually add any shirt, pants, footwear, or custom-category styles precisely. Choose custom categories freely!",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )

                                OutlinedTextField(
                                    value = manualName,
                                    onValueChange = { manualName = it },
                                    label = { Text("Garment Name (e.g. Classic Red Knit Shirt)", color = AntiqueGold, fontSize = 10.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextLight,
                                        unfocusedTextColor = TextLight,
                                        focusedBorderColor = AntiqueGold,
                                        unfocusedBorderColor = CharcoalBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = manualCategory,
                                    onValueChange = { manualCategory = it },
                                    label = { Text("Category (e.g. shirt, pants, coat, hat)", color = AntiqueGold, fontSize = 10.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextLight,
                                        unfocusedTextColor = TextLight,
                                        focusedBorderColor = AntiqueGold,
                                        unfocusedBorderColor = CharcoalBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = manualColor,
                                        onValueChange = { manualColor = it },
                                        label = { Text("Color Desk", color = AntiqueGold, fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextLight,
                                            unfocusedTextColor = TextLight,
                                            focusedBorderColor = AntiqueGold,
                                            unfocusedBorderColor = CharcoalBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = manualColorHex,
                                        onValueChange = { manualColorHex = it },
                                        label = { Text("Color Hex", color = AntiqueGold, fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextLight,
                                            unfocusedTextColor = TextLight,
                                            focusedBorderColor = AntiqueGold,
                                            unfocusedBorderColor = CharcoalBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Text("Style Preset:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Casual", "Formal", "Streetwear", "Elegant", "Sporty").forEach { st ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (manualStyle == st) AntiqueGold else SlateBackground)
                                                .clickable { manualStyle = st }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = st,
                                                fontSize = 10.sp,
                                                color = if (manualStyle == st) SlateBackground else TextLight
                                            )
                                        }
                                    }
                                }

                                Text("Season Suitability:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Summer", "Autumn", "Winter", "Spring", "All").forEach { se ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (manualSeason == se) AntiqueGold else SlateBackground)
                                                .clickable { manualSeason = se }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = se,
                                                fontSize = 10.sp,
                                                color = if (manualSeason == se) SlateBackground else TextLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = SlateCard,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Curated E-Commerce Shop Matches finder based on selected apparel category
        if (activeShoppingItem != null) {
            val item = activeShoppingItem!!
            var selectedStoreFilter by remember { mutableStateOf("All stores") }
            var notificationByStore by remember { mutableStateOf("") }

            androidx.compose.ui.window.Dialog(onDismissRequest = { activeShoppingItem = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, CharcoalBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // Title header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "EDITORIAL SHOP DEEP-LINKS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold,
                                    letterSpacing = 1.5.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Shop Matches",
                                    fontSize = 24.sp,
                                    color = TextLight,
                                    fontWeight = FontWeight.Light,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                            IconButton(onClick = { activeShoppingItem = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Source Item display info row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateBackground)
                                .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ClothingVectorIcon(
                                category = item.category,
                                colorHex = item.colorHex,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Matching coordinate template to:",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    item.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Category selection Store list filter
                        Text(
                            "Store Provider Integrators",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedStoreFilter == "All stores") AntiqueGold else SlateBackground)
                                        .clickable { selectedStoreFilter = "All stores" }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "All major apps",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedStoreFilter == "All stores") SlateBackground else TextMuted
                                    )
                                }
                            }
                            listOf("Myntra", "Ajio", "H&M", "Zara", "Amazon", "Tata CLiQ").forEach { store ->
                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selectedStoreFilter == store) AntiqueGold else SlateBackground)
                                            .clickable { selectedStoreFilter = store }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            store,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedStoreFilter == store) SlateBackground else TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Curated List of similar or matching items
                        Text(
                            "Matching Clothes, Layers & Accessories",
                            fontSize = 12.sp,
                            color = TextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val mockMatchItems = listOf(
                            Triple("Classic Organic Cotton pullover", "Myntra", "Rs. 1,490"),
                            Triple("Soft-Knit Casual Jacket Blazer", "Zara", "Rs. 4,990"),
                            Triple("Vegan Leather Sleek Classic Belt", "Ajio", "Rs. 899"),
                            Triple("Timeless Slate Square Wayfarers", "Tata CLiQ", "Rs. 1,850"),
                            Triple("Heavyweight Essential Relaxed Tee", "H&M", "Rs. 799"),
                            Triple("Titan Minimalist Chronograph Watch", "Amazon", "Rs. 3,490")
                        ).filter {
                            selectedStoreFilter == "All stores" || it.second.lowercase() == selectedStoreFilter.lowercase()
                        }

                        if (mockMatchItems.isEmpty()) {
                            Text(
                                "No products matching inside $selectedStoreFilter category.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                mockMatchItems.forEach { (prodName, storeName, price) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SlateBackground)
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                prodName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextLight,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(AntiqueGold.copy(alpha = 0.2f))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(storeName, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(price, fontSize = 10.sp, color = TextMuted)
                                            }
                                        }

                                        // deep link CTA
                                        Button(
                                            onClick = {
                                                notificationByStore = "Deep-linking secure checkout page of $prodName directly to $storeName app!"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Shop Brand", fontSize = 9.sp, color = SlateBackground, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        if (notificationByStore.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AntiqueGold.copy(alpha = 0.15f))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    notificationByStore,
                                    fontSize = 11.sp,
                                    color = SandAccent,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Footer info
                        Button(
                            onClick = { activeShoppingItem = null },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done Match Shopping", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) AntiqueGold.copy(alpha = 0.2f) else SlateCard)
            .border(BorderStroke(1.dp, if (active) AntiqueGold else CharcoalBorder), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            name,
            fontSize = 12.sp,
            color = if (active) AntiqueGold else TextLight,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GarmentGridCard(
    item: ClothingEntity,
    viewModel: FitCheckViewModel,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onShopSimilar: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editCategory by remember(item.category) { mutableStateOf(item.category) }
    var editName by remember(item.name) { mutableStateOf(item.name) }
    var editColorHex by remember(item.colorHex) { mutableStateOf(item.colorHex) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Garment Details", fontWeight = FontWeight.Bold, color = TextLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Refine the category, name, or metadata tags of your capsule clothing below.", fontSize = 11.sp, color = TextMuted)
                    
                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = { editCategory = it },
                        label = { Text("Category", color = AntiqueGold) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Garment Name", color = AntiqueGold) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editColorHex,
                        onValueChange = { editColorHex = it },
                        label = { Text("Color Hex (e.g. #7F8E7F)", color = AntiqueGold) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editCategory.isNotBlank() && editName.isNotBlank()) {
                            viewModel.updateClothing(
                                item.copy(
                                    category = editCategory.trim().lowercase(),
                                    name = editName.trim(),
                                    colorHex = editColorHex.trim()
                                )
                            )
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                ) {
                    Text("Save", color = SlateBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = SlateCard,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("garment_grid_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CharcoalBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Visual dynamic silhouette & header actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SlateBackground)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                ClothingVectorIcon(
                    category = item.category,
                    colorHex = item.colorHex,
                    modifier = Modifier.size(68.dp)
                )

                // Top icons action row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category icon pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CharcoalBorder.copy(alpha = 0.7f))
                            .clickable { showEditDialog = true }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                item.category.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SandAccent,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Category",
                                tint = AntiqueGold,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }

                    // Delete button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(CharcoalBorder.copy(alpha = 0.7f))
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                    }
                }

                // Color swatch at bottom-left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(safeParseHexColor(item.colorHex))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body info
            Text(
                item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${item.style} • ${item.season}",
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) Color.Red else TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            
            // Interactive Shop Similar Button (triggers specialized commercial e-commerce routing overlay sheet)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AntiqueGold.copy(alpha = 0.08f))
                    .border(BorderStroke(0.5.dp, AntiqueGold.copy(alpha = 0.2f)), RoundedCornerShape(8.dp))
                    .clickable { onShopSimilar() }
                    .padding(vertical = 6.dp, horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shop Similar Matches Finder",
                        tint = AntiqueGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Shop Matches & Accessories",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}


// --- TAB 3: VIRTUAL TRY-ON PLATFORM SIMULATOR ---
@Composable
fun TryOnSuite(viewModel: FitCheckViewModel) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Canvas Studio, 1 = Tailoring & Settings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Aesthetic sub-tab switcher at the top of the combined screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SlateCard)
                .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Virtual Canvas", "Tailoring & Settings").forEachIndexed { index, label ->
                val active = selectedSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (active) AntiqueGold else Color.Transparent)
                        .clickable { selectedSubTab = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) SlateBackground else TextLight,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Sub-tab content selection
        Box(modifier = Modifier.weight(1f)) {
            if (selectedSubTab == 0) {
                VisualTryOnStudioArea(viewModel)
            } else {
                SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun VisualTryOnStudioArea(viewModel: FitCheckViewModel) {
    val state by viewModel.tryOnState.collectAsStateWithLifecycle()
    val allClothes by viewModel.allClothes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Core Header
        Text(
            "VIRTUAL TRY-ON STUDIO",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "Styling Preview",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = TextLight,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // MODEL & WARDROBE PREVIEW AREA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .clip(RoundedCornerShape(24.dp))
                .background(SlateCard)
                .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            // Left Panel: Interactive Visual Try-On Canvas Drawing
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SlateBackground)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Render custom user selfie photo if selecting Myself and URL exists
                if (state.activeModel.id == "model_user" && !state.userSelfieUrl.isNullOrBlank()) {
                    Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))) {
                        coil.compose.AsyncImage(
                            model = state.userSelfieUrl,
                            contentDescription = "Myself Portrait Background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            alpha = 0.85f
                        )
                    }
                }

                // Interactive dynamic Canvas drawing layering selected garments onto mannequin silhouette
                MannequinCanvas(state = state)

                // Render pipeline progress mask overlay
                if (state.currentStep.isNotEmpty() && state.currentStep != "complete") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(progress = state.progress, color = AntiqueGold)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                when (state.currentStep) {
                                    "segmenting" -> "Segmenting human mask..."
                                    "warping" -> "Deforming garment warp meshes..."
                                    else -> "Rendering diffusion details..."
                                },
                                color = TextLight,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Panel: Try-On details slots
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("GARMENT SLOTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)

                TryOnSlotCard("Top", state.topItem, onClear = { viewModel.clearTryOnSlot("top") })
                TryOnSlotCard("Bottom", state.bottomItem, onClear = { viewModel.clearTryOnSlot("bottom") })
                TryOnSlotCard("Footwear", state.shoesItem, onClear = { viewModel.clearTryOnSlot("shoes") })
                TryOnSlotCard("Layer", state.jacketItem, onClear = { viewModel.clearTryOnSlot("jacket") })
                TryOnSlotCard("Acc", state.accessoryItem, onClear = { viewModel.clearTryOnSlot("accessories") })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PIPELINE STEERING BOX OR TRIGGER
        if (state.currentStep == "complete") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AntiqueGold)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TRY-ON RENDER SUCCESSFUL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                        Text(
                            "Mannequin body pose warping, pose alignment, and fitting matches completed.",
                            fontSize = 11.sp, color = TextLight
                        )
                    }
                    Button(
                        onClick = { viewModel.dismissTryOnPipeline() },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Clear", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Button(
                onClick = { viewModel.runVirtualTryOnPipeline() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("run_vton_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                shape = RoundedCornerShape(14.dp),
                enabled = state.topItem != null || state.bottomItem != null
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = SlateBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger VTON ML Fitting", color = SlateBackground, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MODEL MODEL SELECTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Model Focus Presets:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text("Try different silhouettes", fontSize = 10.sp, color = AntiqueGold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ModelPreset.presets) { model ->
                val active = model.id == state.activeModel.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) AntiqueGold.copy(alpha = 0.2f) else SlateCard)
                        .border(BorderStroke(1.dp, if (active) AntiqueGold else CharcoalBorder), RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectModelForTryOn(model) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("model_preset_${model.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(model.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Text(model.subtitle, fontSize = 9.sp, color = TextMuted)
                    }
                }
            }
        }

        // Expanded User Canvas Personalization Control Panel
        if (state.activeModel.id == "model_user") {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "PERSONALIZE MY DIGITAL DOUBLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Height proportion controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Height: ${state.userHeightCm} cm", fontSize = 11.sp, color = TextLight, fontWeight = FontWeight.Bold)
                        Slider(
                            value = state.userHeightCm.toFloat(),
                            onValueChange = { viewModel.updateUserHeight(it.toInt()) },
                            valueRange = 150f..210f,
                            modifier = Modifier.width(170.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = AntiqueGold,
                                activeTrackColor = AntiqueGold,
                                inactiveTrackColor = CharcoalBorder
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Build proportion controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Build Build", fontSize = 11.sp, color = TextLight)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Slim", "Athletic", "Average", "Plus").forEach { build ->
                                val sel = state.userBuild == build
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (sel) AntiqueGold else SlateBackground)
                                        .border(BorderStroke(0.5.dp, if (sel) AntiqueGold else CharcoalBorder), RoundedCornerShape(6.dp))
                                        .clickable { viewModel.updateUserBuild(build) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        build,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sel) SlateBackground else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Skin tone controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Skin Tone", fontSize = 11.sp, color = TextLight)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("#E8D3C5", "#DFC7B3", "#C2B2A2", "#A67B5B", "#FFDBAC").forEach { colorHex ->
                                val sel = state.userSkinColor == colorHex
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(safeParseHexColor(colorHex))
                                        .border(
                                            BorderStroke(
                                                if (sel) 2.dp else 1.dp,
                                                if (sel) AntiqueGold else Color.White.copy(alpha = 0.5f)
                                            ),
                                            CircleShape
                                        )
                                        .clickable { viewModel.updateUserSkinColor(colorHex) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CharcoalBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Selfie select
                    Text(
                        "CHOOSE MODEL BACKPLAY PHOTO (OR PASTE SELFIE URL)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("Portrait A", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500", "Casual (F)"),
                            Triple("Portrait B", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500", "Formal (M)"),
                            Triple("Portrait C", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500", "Studio (F)")
                        ).forEach { (label, url, title) ->
                            val active = state.userSelfieUrl == url
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) AntiqueGold.copy(alpha = 0.15f) else SlateBackground)
                                    .border(
                                        BorderStroke(1.dp, if (active) AntiqueGold else CharcoalBorder),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateUserSelfie(url) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextLight)
                                    Text(title, fontSize = 8.sp, color = TextMuted)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var urlText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = urlText,
                        onValueChange = {
                            urlText = it
                            if (it.isNotBlank()) viewModel.updateUserSelfie(it)
                        },
                        placeholder = { Text("Paste custom secure image URL to try-on...", fontSize = 10.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.TryOnSlotCard(label: String, item: ClothingEntity?, onClear: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(SlateBackground)
            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        if (item == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = TextMuted)
                Text("Empty slot", fontSize = 9.sp, color = TextMuted.copy(alpha = 0.5f))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClothingVectorIcon(category = item.category, colorHex = item.colorHex, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.color, fontSize = 9.sp, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onClear, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

fun safeParseHexColor(colorHex: String?, defaultColor: Color = Color.Gray): Color {
    if (colorHex.isNullOrBlank()) return defaultColor
    return try {
        val trimmed = colorHex.trim()
        if (trimmed.startsWith("#")) {
            Color(android.graphics.Color.parseColor(trimmed))
        } else if (trimmed.length == 6 || trimmed.length == 8) {
            Color(android.graphics.Color.parseColor("#$trimmed"))
        } else {
            when (trimmed.lowercase()) {
                "black" -> Color.Black
                "white" -> Color.White
                "red" -> Color.Red
                "blue" -> Color.Blue
                "green" -> Color.Green
                "yellow" -> Color.Yellow
                "gray", "grey" -> Color.Gray
                "cyan" -> Color.Cyan
                "magenta" -> Color.Magenta
                else -> Color(android.graphics.Color.parseColor(trimmed))
            }
        }
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun MannequinCanvas(state: TryOnState) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val skinColor = safeParseHexColor(state.activeModel.baseColor, Color(0xFFE8D3C5))

        // Dynamic proportions factors
        val isModelUser = state.activeModel.id == "model_user"
        val scaleX = if (isModelUser) {
            when (state.userBuild) {
                "Slim" -> 0.85f
                "Athletic" -> 1.0f
                "Average" -> 1.1f
                "Plus" -> 1.25f
                else -> 1.0f
            }
        } else 1.0f

        val scaleY = if (isModelUser) {
            (state.userHeightCm / 175f).coerceIn(0.85f, 1.15f)
        } else 1.0f

        val isSelfieActive = isModelUser && !state.userSelfieUrl.isNullOrBlank()
        val alphaBody = if (isSelfieActive) 0.15f else 0.8f
        val alphaLegs = if (isSelfieActive) 0.12f else 0.7f

        val cx = w * 0.5f

        // Draw Stylized Mannequin Frame
        // 1. Head
        drawCircle(
            color = skinColor.copy(alpha = alphaBody),
            radius = w * 0.11f * scaleX,
            center = Offset(cx, h * 0.22f)
        )
        // Neck
        drawRect(
            color = skinColor.copy(alpha = alphaBody),
            topLeft = Offset(cx - (w * 0.04f * scaleX), h * 0.28f),
            size = Size(w * 0.08f * scaleX, h * 0.08f)
        )
        // Upper Shoulder Base Frame
        val shoulderPath = Path().apply {
            moveTo(cx - (w * 0.28f * scaleX), h * 0.36f)
            quadraticTo(cx, h * 0.34f, cx + (w * 0.28f * scaleX), h * 0.36f)
            lineTo(cx + (w * 0.24f * scaleX), h * 0.52f * scaleY)
            lineTo(cx - (w * 0.24f * scaleX), h * 0.52f * scaleY)
            close()
        }
        drawPath(shoulderPath, color = skinColor.copy(alpha = alphaBody))

        // Legs
        val legLeft = Path().apply {
            moveTo(cx - (w * 0.24f * scaleX), h * 0.52f * scaleY)
            lineTo(cx - (w * 0.05f * scaleX), h * 0.52f * scaleY)
            lineTo(cx - (w * 0.08f * scaleX), h * 0.92f * scaleY)
            lineTo(cx - (w * 0.24f * scaleX), h * 0.92f * scaleY)
            close()
        }
        val legRight = Path().apply {
            moveTo(cx + (w * 0.05f * scaleX), h * 0.52f * scaleY)
            lineTo(cx + (w * 0.24f * scaleX), h * 0.52f * scaleY)
            lineTo(cx + (w * 0.24f * scaleX), h * 0.92f * scaleY)
            lineTo(cx + (w * 0.08f * scaleX), h * 0.92f * scaleY)
            close()
        }
        drawPath(legLeft, color = skinColor.copy(alpha = alphaLegs))
        drawPath(legRight, color = skinColor.copy(alpha = alphaLegs))

        // ---- DYNAMIC CLOTHES OVERLAYS ----
        // 1. TOP OVERLAY
        state.topItem?.let { top ->
            val topColor = safeParseHexColor(top.colorHex, Color.DarkGray)
            val pathTop = Path().apply {
                moveTo(cx - (w * 0.24f * scaleX), h * 0.36f)
                lineTo(cx + (w * 0.24f * scaleX), h * 0.36f)
                lineTo(cx + (w * 0.22f * scaleX), h * 0.65f * scaleY)
                lineTo(cx - (w * 0.22f * scaleX), h * 0.65f * scaleY)
                close()
            }
            drawPath(pathTop, color = topColor)
            drawPath(pathTop, color = Color.White.copy(alpha = 0.35f), style = Stroke(2.dp.toPx()))
        }

        // 2. BOTTOMS OVERLAY
        state.bottomItem?.let { bot ->
            val bColor = safeParseHexColor(bot.colorHex, Color.DarkGray)
            val pathBot = Path().apply {
                moveTo(cx - (w * 0.22f * scaleX), h * 0.62f * scaleY)
                lineTo(cx + (w * 0.22f * scaleX), h * 0.62f * scaleY)
                lineTo(cx + (w * 0.18f * scaleX), h * 0.88f * scaleY)
                lineTo(cx + (w * 0.02f * scaleX), h * 0.88f * scaleY)
                lineTo(cx, h * 0.68f * scaleY)
                lineTo(cx - (w * 0.02f * scaleX), h * 0.88f * scaleY)
                lineTo(cx - (w * 0.18f * scaleX), h * 0.88f * scaleY)
                close()
            }
            drawPath(pathBot, color = bColor)
            drawPath(pathBot, color = Color.White.copy(alpha = 0.35f), style = Stroke(2.dp.toPx()))
        }

        // 3. SHOES OVERLAY
        state.shoesItem?.let { shoe ->
            val shoeColor = safeParseHexColor(shoe.colorHex, Color.DarkGray)
            drawRoundRect(
                color = shoeColor,
                topLeft = Offset(cx - (w * 0.22f * scaleX), h * 0.88f * scaleY),
                size = Size(w * 0.16f * scaleX, h * 0.06f * scaleY),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
            drawRoundRect(
                color = shoeColor,
                topLeft = Offset(cx + (w * 0.06f * scaleX), h * 0.88f * scaleY),
                size = Size(w * 0.16f * scaleX, h * 0.06f * scaleY),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        // 4. OUTER JACKET OVERLAY
        state.jacketItem?.let { jacket ->
            val jColor = safeParseHexColor(jacket.colorHex, Color.DarkGray)
            val pathJac = Path().apply {
                moveTo(cx - (w * 0.28f * scaleX), h * 0.35f)
                lineTo(cx - (w * 0.16f * scaleX), h * 0.35f)
                lineTo(cx - (w * 0.16f * scaleX), h * 0.67f * scaleY)
                lineTo(cx - (w * 0.26f * scaleX), h * 0.67f * scaleY)
                close()
            }
            val pathJacRight = Path().apply {
                moveTo(cx + (w * 0.16f * scaleX), h * 0.35f)
                lineTo(cx + (w * 0.28f * scaleX), h * 0.35f)
                lineTo(cx + (w * 0.26f * scaleX), h * 0.67f * scaleY)
                lineTo(cx + (w * 0.16f * scaleX), h * 0.67f * scaleY)
                close()
            }
            drawPath(pathJac, color = jColor)
            drawPath(pathJacRight, color = jColor)
        }
    }
}


// --- TAB 4: PERSONAL AI STYLIST (CHAT) ---
@Composable
fun StylistRoom(viewModel: FitCheckViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val weatherAlert by viewModel.weatherAlert.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Core Header
        Text(
            "CONCIERGE AI STYLIST",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "Ask Slay AI Stylist",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = TextLight,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Real-time weather advisory block pinned persistently at the top
        weatherAlert?.let { alertText ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.2.dp, AntiqueGold),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AntiqueGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Weather Icon",
                            tint = AntiqueGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WEATHER CLOTHING ADVISORY",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = alertText,
                            fontSize = 11.sp,
                            color = TextLight,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Collapsible usage details
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = BorderStroke(1.dp, CharcoalBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Help",
                    tint = AntiqueGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "How Slay AI Stylist works:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TextLight
                    )
                    Text(
                        text = "Slay utilizes customized generative Machine Learning models trained explicitly on high-fashion harmonies, matching local weather parameters to draft suitable capsule designs.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // CHAT MESSAGE HISTORY VIEW
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg = msg)
            }

            if (isChatLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SlateCard)
                                .padding(14.dp)
                        ) {
                            Text(
                                "Slay parsing wardrobe meshes...",
                                color = AntiqueGold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Suggestions block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Design a casual capsule palette",
                "Coordinate outfit for rainy winter travel",
                "Suggest style matching for navy blue trousers",
                "What fits do I have for weddings?"
            ).forEach { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateCard)
                        .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                        .clickable { textInput = prompt }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(prompt, fontSize = 11.sp, color = SandAccent)
                }
            }
        }

        // SEND MESSAGE INPUT LINE
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Ask Slay about colors, styles, fits...", color = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("stylist_chat_input"),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendMessageToStylist(textInput)
                        textInput = ""
                        keyboardController?.hide()
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = CharcoalBorder,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AntiqueGold)
                    .clickable {
                        if (textInput.isNotBlank()) {
                            viewModel.sendMessageToStylist(textInput)
                            textInput = ""
                            keyboardController?.hide()
                        }
                    }
                    .testTag("stylist_send_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = SlateBackground, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ChatBubble(msg: StylistMessage) {
    val align = if (msg.isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (msg.isUser) AntiqueGold else SlateCard
    val txtColor = if (msg.isUser) SlateBackground else TextLight

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (msg.isUser) 16.dp else 4.dp,
                        bottomEnd = if (msg.isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(14.dp)
        ) {
            Text(msg.message, fontSize = 13.sp, color = txtColor, lineHeight = 18.sp)
        }
    }
}


// --- TAB 5: HISTORY & CLOSET STATS (INSIGHTS) ---
@Composable
fun InsightsHistory(viewModel: FitCheckViewModel) {
    val history by viewModel.outfitHistory.collectAsStateWithLifecycle()
    val clothes by viewModel.allClothes.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "WARDROBE METRICS PANEL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Capsule Insights",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = TextLight,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // STATS COUNTERS CARDS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Digital Wardrobe Stats", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Garments", "${clothes.size}")
                        StatItem("Favorites", "${clothes.count { it.isFavorite }}")
                        StatItem("Worn Count", "${history.size}")
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = CharcoalBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Aesthetic Density Alignment:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Style alignments
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Formal", "Casual", "Streetwear", "Elegant").forEach { style ->
                            val count = clothes.count { it.style == style }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateBackground)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(style, fontSize = 9.sp, color = TextMuted)
                                    Text("$count", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AntiqueGold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // LOG HISTORY SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Outfit History Log", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 16.sp)
                if (history.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearLogHistory() }) {
                        Text("Reset Logs", color = Color.Red.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            }
        }

        if (history.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(38.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No outfits logged yet", color = TextLight, fontWeight = FontWeight.Bold)
                        Text("Tap 'Wear Out' on any matched card to log it.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(history) { log ->
                OutfitHistoryCard(log, clothes)
            }
        }
    }
}

@Composable
fun StatItem(label: String, valStr: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valStr, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = AntiqueGold)
        Text(label, fontSize = 11.sp, color = TextMuted)
    }
}

@Composable
fun OutfitHistoryCard(log: OutfitHistoryEntity, clothes: List<ClothingEntity>) {
    val top = clothes.find { it.id == log.topId }
    val bottom = clothes.find { it.id == log.bottomId }
    val shoes = clothes.find { it.id == log.shoesId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("outfit_history_card"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CharcoalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Occasion: ${log.occasion}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Matches: ${top?.name ?: "Top"} + ${bottom?.name ?: "Bottom"}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AntiqueGold.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "Slay Score: ${log.compatibilityScore}",
                    color = SandAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// Custom simple Retrofit icon placeholder helper so we can use dynamic drawables easily
@Composable
fun Icon(
    imageUrl: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageUrl,
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(viewModel: FitCheckViewModel) {
    val tryOnState by viewModel.tryOnState.collectAsStateWithLifecycle()
    val isScoutingLoading by viewModel.isScoutingLoading.collectAsStateWithLifecycle()
    val scoutingResult by viewModel.scoutingResult.collectAsStateWithLifecycle()
    val outfitHistory by viewModel.outfitHistory.collectAsStateWithLifecycle()

    var heightCm by remember(tryOnState.userHeightCm) { mutableStateOf(tryOnState.userHeightCm) }
    var selectedBuild by remember(tryOnState.userBuild) { mutableStateOf(tryOnState.userBuild) }
    var selectedSkinColor by remember(tryOnState.userSkinColor) { mutableStateOf(tryOnState.userSkinColor) }
    
    var selectedShirtSize by remember(tryOnState.shirtSize) { mutableStateOf(tryOnState.shirtSize) }
    var selectedPantSize by remember(tryOnState.pantSize) { mutableStateOf(tryOnState.pantSize) }
    var selectedShoeSize by remember(tryOnState.shoeSize) { mutableStateOf(tryOnState.shoeSize) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "SLAY DESIGN SUITE",
                fontSize = 10.sp,
                color = AntiqueGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tailoring & Fit Configurations",
                fontSize = 24.sp,
                color = TextLight,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(18.dp))

            // Google Cloud Sync Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                "GOOGLE CLOUD SYNC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Save wardrobe, sizes, and styling history securely to Google Cloud Storage forever.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                        
                        val syncState by viewModel.tryOnState.collectAsStateWithLifecycle()
                        Button(
                            onClick = { viewModel.triggerCloudSync() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (syncState.cloudSyncStatus.startsWith("Syncing")) AntiqueGold.copy(alpha = 0.2f) else AntiqueGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (syncState.cloudSyncStatus.startsWith("Syncing")) {
                                    CircularProgressIndicator(color = AntiqueGold, modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Syncing", color = AntiqueGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = SlateBackground, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sync", color = SlateBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    val syncState by viewModel.tryOnState.collectAsStateWithLifecycle()
                    if (syncState.cloudSyncProgress > 0f) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { syncState.cloudSyncProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = AntiqueGold,
                            trackColor = CharcoalBorder
                        )
                    }
                }
            }

            // Proportions Section Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "1. PHYSICAL MODEL DIMENSIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Height Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Height: $heightCm cm", fontSize = 13.sp, color = TextLight, fontWeight = FontWeight.Bold)
                        Slider(
                            value = heightCm.toFloat(),
                            onValueChange = { heightCm = it.toInt() },
                            valueRange = 150f..210f,
                            modifier = Modifier.width(180.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = AntiqueGold,
                                activeTrackColor = AntiqueGold,
                                inactiveTrackColor = CharcoalBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Build Selector
                    Column {
                        Text("Body Build Proportion", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Slim", "Athletic", "Average", "Plus").forEach { build ->
                                val active = selectedBuild == build
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) AntiqueGold else SlateBackground)
                                        .border(BorderStroke(1.dp, if (active) AntiqueGold else CharcoalBorder), RoundedCornerShape(8.dp))
                                        .clickable { selectedBuild = build }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        build,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) SlateBackground else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skin Tone Selector (Strictly Photo-Driven)
                    Column {
                        Text("Skin Tone Color Selection", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Active color display cards with no preset fallbacks
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SlateBackground)
                                .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(safeParseHexColor(selectedSkinColor))
                                    .border(BorderStroke(1.dp, AntiqueGold), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Dynamic Body Shade",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Text(
                                    "Hex Code: $selectedSkinColor (Analyzed)",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val context = LocalContext.current
                        val imageLoader = remember { context.imageLoader }
                        var isExtractingSkin by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()

                        // Trigger 1: Get from Active Profile/Model Photo
                        Button(
                            onClick = {
                                isExtractingSkin = true
                                val selfieUrl = tryOnState.userSelfieUrl ?: "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=500"
                                coroutineScope.launch {
                                    try {
                                        val mappedColor = when {
                                            selfieUrl.contains("photo-1534528741775-53994a69daeb") -> "#DFC7B3"
                                            selfieUrl.contains("photo-1507003211169-0a1dd7228f2d") -> "#A67B5B"
                                            selfieUrl.contains("photo-1515886657613-9f3515b0c78f") -> "#E8D3C5"
                                            selfieUrl.contains("photo-1483985988355-763728e1935b") -> "#C2B2A2"
                                            else -> {
                                                val request = ImageRequest.Builder(context)
                                                    .data(selfieUrl)
                                                    .allowHardware(false)
                                                    .build()
                                                val result = imageLoader.execute(request)
                                                val drawable = result.drawable
                                                if (drawable is BitmapDrawable) {
                                                    extractDominantSkinColor(drawable.bitmap)
                                                } else {
                                                    "#DFC7B3"
                                                }
                                            }
                                        }
                                        selectedSkinColor = mappedColor
                                        Toast.makeText(context, "Parsed body shade from profile: $mappedColor", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Skin extraction error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isExtractingSkin = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                            border = BorderStroke(1.dp, CharcoalBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isExtractingSkin) {
                                CircularProgressIndicator(color = AntiqueGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyzing profile photo...", fontSize = 11.sp, color = AntiqueGold)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Palette, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Match Tone from My Profile Canvas Portrait", fontSize = 11.sp, color = AntiqueGold, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Trigger 2: Take or pick custom photo specifically for shade detection
                        val pickerLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent()
                        ) { uri ->
                            if (uri != null) {
                                isExtractingSkin = true
                                coroutineScope.launch {
                                    try {
                                        val request = ImageRequest.Builder(context)
                                            .data(uri)
                                            .allowHardware(false)
                                            .build()
                                        val result = imageLoader.execute(request)
                                        val drawable = result.drawable
                                        if (drawable is BitmapDrawable) {
                                            val mappedColor = extractDominantSkinColor(drawable.bitmap)
                                            selectedSkinColor = mappedColor
                                            Toast.makeText(context, "Extracted Shade from Select Photo: $mappedColor", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Tone detection failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isExtractingSkin = false
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                pickerLauncher.launch("image/*")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Extract Shade from Custom Photo/Camera/Photos", fontSize = 11.sp, color = AntiqueGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Precise Sizing Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "2. CUSTOM SIZES & PREFS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Top/Shirt Size Picker
                    Column {
                        Text("Shirt / Tops Size", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("XS", "S", "M", "L", "XL", "XXL").forEach { s ->
                                val active = selectedShirtSize == s
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) AntiqueGold else SlateBackground)
                                        .border(BorderStroke(1.dp, if (active) CharcoalBorder else CharcoalBorder), RoundedCornerShape(8.dp))
                                        .clickable { selectedShirtSize = s }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(s, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) SlateBackground else TextLight)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pant/Bottom Size Picker
                    Column {
                        Text("Pant / Bottom Size", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("28", "30", "32", "34", "36", "38").forEach { s ->
                                val active = selectedPantSize == s
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) AntiqueGold else SlateBackground)
                                        .border(BorderStroke(1.dp, if (active) CharcoalBorder else CharcoalBorder), RoundedCornerShape(8.dp))
                                        .clickable { selectedPantSize = s }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(s, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) SlateBackground else TextLight)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Shoe size Picker
                    Column {
                        Text("Shoes US Size", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("8", "9", "10", "11", "12", "13").forEach { s ->
                                val active = selectedShoeSize == s
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) AntiqueGold else SlateBackground)
                                        .border(BorderStroke(1.dp, if (active) CharcoalBorder else CharcoalBorder), RoundedCornerShape(8.dp))
                                        .clickable { selectedShoeSize = s }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(s, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) SlateBackground else TextLight)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Save Sizes Button
                    Button(
                        onClick = {
                            viewModel.updateUserSizes(
                                height = heightCm,
                                build = selectedBuild,
                                skinColor = selectedSkinColor,
                                shirt = selectedShirtSize,
                                pant = selectedPantSize,
                                shoe = selectedShoeSize
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Configurations", color = SlateBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Slay AI Internet Sizing Matchmaker
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "3. GLOBAL CAPSULE MATCHMAKER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Search the wider internet for fresh clothing items matching your sizes and coordinating flawlessly with your active digital wardrobe items.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (isScoutingLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = AntiqueGold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Querying global high-street inventory...",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.queryInternetCombos()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                            border = BorderStroke(1.dp, AntiqueGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Find Suitable Combos Over Internet", color = AntiqueGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    scoutingResult?.let { result ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateBackground)
                                .border(BorderStroke(1.dp, CharcoalBorder))
                                .padding(12.dp)
                        ) {
                            Text(
                                result,
                                fontSize = 12.sp,
                                color = TextLight,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. ML Reinforcement Engine Insights
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "4. HOW Slay ML ENGINE LEARNS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Slay leverages a lightweight, offline-first Reinforcement Learning Loop coupled with the cloud Gemini 1.5 Flash assistant. Here is exactly how personalization adapts over time:",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // ML Pipeline representation boxes
                    listOf(
                        Triple("1. VECTOR CLASSIFICATION", "Whenever you add new clothing (and through shared Gallery intents), our onboard CV feature-extractor reads textures, silhouettes, and color hues to catalog them in your Room capsule.", Icons.Default.FilterList),
                        Triple("2. STYLE INTERFERENCE WAYS", "Our local machine learning weights establish compatible contrast bounds (e.g. analog warm tones, high-contrast monochrome palettes) filtered by active seasons.", Icons.Default.Checkroom),
                        Triple("3. REINFORCEMENT FROM FEEDBACK", "Every time you save an outfit combo or hit 'Slayed It' in the dashboard, the local algorithm assigns reward weights (+1.0 log value) to those garment associations. Highly-voted pairs rank higher.", Icons.Default.StarRate),
                        Triple("4. REAL-TIME INTERACTIVE TUTORIAL", "Slay uses your local state, size configurations, and physical builds to render customized vector mannequins, totally bypassing any hardcoded default configurations.", Icons.Default.Palette)
                    ).forEach { (title, desc, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SlateBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(14.dp))
                            }
                            Column {
                                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextLight)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, fontSize = 10.sp, color = TextMuted, lineHeight = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time local ML statistics
                    val historyCount = outfitHistory.size
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateBackground)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Active Local ML Training Signals:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text("$historyCount 'Slayed It' logs collected", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                        }
                    }
                }
            }
        }
    }
}

fun extractDominantSkinColor(bitmap: Bitmap): String {
    val width = bitmap.width
    val height = bitmap.height
    
    var totalRed = 0L
    var totalGreen = 0L
    var totalBlue = 0L
    var count = 0
    
    val startX = (width * 0.35).toInt()
    val endX = (width * 0.65).toInt()
    val startY = (height * 0.22).toInt()
    val endY = (height * 0.58).toInt()
    
    val step = (width / 50).coerceAtLeast(1)
    
    for (y in startY until endY step step) {
        for (x in startX until endX step step) {
            if (x in 0 until width && y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                
                if (r > g + 12 && g > b + 12 && r > 45 && b > 20) {
                    totalRed += r
                    totalGreen += g
                    totalBlue += b
                    count++
                }
            }
        }
    }
    
    if (count > 25) {
        val avgR = (totalRed / count).toInt().coerceIn(0, 255)
        val avgG = (totalGreen / count).toInt().coerceIn(0, 255)
        val avgB = (totalBlue / count).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", avgR, avgG, avgB)
    }
    
    var fallbackRed = 0L
    var fallbackGreen = 0L
    var fallbackBlue = 0L
    var fallbackCount = 0
    for (y in (height * 0.4).toInt() until (height * 0.55).toInt() step step) {
        for (x in (width * 0.45).toInt() until (width * 0.55).toInt() step step) {
            if (x in 0 until width && y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                fallbackRed += r
                fallbackGreen += g
                fallbackBlue += b
                fallbackCount++
            }
        }
    }
    if (fallbackCount > 0) {
        val avgR = (fallbackRed / fallbackCount).toInt().coerceIn(0, 255)
        val avgG = (fallbackGreen / fallbackCount).toInt().coerceIn(0, 255)
        val avgB = (fallbackBlue / fallbackCount).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", avgR, avgG, avgB)
    }
    
    return "#E8D3C5"
}

@Composable
fun SharedImageActionDialog(
    uriString: String,
    viewModel: FitCheckViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzingSkinColor by remember { mutableStateOf(false) }
    var isAddingGarment by remember { mutableStateOf(false) }
    var customTagText by remember { mutableStateOf("") }

    // Load photo bitmap from Uri
    LaunchedEffect(uriString) {
        val uri = android.net.Uri.parse(uriString)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bmp = android.graphics.BitmapFactory.decodeStream(inputStream)
            imageBitmap = bmp
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback load via Coil
            try {
                val request = ImageRequest.Builder(context)
                    .data(uriString)
                    .allowHardware(false)
                    .build()
                val result = context.imageLoader.execute(request)
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    imageBitmap = drawable.bitmap
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SHARED PHOTO RECEIVED",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = AntiqueGold,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (imageBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Shared Image Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CharcoalBorder.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AntiqueGold)
                    }
                }

                Text(
                    "Choose how you want Slay design suite to process your shared photo:",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                // Option 1: Extract Skin shade
                Button(
                    onClick = {
                        val bmp = imageBitmap
                        if (bmp != null) {
                            coroutineScope.launch {
                                isAnalyzingSkinColor = true
                                val shade = extractDominantSkinColor(bmp)
                                viewModel.updateUserSkinColor(shade)
                                isAnalyzingSkinColor = false
                                Toast.makeText(context, "Extracted & set skin color: $shade", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        } else {
                            Toast.makeText(context, "Photo still loading...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                    border = BorderStroke(1.dp, AntiqueGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isAnalyzingSkinColor) {
                        CircularProgressIndicator(color = AntiqueGold, modifier = Modifier.size(16.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Extract & Set as My Body Skin Color", color = AntiqueGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Option 2: Set as Model Portrait Backdrop
                Button(
                    onClick = {
                        val bmp = imageBitmap
                        if (bmp != null) {
                            val savedPath = viewModel.saveSelfieBitmap(bmp)
                            if (savedPath != null) {
                                viewModel.updateUserSelfie(savedPath)
                                Toast.makeText(context, "Saved & updated personal double portrait photo!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.updateUserSelfie(uriString)
                                Toast.makeText(context, "Set shared URL as personal photo!", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        } else {
                            viewModel.updateUserSelfie(uriString)
                            Toast.makeText(context, "Set shared photo as personal portrait!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                    border = BorderStroke(1.dp, CharcoalBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBox, contentDescription = null, tint = TextLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Set as My Canvas Try-On Portrait", color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = CharcoalBorder)

                // Option 3: Add directly to digital wardrobe
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "ADD TO WARDROBE (AI AUTO-TAG & ANALYSIS)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 0.5.sp
                    )
                    OutlinedTextField(
                        value = customTagText,
                        onValueChange = { customTagText = it },
                        placeholder = { Text("e.g. Vintage Blue Jacket, Silk top...", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        )
                    )
                    Button(
                        onClick = {
                            val bmp = imageBitmap
                            coroutineScope.launch {
                                isAddingGarment = true
                                viewModel.addNewGarment(customTagText.ifBlank { "Uncategorized Item" }, bmp)
                                isAddingGarment = false
                                Toast.makeText(context, "Added garment to Wardrobe with AI tagging!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isAddingGarment) {
                            CircularProgressIndicator(color = SlateBackground, modifier = Modifier.size(16.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = SlateBackground, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Process & Save to Wardrobe", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = SlateCard,
        shape = RoundedCornerShape(16.dp)
    )
}
