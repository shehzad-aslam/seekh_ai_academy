package com.tllstudio.smartlearn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.util.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caverock.androidsvg.SVG
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// 100% Original "Lumina Soft Indigo-Violet, Mint-Emerald & Warm Porcelain" Design System Tokens
val SeekhGreen500 = Color(0xFF5E4AE3)       // Primary Royal Soft Indigo-Violet
val SeekhGreen600 = Color(0xFF4B37C8)       // Deep Soft Indigo
val SeekhGreen700 = Color(0xFF3B28B0)       // Rich Indigo Accent
val SeekhGreen900 = Color(0xFF23166B)       // Dark Indigo Ink
val SeekhGreen100 = Color(0xFFE5E0FF)       // Soft Lavender Hairline / Track
val SeekhGreenMint = Color(0xFF10B981)      // Mint Emerald Success Accent
val SeekhGreenSoft = Color(0xFFF3F0FF)      // Soft Lavender Card Tint

val SeekhYellow500 = Color(0xFFFFB800)      // Warm Sunny Gold
val SeekhYellowAmber = Color(0xFFFFCA3A)    // Soft Golden Glow
val SeekhOrangeStar = Color(0xFFFF8552)     // Warm Peach-Coral Accent

val SeekhBlue500 = Color(0xFF0EA5E9)        // Sky Cyan-Teal
val SeekhBlue600 = Color(0xFF0284C7)        // Deep Ocean Teal
val SeekhBlueReading = Color(0xFF4F46E5)    // Active Spoken Word Indigo
val SeekhBlueSoft = Color(0xFFEFF8FF)       // Soft Sky Ice Tint

val SeekhCoralRed = Color(0xFFF43F5E)       // Soft Rose-Coral Alert
val SeekhPinkBadge = Color(0xFFEC4899)      // Soft Berry Pink

val SeekhJumblePurple = Color(0xFF6C5CE7)   // Soft Violet Game Theme
val SeekhJumbleDarkPurple = Color(0xFF4834D4)
val SeekhJumbleSlotPurple = Color(0xFF3C28B8)

val SeekhBalloonSky = Color(0xFFF0F9FF)     // Soft Cloud Sky Arena
val SeekhBalloonDarkBlue = Color(0xFF0F4C81)

val SeekhSpeedYellow = Color(0xFFFF9F43)    // Warm Sunset Peach-Amber
val SeekhSpeedBrown = Color(0xFF6F3B08)

val SeekhVocabLime = Color(0xFF10B981)      // Soft Mint-Emerald Studio
val SeekhVocabDarkGreen = Color(0xFF065F46)

val SeekhTextPrimary = Color(0xFF1E1B4B)    // Deep Midnight Indigo Ink (Softer than harsh black)
val SeekhTextSecondary = Color(0xFF64748B)  // Soft Slate Gray
val SeekhTabUnselected = Color(0xFF94A3B8)
val SeekhSurfaceGray = Color(0xFFF7F8FC)    // Warm Soft Porcelain Background
val SeekhBorderGray = Color(0xFFE4E7F2)     // Soft Periwinkle Border

val GoogleSansFamily = FontFamily(
    Font(R.font.google_sans_regular, FontWeight.Normal),
    Font(R.font.google_sans_medium, FontWeight.Medium),
    Font(R.font.google_sans_bold, FontWeight.Bold),
    Font(R.font.google_sans_display_bold, FontWeight.ExtraBold)
)

val SeekhTypography = Typography(
    displayLarge = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    displayMedium = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 34.sp),
    headlineLarge = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = GoogleSansFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
)

@Composable
fun SeekhAIAcademyTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = SeekhGreen500,
        onPrimary = Color.White,
        primaryContainer = SeekhGreenSoft,
        onPrimaryContainer = SeekhGreen900,
        secondary = SeekhBlue600,
        onSecondary = Color.White,
        tertiary = SeekhYellow500,
        background = SeekhSurfaceGray,
        onBackground = SeekhTextPrimary,
        surface = Color.White,
        onSurface = SeekhTextPrimary,
        surfaceVariant = SeekhSurfaceGray,
        onSurfaceVariant = SeekhTextSecondary
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SeekhTypography,
        content = content
    )
}

// Senior UI/UX Designer Soft Diffused Color-Tinted Elevation Shadow (Replaces flat harsh black Android shadows with warm indigo/emerald ambient halo shadows!)
fun Modifier.softCardShadow(
    elevation: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(22.dp),
    spotColor: Color = Color(0x423B28B0),
    ambientColor: Color = Color(0x261E1B4B)
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = ambientColor,
    spotColor = spotColor
)

// Senior Animator Tactile Press-Scale Micro-Interaction (Spring Bouncy Touch Feedback)
fun Modifier.bouncyClick(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bouncy_click_scale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

// Senior Animator Smooth Staggered Card Entrance Wrapper
@Composable
fun AnimatedEntranceBox(
    delayMs: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(380, easing = FastOutSlowInEasing),
        label = "ent_alpha"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ent_y"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.96f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ent_scale"
    )
    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = offsetY
            this.scaleX = scale
            this.scaleY = scale
        }
    ) {
        content()
    }
}

// High-speed LruCache for Story Book WebP and Vocab SVG Bitmaps
object AssetBitmapCache {
    private val cache = LruCache<String, ImageBitmap>(180)

