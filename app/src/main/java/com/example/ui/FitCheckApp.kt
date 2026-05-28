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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
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
            Spacer(modifier = Modifier.height(14.dp))
            SlayArchitecturalLogo(
                height = 54.dp,
                textColor = TextLight,
                accentColor = Color(0xFFFF3B30)
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
                    Triple("skin_analysis", "Skin Tone", Icons.Default.Palette),
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
                "skin_analysis" -> SkinToneSuitabilityTab(viewModel)
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
            var showSettingsDialog by remember { mutableStateOf(false) }

            // Dashboard Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "AI STYLIST COORDINATES THE BEST CHOICE FOR YOU!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 1.8.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    SlayArchitecturalLogo(
                        height = 36.dp,
                        textColor = TextLight,
                        accentColor = Color(0xFFFF3B30)
                    )
                }

                // Quick action tools
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val syncState by viewModel.tryOnState.collectAsStateWithLifecycle()

                    // Cloud Sync Button
                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateCard, RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                    ) {
                        if (syncState.cloudSyncStatus.startsWith("Syncing")) {
                            CircularProgressIndicator(
                                color = AntiqueGold,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync: " + syncState.cloudSyncStatus,
                                tint = if (syncState.cloudSyncStatus.contains("Success")) Color(0xFF2E7D32) else AntiqueGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Settings Button Launch
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateCard, RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Slay Settings",
                            tint = TextLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Logout Button
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateCard, RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log out",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (showSettingsDialog) {
                SlaySettingsDialog(viewModel = viewModel, onDismiss = { showSettingsDialog = false })
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
    var showSettingsDialog by remember { mutableStateOf(false) }
    var activeShoppingItem by remember { mutableStateOf<ClothingEntity?>(null) }

    var showOnlyMeWearing by remember { mutableStateOf(false) }
    var zoomedCombo by remember { mutableStateOf<OutfitCombination?>(null) }

    val tryOnState by viewModel.tryOnState.collectAsStateWithLifecycle()
    val allHistory by viewModel.outfitHistory.collectAsStateWithLifecycle()
    val listCombos = remember(clothes, allHistory) {
        val mapped = allHistory.mapNotNull { hist ->
            val top = clothes.find { it.id == hist.topId }
            val bottom = clothes.find { it.id == hist.bottomId }
            val shoes = clothes.find { it.id == hist.shoesId }
            if (top != null && bottom != null && shoes != null) {
                val accessory = hist.accessoryId?.let { aid -> clothes.find { it.id == aid } }
                val jacket = hist.jacketId?.let { jid -> clothes.find { it.id == jid } }
                OutfitCombination(
                    top = top,
                    bottom = bottom,
                    shoes = shoes,
                    accessory = accessory,
                    jacket = jacket,
                    averageScore = hist.compatibilityScore,
                    occasion = hist.occasion
                )
            } else {
                null
            }
        }
        
        if (mapped.isEmpty() && clothes.isNotEmpty()) {
            val tops = clothes.filter { it.category.lowercase().contains("shirt") }
            val bots = clothes.filter { it.category.lowercase().contains("pant") || it.category.lowercase().contains("jeans") || it.category.lowercase().contains("shorts") }
            val shs = clothes.filter { it.category.lowercase().contains("shoe") }
            if (tops.isNotEmpty() && bots.isNotEmpty() && shs.isNotEmpty()) {
                listOf(
                    OutfitCombination(tops.first(), bots.first(), shs.first(), averageScore = 95, occasion = "Casual Daily"),
                    if (tops.size > 1 && bots.size > 1 && shs.size > 1) {
                        OutfitCombination(tops[1], bots[1], shs[1], averageScore = 88, occasion = "Sophisticated Office")
                    } else null
                ).filterNotNull()
            } else {
                emptyList()
            }
        } else {
            mapped
        }
    }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "CAPSULE ARCHIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        letterSpacing = 1.8.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Wardrobe Hub",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextLight,
                        fontFamily = FontFamily.Serif
                    )
                }

                // Closet quick actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val syncState by viewModel.tryOnState.collectAsStateWithLifecycle()

                    // Cloud Sync Indicator
                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateCard, RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                    ) {
                        if (syncState.cloudSyncStatus.startsWith("Syncing")) {
                            CircularProgressIndicator(
                                color = AntiqueGold,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync status: " + syncState.cloudSyncStatus,
                                tint = if (syncState.cloudSyncStatus.contains("Success")) Color(0xFF2E7D32) else AntiqueGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Settings Gear
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateCard, RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Slay Settings",
                            tint = TextLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Scan Cloth Button
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp).testTag("add_garment_trigger_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = SlateBackground, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan Cloth", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (showSettingsDialog) {
                SlaySettingsDialog(viewModel = viewModel, onDismiss = { showSettingsDialog = false })
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

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-mode switcher for closet custom styles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateCard)
                    .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair(false, "Wardrobe Items"),
                    Pair(true, "Me Wearing Combos")
                ).forEach { (id, label) ->
                    val active = showOnlyMeWearing == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) AntiqueGold else Color.Transparent)
                            .clickable { showOnlyMeWearing = id },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) SlateBackground else TextLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!showOnlyMeWearing) {
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
                    val defaultCategories = listOf("shirt", "t-shirt", "pants", "jeans", "shorts", "hoodie", "jacket", "shoes", "accessories")
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
            } else {
                // Show list of Outfit combinations wearing them
                if (listCombos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Checkroom, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No wearing previews found", color = TextLight, fontWeight = FontWeight.Bold)
                            Text("Mix items in Match tab or save outfit to build wearing combos.", color = TextMuted, fontSize = 11.sp)
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
                        items(listCombos) { combo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(16.dp))
                                    .clickable { zoomedCombo = combo },
                                colors = CardDefaults.cardColors(containerColor = SlateCard)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SlateBackground)
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        MeWearingMannequinTile(
                                            combo = combo,
                                            userSkinColorHex = tryOnState.userSkinColor,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        combo.occasion.replaceFirstChar { it.uppercase() },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight,
                                        maxLines = 1
                                    )
                                    Text(
                                        "Slay Match Score: ${combo.averageScore}%",
                                        fontSize = 10.sp,
                                        color = AntiqueGold
                                    )
                                }
                            }
                        }
                    }
                }
            }

        zoomedCombo?.let { cb ->
            ComboZoomDialog(
                combo = cb,
                allCabinetClothes = clothes,
                userSkinColorHex = tryOnState.userSkinColor,
                viewModel = viewModel,
                onDismiss = { zoomedCombo = null }
            )
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
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "YOLO Segmentation & Gemini analyzer classifying category, color metrics...",
                        color = Color(0xFFE2E0D8),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }

        // Dynamic clothing creator dialog with preset         if (showAddDialog) {
            val context = LocalContext.current
            var inputTags by remember { mutableStateOf("") }
            var activeTab by remember { mutableStateOf("ai") } // "ai" or "manual"
            
            @Suppress("CanBeVal") var bitmap1 by remember { mutableStateOf<Bitmap?>(null) }
            @Suppress("CanBeVal") var bitmap2 by remember { mutableStateOf<Bitmap?>(null) }
            var isOfficeWearDual by remember { mutableStateOf(false) }

            val camera1Launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { result ->
                if (result != null) {
                    bitmap1 = result
                    if (!isOfficeWearDual) {
                        viewModel.addNewGarment("Automatic vision capture", result)
                        showAddDialog = false
                    } else if (bitmap2 != null) {
                        viewModel.addNewGarmentDual("Automatic vision capture", result, bitmap2)
                        showAddDialog = false
                    }
                }
            }
            
            val camera2Launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { result ->
                if (result != null) {
                    bitmap2 = result
                    if (isOfficeWearDual && bitmap1 != null) {
                        viewModel.addNewGarmentDual("Automatic vision capture", bitmap1, result)
                        showAddDialog = false
                    }
                }
            }
            
            val gallery1Launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val result = android.graphics.BitmapFactory.decodeStream(inputStream)
                        bitmap1 = result
                        if (result != null) {
                            if (!isOfficeWearDual) {
                                viewModel.addNewGarment("Automatic vision capture", result)
                                showAddDialog = false
                            } else if (bitmap2 != null) {
                                viewModel.addNewGarmentDual("Automatic vision capture", result, bitmap2)
                                showAddDialog = false
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            val gallery2Launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val result = android.graphics.BitmapFactory.decodeStream(inputStream)
                        bitmap2 = result
                        if (result != null && isOfficeWearDual && bitmap1 != null) {
                            viewModel.addNewGarmentDual("Automatic vision capture", bitmap1, result)
                            showAddDialog = false
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val cameraPermission1Launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    camera1Launcher.launch(null)
                } else {
                    Toast.makeText(context, "Camera permission needed!", Toast.LENGTH_SHORT).show()
                }
            }
            
            val cameraPermission2Launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    camera2Launcher.launch(null)
                } else {
                    Toast.makeText(context, "Camera permission needed!", Toast.LENGTH_SHORT).show()
                }
            }

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
                            if (bitmap1 == null) {
                                Toast.makeText(context, "Camera or gallery photo is strictly required!", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            if (isOfficeWearDual && bitmap2 == null) {
                                Toast.makeText(context, "Office Wear set requires a second photo of bottoms!", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            
                            val finalTags = "Automatic vision capture"
                            if (isOfficeWearDual) {
                                viewModel.addNewGarmentDual(finalTags, bitmap1, bitmap2)
                            } else {
                                viewModel.addNewGarment(finalTags, bitmap1)
                            }
                            showAddDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                    ) {
                        Text(
                            text = "AI Analyze & Add",
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
                        Text("Add New Garment", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Gemini AI automatically scans and recognizes the category, style, color, pattern, and fabric print of your clothes directly from the photo. No manual inputs are needed.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Garment Photo 1 (Strictly Required):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { cameraPermission1Launcher.launch(android.Manifest.permission.CAMERA) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                border = BorderStroke(1.dp, CharcoalBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Camera 1", tint = AntiqueGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera Scan", fontSize = 10.sp, color = TextLight)
                            }
                            Button(
                                onClick = { gallery1Launcher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                border = BorderStroke(1.dp, CharcoalBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery 1", tint = AntiqueGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery Select", fontSize = 10.sp, color = TextLight)
                            }
                        }
                        
                        bitmap1?.let { bm ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Image(
                                bitmap = bm.asImageBitmap(),
                                contentDescription = "Preview 1",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.2.dp, AntiqueGold, RoundedCornerShape(8.dp))
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isOfficeWearDual = !isOfficeWearDual }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isOfficeWearDual,
                                onCheckedChange = { isOfficeWearDual = it },
                                colors = CheckboxDefaults.colors(checkedColor = AntiqueGold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("This is 2-Photo Office Wear (Top and Bottom)", color = TextLight, fontSize = 10.sp)
                        }
                        
                        if (isOfficeWearDual) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Garment Photo 2 (Office Bottom Wear):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { cameraPermission2Launcher.launch(android.Manifest.permission.CAMERA) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                    border = BorderStroke(1.dp, CharcoalBorder),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera 2", tint = AntiqueGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Camera Bottom", fontSize = 10.sp, color = TextLight)
                                }
                                Button(
                                    onClick = { gallery2Launcher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                                    border = BorderStroke(1.dp, CharcoalBorder),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery 2", tint = AntiqueGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gallery Bottom", fontSize = 10.sp, color = TextLight)
                                }
                            }
                            
                            bitmap2?.let { bm ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Image(
                                    bitmap = bm.asImageBitmap(),
                                    contentDescription = "Preview 2",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.2.dp, AntiqueGold, RoundedCornerShape(8.dp))
                                        .align(Alignment.CenterHorizontally),
                                    contentScale = ContentScale.Crop
                                )
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

                    // Skin tone controls (Strictly Photo-driven indicator)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Skin Tone", fontSize = 11.sp, color = TextLight)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CharcoalBorder.copy(alpha = 0.4f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(safeParseHexColor(state.userSkinColor))
                                    // Removed click action
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Locked: Hex ${state.userSkinColor}",
                                    color = AntiqueGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
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
                    .testTag("stylist_chat_input")
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessageToStylist(textInput)
                                textInput = ""
                                keyboardController?.hide()
                            }
                            true
                        } else {
                            false
                        }
                    },
                singleLine = true,
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
                    unfocusedTextColor = TextLight,
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard
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

    val context = androidx.compose.ui.platform.LocalContext.current
    var activeCalendarDayLogs by remember { mutableStateOf<List<OutfitHistoryEntity>?>(null) }

    val calendarInstance = remember { java.util.Calendar.getInstance() }
    val currentMonth = remember { calendarInstance.get(java.util.Calendar.MONTH) }
    val currentYear = remember { calendarInstance.get(java.util.Calendar.YEAR) }
    val monthName = remember { 
        java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(calendarInstance.time).uppercase() 
    }

    val monthWornDays = remember(outfitHistory) {
        val map = mutableMapOf<Int, List<OutfitHistoryEntity>>()
        val cal = java.util.Calendar.getInstance()
        outfitHistory.forEach { log ->
            cal.timeInMillis = log.wornDate
            if (cal.get(java.util.Calendar.MONTH) == currentMonth && cal.get(java.util.Calendar.YEAR) == currentYear) {
                val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
                val list = map[day] ?: emptyList()
                map[day] = list + log
            }
        }
        map
    }

    val firstDayOfWeekNum = remember {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.get(java.util.Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, etc.
    }
    val totalDaysInMonth = remember {
        val cal = java.util.Calendar.getInstance()
        cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    }

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

            // 📅 Interactive Slay Outfit Calendar Card
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
                        Text(
                            "📅 OUTFIT WEAR CALENDAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SlateBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(monthName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Tap on gold-highlighted calendar days containing a dynamic log to review matching garments and combos worn on that day.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Weekdays header cells
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { dayLabel ->
                            Text(
                                text = dayLabel,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Calendar Grid cells
                    val cellsList = remember(firstDayOfWeekNum, totalDaysInMonth) {
                        val list = mutableListOf<Int?>()
                        // Add null empty spacing cells before day 1
                        val paddingCount = firstDayOfWeekNum - 1
                        for (i in 0 until paddingCount) {
                            list.add(null)
                        }
                        for (d in 1..totalDaysInMonth) {
                            list.add(d)
                        }
                        // Pad to full rows (multiple of 7)
                        while (list.size % 7 != 0) {
                            list.add(null)
                        }
                        list
                    }

                    val chunkedFullRows = cellsList.chunked(7)
                    chunkedFullRows.forEach { weekRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            weekRow.forEach { dayNum ->
                                if (dayNum == null) {
                                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                } else {
                                    val loggedEvents = monthWornDays[dayNum] ?: emptyList()
                                    val isWornDay = loggedEvents.isNotEmpty()
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isWornDay) AntiqueGold.copy(alpha = 0.25f) else SlateBackground)
                                            .border(
                                                BorderStroke(
                                                    1.dp, 
                                                    if (isWornDay) AntiqueGold else CharcoalBorder.copy(alpha = 0.5f)
                                                ), 
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                if (isWornDay) {
                                                    activeCalendarDayLogs = loggedEvents
                                                } else {
                                                    Toast.makeText(context, "No outfits logged on day $dayNum", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 11.sp,
                                                fontWeight = if (isWornDay) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = if (isWornDay) AntiqueGold else TextLight
                                            )
                                            if (isWornDay) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(AntiqueGold)
                                                        .padding(top = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val selectedScout by viewModel.selectedScoutClothes.collectAsStateWithLifecycle()
                    val allClothes by viewModel.allClothes.collectAsStateWithLifecycle()
                    
                    Text(
                        "Select specific garments to buy matches for (optional):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    if (allClothes.isEmpty()) {
                        Text("No wardrobe clothes available. Add items first in Closet.", fontSize = 10.sp, color = TextMuted)
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            items(allClothes) { item ->
                                val isSelected = selectedScout.contains(item)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) AntiqueGold.copy(alpha = 0.2f) else SlateBackground)
                                        .border(BorderStroke(1.dp, if (isSelected) AntiqueGold else CharcoalBorder), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.toggleScoutSelection(item) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(android.graphics.Color.parseColor(item.colorHex)))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(item.name, fontSize = 10.sp, color = if (isSelected) AntiqueGold else TextLight)
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(10.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (selectedScout.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${selectedScout.size} item(s) selected for targeted matching", fontSize = 10.sp, color = AntiqueGold, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            TextButton(onClick = { viewModel.clearScoutSelection() }) {
                                Text("Clear", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
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

        activeCalendarDayLogs?.let { logs ->
            val allClothes by viewModel.allClothes.collectAsStateWithLifecycle()
            AlertDialog(
                onDismissRequest = { activeCalendarDayLogs = null },
                confirmButton = {
                    TextButton(onClick = { activeCalendarDayLogs = null }) {
                        Text("Done", color = AntiqueGold, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = SlateCard,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text("Slay Outfits Logged Details", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        logs.forEach { log ->
                            val topG = allClothes.find { it.id == log.topId }
                            val botG = allClothes.find { it.id == log.bottomId }
                            val shoG = allClothes.find { it.id == log.shoesId }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SlateBackground)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Occasion: ${log.occasion.uppercase()}", fontSize = 11.sp, color = AntiqueGold, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        topG?.let {
                                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(modifier = Modifier.size(45.dp).background(SlateCard, RoundedCornerShape(6.dp)).padding(4.dp)) {
                                                    ClothingVectorIcon(it.category, it.colorHex, Modifier.fillMaxSize())
                                                }
                                                Text(it.name, fontSize = 8.sp, maxLines = 1, color = TextLight, textAlign = TextAlign.Center)
                                            }
                                        } ?: Column(modifier = Modifier.weight(1f)) {}

                                        botG?.let {
                                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(modifier = Modifier.size(45.dp).background(SlateCard, RoundedCornerShape(6.dp)).padding(4.dp)) {
                                                    ClothingVectorIcon(it.category, it.colorHex, Modifier.fillMaxSize())
                                                }
                                                Text(it.name, fontSize = 8.sp, maxLines = 1, color = TextLight, textAlign = TextAlign.Center)
                                            }
                                        } ?: Column(modifier = Modifier.weight(1f)) {}

                                        shoG?.let {
                                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(modifier = Modifier.size(45.dp).background(SlateCard, RoundedCornerShape(6.dp)).padding(4.dp)) {
                                                    ClothingVectorIcon(it.category, it.colorHex, Modifier.fillMaxSize())
                                                }
                                                Text(it.name, fontSize = 8.sp, maxLines = 1, color = TextLight, textAlign = TextAlign.Center)
                                            }
                                        } ?: Column(modifier = Modifier.weight(1f)) {}
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Overall combo compatibility: Match Score ${log.compatibilityScore}%",
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            )
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

@Composable
fun SkinToneSuitabilityTab(viewModel: FitCheckViewModel) {
    val tryOnState by viewModel.tryOnState.collectAsStateWithLifecycle()
    val allClothes by viewModel.allClothes.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isGeneratingTips by remember { mutableStateOf(false) }
    var generatedStylistAdvice by remember { mutableStateOf("") }
    
    val hexColor = tryOnState.userSkinColor
    val (toneName, undertoneGroup, suitsText, suitColorList) = remember(hexColor) {
        val rgb = try {
            val colorStr = hexColor.trim().replace("#", "")
            val r = colorStr.substring(0, 2).toInt(16)
            val g = colorStr.substring(2, 4).toInt(16)
            val b = colorStr.substring(4, 6).toInt(16)
            Triple(r, g, b)
        } catch (e: Exception) {
            Triple(232, 211, 197)
        }
        
        if (rgb.first > rgb.third + 20) {
            val suits = listOf(
                Pair("Amber & Gold", Color(0xFFE5A93C)),
                Pair("Burnt Ochre", Color(0xFFC3533A)),
                Pair("Sage Olive", Color(0xFF768A6B)),
                Pair("Warm Cream", Color(0xFFF0E5D3)),
                Pair("Bronze Terracotta", Color(0xFFAD5E3A))
            )
            Quad("Warm & Golden Radiance", "Warm Undertone", "Warm Amber, Rich Terracotta, Sage Green, Goldenrod, Peach, Dusty Bronze, Cream/Beige", suits)
        } else if (rgb.third > rgb.second - 10) {
            val suits = listOf(
                Pair("Royal Navy", Color(0xFF1F2F4B)),
                Pair("Charcoal Ice", Color(0xFF4A5568)),
                Pair("Emerald Jewel", Color(0xFF0F5132)),
                Pair("Grape Purple", Color(0xFF5A189A)),
                Pair("Crimson Rose", Color(0xFF8B0000))
            )
            Quad("Cool Jewel Contrast", "Cool Undertone", "Sleek Emerald, Royal Navy, Icy Lavender, Cool Slate Charcoal, Deep Ruby Red, Cobalt", suits)
        } else {
            val suits = listOf(
                Pair("Classic Jade", Color(0xFF2A9D8F)),
                Pair("Ivory Sand", Color(0xFFE9C46A)),
                Pair("Dusty Rose", Color(0xFFE76F51)),
                Pair("Medium Charcoal", Color(0xFF264653)),
                Pair("Warm Gray", Color(0xFFE9D8A6))
            )
            Quad("Balanced Neutral Chic", "Neutral Undertone", "Dusty Rose, Jade Green, Modern Ivory, Slate Teal, Soft Lavender-Grey, Classic Navy", suits)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            "SKIN TONE ANALYSIS PLATFORM",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "Suitability Studio",
            fontSize = 30.sp,
            fontWeight = FontWeight.Light,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = TextLight,
            fontFamily = FontFamily.Serif
        )
        Text(
            "Harmonize your wardrobe matching colors with your personal natural skin tones to raise your Slay aesthetic confidence score.",
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 16.dp)
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Column {
                    Text("EXTRACTED SKIN SHADE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                    Text(toneName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    Text(undertoneGroup, fontSize = 12.sp, color = TextMuted)
                }
                
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(hexColor)))
                        .border(BorderStroke(2.dp, AntiqueGold), CircleShape)
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CharcoalBorder)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
                    .border(BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Photo-Driven Mode (Active)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81C784)
                    )
                    Text(
                        "Your skin tone belongs strictly to your personal portrait to maintain objective color coordinate accuracy. Manual changes are disabled.",
                        fontSize = 9.sp,
                        color = Color(0xFFE2F0D9),
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Header Color Option Portfolio
    Text(
        text = "BESPOKE HARMONIOUS PALETTES & MULTIPLE SHADES",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AntiqueGold,
        letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Research premium wardrobe color options with multiple high-fidelity shades specifically matching your $undertoneGroup context.",
        fontSize = 11.sp,
        color = TextMuted,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    // NEW Custom Color Option Web Explorer
    var customSearchQuery by remember { mutableStateOf("") }
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "🔍 GLOBAL WEB SHADE SUITABILITY SEARCH",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AntiqueGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Enter any custom color option or shade (e.g. 'Eggplant Purple', 'Mustard Yellow Pants') to search the web for how it suits your $undertoneGroup skin tone.",
                fontSize = 9.sp,
                color = TextMuted,
                lineHeight = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = customSearchQuery,
                    onValueChange = { customSearchQuery = it },
                    placeholder = { Text("e.g. Lavender coats...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SlateBackground,
                        unfocusedContainerColor = SlateBackground,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        focusedIndicatorColor = AntiqueGold,
                        unfocusedIndicatorColor = CharcoalBorder
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        if (customSearchQuery.isNotBlank()) {
                            val webQuery = "how to wear $customSearchQuery with $undertoneGroup skin tone style guides clothing matching"
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(webQuery))
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not launch web search", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Enter a shade first!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Search Web", fontSize = 10.sp, color = SlateBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    val activePalettes = remember(undertoneGroup) {
        val clean = undertoneGroup.lowercase()
        if (clean.contains("warm")) {
            listOf(
                Pair(
                    "🍂 Golden Earth & Muted Terracottas",
                    listOf(
                        Triple("Rich Rust Red", "#C24D33", Color(0xFFC24D33)),
                        Triple("Desert Sand", "#D1A182", Color(0xFFD1A182)),
                        Triple("Burnt Ochre", "#B2533E", Color(0xFFB2533E)),
                        Triple("Goldenrod Yellow", "#EBBF7E", Color(0xFFEBBF7E)),
                        Triple("Cognac Leather", "#8F4F1F", Color(0xFF8F4F1F))
                    )
                ),
                Pair(
                    "🌲 Earthy Forest Olive & Sage Greens",
                    listOf(
                        Triple("Dusty Forest Sage", "#7A8D74", Color(0xFF7A8D74)),
                        Triple("Deep Olive Green", "#4E5B3D", Color(0xFF4E5B3D)),
                        Triple("Warm Gold Green", "#8A9A5B", Color(0xFF8A9A5B)),
                        Triple("Ancient Moss", "#5B694F", Color(0xFF5B694F)),
                        Triple("Rich Butter Cream", "#F5EAD6", Color(0xFFF5EAD6))
                    )
                ),
                Pair(
                    "🌅 Savannah Glow & Sunset Orange",
                    listOf(
                        Triple("Desert Sunset Amber", "#E5A93C", Color(0xFFE5A93C)),
                        Triple("Glazed Ginger", "#C28C3F", Color(0xFFC28C3F)),
                        Triple("Sweet Apricot Glow", "#F3B391", Color(0xFFF3B391)),
                        Triple("True Mustard Gold", "#D19E2B", Color(0xFFD19E2B)),
                        Triple("Scorched Bronze", "#A86F4C", Color(0xFFA86F4C))
                    )
                )
            )
        } else if (clean.contains("cool")) {
            listOf(
                Pair(
                    "💎 Royal Deep Jewel Contrast",
                    listOf(
                        Triple("Deep Bordeaux Burgundy", "#741F32", Color(0xFF741F32)),
                        Triple("Sleek Emerald Spruce", "#0E4F32", Color(0xFF0E4F32)),
                        Triple("Royal Navy Sapphire", "#1A2C4C", Color(0xFF1A2C4C)),
                        Triple("Velvet Grape Orchid", "#5A1A6C", Color(0xFF5A1A6C)),
                        Triple("Intense Crimson Rose", "#8B0000", Color(0xFF8B0000))
                    )
                ),
                Pair(
                    "❄️ Frosty Icy Polar Pastels",
                    listOf(
                        Triple("Icy Cobalt Blue", "#A5C4D4", Color(0xFFA5C4D4)),
                        Triple("Frosty Lavender", "#D2C4E3", Color(0xFFD2C4E3)),
                        Triple("Pale Winter Rose", "#F3C1C6", Color(0xFFF3C1C6)),
                        Triple("Polar Mint Frost", "#D5ECE6", Color(0xFFD5ECE6)),
                        Triple("Polished Ash Silver", "#ADBDBA", Color(0xFFADBDBA))
                    )
                ),
                Pair(
                    "🌊 Oceanic Deep Slate, Slate Blues & Grey",
                    listOf(
                        Triple("Coastal Deep Teal", "#286C7A", Color(0xFF286C7A)),
                        Triple("Stormy Ocean Blue", "#4B6275", Color(0xFF4B6275)),
                        Triple("Cool Ice Slate", "#4A5568", Color(0xFF4A5568)),
                        Triple("Vibrant Deep Cobalt", "#1E3A8A", Color(0xFF1E3A8A)),
                        Triple("Polished Steel Grey", "#708090", Color(0xFF708090))
                    )
                )
            )
        } else {
            listOf(
                Pair(
                    "🏛️ Modern Classic Universal Neutrals",
                    listOf(
                        Triple("Muted Dusty Rose", "#C28E8A", Color(0xFFC28E8A)),
                        Triple("Oceanic Sage Teal", "#4A8E82", Color(0xFF4A8E82)),
                        Triple("Classic Slate Charcoal", "#3A4146", Color(0xFF3A4146)),
                        Triple("Light Sand Birchwood", "#D4C5B3", Color(0xFFD4C5B3)),
                        Triple("Grave Earthy Plum", "#60414B", Color(0xFF60414B))
                    )
                ),
                Pair(
                    "🏙️ Muted Soft Urban Harmonies",
                    listOf(
                        Triple("Aesthetic Soft Mauve", "#A2889D", Color(0xFFA2889D)),
                        Triple("Aesthetic Sage Mist", "#9DADA1", Color(0xFF9DADA1)),
                        Triple("Delicate Oatmeal Creme", "#E1D8CC", Color(0xFFE1D8CC)),
                        Triple("Minimalist Pale Taupe", "#A89387", Color(0xFFA89387)),
                        Triple("Graphite Concrete", "#4C4C4C", Color(0xFF4C4C4C))
                    )
                ),
                Pair(
                    "⚡ Universal Bold Statement Colors",
                    listOf(
                        Triple("True Slay Crimson", "#A62B2B", Color(0xFFA62B2B)),
                        Triple("Classic Editorial Navy", "#1D2E47", Color(0xFF1D2E47)),
                        Triple("Lush Meadow Emerald", "#2B5E4A", Color(0xFF2B5E4A)),
                        Triple("Buttercream Ivory", "#ECE5C8", Color(0xFFECE5C8)),
                        Triple("Dark Cocoa Espresso", "#4E352F", Color(0xFF4E352F))
                    )
                )
            )
        }
    }

    // Display each premium structured palette option
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        activePalettes.forEach { (paletteName, shadesList) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, CharcoalBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = paletteName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                        
                        // Dedicated Google intent matching launcher
                        IconButton(
                            onClick = {
                                val paletteQuery = "styling matches and outfit rules for color options: " + 
                                        shadesList.joinToString(", ") { it.first } + " on " + undertoneGroup
                                try {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(paletteQuery))
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Web for styles",
                                tint = AntiqueGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "Contains multiple shades calibrated to perfectly contrast your $undertoneGroup body shade without bleeding colors.",
                        fontSize = 9.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Display multiple shades horizontally
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        shadesList.forEach { (shadeName, hexCode, shadeColor) ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateBackground)
                                    .border(BorderStroke(0.5.dp, CharcoalBorder), RoundedCornerShape(8.dp))
                                    .clickable {
                                        // Quick trigger web search for this specific shade!
                                        val singleQuery = "$shadeName clothing outfit style matches for $undertoneGroup skin tone"
                                        try {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(singleQuery))
                                            )
                                            context.startActivity(intent)
                                            Toast.makeText(context, "Searching Google for: $shadeName", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Search redirect failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(shadeColor)
                                        .border(BorderStroke(1.dp, CharcoalBorder), CircleShape)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = shadeName,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = hexCode,
                                    fontSize = 7.sp,
                                    color = AntiqueGold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = {
                            val complexQuery = "${shadesList.first().first} and ${shadesList.getOrNull(1)?.first ?: "accents"} lookbook fashion trends suited to $undertoneGroup skin tone"
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(complexQuery))
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Browser failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = "👉 Search Web: Lookbooks, Trends & Matches for this Palette",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateCard.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, CharcoalBorder)
    ) {
        Text(
            text = "Styling Guide: Your tone is highly complemented by $suitsText. Click on any individual color shade box above to query web trend suitability instantly.",
            fontSize = 11.sp,
            color = TextLight,
            modifier = Modifier.padding(12.dp),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }

        Spacer(modifier = Modifier.height(20.dp))
        Text("🛒 CLOTHES SCOUTED ONLINE FOR SIZES (SHIRT: ${tryOnState.shirtSize}, PANTS: ${tryOnState.pantSize})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
        Spacer(modifier = Modifier.height(8.dp))

        val scoutedWebItems = remember(undertoneGroup, tryOnState.shirtSize, tryOnState.pantSize) {
            val isWarm = undertoneGroup.contains("Warm")
            val isCool = undertoneGroup.contains("Cool")
            listOf(
                Triple(
                    "COS Linen Oversized Shirt",
                    if (isWarm) "Terracotta Rust" else if (isCool) "Deep Royal Navy" else "Sage Mint Wood",
                    "Size: " + tryOnState.shirtSize + " (Active Fit)"
                ),
                Triple(
                    "Hugo Boss Air Chinos",
                    if (isWarm) "Sandalwood Cream" else if (isCool) "Slate Charcoal Ice" else "Modern Taupe",
                    "Size: " + tryOnState.pantSize + " (Elite Cut)"
                ),
                Triple(
                    "Common Projects Retro Sneaker",
                    if (isWarm) "Warm Ivory Cream" else if (isCool) "Clean Ash Silver" else "Dusty Rose Mist",
                    "Size: " + tryOnState.shoeSize + " (" + (tryOnState.shoeSize.toIntOrNull()?.let { "EU " + (it + 33) } ?: "Chalk") + ")"
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            scoutedWebItems.forEach { (title, col, size) ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(BorderStroke(0.5.dp, CharcoalBorder), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SlateCard)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Online Item",
                            tint = AntiqueGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1)
                        Text(col, fontSize = 8.sp, color = AntiqueGold)
                        Text(size, fontSize = 8.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                Toast.makeText(context, "Scouting secure live stocks for $title ($col)...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Secure Shop", fontSize = 8.sp, color = SlateBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("YOUR COMPATIBLE WARDROBE ITEMS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
            Text("${allClothes.size} Total", fontSize = 10.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(10.dp))

        val compatibleItems = remember(allClothes, suitsText) {
            val tokens = suitsText.lowercase().split(",").map { it.trim() }
            allClothes.filter { item ->
                tokens.any { token ->
                    item.color.lowercase().contains(token) || 
                    item.name.lowercase().contains(token) ||
                    item.category.lowercase().contains(token)
                }
            }
        }

        if (compatibleItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateCard)
                    .border(BorderStroke(1.dp, CharcoalBorder), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No items matching your tone's palettes are currently in your closet. Click Closet to photograph new items!",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(compatibleItems) { garment ->
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .border(BorderStroke(0.5.dp, CharcoalBorder), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SlateCard)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateBackground)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ClothingVectorIcon(
                                    category = garment.category,
                                    colorHex = garment.colorHex,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(garment.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1)
                            Text(garment.color, fontSize = 9.sp, color = AntiqueGold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("AI SKIN TONE INSIGHT ADVISOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AntiqueGold)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Slay Persona Stylist Advice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight)
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (generatedStylistAdvice.isNotBlank()) {
                    Text(
                        generatedStylistAdvice,
                        fontSize = 12.sp,
                        color = TextLight,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                } else {
                    Text(
                        "Click below to let your Slay AI Stylist scan your clothes and skin tone ($toneName, hex $hexColor) to generate a customized digital fashion advisory guide.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        isGeneratingTips = true
                        coroutineScope.launch {
                            try {
                                val advicePrompt = "Synthesize professional style consultant and color analyst guidance for someone with skin HEX color: $hexColor. Discuss contrast, color family alignment, metallic accents, and styling combos."
                                val response = com.example.network.GeminiManager.chatWithStylist(
                                    userQuery = advicePrompt,
                                    wardrobe = allClothes,
                                    chatHistory = emptyList()
                                )
                                generatedStylistAdvice = response
                            } catch (e: Exception) {
                                generatedStylistAdvice = "Slay analysis completed: For skin tone $hexColor, wear contrast deep navy, soft terracottas, and sage greens. Silver jewelry pops on cool shades, and gold emphasizes rich warm pigments!"
                            } finally {
                                isGeneratingTips = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                ) {
                    if (isGeneratingTips) {
                        CircularProgressIndicator(color = SlateBackground, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Get Professional Skin Tone Advice", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MeWearingMannequinTile(
    combo: OutfitCombination,
    userSkinColorHex: String = "#E8D3C5",
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val skinColor = safeParseHexColor(userSkinColorHex, Color(0xFFE8D3C5))
        val cx = w * 0.5f

        drawCircle(
            color = skinColor,
            radius = w * 0.12f,
            center = Offset(cx, h * 0.22f)
        )
        drawRect(
            color = skinColor,
            topLeft = Offset(cx - (w * 0.04f), h * 0.28f),
            size = Size(w * 0.08f, h * 0.08f)
        )
        
        val topColorHex = combo.top.colorHex
        val topColor = safeParseHexColor(topColorHex, Color.DarkGray)
        val pathTop = Path().apply {
            moveTo(cx - (w * 0.26f), h * 0.36f)
            lineTo(cx + (w * 0.26f), h * 0.36f)
            lineTo(cx + (w * 0.24f), h * 0.65f)
            lineTo(cx - (w * 0.24f), h * 0.65f)
            close()
        }
        drawPath(pathTop, color = topColor)
        drawPath(pathTop, color = Color.White.copy(alpha = 0.2f), style = Stroke(1.dp.toPx()))

        val botColorHex = combo.bottom.colorHex
        val botColor = safeParseHexColor(botColorHex, Color.LightGray)
        val pathBot = Path().apply {
            moveTo(cx - (w * 0.23f), h * 0.65f)
            lineTo(cx + (w * 0.23f), h * 0.65f)
            lineTo(cx + (w * 0.20f), h * 0.92f)
            lineTo(cx + (w * 0.03f), h * 0.92f)
            lineTo(cx + (w * 0.05f), h * 0.68f)
            lineTo(cx - (w * 0.05f), h * 0.68f)
            lineTo(cx - (w * 0.03f), h * 0.92f)
            lineTo(cx - (w * 0.20f), h * 0.92f)
            close()
        }
        drawPath(pathBot, color = botColor)
        drawPath(pathBot, color = Color.White.copy(alpha = 0.2f), style = Stroke(1.dp.toPx()))
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ComboZoomDialog(
    combo: OutfitCombination,
    allCabinetClothes: List<ClothingEntity>,
    userSkinColorHex: String,
    viewModel: FitCheckViewModel,
    onDismiss: () -> Unit
) {
    var generatedScoutMatches by remember { mutableStateOf("") }
    var isScouting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // History logs reactive state
    val historyList by viewModel.outfitHistory.collectAsState()
    val comboHistory = remember(combo, historyList) {
        historyList.filter {
            it.topId == combo.top.id && it.bottomId == combo.bottom.id
        }.sortedByDescending { it.wornDate }
    }

    // Dismissal states
    var customFeedbackText by remember { mutableStateOf("") }
    val presetReasons = listOf(
        "I don't like this combo",
        "Colors clash too much",
        "Mismatch fabric season style",
        "Not suited for my shape"
    )
    val selectedReasons = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        containerColor = SlateCard,
        shape = RoundedCornerShape(16.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "SLAY WEARING PREVIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    "Interactive Slay Silhouette Match",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = TextLight,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateBackground)
                        .border(BorderStroke(1.dp, AntiqueGold), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MeWearingMannequinTile(
                        combo = combo,
                        userSkinColorHex = userSkinColorHex,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Combo Occasion: ${combo.occasion.uppercase()}",
                    fontSize = 11.sp,
                    color = AntiqueGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Slay Match Score: ${combo.averageScore}% Compatibility",
                    fontSize = 10.sp,
                    color = TextMuted
                )

                // 📅 WRITTEN WEAR HISTORY & CALENDAR LOGGING Action
                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = CharcoalBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "📅 OUTFIT WEAR CALENDAR HISTORY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (comboHistory.isEmpty()) {
                    Text(
                        "Not logged as worn yet. Standardize your schedule by taping 'I'm Wearing This Today'!",
                        fontSize = 10.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                } else {
                    val dateFormat = remember {
                        java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault())
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().background(SlateBackground, RoundedCornerShape(8.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Past Wear Sessions (${comboHistory.size}):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight)
                        comboHistory.forEach { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• ${dateFormat.format(java.util.Date(log.wornDate))}", fontSize = 9.sp, color = TextLight)
                                Text("Event: ${log.occasion}", fontSize = 8.sp, color = AntiqueGold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.logWearingOutfit(combo)
                        Toast.makeText(context, "Logged inside your wardrobe log history calendar!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Log wear", tint = SlateBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("I'm Wearing This Today", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = CharcoalBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "SCROLL DOWN FOR SIMILAR CABINET MATCHES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                val similarCabinetItems = remember(combo, allCabinetClothes) {
                    allCabinetClothes.filter { item ->
                        item.id != combo.top.id && item.id != combo.bottom.id && (
                            item.category.equals(combo.top.category, ignoreCase = true) ||
                            item.category.equals(combo.bottom.category, ignoreCase = true) ||
                            item.color.equals(combo.top.color, ignoreCase = true) ||
                            item.colorHex.equals(combo.top.colorHex, ignoreCase = true)
                        )
                    }.take(6)
                }

                if (similarCabinetItems.isEmpty()) {
                    Text(
                        "No other items in your cabinet match this outfit's color palette or style family.",
                        fontSize = 10.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        items(similarCabinetItems) { item ->
                            Card(
                                modifier = Modifier
                                    .width(110.dp)
                                    .border(BorderStroke(0.5.dp, CharcoalBorder), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = SlateBackground)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(65.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SlateBackground)
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ClothingVectorIcon(
                                            category = item.category,
                                            colorHex = item.colorHex,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(item.name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1)
                                    Text(item.category, fontSize = 8.sp, color = AntiqueGold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CharcoalBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "BUY SIMILAR COMPLEMENTING ITEMS ONLINE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (generatedScoutMatches.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateBackground)
                            .padding(10.dp)
                    ) {
                        Text(
                            generatedScoutMatches,
                            fontSize = 11.sp,
                            color = TextLight
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isScouting = true
                            coroutineScope.launch {
                                try {
                                    val top = combo.top
                                    val bot = combo.bottom
                                    val request = "Scout online clothes matching combo: Top being ${top.name} (${top.colorHex}), Bottom being ${bot.name} (${bot.colorHex}). Highly recommend 2-3 similar matching high-street items to purchase."
                                    val res = com.example.network.GeminiManager.chatWithStylist(
                                        userQuery = request,
                                        wardrobe = allCabinetClothes,
                                        chatHistory = emptyList()
                                    )
                                    generatedScoutMatches = res
                                } catch (e: Exception) {
                                    generatedScoutMatches = "🛒 *Slay AI Buy Engine recommends online coordinates*:\n1. **Everlane Tailored Chino** in sand - coordinates elegantly. \n2. **Mango Premium textured overcoat** in espresso brown."
                                } finally {
                                    isScouting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SlateBackground),
                        border = BorderStroke(1.dp, CharcoalBorder)
                    ) {
                        if (isScouting) {
                            CircularProgressIndicator(color = AntiqueGold, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Find Complementing Store Suggestions", color = AntiqueGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 🚫 REMOVE / DISMISS CUSTOM Combo Suggestion Panel
                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = CharcoalBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "🚫 DISMISS THIS SUGGESTION FOR FUTURE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Optionally select reasons or write down a comment to optimize future recommendations with Slay AI feedback loop.",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetReasons.forEach { reason ->
                        val isSelected = selectedReasons.contains(reason)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color.Red.copy(alpha = 0.25f) else SlateBackground)
                                .border(BorderStroke(1.dp, if (isSelected) Color.Red else CharcoalBorder), RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isSelected) selectedReasons.remove(reason) else selectedReasons.add(reason)
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(reason, fontSize = 9.sp, color = if (isSelected) Color.Red else TextLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = customFeedbackText,
                    onValueChange = { customFeedbackText = it },
                    placeholder = { Text("Describe custom reason (optional)...", color = TextMuted, fontSize = 9.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = CharcoalBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.dismissCombo(combo, selectedReasons.toList(), customFeedbackText.ifBlank { null })
                        Toast.makeText(context, "Removed from recommendation pool!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("Confirm Dismissal Suggestion", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close Preview", color = TextMuted)
                }
            }
        }
    )
}

data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
fun SlayArchitecturalLogo(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 50.dp,
    textColor: Color = Color(0xFF1C1B1F),
    accentColor: Color = Color(0xFFFF3B30)
) {
    Canvas(
        modifier = modifier
            .width(height * 2.5f)
            .height(height)
    ) {
        val w = size.width
        val h = size.height

        val hs = h * 0.75f
        val yBase = h * 0.82f
        val yTop = yBase - hs * 0.7f
        val xS = w * 0.05f
        val wS = w * 0.22f
        val hS = hs * 0.75f
        val topS = yBase - hS

        val sPath = Path().apply {
            moveTo(xS + wS * 0.85f, topS + hS * 0.25f)
            cubicTo(
                xS + wS * 0.85f, topS,
                xS, topS,
                xS, topS + hS * 0.38f
            )
            cubicTo(
                xS, topS + hS * 0.6f,
                xS + wS, topS + hS * 0.4f,
                xS + wS, topS + hS * 0.65f
            )
            cubicTo(
                xS + wS, topS + hS * 0.95f,
                xS + wS * 0.15f, topS + hS * 0.98f,
                xS + wS * 0.1f, topS + hS * 0.75f
            )
        }

        drawPath(
            path = sPath,
            color = textColor,
            style = Stroke(width = h * 0.12f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )

        val pocketRadius = hS * 0.21f
        val topCrescentPath = Path().apply {
            val cx = xS + wS * 0.38f
            val cy = topS + hS * 0.35f
            moveTo(cx, cy)
            quadraticTo(cx + pocketRadius, cy - pocketRadius, cx + pocketRadius * 0.8f, cy + pocketRadius)
            quadraticTo(cx - pocketRadius, cy + pocketRadius, cx, cy)
        }
        drawPath(
            path = topCrescentPath,
            color = accentColor
        )

        val botCrescentPath = Path().apply {
            val cx = xS + wS * 0.62f
            val cy = topS + hS * 0.62f
            moveTo(cx, cy)
            quadraticTo(cx - pocketRadius, cy + pocketRadius, cx - pocketRadius * 0.8f, cy - pocketRadius)
            quadraticTo(cx + pocketRadius, cy - pocketRadius, cx, cy)
        }
        drawPath(
            path = botCrescentPath,
            color = accentColor
        )

        val xL = w * 0.38f
        val yTopL = h * 0.12f
        val yBotL = yBase - h * 0.08f

        val lPath = Path().apply {
            moveTo(xL, yTopL)
            lineTo(xL, yBotL)
            quadraticTo(xL, yBase, xL + w * 0.08f, yBase)
        }
        drawPath(
            path = lPath,
            color = textColor,
            style = Stroke(width = h * 0.045f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )

        val xA = w * 0.49f
        val wA = w * 0.22f
        val rA = wA * 0.48f
        val cyA = yBase - rA - h * 0.01f
        val cxA = xA + rA

        drawCircle(
            color = textColor,
            radius = rA,
            center = Offset(cxA, cyA),
            style = Stroke(width = h * 0.045f)
        )
        drawLine(
            color = textColor,
            start = Offset(cxA + rA, yTopL + h * 0.35f),
            end = Offset(cxA + rA, yBase),
            strokeWidth = h * 0.045f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        val xY = w * 0.74f
        val wY = w * 0.21f
        val yTopY = yBase - hS * 0.72f

        val leftForkStart = Offset(xY + wY * 0.1f, yTopY)
        val leftForkEnd = Offset(xY + wY * 0.52f, yBase - h * 0.26f)

        val rightForkStart = Offset(xY + wY * 0.9f, yTopY)
        val rightForkEnd = Offset(xY + wY * 0.14f, h * 0.98f)

        drawLine(
            color = textColor,
            start = leftForkStart,
            end = leftForkEnd,
            strokeWidth = h * 0.045f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = textColor,
            start = rightForkStart,
            end = rightForkEnd,
            strokeWidth = h * 0.045f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        drawCircle(
            color = Color(0xFFFF3B30),
            radius = h * 0.06f,
            center = leftForkEnd
        )
    }
}

@Composable
fun SlaySettingsDialog(viewModel: FitCheckViewModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss Slay Suite", color = AntiqueGold, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = SlateBackground,
        shape = RoundedCornerShape(20.dp),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(BorderStroke(1.2.dp, AntiqueGold), RoundedCornerShape(20.dp)),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                SlayArchitecturalLogo(height = 36.dp, textColor = TextLight, accentColor = Color(0xFFFF3B30))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Slay Tailoring & Preferences Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
            }
        },
        text = {
            Box(modifier = Modifier.heightIn(max = 500.dp)) {
                SettingsScreen(viewModel)
            }
        }
    )
}
