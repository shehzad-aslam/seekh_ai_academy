package com.tllstudio.smartlearn

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Soft & Smooth On-Device AI Privacy Crest Splash & Security Screen
@Composable
fun PrivacyShieldSplashScreen(
    isStandaloneView: Boolean = false,
    onContinue: () -> Unit
) {
    BackHandler(enabled = isStandaloneView) {
        onContinue()
    }

    val scaleAnim = remember { Animatable(0.88f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        if (!isStandaloneView) {
            kotlinx.coroutines.delay(2400)
            onContinue()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        // Soft pastel constellation & shield watermark background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = 4
            val rows = 6
            val cellW = size.width / cols
            val cellH = size.height / rows
            for (r in 0 until rows) {
                val offsetX = if (r % 2 == 1) cellW * 0.45f else 0f
                for (c in 0 until cols) {
                    val cx = c * cellW + cellW * 0.5f + offsetX
                    val cy = r * cellH + cellH * 0.5f
                    if (cx < size.width - 20f) {
                        drawCircle(
                            color = Color(0xFFE8E4FF),
                            radius = 18.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar with Back button (when standalone) + Privacy Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isStandaloneView) {
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onContinue() },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SeekhBorderGray)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SeekhTextPrimary
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SeekhGreenSoft,
                    border = BorderStroke(1.dp, SeekhGreen100)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = SeekhGreen500,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "100% On-Device Safe AI",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SeekhGreen600
                        )
                    }
                }

                Spacer(modifier = Modifier.size(42.dp))
            }

            // Center Soft Bento Card with Golden-Indigo Crest & Noor Astro-Owl Mascot
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value),
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, SeekhGreen100)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(210.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val s = size.width * 0.45f
                            val shieldPath = Path().apply {
                                moveTo(cx, cy - s * 0.94f)
                                lineTo(cx + s * 0.86f, cy - s * 0.54f)
                                lineTo(cx + s * 0.86f, cy + s * 0.14f)
                                quadraticTo(cx + s * 0.70f, cy + s * 0.80f, cx, cy + s * 1.04f)
                                quadraticTo(cx - s * 0.70f, cy + s * 0.80f, cx - s * 0.86f, cy + s * 0.14f)
                                lineTo(cx - s * 0.86f, cy - s * 0.54f)
                                close()
                            }
                            drawPath(
                                path = shieldPath,
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFFFFE57F), Color(0xFFFFB800))
                                )
                            )
                        }

                        NoorMascotVectorAvatar(
                            mood = BuddyMood.IDLE,
                            modifier = Modifier.size(164.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Seekh AI Academy",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = SeekhTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Built with child-first digital safety. Voice tutoring with Noor runs locally on your device with zero third-party ads.",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = SeekhTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PrivacyFeatureChip("Offline Voice AI")
                        PrivacyFeatureChip("Zero Ads")
                        PrivacyFeatureChip("Kid Safe")
                    }
                }
            }

            // Bottom CTA Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text(
                    text = if (isStandaloneView) "Return to Academy" else "Start Learning Journey",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PrivacyFeatureChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = SeekhSurfaceGray,
        border = BorderStroke(1.dp, SeekhBorderGray)
    ) {
        Text(
            text = text,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = SeekhGreen600,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// 2. Onboarding & Drawer Language Selection Screen (With Proper Back Key Support!)
@Composable
fun LanguageSelectionScreen(
    selectedLangCode: String,
    buddyMood: BuddyMood,
    onSelectLanguage: (LearningLanguageOption) -> Unit,
    onContinue: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    BackHandler(enabled = onBack != null) {
        onBack?.invoke()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 190.dp, curveDepth = 30.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onBack() },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bilingual Learning Mode",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Choose your preferred reading & helper language",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Language cards list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 115.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SeekhCatalog.supportedLanguages) { lang ->
                    val isSelected = lang.code == selectedLangCode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLanguage(lang) },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = if (isSelected) 6.dp else 2.dp,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) SeekhGreen500 else SeekhBorderGray
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                CustomLanguageGlobeBadge(
                                    assetHint = lang.svgAsset,
                                    modifier = Modifier.size(46.dp)
                                )
                                Column {
                                    Text(
                                        text = lang.title,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = SeekhTextPrimary
                                    )
                                    Text(
                                        text = "Stories & bilingual meanings in ${lang.nativeName}",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = SeekhTextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SeekhGreen500 else SeekhSurfaceGray)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isSelected) SeekhGreen500 else SeekhBorderGray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Continue Bar + Noor Mascot
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                NoorMascotVectorAvatar(
                    mood = buddyMood,
                    modifier = Modifier.size(54.dp)
                )
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text(
                        text = "Save & Continue",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// 3. Interactive AI Tutor Practice Onboarding ("Meet Noor, Your Wise Astro-Owl Reading Tutor")
@Composable
fun InteractiveBuddyIntroScreen(
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onPracticeListen: (() -> Unit) -> Unit,
    onFinishOnboarding: () -> Unit,
    onBack: () -> Unit = {}
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    val practiceWords = listOf("I", "love", "to", "read", "stories!")
    var highlightedCount by remember { mutableIntStateOf(0) }

    // Step-by-step Back key handling inside onboarding!
    BackHandler {
        if (stepIndex > 0) {
            stepIndex--
        } else {
            onBack()
        }
    }

    LaunchedEffect(stepIndex) {
        when (stepIndex) {
            0 -> onSpeakText("Hi! I am Noor, your wise reading owl. Let's explore magical stories together!")
            1 -> onSpeakText("Tap on any word to hear its syllables, or read out loud to earn golden stars!")
            2 -> onSpeakText("Try reading this sentence out loud, or tap each word below!")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 165.dp, curveDepth = 28.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Back Button + Step Progress Dots + Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            if (stepIndex > 0) stepIndex-- else onBack()
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.20f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous step",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { idx ->
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (idx == stepIndex) 30.dp else 10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (idx <= stepIndex) Color.White else Color.White.copy(alpha = 0.35f))
                        )
                    }
                }

                TextButton(onClick = onFinishOnboarding) {
                    Text(
                        text = "Skip",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            // Center Custom Vector Illustration Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 14.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, SeekhGreen100)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomOnboardingHeroIllustration(
                        step = stepIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = when (stepIndex) {
                            0 -> "Meet Noor, your AI Reading Tutor"
                            1 -> "Earn crystal stars & golden trophies"
                            else -> "Try reading aloud right now!"
                        },
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        color = SeekhTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (stepIndex < 2) {
                        Text(
                            text = when (stepIndex) {
                                0 -> "Noor listens as you read stories out loud, breaks tricky words into syllables, and translates sentences."
                                else -> "Play Word Scramble, Sky Balloon Pop, Speed Reading, and Vocabulary Studio to unlock royal badges."
                            },
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = SeekhTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Interactive Try-Reading Practice Box
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SeekhGreenSoft,
                            border = BorderStroke(1.dp, SeekhGreen100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    practiceWords.forEachIndexed { idx, word ->
                                        val isRead = idx < highlightedCount
                                        Text(
                                            text = "$word ",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 21.sp,
                                            color = if (isRead) SeekhGreen500 else SeekhTextPrimary,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    onSpeakText(word)
                                                    if (idx >= highlightedCount) {
                                                        highlightedCount = idx + 1
                                                    }
                                                }
                                                .padding(horizontal = 3.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        onPracticeListen {
                                            highlightedCount = practiceWords.size
                                            onSpeakText("Wonderful reading! You earned fifty golden stars!")
                                        }
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.5.dp, SeekhGreen500)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Read Aloud",
                                        tint = SeekhGreen500
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (highlightedCount >= practiceWords.size) "Awesome! +50 Stars ⭐" else "Tap Mic or Tap Words to Read",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = SeekhGreen500
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Noor Avatar + Next Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NoorMascotVectorAvatar(
                    mood = buddyMood,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            onSpeakText("Let's open your reading library!")
                        }
                )

                Button(
                    onClick = {
                        if (stepIndex < 2) {
                            stepIndex++
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text(
                        text = if (stepIndex < 2) "Next Step" else "Choose Reader Profile",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private val avatarCharacterNames = listOf(
    "Leo Lion",
    "Pandy Panda",
    "Luna Kitty",
    "Koko Koala",
    "Foxy Explorer",
    "Bobo Bunny",
    "Ollie Owl",
    "Barnaby Bear",
    "Pip Penguin",
    "Uni Star",
    "Reddy Panda",
    "Sunny Puppy"
)

fun getAvatarCharacterName(avatarIndex: Int): String {
    val safe = ((avatarIndex - 1).mod(avatarCharacterNames.size) + avatarCharacterNames.size) % avatarCharacterNames.size
    return avatarCharacterNames[safe]
}

// 4. Redesigned Soft Bento Profile Switcher & First-Profile Studio Screen (Zero Pre-Added Dummy Profiles + Real-Time Progress Rings!)
@Composable
fun ProfileSwitcherScreen(
    profiles: List<LearnerProfile>,
    activeProfileId: String,
    buddyMood: BuddyMood,
    accountState: SignedInAccountState = SignedInAccountState(),
    onUpdateAccountState: (SignedInAccountState) -> Unit = {},
    onSelectProfile: (LearnerProfile) -> Unit,
    onCreateProfile: (String, Int, Int) -> Unit,
    onEditProfile: (LearnerProfile, String, Int) -> Unit = { _, _, _ -> },
    onDeleteProfile: (LearnerProfile) -> Unit = {},
    onOpenParentsSection: () -> Unit,
    onBuddyClick: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var showCreateProfileDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<LearnerProfile?>(null) }
    var deletingProfile by remember { mutableStateOf<LearnerProfile?>(null) }
    var showAccountMenuDialog by remember { mutableStateOf(false) }

    // Inline First-Profile Studio State when profiles list is empty (zero dummy profiles!)
    var firstProfileName by remember { mutableStateOf("") }
    var firstProfileAvatar by remember { mutableIntStateOf(1) }
    var firstProfileGoalMinutes by remember { mutableIntStateOf(10) }

    BackHandler(
        enabled = onBack != null || showCreateProfileDialog || editingProfile != null || deletingProfile != null || showAccountMenuDialog
    ) {
        when {
            showAccountMenuDialog -> showAccountMenuDialog = false
            deletingProfile != null -> deletingProfile = null
            editingProfile != null -> editingProfile = null
            showCreateProfileDialog -> showCreateProfileDialog = false
            else -> onBack?.invoke()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 220.dp, curveDepth = 36.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with optional Back button + Cloud Account Sync chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onBack != null && profiles.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .bouncyClick { onBack() },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.20f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (profiles.isEmpty()) "Create Your Profile" else "Who is reading today?",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 23.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (profiles.isEmpty()) {
                            "Personalize your avatar & daily reading goal"
                        } else {
                            "Tap your profile to continue your reading progress"
                        },
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.90f)
                    )
                }

                Spacer(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (profiles.isEmpty()) {
                    // ZERO PRE-ADDED DUMMY PROFILES: Render Soft Interactive First-Reader Profile Studio Card!
                    item {
                        AnimatedEntranceBox {
                            val avatarScale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "first_avatar_scale"
                            )

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                color = Color.White,
                                shadowElevation = 10.dp,
                                border = BorderStroke(1.dp, SeekhGreen100)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 22.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Live Animated Character Avatar Preview
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.scale(avatarScale)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(102.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(SeekhGreenSoft, Color.Transparent)
                                                    )
                                                )
                                        )
                                        ProfileAvatarCircle(
                                            avatarIndex = firstProfileAvatar,
                                            size = 86.dp,
                                            borderWidth = 3.dp,
                                            borderColor = SeekhGreen500
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = SeekhGreenSoft
                                    ) {
                                        Text(
                                            text = "Companion: ${getAvatarCharacterName(firstProfileAvatar)}",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = SeekhGreen600,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    OutlinedTextField(
                                        value = firstProfileName,
                                        onValueChange = { firstProfileName = it },
                                        label = { Text("Enter reader's name", fontFamily = GoogleSansFamily) },
                                        placeholder = { Text("e.g. Ali, Fatima, Rayan...", fontFamily = GoogleSansFamily) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SeekhGreen500,
                                            unfocusedBorderColor = SeekhBorderGray
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Choose your character avatar",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SeekhTextPrimary,
                                        modifier = Modifier.align(Alignment.Start)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // 2 Rows of 6 Original Vector Avatars with Spring Bounce Selection
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        listOf((1..6).toList(), (7..12).toList()).forEach { rowIndices ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                rowIndices.forEach { idx ->
                                                    val isChosen = idx == firstProfileAvatar
                                                    val itemScale by animateFloatAsState(
                                                        targetValue = if (isChosen) 1.12f else 1f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessMedium
                                                        ),
                                                        label = "av_sel_$idx"
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .scale(itemScale)
                                                            .clip(CircleShape)
                                                            .border(
                                                                width = if (isChosen) 2.5.dp else 1.dp,
                                                                color = if (isChosen) SeekhGreen500 else SeekhBorderGray,
                                                                shape = CircleShape
                                                            )
                                                            .bouncyClick { firstProfileAvatar = idx },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        ProfileAvatarCircle(
                                                            avatarIndex = idx,
                                                            size = 44.dp,
                                                            borderWidth = 0.dp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Daily Reading Goal Selector (5 min, 10 min, 15 min, 20 min)
                                    Text(
                                        text = "Daily reading goal",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SeekhTextPrimary,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(5, 10, 15, 20).forEach { mins ->
                                            val isSelected = firstProfileGoalMinutes == mins
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .bouncyClick { firstProfileGoalMinutes = mins },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (isSelected) SeekhGreen500 else SeekhSurfaceGray,
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (isSelected) SeekhGreen500 else SeekhBorderGray
                                                )
                                            ) {
                                                Text(
                                                    text = "$mins min",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = if (isSelected) Color.White else SeekhTextPrimary,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 10.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            val finalName = firstProfileName.trim()
                                                .ifEmpty { getAvatarCharacterName(firstProfileAvatar).substringBefore(" ") }
                                            onCreateProfile(finalName, firstProfileAvatar, firstProfileGoalMinutes)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp),
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                    ) {
                                        Text(
                                            text = "Create Profile & Start Reading",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(profiles.size) { index ->
                        val profile = profiles[index]
                        val isCurrent = profile.id == activeProfileId
                        val vehicle = SeekhCatalog.getVehicleForWpm(profile.wordsPerMinute)
                        val goalFraction = (profile.minutesReadToday.toFloat() / profile.dailyGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                        val animatedGoalFrac by animateFloatAsState(
                            targetValue = goalFraction,
                            animationSpec = tween(550, easing = FastOutSlowInEasing),
                            label = "prof_goal_ring_$index"
                        )
                        val inProgressCount = profile.bookPageProgress.count { it.value > 0 }

                        AnimatedEntranceBox(delayMs = index * 65) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bouncyClick(scaleDown = 0.97f) { onSelectProfile(profile) },
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White,
                                shadowElevation = if (isCurrent) 8.dp else 3.dp,
                                border = BorderStroke(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) SeekhGreen500 else SeekhBorderGray
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Avatar with Animated Circular Daily Goal Progress Ring!
                                            Box(
                                                modifier = Modifier.size(72.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    progress = { animatedGoalFrac.coerceAtLeast(0.06f) },
                                                    modifier = Modifier.fillMaxSize(),
                                                    color = if (goalFraction >= 1f) SeekhGreenMint else SeekhGreen500,
                                                    trackColor = SeekhGreen100,
                                                    strokeWidth = 3.5.dp
                                                )
                                                ProfileAvatarCircle(
                                                    avatarIndex = profile.avatarIndex,
                                                    size = 60.dp,
                                                    borderWidth = 1.5.dp,
                                                    borderColor = Color.White
                                                )
                                            }

                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = profile.name,
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 18.sp,
                                                        color = SeekhTextPrimary
                                                    )
                                                    if (isCurrent) {
                                                        Surface(
                                                            shape = RoundedCornerShape(50),
                                                            color = SeekhGreenSoft
                                                        ) {
                                                            Text(
                                                                text = "ACTIVE",
                                                                fontFamily = GoogleSansFamily,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 9.sp,
                                                                color = SeekhGreen500,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = buildString {
                                                        append("${profile.completedBookIds.size} finished")
                                                        if (inProgressCount > 0) append(" • $inProgressCount reading")
                                                        if (profile.wordsPerMinute > 0) {
                                                            append(" • ${vehicle.emoji} ${profile.wordsPerMinute} WPM")
                                                        }
                                                    },
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    color = SeekhTextSecondary
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Today: ${profile.minutesReadToday}/${profile.dailyGoalMinutes} min • Streak: ${profile.streakDays}d",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (goalFraction >= 1f) SeekhGreenMint else SeekhGreen600
                                                )
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // Soft Golden-Amber Star Pill
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = Color(0xFFFFF8E1),
                                                border = BorderStroke(1.dp, SeekhYellow500)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    GoldenStarIcon(size = 15.dp)
                                                    Text(
                                                        text = profile.stars.toString(),
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 15.sp,
                                                        color = SeekhTextPrimary
                                                    )
                                                }
                                            }

                                            // Edit profile button
                                            IconButton(
                                                onClick = { editingProfile = profile },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit profile",
                                                    tint = SeekhGreen600,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            // Delete profile button
                                            IconButton(
                                                onClick = { deletingProfile = profile },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Delete profile",
                                                    tint = SeekhCoralRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Smooth Animated Daily Goal Progress Bar inside Profile Card
                                    LinearProgressIndicator(
                                        progress = { animatedGoalFrac.coerceAtLeast(0.04f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(50)),
                                        color = if (goalFraction >= 1f) SeekhGreenMint else SeekhGreen500,
                                        trackColor = SeekhGreen100
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Row: Add New Reader (when at least 1 profile exists)
                if (profiles.isNotEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bouncyClick { showCreateProfileDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.dp, SeekhBorderGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SeekhGreenSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "New Reader",
                                        tint = SeekhGreen500,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Add Reader",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SeekhTextPrimary
                                    )
                                    Text(
                                        text = "Create new profile",
                                        fontFamily = GoogleSansFamily,
                                        fontSize = 11.sp,
                                        color = SeekhTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Integrated Noor Companion Tip Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .bouncyClick { onBuddyClick() },
                        shape = RoundedCornerShape(22.dp),
                        color = SeekhGreenSoft,
                        border = BorderStroke(1.dp, SeekhGreen100)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            NoorMascotVectorAvatar(
                                mood = buddyMood,
                                modifier = Modifier.size(48.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Noor • Real-Time Progress Tracker",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SeekhGreen600
                                )
                                Text(
                                    text = "Every page you read, word you master, and game you play is saved automatically to your profile!",
                                    fontFamily = GoogleSansFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = SeekhTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateProfileDialog) {
        CreateProfileDialog(
            onDismiss = { showCreateProfileDialog = false },
            onCreate = { name, avatarIdx, goalMins ->
                onCreateProfile(name, avatarIdx, goalMins)
                showCreateProfileDialog = false
            }
        )
    }

    editingProfile?.let { prof ->
        ChangeProfileNameAndAvatarDialog(
            profile = prof,
            onDismiss = { editingProfile = null },
            onSave = { newName, newAvatar ->
                onEditProfile(prof, newName, newAvatar)
                editingProfile = null
            }
        )
    }

    deletingProfile?.let { prof ->
        AlertDialog(
            onDismissRequest = { deletingProfile = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Delete profile \"${prof.name}\"?",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = SeekhTextPrimary
                )
            },
            text = {
                Text(
                    text = "This will permanently remove ${prof.name}'s profile, ${prof.stars} stars, and story bookmarks from this device.",
                    fontFamily = GoogleSansFamily,
                    fontSize = 14.sp,
                    color = SeekhTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProfile(prof)
                        deletingProfile = null
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhCoralRed)
                ) {
                    Text("Delete Profile", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingProfile = null }) {
                    Text("Cancel", fontFamily = GoogleSansFamily)
                }
            }
        )
    }

    if (showAccountMenuDialog) {
        AccountMenuDialog(
            accountState = accountState,
            onUpdateAccountState = { updated ->
                onUpdateAccountState(updated)
                showAccountMenuDialog = false
            },
            onDismiss = { showAccountMenuDialog = false }
        )
    }
}

// Signed-In Account Menu & Account Selection Dialog (Zero hardcoded dummy email required; user can enter their own email or use without an account)
@Composable
fun AccountMenuDialog(
    accountState: SignedInAccountState,
    onUpdateAccountState: (SignedInAccountState) -> Unit,
    onDismiss: () -> Unit
) {
    var emailInput by remember(accountState.email) { mutableStateOf(accountState.email) }
    var displayNameInput by remember(accountState.displayName) {
        mutableStateOf(if (accountState.isSignedIn) accountState.displayName else "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (accountState.isSignedIn) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = SeekhGreen500,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Account & Cloud Sync",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = SeekhTextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Link your email account to sync learner profiles, classroom assignments, and reading groups across devices.",
                    fontFamily = GoogleSansFamily,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = SeekhTextSecondary
                )

                OutlinedTextField(
                    value = displayNameInput,
                    onValueChange = { displayNameInput = it },
                    label = { Text("Parent / Teacher Name", fontFamily = GoogleSansFamily) },
                    placeholder = { Text("e.g. Shehzad Aslam", fontFamily = GoogleSansFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email address", fontFamily = GoogleSansFamily) },
                    placeholder = { Text("your.email@gmail.com", fontFamily = GoogleSansFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (accountState.isSignedIn) {
                    TextButton(
                        onClick = {
                            onUpdateAccountState(
                                SignedInAccountState(
                                    isSignedIn = false,
                                    email = "",
                                    displayName = "Local Device Account",
                                    syncStatus = "Using without an account"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sign out & Use without an account",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            color = SeekhCoralRed
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanEmail = emailInput.trim()
                    if (cleanEmail.isNotEmpty()) {
                        val cleanName = displayNameInput.trim().ifEmpty { "Parent & Teacher Account" }
                        onUpdateAccountState(
                            SignedInAccountState(
                                isSignedIn = true,
                                email = cleanEmail,
                                displayName = cleanName,
                                syncStatus = "Now your profile is saved with $cleanEmail"
                            )
                        )
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text("Save & Link Account", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = GoogleSansFamily)
            }
        }
    )
}

@Composable
fun CreateProfileDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableIntStateOf(2) }
    var dailyGoalMins by remember { mutableIntStateOf(10) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Create New Reader Profile",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = SeekhTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatarCircle(
                    avatarIndex = selectedAvatar,
                    size = 74.dp,
                    borderWidth = 2.5.dp,
                    borderColor = SeekhGreen500
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = getAvatarCharacterName(selectedAvatar),
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SeekhGreen600
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Reader name", fontFamily = GoogleSansFamily) },
                    placeholder = { Text("Enter reader's name", fontFamily = GoogleSansFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items((1..12).toList()) { idx ->
                        val isChosen = idx == selectedAvatar
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isChosen) 3.dp else 1.dp,
                                    color = if (isChosen) SeekhGreen500 else SeekhBorderGray,
                                    shape = CircleShape
                                )
                                .bouncyClick { selectedAvatar = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileAvatarCircle(
                                avatarIndex = idx,
                                size = 50.dp,
                                borderWidth = 0.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 15, 20).forEach { mins ->
                        val active = dailyGoalMins == mins
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .bouncyClick { dailyGoalMins = mins },
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) SeekhGreen500 else SeekhSurfaceGray
                        ) {
                            Text(
                                text = "${mins}m",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (active) Color.White else SeekhTextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = name.trim().ifEmpty { getAvatarCharacterName(selectedAvatar).substringBefore(" ") }
                    onCreate(finalName, selectedAvatar, dailyGoalMins)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text("Create Profile", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = GoogleSansFamily, color = SeekhTextSecondary)
            }
        }
    )
}