    fun loadWebpOrPng(context: Context, assetPath: String): ImageBitmap? {
        cache.get(assetPath)?.let { return it }
        return try {
            context.assets.open(assetPath).use { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                bmp?.asImageBitmap()?.also { cache.put(assetPath, it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun loadSvg(context: Context, assetPath: String, targetPx: Int = 256): ImageBitmap? {
        val key = "$assetPath@$targetPx"
        cache.get(key)?.let { return it }
        return try {
            context.assets.open(assetPath).use { stream ->
                val svg = SVG.getFromInputStream(stream)
                val docW = if (svg.documentWidth > 0) svg.documentWidth else svg.documentViewBox?.width() ?: 120f
                val docH = if (svg.documentHeight > 0) svg.documentHeight else svg.documentViewBox?.height() ?: 120f
                val ratio = if (docH > 0f) docW / docH else 1f
                val w = if (ratio >= 1f) targetPx else (targetPx * ratio).toInt().coerceAtLeast(32)
                val h = if (ratio >= 1f) (targetPx / ratio).toInt().coerceAtLeast(32) else targetPx
                if (svg.documentViewBox == null) {
                    svg.setDocumentViewBox(0f, 0f, docW, docH)
                }
                svg.setDocumentWidth("100%")
                svg.setDocumentHeight("100%")
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = AndroidCanvas(bmp)
                svg.renderToCanvas(canvas)
                bmp.asImageBitmap().also { cache.put(key, it) }
            }
        } catch (e: Exception) {
            null
        }
    }
}

// Clean Flat-Crisp Story Illustration Renderer (Loads the newly generated hand-drawn children's storybook illustrations from assets/books/ with zero 3D glaze or tint overlays!)
@Composable
fun AssetWebpImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val bitmap = remember(assetPath) {
        AssetBitmapCache.loadWebpOrPng(context, assetPath)
    }
    Box(modifier = modifier.clipToBounds()) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(SeekhGreenSoft, SeekhBlueSoft)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                GoldenStarIcon(size = 36.dp)
            }
        }
    }
}

// Custom Vector Interceptor: Replaces any proprietary Google UI/Mascot/Prize/Profile SVGs with 100% Original Custom Vector Canvas Art!
@Composable
fun AssetSvgImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    targetPx: Int = 256,
    contentScale: ContentScale = ContentScale.Fit
) {
    when {
        assetPath.contains("diya") -> {
            val mood = when {
                assetPath.contains("listening") -> BuddyMood.LISTENING
                assetPath.contains("speaking") -> BuddyMood.SPEAKING
                assetPath.contains("thinking") -> BuddyMood.THINKING
                else -> BuddyMood.IDLE
            }
            NoorMascotVectorAvatar(mood = mood, modifier = modifier)
        }
        assetPath.contains("profile_face_") -> {
            val idx = assetPath.filter { it.isDigit() }.toIntOrNull() ?: 1
            CustomLearnerVectorAvatar(avatarIndex = idx, modifier = modifier)
        }
        assetPath.contains("prize_") -> {
            CustomPrizeMedallionVector(assetHint = assetPath, modifier = modifier)
        }
        assetPath.contains("star.svg") -> {
            GoldenStarIcon(modifier = modifier)
        }
        assetPath.contains("hand_pointer") -> {
            CustomMagicPointerIcon(modifier = modifier)
        }
        assetPath.contains("dog_studying") -> {
            CustomScholarOwlIllustration(modifier = modifier)
        }
        assetPath.contains("oobe_ra_intro_") -> {
            val step = when {
                assetPath.contains("1") -> 0
                assetPath.contains("2") -> 1
                else -> 2
            }
            CustomOnboardingHeroIllustration(step = step, modifier = modifier)
        }
        assetPath.contains("language_selection_") -> {
            CustomLanguageGlobeBadge(assetHint = assetPath, modifier = modifier)
        }
        assetPath.contains("gen_ai") -> {
            CustomAiSparkleBadge(modifier = modifier)
        }
        assetPath.contains("empty_screen") -> {
            CustomSearchLensIllustration(modifier = modifier)
        }
        assetPath.contains("download.svg") -> {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = contentDescription,
                tint = SeekhGreen500,
                modifier = modifier
            )
        }
        assetPath.contains("author.svg") -> {
            Icon(imageVector = Icons.Default.EditNote, contentDescription = contentDescription, tint = SeekhGreen500, modifier = modifier)
        }
        assetPath.contains("artist.svg") -> {
            Icon(imageVector = Icons.Default.Palette, contentDescription = contentDescription, tint = SeekhPinkBadge, modifier = modifier)
        }
        assetPath.contains("publisher.svg") -> {
            Icon(imageVector = Icons.Default.AutoStories, contentDescription = contentDescription, tint = SeekhBlue600, modifier = modifier)
        }
        else -> {
            // Vocabulary items (vocab/ic_vocab_*.svg) rendered inside a soft pastel vector halo frame
            val context = LocalContext.current
            val bitmap = remember(assetPath, targetPx) {
                AssetBitmapCache.loadSvg(context, assetPath, targetPx)
            }
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.88f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(SeekhGreenSoft, SeekhBlueSoft.copy(alpha = 0.45f), Color.Transparent)
                            )
                        )
                )
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(0.82f),
                        contentScale = contentScale
                    )
                } else {
                    GoldenStarIcon(size = 28.dp)
                }
            }
        }
    }
}

// 100% Original Soft Aurora Silk Wave Header Background (Multi-ribbon pastel gradient + drifting bokeh pearls)
@Composable
fun CurvedGreenHeaderBackground(
    headerHeight: Dp = 185.dp,
    curveDepth: Dp = 32.dp,
    color: Color = SeekhGreen500
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_header")
    val shimmerShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_phase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        val w = size.width
        val h = size.height
        val cDepth = curveDepth.toPx()
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, h - cDepth)
            cubicTo(
                w * 0.72f, h + cDepth * (0.55f + wavePhase * 0.12f),
                w * 0.28f, h - cDepth * (0.65f - wavePhase * 0.12f),
                0f, h - cDepth * 0.35f
            )
            close()
        }
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF533FD8),
                    Color(0xFF6E5EF0),
                    Color(0xFF22B8F0)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )
        // Secondary soft translucent silk wave ribbon
        val ribbonPath = Path().apply {
            moveTo(0f, h * 0.35f)
            cubicTo(
                w * 0.35f, h * (0.58f + wavePhase * 0.08f),
                w * 0.65f, h * (0.22f - wavePhase * 0.08f),
                w, h * 0.48f
            )
            lineTo(w, 0f)
            lineTo(0f, 0f)
            close()
        }
        drawPath(
            path = ribbonPath,
            color = Color.White.copy(alpha = 0.07f)
        )

        // Soft floating glass bokeh orbs inside header
        drawCircle(
            color = Color.White.copy(alpha = 0.10f),
            radius = w * 0.24f,
            center = Offset(w * (0.18f + shimmerShift * 0.07f), h * (0.28f + wavePhase * 0.03f))
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.09f),
            radius = w * 0.18f,
            center = Offset(w * (0.84f - shimmerShift * 0.06f), h * (0.36f - wavePhase * 0.03f))
        )
        drawCircle(
            color = Color(0x26FFE066),
            radius = w * 0.12f,
            center = Offset(w * (0.52f + wavePhase * 0.04f), h * 0.20f)
        )
    }
}

// 100% Original Custom Vector 3D Faceted Star Crystal Icon (Zero dependency on star.svg)
@Composable
fun GoldenStarIcon(size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val outerR = this.size.minDimension * 0.46f
        val innerR = outerR * 0.46f
        val starPath = Path()
        for (i in 0 until 10) {
            val angle = -PI / 2.0 + i * (PI / 5.0)
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
        }
        starPath.close()

        // Soft outer golden glow
        drawCircle(
            color = Color(0x33FFB800),
            radius = outerR * 1.05f,
            center = Offset(cx, cy)
        )
        // Rich 3D golden gradient star
        drawPath(
            path = starPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFE066), Color(0xFFFFB800), Color(0xFFF59E0B))
            )
        )
        drawPath(
            path = starPath,
            color = Color.White.copy(alpha = 0.65f),
            style = Stroke(width = this.size.minDimension * 0.05f)
        )
        // Inner sparkle highlight dot
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = innerR * 0.28f,
            center = Offset(cx - outerR * 0.12f, cy - outerR * 0.14f)
        )
    }
}

// 100% Original Animated Custom Vector AI Tutor Mascot: "Noor the Wise Astro-Owl" (60 FPS Breathing, Head Sway, Wing Flap & Sonar Aura)
@Composable
fun NoorMascotVectorAvatar(
    mood: BuddyMood,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "noor_mascot")
    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (mood == BuddyMood.SPEAKING) 280 else 1050, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_flap"
    )
    val beakOpen by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = if (mood == BuddyMood.SPEAKING) 1f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(190, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beak"
    )
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )
    val headSwayDeg by infiniteTransition.animateFloat(
        initialValue = -2.8f,
        targetValue = 2.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "head_sway"
    )
    val sonarRingPulse by infiniteTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar_ring"
    )

    // Smooth periodic eye blink every 3.1 seconds
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            isBlinking = true
            kotlinx.coroutines.delay(140)
            isBlinking = false
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.54f
        val bodyR = minOf(w, h) * 0.36f * breathScale

        // Soft glowing aura ring behind Noor
        val auraColor = when (mood) {
            BuddyMood.LISTENING -> Color(0x4010B981)
            BuddyMood.SPEAKING -> Color(0x40FFB800)
            BuddyMood.THINKING -> Color(0x400EA5E9)
            BuddyMood.IDLE -> Color(0x245E4AE3)
        }
        drawCircle(
            color = auraColor,
            radius = bodyR * 1.26f,
            center = Offset(cx, cy)
        )
        if (mood == BuddyMood.LISTENING || mood == BuddyMood.SPEAKING) {
            val fadeAlpha = (1.42f - sonarRingPulse).coerceIn(0f, 0.45f)
            drawCircle(
                color = auraColor.copy(alpha = fadeAlpha),
                radius = bodyR * sonarRingPulse,
                center = Offset(cx, cy),
                style = Stroke(width = bodyR * 0.08f)
            )
        }

        rotate(degrees = headSwayDeg, pivot = Offset(cx, cy)) {
            // Left & Right Flapping Wings
            val wingOffsetY = (wingFlap / 10f) * bodyR * 0.24f
            drawOval(
                color = Color(0xFF4B37C8),
                topLeft = Offset(cx - bodyR * 1.18f, cy - bodyR * 0.35f + wingOffsetY),
                size = Size(bodyR * 0.52f, bodyR * 0.85f)
            )
            drawOval(
                color = Color(0xFF4B37C8),
                topLeft = Offset(cx + bodyR * 0.66f, cy - bodyR * 0.35f + wingOffsetY),
                size = Size(bodyR * 0.52f, bodyR * 0.85f)
            )

            // Cute Owl Ear Tufts
            val leftEar = Path().apply {
                moveTo(cx - bodyR * 0.72f, cy - bodyR * 0.55f)
                lineTo(cx - bodyR * 0.92f, cy - bodyR * 1.05f)
                lineTo(cx - bodyR * 0.32f, cy - bodyR * 0.82f)
                close()
            }
            val rightEar = Path().apply {
                moveTo(cx + bodyR * 0.72f, cy - bodyR * 0.55f)
                lineTo(cx + bodyR * 0.92f, cy - bodyR * 1.05f)
                lineTo(cx + bodyR * 0.32f, cy - bodyR * 0.82f)
                close()
            }
            drawPath(leftEar, color = Color(0xFF4B37C8))
            drawPath(rightEar, color = Color(0xFF4B37C8))

            // Rounded Royal Indigo-Violet Owl Body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF7C66FF), Color(0xFF5E4AE3), Color(0xFF4330B8))
                ),
                topLeft = Offset(cx - bodyR * 0.92f, cy - bodyR * 0.88f),
                size = Size(bodyR * 1.84f, bodyR * 1.78f),
                cornerRadius = CornerRadius(bodyR * 0.88f, bodyR * 0.88f)
            )

            // Soft Cream Belly Patch with Feather Scallops
            drawOval(
                color = Color(0xFFF3F0FF),
                topLeft = Offset(cx - bodyR * 0.58f, cy + bodyR * 0.08f),
                size = Size(bodyR * 1.16f, bodyR * 0.74f)
            )

            // Cream Facial Disc (Two overlapping soft circles)
            val eyeCenterY = cy - bodyR * 0.22f
            val eyeOffsetX = bodyR * 0.38f
            drawCircle(
                color = Color(0xFFFFFDF9),
                radius = bodyR * 0.42f,
                center = Offset(cx - eyeOffsetX, eyeCenterY)
            )
            drawCircle(
                color = Color(0xFFFFFDF9),
                radius = bodyR * 0.42f,
                center = Offset(cx + eyeOffsetX, eyeCenterY)
            )

            // Big Expressive Eyes (or curved happy blink lines)
            if (isBlinking) {
                drawArc(
                    color = Color(0xFF1E1B4B),
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(cx - eyeOffsetX - bodyR * 0.20f, eyeCenterY - bodyR * 0.12f),
                    size = Size(bodyR * 0.40f, bodyR * 0.24f),
                    style = Stroke(width = bodyR * 0.08f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF1E1B4B),
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(cx + eyeOffsetX - bodyR * 0.20f, eyeCenterY - bodyR * 0.12f),
                    size = Size(bodyR * 0.40f, bodyR * 0.24f),
                    style = Stroke(width = bodyR * 0.08f, cap = StrokeCap.Round)
                )
            } else {
                // Left & Right Iris + Starry Pupils
                listOf(cx - eyeOffsetX, cx + eyeOffsetX).forEach { ex ->
                    drawCircle(
                        color = Color(0xFF1E1B4B),
                        radius = bodyR * 0.22f,
                        center = Offset(ex, eyeCenterY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = bodyR * 0.08f,
                        center = Offset(ex - bodyR * 0.06f, eyeCenterY - bodyR * 0.06f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.7f),
                        radius = bodyR * 0.035f,
                        center = Offset(ex + bodyR * 0.07f, eyeCenterY + bodyR * 0.07f)
                    )
                }
            }

            // Rosy Peach Cheeks
            drawCircle(
                color = Color(0x66FF7A59),
                radius = bodyR * 0.14f,
                center = Offset(cx - bodyR * 0.56f, eyeCenterY + bodyR * 0.28f)
            )
            drawCircle(
                color = Color(0x66FF7A59),
                radius = bodyR * 0.14f,
                center = Offset(cx + bodyR * 0.56f, eyeCenterY + bodyR * 0.28f)
            )

            // Animated Golden Beak
            val beakDrop = bodyR * (0.14f + 0.12f * beakOpen)
            val beakPath = Path().apply {
                moveTo(cx - bodyR * 0.15f, eyeCenterY + bodyR * 0.10f)
                lineTo(cx + bodyR * 0.15f, eyeCenterY + bodyR * 0.10f)
                lineTo(cx, eyeCenterY + bodyR * 0.10f + beakDrop)
                close()
            }
            drawPath(beakPath, color = Color(0xFFFFB800))

            // Little Golden Star Crown / Scholar Crest above forehead
            drawCircle(
                color = Color(0xFFFFB800),
                radius = bodyR * 0.14f,
                center = Offset(cx, cy - bodyR * 0.92f)
            )
            drawCircle(
                color = Color.White,
                radius = bodyR * 0.05f,
                center = Offset(cx - bodyR * 0.03f, cy - bodyR * 0.95f)
            )

            // Cute Little Golden Feet
            drawOval(
                color = Color(0xFFFFB800),
                topLeft = Offset(cx - bodyR * 0.45f, cy + bodyR * 0.80f),
                size = Size(bodyR * 0.30f, bodyR * 0.16f)
            )
            drawOval(
                color = Color(0xFFFFB800),
                topLeft = Offset(cx + bodyR * 0.15f, cy + bodyR * 0.80f),
                size = Size(bodyR * 0.30f, bodyR * 0.16f)
            )
        }
    }
}

// 12 Original Custom Vector Learner Avatars (100% Copyright-Safe, Zero Google Monkey SVGs!)
@Composable
fun CustomLearnerVectorAvatar(
    avatarIndex: Int,
    modifier: Modifier = Modifier
) {
    val safeIndex = ((avatarIndex - 1).mod(12)) + 1
    val palettes = listOf(
        Pair(Color(0xFFFFF3D6), Color(0xFFF59E0B)), // 1: Leo Lion (Gold)
        Pair(Color(0xFFE6F9F0), Color(0xFF10B981)), // 2: Pandy Panda (Mint)
        Pair(Color(0xFFF3E8FF), Color(0xFF8B5CF6)), // 3: Luna Kitty (Violet)
        Pair(Color(0xFFE0F2FE), Color(0xFF0EA5E9)), // 4: Koko Koala (Sky)
        Pair(Color(0xFFFFE4E6), Color(0xFFF43F5E)), // 5: Foxy Explorer (Coral)
        Pair(Color(0xFFFCE7F3), Color(0xFFEC4899)), // 6: Bobo Bunny (Pink)
        Pair(Color(0xFFEEF2FF), Color(0xFF4F46E5)), // 7: Ollie Owl (Indigo)
        Pair(Color(0xFFFEF3C7), Color(0xFFD97706)), // 8: Barnaby Bear (Honey)
        Pair(Color(0xFFCCFBF1), Color(0xFF0D9488)), // 9: Pip Penguin (Teal)
        Pair(Color(0xFFF5F3FF), Color(0xFF7C3AED)), // 10: Uni Star (Lavender)
        Pair(Color(0xFFFFEDD5), Color(0xFFEA580C)), // 11: Reddy Panda (Cinnamon)
        Pair(Color(0xFFECFCCB), Color(0xFF65A30D))  // 12: Sunny Puppy (Lime)
    )
    val (bgPastel, mainColor) = palettes[(safeIndex - 1) % palettes.size]

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.54f
        val r = minOf(w, h) * 0.32f

        // Background pastel circle
        drawCircle(color = bgPastel, radius = minOf(w, h) * 0.5f, center = Offset(cx, h * 0.5f))

        // Character Ears (varies by index: round bear/panda ears, pointed cat/fox ears, or long bunny ears)
        when (safeIndex % 3) {
            0 -> {
                // Pointed Cat / Fox Ears
                val lEar = Path().apply {
                    moveTo(cx - r * 0.85f, cy - r * 0.25f)
                    lineTo(cx - r * 0.95f, cy - r * 1.15f)
                    lineTo(cx - r * 0.22f, cy - r * 0.78f)
                    close()
                }
                val rEar = Path().apply {
                    moveTo(cx + r * 0.85f, cy - r * 0.25f)
                    lineTo(cx + r * 0.95f, cy - r * 1.15f)
                    lineTo(cx + r * 0.22f, cy - r * 0.78f)
                    close()
                }
                drawPath(lEar, color = mainColor)
                drawPath(rEar, color = mainColor)
            }
            1 -> {
                // Round Bear / Lion / Koala Ears
                drawCircle(color = mainColor, radius = r * 0.38f, center = Offset(cx - r * 0.75f, cy - r * 0.72f))
                drawCircle(color = mainColor, radius = r * 0.38f, center = Offset(cx + r * 0.75f, cy - r * 0.72f))
                drawCircle(color = Color.White.copy(alpha = 0.65f), radius = r * 0.20f, center = Offset(cx - r * 0.75f, cy - r * 0.72f))
                drawCircle(color = Color.White.copy(alpha = 0.65f), radius = r * 0.20f, center = Offset(cx + r * 0.75f, cy - r * 0.72f))
            }
            else -> {
                // Tall Lop Bunny / Explorer Ears
                drawOval(
                    color = mainColor,
                    topLeft = Offset(cx - r * 0.78f, cy - r * 1.32f),
                    size = Size(r * 0.38f, r * 0.85f)
                )
                drawOval(
                    color = mainColor,
                    topLeft = Offset(cx + r * 0.40f, cy - r * 1.32f),
                    size = Size(r * 0.38f, r * 0.85f)
                )
            }
        }

        // Main Head Circle
        drawCircle(color = mainColor, radius = r, center = Offset(cx, cy))

        // Soft Cream Muzzle / Face Patch
        drawOval(
            color = Color(0xFFFFFDF9),
            topLeft = Offset(cx - r * 0.62f, cy - r * 0.12f),
            size = Size(r * 1.24f, r * 0.88f)
        )

        // Cute Eyes with White Catchlights
        val eyeY = cy - r * 0.18f
        val eyeX = r * 0.36f
        drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.14f, center = Offset(cx - eyeX, eyeY))
        drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.14f, center = Offset(cx + eyeX, eyeY))
        drawCircle(color = Color.White, radius = r * 0.05f, center = Offset(cx - eyeX - r * 0.03f, eyeY - r * 0.04f))
        drawCircle(color = Color.White, radius = r * 0.05f, center = Offset(cx + eyeX - r * 0.03f, eyeY - r * 0.04f))

        // Rosy Cheeks
        drawCircle(color = Color(0x66F43F5E), radius = r * 0.14f, center = Offset(cx - r * 0.52f, cy + r * 0.12f))
        drawCircle(color = Color(0x66F43F5E), radius = r * 0.14f, center = Offset(cx + r * 0.52f, cy + r * 0.12f))

        // Cute Little Button Nose & Smile
        drawOval(
            color = Color(0xFF1E1B4B),
            topLeft = Offset(cx - r * 0.13f, cy + r * 0.04f),
            size = Size(r * 0.26f, r * 0.18f)
        )
        drawArc(
            color = Color(0xFF1E1B4B),
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - r * 0.24f, cy + r * 0.14f),
            size = Size(r * 0.48f, r * 0.28f),
            style = Stroke(width = r * 0.08f, cap = StrokeCap.Round)
        )
    }
}

// 100% Original Custom Vector 3D Trophy & Achievement Medallions
@Composable
fun CustomPrizeMedallionVector(
    assetHint: String,
    modifier: Modifier = Modifier
) {
    val isStreak = assetHint.contains("daily_goal") || assetHint.contains("streak")
    val isBook = assetHint.contains("book")
    val gradientColors = when {
        isStreak -> listOf(Color(0xFFFF9F43), Color(0xFFF43F5E))
        isBook -> listOf(Color(0xFF10B981), Color(0xFF0EA5E9))
        else -> listOf(Color(0xFF7C66FF), Color(0xFF5E4AE3))
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        val r = minOf(w, h) * 0.44f

        // Outer metallic gold ring
        drawCircle(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFE57F), Color(0xFFFFB800), Color(0xFFD97706))),
            radius = r,
            center = Offset(cx, cy)
        )
        // Inner jewel medallion core
        drawCircle(
            brush = Brush.linearGradient(gradientColors),
            radius = r * 0.82f,
            center = Offset(cx, cy)
        )
        // Glossy top crescent highlight
        drawArc(
            color = Color.White.copy(alpha = 0.25f),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - r * 0.72f, cy - r * 0.72f),
            size = Size(r * 1.44f, r * 1.44f),
            style = Stroke(width = r * 0.12f, cap = StrokeCap.Round)
        )

        if (isStreak) {
            // Vector Flame Crest inside medallion
            val flame = Path().apply {
                moveTo(cx, cy - r * 0.48f)
                quadraticTo(cx + r * 0.42f, cy - r * 0.05f, cx + r * 0.28f, cy + r * 0.35f)
                quadraticTo(cx, cy + r * 0.52f, cx - r * 0.28f, cy + r * 0.35f)
                quadraticTo(cx - r * 0.42f, cy - r * 0.05f, cx, cy - r * 0.48f)
                close()
            }
            drawPath(flame, color = Color(0xFFFFE066))
            drawCircle(color = Color.White, radius = r * 0.16f, center = Offset(cx, cy + r * 0.16f))
        } else if (isBook) {
            // Vector Open Storybook Emblem inside medallion
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cx - r * 0.44f, cy - r * 0.30f),
                size = Size(r * 0.41f, r * 0.56f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = Color(0xFFE0F2FE),
                topLeft = Offset(cx + r * 0.03f, cy - r * 0.30f),
                size = Size(r * 0.41f, r * 0.56f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        } else {
            // Vector 5-Pointed Golden Star inside medallion
            val starPath = Path()
            val outerR = r * 0.48f
            val innerR = outerR * 0.46f
            for (i in 0 until 10) {
                val angle = -PI / 2.0 + i * (PI / 5.0)
                val rad = if (i % 2 == 0) outerR else innerR
                val x = cx + (rad * cos(angle)).toFloat()
                val y = cy + (rad * sin(angle)).toFloat()
                if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
            }
            starPath.close()
            drawPath(starPath, color = Color(0xFFFFE066))
        }
    }
}

// Custom Magic Star Reading Pointer (Replaces Google's hand_pointer.svg)
@Composable
fun CustomMagicPointerIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        // Upward glowing pointer triangle + golden star tip
        val tri = Path().apply {
            moveTo(cx, h * 0.08f)
            lineTo(cx + w * 0.34f, h * 0.65f)
            lineTo(cx - w * 0.34f, h * 0.65f)
            close()
        }
        drawPath(tri, color = SeekhGreen500)
        drawCircle(color = SeekhYellow500, radius = minOf(w, h) * 0.22f, center = Offset(cx, h * 0.72f))
    }
}

// Custom Scholar Owl Mascot Illustration for Daily Goal Card (Replaces dog_studying.svg)
@Composable
fun CustomScholarOwlIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(SeekhGreenSoft, SeekhBlueSoft)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        NoorMascotVectorAvatar(
            mood = BuddyMood.IDLE,
            modifier = Modifier.fillMaxSize(0.86f)
        )
    }
}

// Custom Onboarding Hero Illustrations (Replaces oobe_ra_intro_1..3.svg)
@Composable
fun CustomOnboardingHeroIllustration(step: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    when (step) {
                        0 -> listOf(Color(0xFFF3F0FF), Color(0xFFE0F2FE))
                        1 -> listOf(Color(0xFFFEF3C7), Color(0xFFFCE7F3))
                        else -> listOf(Color(0xFFECFDF5), Color(0xFFEEF2FF))
                    }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            NoorMascotVectorAvatar(
                mood = when (step) {
                    0 -> BuddyMood.SPEAKING
                    1 -> BuddyMood.IDLE
                    else -> BuddyMood.LISTENING
                },
                modifier = Modifier.size(135.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GoldenStarIcon(size = 44.dp)
                CustomPrizeMedallionVector(
                    assetHint = if (step == 1) "prize_star" else "prize_book",
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@Composable
fun CustomLanguageGlobeBadge(assetHint: String, modifier: Modifier = Modifier) {
    val label = when {
        assetHint.contains("urdu") -> "اردو"
        assetHint.contains("hindi") -> "हिं"
        assetHint.contains("spanish") -> "ES"
        assetHint.contains("arabic") -> "ع"
        assetHint.contains("portuguese") -> "PT"
        else -> "EN"
    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(SeekhGreen500, SeekhBlue500))
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White
        )
    }
}

@Composable
fun CustomAiSparkleBadge(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = "AI Story Studio",
        tint = Color.White,
        modifier = modifier
    )
}

@Composable
fun CustomSearchLensIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(SeekhGreenSoft),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = "No stories found",
            tint = SeekhGreen500,
            modifier = Modifier.fillMaxSize(0.55f)
        )
    }
}

// Top App Bar Star Counter Pill (e.g., "5212 ⭐")
@Composable
fun StarCounterPill(
    stars: Int,
    backgroundColor: Color = Color(0x2EFFFFFF),
    textColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    var prevStars by remember { mutableIntStateOf(stars) }
    val pulseScale = remember { Animatable(1f) }
    LaunchedEffect(stars) {
        if (stars != prevStars) {
            prevStars = stars
            pulseScale.animateTo(1.18f, tween(140))
            pulseScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Surface(
        modifier = Modifier
            .scale(pulseScale.value)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GoldenStarIcon(size = 20.dp)
            Text(
                text = stars.toString(),
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = textColor
            )
        }
    }
}

// Circular Learner Profile Avatar (Renders 100% Original Custom Vector Learner Portrait!)
@Composable
fun ProfileAvatarCircle(
    avatarIndex: Int,
    size: Dp = 44.dp,
    borderWidth: Dp = 2.dp,
    borderColor: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(3.dp, CircleShape)
            .clip(CircleShape)
            .border(borderWidth, borderColor, CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        CustomLearnerVectorAvatar(
            avatarIndex = avatarIndex,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Main Top Bar used on Library, Prizes, and Activity tabs (Exact reference layout: Menu on left, Search + Star Pill + Avatar on right)
@Composable
fun SeekhMainTopBar(
    stars: Int,
    avatarIndex: Int,
    isOfflineReady: Boolean = true,
    onMenuClick: () -> Unit,
    onStarClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: (() -> Unit)? = null,
    onOfflineBadgeClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .size(42.dp)
                .clickable { onMenuClick() },
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (onSearchClick != null) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onSearchClick() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search stories",
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }
            StarCounterPill(
                stars = stars,
                backgroundColor = Color.White.copy(alpha = 0.20f),
                textColor = Color.White,
                onClick = onStarClick
            )
            ProfileAvatarCircle(
                avatarIndex = avatarIndex,
                size = 44.dp,
                borderWidth = 2.dp,
                borderColor = Color.White,
                onClick = onProfileClick
            )
        }
    }
}

// Clean Soft Bento Story Book Card (Zero 3D thumbnail effects, pure unobstructed flat-crisp illustration + clean pastel level & page metadata row below thumbnail!)
@Composable
fun StoryBookCard(
    book: StoryBook,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedLanguageCode: String = "en_ur",
    progressFraction: Float = 0f,
    needsDownload: Boolean = false,
    isDownloading: Boolean = false
) {
    val animatedProg by animateFloatAsState(
        targetValue = progressFraction.coerceIn(0f, 1f),
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "book_prog"
    )

    val cardShape = RoundedCornerShape(20.dp)
    val (levelBgColor, levelTextColor) = when (book.level) {
        1 -> Color(0xFFECFDF5) to Color(0xFF059669)
        2 -> Color(0xFFEFF6FF) to Color(0xFF0284C7)
        3 -> SeekhGreenSoft to SeekhGreen600
        else -> Color(0xFFFFFBEB) to Color(0xFFD97706)
    }

    Surface(
        modifier = modifier
            .softCardShadow(
                elevation = 7.dp,
                shape = cardShape,
                spotColor = if (isCompleted) Color(0x3310B981) else Color(0x2B3B28B0),
                ambientColor = Color(0x181E1B4B)
            )
            .bouncyClick(scaleDown = 0.96f, onClick = onClick),
        shape = cardShape,
        color = Color.White,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (isCompleted) SeekhGreenMint.copy(alpha = 0.45f) else Color(0xFFEAEEF8)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Pure Flat-Clean Thumbnail Box (Zero 3D spine, zero dark scrim, zero inner shadow!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.96f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SeekhSurfaceGray)
            ) {
                AssetWebpImage(
                    assetPath = book.coverAssetPath,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Cloud Download Badge or Downloading Spinner at top-left
                if (needsDownload || isDownloading) {
                    Surface(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(26.dp)
                            .align(Alignment.TopStart),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(15.dp),
                                    color = SeekhGreen500,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = "Download Story",
                                    tint = SeekhGreen500,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // Mint Emerald Circular Checkmark Badge at top-right if completed
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(22.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(SeekhGreenMint)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            if (progressFraction > 0f && !isCompleted) {
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { animatedProg.coerceIn(0.08f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)),
                    color = SeekhGreen500,
                    trackColor = SeekhGreen100
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Clean Soft Pastel Level Pill & Page Count Row right below thumbnail (keeps thumbnail 100% clean!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = levelBgColor
                ) {
                    Text(
                        text = "Lvl ${book.level}",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        color = levelTextColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${book.pages.size}p",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    color = SeekhTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = book.title,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp,
                lineHeight = 16.sp,
                color = SeekhTextPrimary,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            val nativeSub = when {
                selectedLanguageCode.contains("ur") -> book.nativeTitleUrdu
                selectedLanguageCode.contains("hi") -> book.nativeTitleHindi
                else -> ""
            }
            if (nativeSub.isNotBlank()) {
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = nativeSub,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.5.sp,
                    lineHeight = 13.5.sp,
                    color = SeekhGreen600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

// Parabolic Flying Stars Overlay when stars are earned
@Composable
fun FlyingStarsOverlay(
    triggerCount: Int,
    modifier: Modifier = Modifier
) {
    var activeBurst by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            activeBurst = true
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
            activeBurst = false
        }
    }

    if (activeBurst) {
        val p = progress.value
        val starOffsets = remember {
            listOf(
                Offset(0.25f, 0.65f),
                Offset(0.42f, 0.70f),
                Offset(0.55f, 0.62f),
                Offset(0.68f, 0.68f),
                Offset(0.35f, 0.58f)
            )
        }
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val w = maxWidth
            val h = maxHeight
            val targetX = w * 0.72f
            val targetY = 36.dp
            starOffsets.forEachIndexed { idx, start ->
                val delayFrac = idx * 0.07f
                val localP = ((p - delayFrac) / (1f - delayFrac)).coerceIn(0f, 1f)
                if (localP > 0f && localP < 1f) {
                    val curX = (w * start.x) + (targetX - (w * start.x)) * localP
                    val arcLift = kotlin.math.sin(localP * Math.PI).toFloat() * 65f
                    val curY = (h * start.y) + (targetY - (h * start.y)) * localP - arcLift.dp
                    val scale = 0.6f + kotlin.math.sin(localP * Math.PI).toFloat() * 0.7f
                    GoldenStarIcon(
                        size = 32.dp,
                        modifier = Modifier
                            .offset(x = curX, y = curY)
                            .scale(scale)
                    )
                }
            }
        }
    }
}

// Featured Hero Story Card at the top of the Library (Clean flat-crisp cover illustration without dark scrims + soft card shadow + "Read ▶" CTA!)
@Composable
fun FeaturedStoryHeroCard(
    book: StoryBook,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heroShape = RoundedCornerShape(26.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .softCardShadow(
                elevation = 12.dp,
                shape = heroShape,
                spotColor = Color(0x363B28B0),
                ambientColor = Color(0x1E1E1B4B)
            )
            .bouncyClick(scaleDown = 0.97f, onClick = onClick),
        shape = heroShape,
        color = Color.White,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFE8ECF7))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Pure Bright Cover Illustration (No dark scrims or 3D inner shadows!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                AssetWebpImage(
                    assetPath = book.coverAssetPath,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(28.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(SeekhGreenMint)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SeekhGreenSoft
                        ) {
                            Text(
                                text = "Level ${book.level}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp,
                                color = SeekhGreen600,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "• ${book.pages.size} Pages • ${book.categoryTag}",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = SeekhTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = book.title,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = SeekhTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (book.nativeTitleUrdu.isNotBlank()) {
                        Text(
                            text = book.nativeTitleUrdu,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = SeekhGreen600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SeekhGreen500
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Read",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Read story",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Friendly Animated AI Companion "Noor" (Compact Non-Overlapping Floating Orb + Speech Pill)
enum class BuddyMood {
    IDLE, SPEAKING, LISTENING, THINKING
}

@Composable
fun FloatingReadingBuddy(
    mood: BuddyMood,
    speechBubbleText: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 76.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buddy_bob")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    val actualSize = size.coerceAtMost(84.dp)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (!speechBubbleText.isNullOrBlank()) {
            val bubbleShape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
            Surface(
                shape = bubbleShape,
                color = Color.White,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .widthIn(max = 215.dp)
                    .softCardShadow(elevation = 10.dp, shape = bubbleShape)
                    .border(1.dp, SeekhGreen100, bubbleShape)
                    .clickable { onClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (mood == BuddyMood.LISTENING) Icons.Default.Mic else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = SeekhGreen500,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = speechBubbleText,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = SeekhTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .size(actualSize)
                .offset(y = bobOffset.dp)
                .softCardShadow(
                    elevation = 14.dp,
                    shape = CircleShape,
                    spotColor = Color(0x4D3B28B0)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 0.dp,
            border = BorderStroke(
                2.dp,
                when (mood) {
                    BuddyMood.LISTENING -> SeekhGreenMint
                    BuddyMood.SPEAKING -> SeekhYellow500
                    else -> SeekhGreen100
                }
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                NoorMascotVectorAvatar(
                    mood = mood,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// Redesigned Floating Soft Glass Pill Bottom Navigation Bar (100% Original Vector Icons & Expanding Pill Tabs)
enum class MainTab {
    LIBRARY, PRIZES, ACTIVITY
}

@Composable
fun SeekhBottomNavigationBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    val targetTabIndex = when (selectedTab) {
        MainTab.LIBRARY -> 0f
        MainTab.PRIZES -> 1f
        MainTab.ACTIVITY -> 2f
    }
    // Physics-driven spring sliding position (glides smoothly from button to button with gentle bounce)
    val animatedTabIndex by animateFloatAsState(
        targetValue = targetTabIndex,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bottom_nav_sliding_index"
    )
    // Elastic liquid stretch while the indicator pill is traveling between buttons
    val travelVelocity = kotlin.math.abs(targetTabIndex - animatedTabIndex).coerceIn(0f, 1.5f)
    val pillScaleX = 1f + travelVelocity * 0.18f
    val pillScaleY = 1f - travelVelocity * 0.06f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        val navBarShape = RoundedCornerShape(30.dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .softCardShadow(
                    elevation = 20.dp,
                    shape = navBarShape,
                    spotColor = Color(0x4D3B28B0),
                    ambientColor = Color(0x2E1E1B4B)
                ),
            shape = navBarShape,
            color = Color.White,
            border = BorderStroke(1.2.dp, Color(0xFFE2E6F7))
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                val slotWidth = maxWidth / 3f
                val slidingOffsetX = slotWidth * animatedTabIndex

                // Physically Gliding Liquid Pill Indicator Layer behind the 3 tab buttons!
                Box(
                    modifier = Modifier
                        .offset(x = slidingOffsetX)
                        .width(slotWidth)
                        .height(48.dp)
                        .padding(horizontal = 3.dp)
                        .graphicsLayer {
                            scaleX = pillScaleX
                            scaleY = pillScaleY
                        }
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SeekhGreenSoft,
                                    Color(0xFFE0F2FE).copy(alpha = 0.85f),
                                    SeekhGreenSoft
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    SeekhGreen500.copy(alpha = 0.42f),
                                    SeekhBlue500.copy(alpha = 0.35f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Glowing active indicator bar inside the sliding pill
                    Box(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .width(22.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SeekhGreen500)
                    )
                }

                // Foreground Row of Interactive Tab Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavTabItem(
                        label = "Library",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        isSelected = selectedTab == MainTab.LIBRARY,
                        onClick = { onTabSelected(MainTab.LIBRARY) },
                        modifier = Modifier.weight(1f)
                    )
                    BottomNavTabItem(
                        label = "Prizes",
                        icon = Icons.Default.EmojiEvents,
                        isSelected = selectedTab == MainTab.PRIZES,
                        onClick = { onTabSelected(MainTab.PRIZES) },
                        modifier = Modifier.weight(1f)
                    )
                    BottomNavTabItem(
                        label = "Activity",
                        icon = Icons.Default.Insights,
                        isSelected = selectedTab == MainTab.ACTIVITY,
                        onClick = { onTabSelected(MainTab.ACTIVITY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavTabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) SeekhGreen500 else SeekhTabUnselected,
        animationSpec = tween(260, easing = FastOutSlowInEasing),
        label = "tab_fg"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.18f else 0.98f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tab_icon_scale"
    )
    val liftOffsetY by animateFloatAsState(
        targetValue = if (isSelected) -2.2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tab_lift_y"
    )
    val wiggleAngle = remember { Animatable(0f) }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            wiggleAngle.animateTo(-11f, tween(90))
            wiggleAngle.animateTo(9f, tween(110))
            wiggleAngle.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        } else {
            wiggleAngle.snapTo(0f)
        }
    }

    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .bouncyClick(scaleDown = 0.91f, onClick = onClick)
            .offset(y = liftOffsetY.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier
                .size(21.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                    rotationZ = wiggleAngle.value
                }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            fontSize = if (isSelected) 13.5.sp else 13.sp,
            color = contentColor,
            maxLines = 1
        )
    }
}
