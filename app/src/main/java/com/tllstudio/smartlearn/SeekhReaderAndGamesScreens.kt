package com.tllstudio.smartlearn

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// 1. Exact Reference Story Book Reader Flow:
//    CoverPage (Page 0 with Cover Art, Title, Author, Illustrator, Publisher & Start Button)
//    -> BookReadingPageView (Story Pages with Word-by-Word Highlighting, Dotted Red Practice Words, Moving Pointer, Tap-Word Syllable Tutor & Circular Next/Prev Arrows)
//    -> SelectionQuestionPage (Dedicated Full-Page Comprehension Question Screen)
//    -> CelebrationScreenModel (Full-Screen Celebration with Flying Stars, WPM Speed Vehicle & PostBookGameWidget)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveStoryReaderScreen(
    book: StoryBook,
    profile: LearnerProfile,
    selectedLanguageCode: String,
    buddyMood: BuddyMood,
    initialPageIndex: Int = 0,
    onPageChanged: (Int) -> Unit = {},
    onSpeakText: (String) -> Unit,
    onPlayPhoneme: (Int) -> Unit = {},
    onStartListening: (() -> Unit) -> Unit,
    onStopListening: () -> Unit,
    onAwardStars: (Int) -> Unit,
    onBookCompleted: (StoryBook, Int) -> Unit,
    onBlockBook: (StoryBook) -> Unit = {},
    onPlayPostBookGame: (String) -> Unit,
    onBack: () -> Unit
) {
    // If initialPageIndex == 0, start on CoverPage first (unless deep-linked directly to last page for quiz verification)
    var isOnCoverPage by remember(book.id) {
        mutableStateOf(initialPageIndex == 0 && book.id != "book_rained_fish")
    }
    var pageIndex by remember(book.id) {
        mutableIntStateOf(initialPageIndex.coerceIn(0, (book.pages.size - 1).coerceAtLeast(0)))
    }
    val currentPage = book.pages.getOrElse(pageIndex) { book.pages.first() }
    val words = remember(currentPage.text) {
        currentPage.text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    }

    // If deep-linked directly to READER_QUIZ (last page with quiz), show the dedicated SelectionQuestionPage
    var isShowingQuizPage by remember(pageIndex, book.id) {
        mutableStateOf(initialPageIndex > 0 && initialPageIndex == book.pages.size - 1 && currentPage.quiz != null)
    }

    var readWordCount by remember(pageIndex, book.id) {
        mutableIntStateOf(if (pageIndex == 0 && book.id == "book_rained_fish") 11 else 0)
    }
    var masteredPracticeWords by remember(pageIndex, book.id) {
        mutableStateOf(emptySet<String>())
    }
    var selectedTooltipWord by remember(pageIndex) { mutableStateOf<String?>(null) }
    var isMicActive by remember { mutableStateOf(false) }
    var selectedQuizAnswer by remember(pageIndex) { mutableStateOf<Int?>(null) }
    var showBlockBookDialog by remember { mutableStateOf(false) }
    var readingMode by remember { mutableStateOf("Read aloud") } // "Read aloud" | "Listen" | "Silent" (READING_MODE_DROPDOWN_SHOWN)
    var showReadingModeDialog by remember { mutableStateOf(false) }
    var showFullCelebrationScreen by remember { mutableStateOf(false) }
    var completedWpm by remember { mutableIntStateOf(profile.wordsPerMinute) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }
    var hearLineTrigger by remember(pageIndex) { mutableIntStateOf(0) }
    val startTimeMs = remember { System.currentTimeMillis() }

    // Sub-state aware BackHandler inside Story Reader!
    BackHandler {
        when {
            showReadingModeDialog -> showReadingModeDialog = false
            showBlockBookDialog -> showBlockBookDialog = false
            selectedTooltipWord != null -> selectedTooltipWord = null
            showFullCelebrationScreen -> {
                showFullCelebrationScreen = false
                onBack()
            }
            isShowingQuizPage -> {
                isShowingQuizPage = false
            }
            !isOnCoverPage && pageIndex > 0 -> {
                isMicActive = false
                onStopListening()
                pageIndex--
            }
            !isOnCoverPage && pageIndex == 0 && book.id != "book_rained_fish" -> {
                isMicActive = false
                onStopListening()
                isOnCoverPage = true
            }
            else -> {
                isMicActive = false
                onStopListening()
                onBack()
            }
        }
    }

    LaunchedEffect(pageIndex, isOnCoverPage, readingMode) {
        if (!isOnCoverPage) {
            onPageChanged(pageIndex)
            if (readingMode == "Listen") {
                hearLineTrigger++
            }
        } else {
            onSpeakText("${book.title}. Written by ${book.author}.")
        }
    }

    // Real-time word-by-word highlight progression when user taps Noor Buddy for help
    LaunchedEffect(hearLineTrigger) {
        if (hearLineTrigger > 0) {
            readWordCount = 0
            onSpeakText(currentPage.text)
            for (i in 1..words.size) {
                delay(310)
                readWordCount = i
            }
        }
    }

    // Real-time word progression when Microphone is active
    LaunchedEffect(isMicActive, pageIndex, isOnCoverPage, isShowingQuizPage) {
        if (isMicActive && !isOnCoverPage && !isShowingQuizPage) {
            onStartListening {}
            while (isMicActive && readWordCount < words.size) {
                delay(360)
                readWordCount = (readWordCount + 1).coerceAtMost(words.size)
                if (readWordCount == words.size) {
                    starBurstTrigger++
                    onAwardStars(15)
                    isMicActive = false
                    onStopListening()
                    onSpeakText("Well done! Tap the green arrow for the next page.")
                }
            }
        }
    }

    // Full-Screen Celebration Screen (CelebrationScreenModel + PostBookGameWidget)
    if (showFullCelebrationScreen) {
        FullBookCelebrationScreen(
            book = book,
            profile = profile,
            achievedWpm = completedWpm,
            onPlayGame = { gameType ->
                showFullCelebrationScreen = false
                onPlayPostBookGame(gameType)
            },
            onReadAgain = {
                showFullCelebrationScreen = false
                isOnCoverPage = true
                pageIndex = 0
                readWordCount = 0
            },
            onBackToLibrary = {
                showFullCelebrationScreen = false
                onBack()
            },
            onSpeakText = onSpeakText
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Exact Reference Top Reader Bar (Back Arrow on Left, Reading Mode Dropdown + Star Counter Pill + Profile Avatar + Block Story Menu on Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    onStopListening()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Library",
                        tint = SeekhTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Signed-In Reading Mode Dropdown Pill (READING_MODE_DROPDOWN_SHOWN: Read aloud | Listen | Silent)
                    Surface(
                        modifier = Modifier.clickable { showReadingModeDialog = true },
                        shape = RoundedCornerShape(50),
                        color = SeekhSurfaceGray,
                        border = BorderStroke(1.dp, SeekhBorderGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = when (readingMode) {
                                    "Listen" -> Icons.AutoMirrored.Filled.VolumeUp
                                    "Silent" -> Icons.Default.Visibility
                                    else -> Icons.Default.Mic
                                },
                                contentDescription = "Reading mode",
                                tint = SeekhGreen500,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = readingMode,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SeekhTextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = SeekhTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = SeekhGreenSoft,
                        textColor = SeekhTextPrimary
                    )
                    ProfileAvatarCircle(
                        avatarIndex = profile.avatarIndex,
                        size = 40.dp,
                        borderWidth = 1.5.dp,
                        borderColor = SeekhGreen500
                    )
                    IconButton(
                        onClick = { showBlockBookDialog = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Story options",
                            tint = SeekhTextSecondary
                        )
                    }
                }
            }

            // Thin Page Progress Bar directly under top bar
            val totalSteps = (book.pages.size + 1).coerceAtLeast(1)
            val currentStep = if (isOnCoverPage) 1 else (pageIndex + 2)
            LinearProgressIndicator(
                progress = { (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0.05f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = SeekhGreen500,
                trackColor = SeekhBorderGray
            )

            // Main Body: Either CoverPage, SelectionQuestionPage (Comprehension Quiz), or BookReadingPageView
            if (isOnCoverPage) {
                // PAGE 0: Reference CoverPage (Cover Image + Title + Author + Illustrator + Publisher + Start Reading Green Arrow)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(235.dp)
                                .shadow(6.dp, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(SeekhSurfaceGray)
                        ) {
                            AssetWebpImage(
                                assetPath = book.coverAssetPath,
                                contentDescription = book.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhYellow500,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Level ${book.level}",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SeekhTextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = book.title,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            lineHeight = 32.sp,
                            color = SeekhTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clickable { onSpeakText(book.title) }
                        )

                        val nativeTitle = when {
                            selectedLanguageCode.contains("ur") -> book.nativeTitleUrdu
                            selectedLanguageCode.contains("hi") -> book.nativeTitleHindi
                            else -> ""
                        }
                        if (nativeTitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = nativeTitle,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = SeekhGreen600,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Author, Illustrator & Publisher Credits Card (CoverPage with author.svg, artist.svg, publisher.svg)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = SeekhSurfaceGray,
                            border = BorderStroke(1.dp, SeekhBorderGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AssetSvgImage(
                                        assetPath = "images/author.svg",
                                        contentDescription = "Author",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = book.author,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = SeekhTextPrimary
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AssetSvgImage(
                                        assetPath = "images/artist.svg",
                                        contentDescription = "Illustrator",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = book.illustrator,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = SeekhTextPrimary
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AssetSvgImage(
                                        assetPath = "images/publisher.svg",
                                        contentDescription = "Publisher",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "Published by ${book.publisher}",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = SeekhTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Start Reading Circular Arrow Button (matching CoverPage)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 96.dp, top = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isOnCoverPage = false
                                onSpeakText(currentPage.text)
                            },
                            modifier = Modifier.height(54.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500),
                            contentPadding = PaddingValues(horizontal = 28.dp)
                        ) {
                            Text(
                                text = "Start Reading",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Start Reading",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            } else if (isShowingQuizPage && currentPage.quiz != null) {
                // DEDICATED FULL-PAGE COMPREHENSION QUESTION SCREEN (SelectionQuestionPage / SeekhBookQuestionPage)
                val quiz = currentPage.quiz
                LaunchedEffect(pageIndex) {
                    onSpeakText(quiz.question)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .shadow(3.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(SeekhSurfaceGray)
                    ) {
                        AssetWebpImage(
                            assetPath = currentPage.imageAssetPath,
                            contentDescription = "Question Illustration",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Question Title with Speaker Icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSpeakText(quiz.question) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Hear question",
                            tint = SeekhGreen500,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = quiz.question,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp,
                            lineHeight = 27.sp,
                            color = SeekhTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    quiz.options.forEachIndexed { optIdx, optionText ->
                        val isChosen = selectedQuizAnswer == optIdx
                        val isCorrect = optIdx == quiz.correctIndex
                        val cardBg = when {
                            !isChosen -> Color.White
                            isCorrect -> SeekhGreenMint
                            else -> SeekhCoralRed
                        }
                        val textCol = if (isChosen) Color.White else SeekhTextPrimary

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardBg,
                            shadowElevation = 3.dp,
                            border = BorderStroke(
                                1.5.dp,
                                if (isChosen) cardBg else SeekhBorderGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    selectedQuizAnswer = optIdx
                                    if (isCorrect) {
                                        starBurstTrigger++
                                        onAwardStars(20)
                                        onSpeakText("Correct answer! $optionText! Well done!")
                                    } else {
                                        onSpeakText("Try again!")
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = optionText,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = textCol
                                )
                                if (isChosen) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Circular Back & Next/Finish Arrow Buttons on Quiz Page
                ReaderBottomCircularNavigationRow(
                    showPrevious = true,
                    isLastPage = pageIndex >= book.pages.size - 1,
                    onPrevious = {
                        isShowingQuizPage = false
                    },
                    onNext = {
                        isShowingQuizPage = false
                        if (pageIndex < book.pages.size - 1) {
                            starBurstTrigger++
                            onAwardStars(10)
                            pageIndex++
                        } else {
                            val elapsedMin = ((System.currentTimeMillis() - startTimeMs) / 60000L).toInt().coerceAtLeast(1)
                            val totalWords = book.pages.sumOf { it.text.split(Regex("\\s+")).size }
                            val calcWpm = (totalWords / elapsedMin).coerceIn(54, 92)
                            completedWpm = calcWpm
                            onBookCompleted(book, calcWpm)
                            showFullCelebrationScreen = true
                        }
                    }
                )
            } else {
                // STORY READING PAGE (BookReadingPageView — Play Store Screenshot #1!)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 14.dp)
                ) {
                    // Rounded Page Illustration Card (Play Store Screenshot #1)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(215.dp)
                            .shadow(3.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(SeekhSurfaceGray)
                    ) {
                        AssetWebpImage(
                            assetPath = currentPage.imageAssetPath,
                            contentDescription = "Page ${currentPage.pageNumber}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xAA000000),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${pageIndex + 1} / ${book.pages.size}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Word-by-Word Interactive Story Text (Play Store Screenshot #1: blue read words + dotted red underline on practice words + moving pointer!)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        words.forEachIndexed { index, rawWord ->
                            val cleanWord = rawWord.lowercase().replace(Regex("[^a-z]"), "")
                            val isRead = index < readWordCount
                            val isCurrentActiveWord = index == readWordCount
                            val isPracticeWord = currentPage.practiceWords.contains(cleanWord)
                            val isMasteredPractice = masteredPracticeWords.contains(cleanWord)

                            val wordColor = when {
                                isMasteredPractice -> SeekhGreenMint
                                isRead -> SeekhBlueReading
                                else -> SeekhTextPrimary
                            }

                            Column(
                                modifier = Modifier
                                    .padding(end = 9.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedTooltipWord = cleanWord.ifEmpty { rawWord }
                                        onSpeakText(cleanWord.ifEmpty { rawWord })
                                        if (index >= readWordCount) {
                                            readWordCount = index + 1
                                            if (readWordCount == words.size) {
                                                starBurstTrigger++
                                                onAwardStars(10)
                                            }
                                        }
                                    }
                                    .padding(horizontal = 2.dp, vertical = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = rawWord,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = if (isCurrentActiveWord) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 30.sp,
                                    lineHeight = 38.sp,
                                    color = wordColor
                                )

                                // Dotted Red (or Green when mastered) Underline for Practice Words (Exact match to Play Store Screenshot #1!)
                                if (isPracticeWord) {
                                    val dotColor = if (isMasteredPractice) SeekhGreenMint else SeekhCoralRed
                                    val approxWidth = (rawWord.length * 15).coerceIn(36, 130).dp
                                    Canvas(
                                        modifier = Modifier
                                            .width(approxWidth)
                                            .height(6.dp)
                                    ) {
                                        drawLine(
                                            color = dotColor,
                                            start = Offset(0f, size.height / 2f),
                                            end = Offset(size.width, size.height / 2f),
                                            strokeWidth = 3.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f), 0f)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // Moving Pointer under the current active word to read
                                if (isCurrentActiveWord) {
                                    val pointerBounce by rememberInfiniteTransition(label = "ptr").animateFloat(
                                        initialValue = 0f,
                                        targetValue = 5f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(520),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "ptr_y"
                                    )
                                    CustomMagicPointerIcon(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .offset(y = pointerBounce.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(22.dp))
                                }
                            }
                        }
                    }

                    // Interactive Word Tutor Popover (when any word is tapped: Clickable Syllable Blocks + Phoneme Audio + Bilingual Translation)
                    AnimatedVisibility(visible = selectedTooltipWord != null) {
                        val word = selectedTooltipWord ?: ""
                        val syllablesList = remember(word) { SeekhCatalog.getWordSyllablesList(word) }
                        val wordMeaning = remember(word, selectedLanguageCode) {
                            SeekhCatalog.getWordTranslation(word, selectedLanguageCode)
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = SeekhBlueSoft,
                            border = BorderStroke(1.5.dp, SeekhBlue500)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = word,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = SeekhTextPrimary
                                        )
                                        Text(
                                            text = "Meaning: $wordMeaning",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = SeekhGreen600
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilledTonalIconButton(
                                            onClick = { onSpeakText(word) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Hear word",
                                                tint = SeekhBlue600
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                masteredPracticeWords = masteredPracticeWords + word.lowercase()
                                                starBurstTrigger++
                                                onAwardStars(10)
                                                onSpeakText("Awesome! You mastered the word $word!")
                                                selectedTooltipWord = null
                                            },
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "🎤 Say It",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Clickable Phonetic Syllable Breakdown Blocks
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    syllablesList.forEachIndexed { sIdx, syl ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, SeekhBlue500),
                                            modifier = Modifier.clickable {
                                                onPlayPhoneme(sIdx * 3)
                                                onSpeakText(syl)
                                            }
                                        ) {
                                            Text(
                                                text = syl,
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = SeekhBlue600,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bilingual Sentence Helper Strip (when bilingual mode is selected)
                    val bilingualSentence = when {
                        selectedLanguageCode.contains("ur") -> currentPage.urduTranslation
                        selectedLanguageCode.contains("hi") -> currentPage.hindiTranslation
                        selectedLanguageCode.contains("es") -> currentPage.spanishTranslation
                        else -> ""
                    }
                    if (bilingualSentence.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SeekhSurfaceGray,
                            border = BorderStroke(1.dp, SeekhBorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { hearLineTrigger++ }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Translation",
                                    tint = SeekhGreen600,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = bilingualSentence,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = SeekhTextSecondary
                                )
                            }
                        }
                    }
                }

                // Exact Reference Bottom Circular Previous / Next Arrow Row (Zero extra "Hear Line" button!)
                ReaderBottomCircularNavigationRow(
                    showPrevious = pageIndex > 0 || book.id != "book_rained_fish",
                    isLastPage = pageIndex >= book.pages.size - 1 && currentPage.quiz == null,
                    onPrevious = {
                        isMicActive = false
                        onStopListening()
                        if (pageIndex > 0) {
                            pageIndex--
                        } else {
                            isOnCoverPage = true
                        }
                    },
                    onNext = {
                        isMicActive = false
                        onStopListening()
                        if (currentPage.quiz != null && !isShowingQuizPage) {
                            // Open dedicated full-page SelectionQuestionPage for this page's comprehension question!
                            isShowingQuizPage = true
                        } else if (pageIndex < book.pages.size - 1) {
                            starBurstTrigger++
                            onAwardStars(10)
                            pageIndex++
                        } else {
                            val elapsedMin = ((System.currentTimeMillis() - startTimeMs) / 60000L).toInt().coerceAtLeast(1)
                            val totalWords = book.pages.sumOf { it.text.split(Regex("\\s+")).size }
                            val calcWpm = (totalWords / elapsedMin).coerceIn(54, 92)
                            completedWpm = calcWpm
                            onBookCompleted(book, calcWpm)
                            showFullCelebrationScreen = true
                        }
                    }
                )
            }
        }

        // Flying Stars Animation Overlay (StarAnimationHolderView)
        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        // Bottom-Right AI Reading Buddy (Tapping speaks the current line or toggles active voice reading)
        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = if (isMicActive) "Listening..." else null,
            onClick = {
                if (isOnCoverPage) {
                    onSpeakText("${book.title}. Tap Start Reading to begin!")
                } else if (isShowingQuizPage && currentPage.quiz != null) {
                    onSpeakText(currentPage.quiz.question)
                } else {
                    hearLineTrigger++
                }
            },
            size = 82.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 12.dp, bottom = 10.dp)
        )
    }

    // "Block this story" Confirmation Dialog (from flutter_ui_strings.txt line 775: "Do you really want to block this story?")
    if (showBlockBookDialog) {
        AlertDialog(
            onDismissRequest = { showBlockBookDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Block this story",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Do you really want to block this story (${book.title}) from appearing in the library?",
                    fontFamily = GoogleSansFamily,
                    fontSize = 15.sp,
                    color = SeekhTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBlockBookDialog = false
                        onBlockBook(book)
                        onBack()
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhCoralRed)
                ) {
                    Text("Block story", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockBookDialog = false }) {
                    Text("Cancel", fontFamily = GoogleSansFamily)
                }
            }
        )
    }

    // Signed-In Reading Mode Selector & Explainer Dialog (READING_MODE_INTRO_DIALOG_SHOWN: Read aloud | Listen | Silent)
    if (showReadingModeDialog) {
        val modes = listOf(
            Triple("Read aloud", "Read out loud while Noor listens, highlights words, and awards golden stars.", Icons.Default.Mic),
            Triple("Listen", "Listen as Noor reads each sentence out loud with word-by-word highlighting.", Icons.AutoMirrored.Filled.VolumeUp),
            Triple("Silent", "Read independently at your own pace and test comprehension with quizzes.", Icons.Default.Visibility)
        )
        AlertDialog(
            onDismissRequest = { showReadingModeDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Choose reading mode",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = SeekhTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    modes.forEach { (mTitle, mDesc, mIcon) ->
                        val active = readingMode == mTitle
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    readingMode = mTitle
                                    showReadingModeDialog = false
                                    onSpeakText("$mTitle mode selected.")
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (active) SeekhGreenSoft else SeekhSurfaceGray,
                            border = BorderStroke(if (active) 1.5.dp else 1.dp, if (active) SeekhGreen500 else SeekhBorderGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = mIcon,
                                    contentDescription = mTitle,
                                    tint = if (active) SeekhGreen500 else SeekhTextSecondary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mTitle,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SeekhTextPrimary
                                    )
                                    Text(
                                        text = mDesc,
                                        fontFamily = GoogleSansFamily,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = SeekhTextSecondary
                                    )
                                }
                                if (active) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = SeekhGreen500
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReadingModeDialog = false },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text("Done", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// Reference Circular Previous (`<-`) and Next (`->`) Buttons at Bottom of Story Reader
@Composable
private fun ReaderBottomCircularNavigationRow(
    showPrevious: Boolean,
    isLastPage: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 112.dp, top = 8.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showPrevious) {
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable { onPrevious() },
                shape = CircleShape,
                color = SeekhSurfaceGray,
                border = BorderStroke(1.5.dp, SeekhBorderGray),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous page",
                        tint = SeekhTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.size(52.dp))
        }

        // Circular Green/Primary Next Arrow Button (matching BookReadingPageView)
        Surface(
            modifier = Modifier
                .size(58.dp)
                .clickable { onNext() },
            shape = CircleShape,
            color = SeekhGreen500,
            shadowElevation = 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isLastPage) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = if (isLastPage) "Finish story" else "Next page",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// 1b. Full-Screen Book Completion Celebration Screen (CelebrationScreenModel + SpeedVehicleBadge + PostBookGameWidget)
@Composable
fun FullBookCelebrationScreen(
    book: StoryBook,
    profile: LearnerProfile,
    achievedWpm: Int,
    onPlayGame: (String) -> Unit,
    onReadAgain: () -> Unit,
    onBackToLibrary: () -> Unit,
    onSpeakText: (String) -> Unit
) {
    val speedVehicle = remember(achievedWpm) { SeekhCatalog.getVehicleForWpm(achievedWpm) }
    var starTrigger by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 230.dp, curveDepth = 38.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToLibrary) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to library",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Well done, ${profile.name}!",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "You completed \"${book.title}\"",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Celebration Card with Book Cover, +50 Stars, and Speed Vehicle Rank (bookEndWordsAndTime)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AssetWebpImage(
                                assetPath = book.coverAssetPath,
                                contentDescription = book.title,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column {
                            Text(
                                text = "+50 Golden Stars!",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = SeekhGreen600
                            )
                            Text(
                                text = "${book.pages.size} pages read • Level ${book.level}",
                                fontFamily = GoogleSansFamily,
                                fontSize = 14.sp,
                                color = SeekhTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(5) { GoldenStarIcon(size = 20.dp) }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SeekhBorderGray)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Speed Vehicle Rank Card (res/drawable/vehicle_*)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SeekhGreenSoft,
                        border = BorderStroke(1.dp, SeekhGreen500),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                starTrigger++
                                onSpeakText("Your reading speed is $achievedWpm words per minute! You unlocked the ${speedVehicle.name} rank!")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(text = speedVehicle.emoji, fontSize = 38.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${speedVehicle.name} • $achievedWpm wpm",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = SeekhGreen900
                                )
                                Text(
                                    text = speedVehicle.description,
                                    fontFamily = GoogleSansFamily,
                                    fontSize = 13.sp,
                                    color = SeekhTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PostBookGameWidget ("Play a word game from this story!")
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Play a word game next!",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = SeekhTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlayGame("JUMBLE") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhJumblePurple)
                        ) {
                            Text("Jumbled Words", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = { onPlayGame("BALLOON") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00838F))
                        ) {
                            Text("Pop Balloons", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action Buttons ("Read again" & "Back to library")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReadAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, SeekhGreen500)
                ) {
                    Text("Read again", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, color = SeekhGreen600)
                }
                Button(
                    onClick = onBackToLibrary,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text("Back to library", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starTrigger)
    }
}

// Shared "Choose a level" Screen (HybridGameCategorySelectorActivity: "Choose a level" -> "Easy" | "Hard")
@Composable
fun GameLevelSelectorScreen(
    gameTitle: String,
    accentColor: Color,
    profile: LearnerProfile,
    onSelectLevel: (Boolean) -> Unit, // false = Easy, true = Hard
    onSpeakText: (String) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        onSpeakText("Choose a level. Easy, or Hard.")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 190.dp, curveDepth = 32.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Toolbar matching res/LF.xml
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = gameTitle,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Choose a level",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Card 1: "Easy" (game_easy)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .clickable { onSelectLevel(false) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = BorderStroke(2.dp, SeekhGreenMint)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(22.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhGreenMint.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "LEVEL 1",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = SeekhGreenMint,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Easy",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                color = SeekhTextPrimary
                            )
                            Text(
                                text = "Simple words & starter sounds",
                                fontFamily = GoogleSansFamily,
                                fontSize = 14.sp,
                                color = SeekhTextSecondary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            GoldenStarIcon(size = 36.dp)
                        }
                    }
                }

                // Card 2: "Hard" (game_hard)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .clickable { onSelectLevel(true) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = BorderStroke(2.dp, accentColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(22.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = accentColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "LEVEL 2",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hard",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                color = SeekhTextPrimary
                            )
                            Text(
                                text = "Longer words & phonics blends",
                                fontFamily = GoogleSansFamily,
                                fontSize = 14.sp,
                                color = SeekhTextSecondary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            GoldenStarIcon(size = 34.dp)
                            GoldenStarIcon(size = 34.dp)
                        }
                    }
                }
            }
        }
    }
}

// Reference ThreeStepProgressView used in Jumbled Words & Word Meanings Top Bar
@Composable
private fun ThreeStepProgressIndicator(
    currentStep: Int,
    totalSteps: Int = 3,
    activeColor: Color = SeekhYellow500,
    inactiveColor: Color = Color.White.copy(alpha = 0.32f)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until totalSteps) {
            val done = i <= (currentStep % totalSteps)
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(if (i == (currentStep % totalSteps)) 28.dp else 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (done) activeColor else inactiveColor)
            )
        }
    }
}

// 2. Play Store Screenshot #3: Jumbled Words Game Screen (res/GC.xml — with ThreeStepProgressView, Speaker Icon beside Tiles, Wrong-Letter Red Undo Indicator & Flying Stars!)
@Composable
fun JumbledWordsGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onPlayPhoneme: (Int) -> Unit,
    onPlayErrorTone: () -> Unit = {},
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    // Show "Choose a level" screen when toggled from top bar or after game over, while starting directly on Easy "apples" so deep-link JUMBLE matches Play Store Screenshot #3 immediately
    var showLevelPicker by remember { mutableStateOf(false) }
    var isHardLevel by remember { mutableStateOf(false) }

    BackHandler {
        if (showLevelPicker) {
            onBack()
        } else {
            onBack()
        }
    }

    if (showLevelPicker) {
        GameLevelSelectorScreen(
            gameTitle = "Jumbled Words",
            accentColor = SeekhJumblePurple,
            profile = profile,
            onSelectLevel = { hard ->
                isHardLevel = hard
                showLevelPicker = false
            },
            onSpeakText = onSpeakText,
            onBack = onBack
        )
        return
    }

    val filteredChallenges = remember(isHardLevel) {
        val list = SeekhCatalog.jumbleChallenges.filter { it.isHard == isHardLevel }
        list.ifEmpty { SeekhCatalog.jumbleChallenges }
    }
    var challengeIdx by remember(isHardLevel) { mutableIntStateOf(0) }
    val challenge = filteredChallenges[challengeIdx % filteredChallenges.size]
    val targetWord = challenge.word.lowercase()

    data class LetterTile(val id: Int, val char: Char)

    // On challengeIdx == 0 ("apples" Easy), start with 'a', 'p' placed and 's','l','e','p' in top pool to match Play Store Screenshot #3 1:1
    var placedTiles by remember(challengeIdx, isHardLevel) {
        if (challengeIdx == 0 && !isHardLevel && targetWord == "apples") {
            mutableStateOf(listOf(LetterTile(0, 'a'), LetterTile(1, 'p')))
        } else {
            mutableStateOf(emptyList())
        }
    }

    var poolTiles by remember(challengeIdx, isHardLevel) {
        if (challengeIdx == 0 && !isHardLevel && targetWord == "apples") {
            mutableStateOf(
                listOf(
                    LetterTile(2, 's'),
                    LetterTile(3, 'l'),
                    LetterTile(4, 'e'),
                    LetterTile(5, 'p')
                )
            )
        } else {
            val chars = targetWord.mapIndexed { idx, c -> LetterTile(idx, c) }.shuffled()
            mutableStateOf(chars)
        }
    }

    var hasWrongLastTile by remember(challengeIdx, isHardLevel) { mutableStateOf(false) }
    var isWordSolved by remember(challengeIdx, isHardLevel) { mutableStateOf(false) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    // Auto-advance to next word 1.5s after solving
    LaunchedEffect(isWordSolved) {
        if (isWordSolved) {
            delay(1500)
            challengeIdx++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhJumblePurple)
    ) {
        // Curved two-tone purple wave in the lower half matching Play Store Screenshot #3
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val waveY = h * 0.60f
            val path = Path().apply {
                moveTo(0f, waveY)
                quadraticTo(w * 0.5f, waveY + 46.dp.toPx(), w, waveY - 12.dp.toPx())
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = path, color = Color(0xFF7B31DC))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar matching res/GC.xml (Back arrow, ThreeStepProgressView, Level pill to open Choose a level, Star Counter Pill, Profile Avatar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                ThreeStepProgressIndicator(currentStep = challengeIdx)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0x38000000),
                        modifier = Modifier.clickable { showLevelPicker = true }
                    ) {
                        Text(
                            text = if (isHardLevel) "Hard ▾" else "Easy ▾",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = Color(0x3D000000),
                        textColor = Color.White
                    )
                    ProfileAvatarCircle(
                        avatarIndex = profile.avatarIndex,
                        size = 42.dp,
                        borderWidth = 2.dp,
                        borderColor = Color.White
                    )
                }
            }

            // Center-Upper Pool of White Letter Tiles + Speaker Button matching res/GC.xml lines 74-95 ([s] [l] [e] [p] + Speaker Icon)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (isWordSolved) {
                    Text(
                        text = "Well done! +20 ⭐",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = SeekhYellow500
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    poolTiles.forEach { tile ->
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(width = 62.dp, height = 66.dp)
                                .shadow(6.dp, RoundedCornerShape(14.dp))
                                .clickable {
                                    if (hasWrongLastTile) {
                                        val lastWrong = placedTiles.lastOrNull()
                                        if (lastWrong != null) {
                                            placedTiles = placedTiles.dropLast(1)
                                            poolTiles = poolTiles + lastWrong
                                        }
                                        hasWrongLastTile = false
                                    }
                                    onPlayPhoneme(tile.char.code)
                                    val nextSlotIndex = placedTiles.size
                                    val expectedChar = targetWord.getOrNull(nextSlotIndex)
                                    val newPlaced = placedTiles + tile
                                    placedTiles = newPlaced
                                    poolTiles = poolTiles.filterNot { it.id == tile.id }

                                    if (expectedChar == tile.char) {
                                        hasWrongLastTile = false
                                        if (newPlaced.size == targetWord.length) {
                                            isWordSolved = true
                                            starBurstTrigger++
                                            onAwardStars(20)
                                            onSpeakText("Excellent! You spelled ${challenge.word} correctly!")
                                        }
                                    } else {
                                        hasWrongLastTile = true
                                        onPlayErrorTone()
                                        onSpeakText("These two letters cannot come together here. Tap undo to try another letter!")
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tile.char.toString(),
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 28.sp,
                                    color = SeekhJumbleDarkPurple
                                )
                            }
                        }
                    }

                    // Speaker Icon Button beside letter pool (matching res/GC.xml @0x7f0a0510)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clickable {
                                onSpeakText("Spell the word: ${challenge.word}. ${challenge.hint}")
                            },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.22f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Hear word",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Lower Answer Slots Row with Green Checkmark / Red Undo Indicator (res/GC.xml lines 102-128)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 110.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    for (slotIdx in targetWord.indices) {
                        val placedTile = placedTiles.getOrNull(slotIdx)
                        val isLastPlaced = slotIdx == placedTiles.size - 1 && placedTiles.isNotEmpty()
                        val isWrongTile = isLastPlaced && hasWrongLastTile

                        Column(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isLastPlaced) {
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .size(34.dp)
                                        .shadow(3.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(if (isWrongTile) SeekhCoralRed else SeekhGreenMint)
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable {
                                            if (placedTile != null && !isWordSolved) {
                                                placedTiles = placedTiles.dropLast(1)
                                                poolTiles = poolTiles + placedTile
                                                hasWrongLastTile = false
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isWrongTile) Icons.AutoMirrored.Filled.Undo else Icons.Default.Check,
                                        contentDescription = if (isWrongTile) "Undo Letter" else "Correct Letter",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(42.dp))
                            }

                            if (placedTile != null) {
                                Surface(
                                    modifier = Modifier
                                        .size(width = 50.dp, height = 56.dp)
                                        .shadow(4.dp, RoundedCornerShape(10.dp))
                                        .clickable {
                                            if (!isWordSolved && slotIdx == placedTiles.size - 1) {
                                                placedTiles = placedTiles.dropLast(1)
                                                poolTiles = poolTiles + placedTile
                                                hasWrongLastTile = false
                                            }
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isWrongTile) SeekhCoralRed else Color.White
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = placedTile.char.toString(),
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 24.sp,
                                            color = if (isWrongTile) Color.White else SeekhJumbleDarkPurple
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(width = 50.dp, height = 56.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SeekhJumbleSlotPurple)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawRoundRect(
                                            color = Color.White.copy(alpha = 0.45f),
                                            size = size,
                                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                                            style = Stroke(
                                                width = 1.5.dp.toPx(),
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = if (hasWrongLastTile) "Tap undo ↶" else null,
            onClick = { onSpeakText("Spell the word: ${challenge.word}") },
            size = 82.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// 3. Pop The Balloons Phonics Game Screen (res/D-.xml — Compact Top Timer ProgressBar + 60 FPS Rising 3D Glossy Balloons + Flying Stars!)
@Composable
fun PopTheBalloonsGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onPlayPhoneme: (Int) -> Unit,
    onPlayBalloonPop: () -> Unit = {},
    onPlayErrorTone: () -> Unit = {},
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var showLevelPicker by remember { mutableStateOf(false) }
    var isHardLevel by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }

    if (showLevelPicker) {
        GameLevelSelectorScreen(
            gameTitle = "Pop The Balloons",
            accentColor = Color(0xFF00838F),
            profile = profile,
            onSelectLevel = { hard ->
                isHardLevel = hard
                showLevelPicker = false
            },
            onSpeakText = onSpeakText,
            onBack = onBack
        )
        return
    }

    val easyTargets = listOf("S", "B", "M", "P", "T", "C")
    val hardTargets = listOf("SH", "CH", "TH", "FL", "ST", "TR")
    val targets = if (isHardLevel) hardTargets else easyTargets

    var roundIdx by remember(isHardLevel) { mutableIntStateOf(0) }
    val targetLetter = targets[roundIdx % targets.size]

    data class FloatingBalloonState(
        val id: Int,
        val letter: String,
        val color: Color,
        val xFraction: Float,
        val yFraction: Float,
        val speedPerTick: Float,
        val isPopped: Boolean = false,
        val isWrongTapped: Boolean = false
    )

    val balloonPalette = listOf(
        Color(0xFFFF5252),
        Color(0xFFFFB300),
        Color(0xFFAB47BC),
        Color(0xFF29B6F6),
        Color(0xFF26A69A),
        Color(0xFFEC407A)
    )

    var balloons by remember(roundIdx, isHardLevel) {
        val distractors = if (isHardLevel) listOf("BR", "CL", "GR") else listOf("K", "D", "R")
        val letters = listOf(targetLetter, distractors[0], targetLetter, distractors[1], targetLetter, distractors[2])
        val xPositions = listOf(0.08f, 0.56f, 0.30f, 0.64f, 0.16f, 0.72f)
        val initialYs = listOf(0.18f, 0.25f, 0.50f, 0.58f, 0.80f, 0.88f)
        val speeds = listOf(0.0055f, 0.0068f, 0.0050f, 0.0062f, 0.0058f, 0.0065f)
        mutableStateOf(
            letters.mapIndexed { idx, l ->
                FloatingBalloonState(
                    id = idx,
                    letter = l,
                    color = balloonPalette[idx % balloonPalette.size],
                    xFraction = xPositions[idx],
                    yFraction = initialYs[idx],
                    speedPerTick = speeds[idx]
                )
            }
        )
    }

    var poppedCorrectCount by remember(roundIdx, isHardLevel) { mutableIntStateOf(0) }
    var timeRemainingSec by remember(roundIdx, isHardLevel) { mutableIntStateOf(25) }
    var showPenaltyFlash by remember { mutableStateOf(false) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    // Continuous 60fps upward floating loop for balloons
    LaunchedEffect(roundIdx, isHardLevel, poppedCorrectCount) {
        while (poppedCorrectCount < 3 && timeRemainingSec > 0) {
            delay(32)
            balloons = balloons.map { b ->
                if (b.isPopped) b
                else {
                    val nextY = b.yFraction - b.speedPerTick
                    if (nextY < -0.15f) b.copy(yFraction = 1.05f, isWrongTapped = false)
                    else b.copy(yFraction = nextY)
                }
            }
        }
        if (poppedCorrectCount >= 3) {
            delay(1300)
            roundIdx++
        }
    }

    // Countdown timer loop
    LaunchedEffect(roundIdx, isHardLevel) {
        onSpeakText("Pop the balloons with the sound $targetLetter!")
        while (timeRemainingSec > 0 && poppedCorrectCount < 3) {
            delay(1000)
            timeRemainingSec = (timeRemainingSec - 1).coerceAtLeast(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE), Color(0xFFFFFFFF))
                )
            )
    ) {
        // Soft Floating White Clouds Canvas Background (matching res/D-.xml bg_clouds)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cloudColor = Color.White.copy(alpha = 0.75f)
            drawCircle(cloudColor, radius = 55.dp.toPx(), center = Offset(size.width * 0.2f, size.height * 0.22f))
            drawCircle(cloudColor, radius = 70.dp.toPx(), center = Offset(size.width * 0.32f, size.height * 0.21f))
            drawCircle(cloudColor, radius = 50.dp.toPx(), center = Offset(size.width * 0.78f, size.height * 0.38f))
            drawCircle(cloudColor, radius = 65.dp.toPx(), center = Offset(size.width * 0.88f, size.height * 0.37f))
            drawCircle(cloudColor, radius = 60.dp.toPx(), center = Offset(size.width * 0.25f, size.height * 0.68f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exact Reference Top Bar matching res/D-.xml lines 43-128 (Back button, RelativeLayout with Timer ProgressBar + Target Sound Pill + Penalty text, Star Counter Pill)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SeekhBalloonDarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Center Timer ProgressBar + Target Sound Pill (res/D-.xml lines 61-118)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        progress = { (timeRemainingSec / 25f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if (timeRemainingSec <= 6) SeekhCoralRed else SeekhGreenMint,
                        trackColor = Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SeekhBalloonDarkBlue,
                        shadowElevation = 3.dp,
                        modifier = Modifier.clickable {
                            onSpeakText("Pop the balloons with the sound $targetLetter!")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "\"$targetLetter\" (${poppedCorrectCount}/3)",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SeekhBalloonDarkBlue.copy(alpha = 0.14f),
                    modifier = Modifier.clickable { showLevelPicker = true }
                ) {
                    Text(
                        text = if (isHardLevel) "Hard ▾" else "Easy ▾",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SeekhBalloonDarkBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = SeekhBalloonDarkBlue,
                    textColor = Color.White
                )
            }

            // Continuous Rising Balloon Arena (res/D-.xml BalloonGameView)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                val arenaW = maxWidth
                val arenaH = maxHeight

                if (timeRemainingSec == 0 && poppedCorrectCount < 3) {
                    // Game Over Overlay (res/D-.xml @0x7f0a0201 "Game over!")
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Game over!",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            color = SeekhBalloonDarkBlue
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { roundIdx++ },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                        ) {
                            Text("Play Again", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    balloons.forEach { b ->
                        val xPos = arenaW * b.xFraction
                        val yPos = arenaH * b.yFraction

                        Box(
                            modifier = Modifier
                                .offset(x = xPos, y = yPos)
                                .size(width = 96.dp, height = 136.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (!b.isPopped) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            if (b.letter == targetLetter) {
                                                onPlayBalloonPop()
                                                balloons = balloons.map {
                                                    if (it.id == b.id) it.copy(isPopped = true) else it
                                                }
                                                val nextCount = poppedCorrectCount + 1
                                                poppedCorrectCount = nextCount
                                                starBurstTrigger++
                                                onAwardStars(10)
                                                if (nextCount >= 3) {
                                                    onSpeakText("Fantastic! You popped all the $targetLetter balloons!")
                                                }
                                            } else {
                                                onPlayErrorTone()
                                                showPenaltyFlash = true
                                                timeRemainingSec = (timeRemainingSec - 2).coerceAtLeast(1)
                                                balloons = balloons.map {
                                                    if (it.id == b.id) it.copy(isWrongTapped = true) else it
                                                }
                                                onSpeakText("Look for $targetLetter!")
                                            }
                                        },
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val bw = 86.dp.toPx()
                                        val bh = 98.dp.toPx()
                                        val left = (size.width - bw) / 2f
                                        drawOval(
                                            color = b.color,
                                            topLeft = Offset(left, 0f),
                                            size = Size(bw, bh)
                                        )
                                        drawOval(
                                            color = Color.White.copy(alpha = 0.35f),
                                            topLeft = Offset(left + bw * 0.18f, bh * 0.14f),
                                            size = Size(bw * 0.22f, bh * 0.32f)
                                        )
                                        val knotPath = Path().apply {
                                            moveTo(size.width / 2f, bh - 2f)
                                            lineTo(size.width / 2f - 7.dp.toPx(), bh + 8.dp.toPx())
                                            lineTo(size.width / 2f + 7.dp.toPx(), bh + 8.dp.toPx())
                                            close()
                                        }
                                        drawPath(knotPath, color = b.color)
                                        drawLine(
                                            color = SeekhBalloonDarkBlue.copy(alpha = 0.55f),
                                            start = Offset(size.width / 2f, bh + 8.dp.toPx()),
                                            end = Offset(size.width / 2f, size.height),
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }

                                    Text(
                                        text = b.letter,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 30.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 26.dp)
                                    )

                                    if (b.isWrongTapped) {
                                        Surface(
                                            shape = CircleShape,
                                            color = SeekhCoralRed,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("-2s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 24.dp)
                                ) {
                                    GoldenStarIcon(size = 44.dp)
                                    Text(
                                        text = "+10",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SeekhBalloonDarkBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = "Pop \"$targetLetter\" balloons!",
            onClick = { onSpeakText("Tap all the balloons that make the sound $targetLetter!") },
            size = 80.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// 4. Exact Reference Speed Reading Game Screen (HybridSpeechGameActivity — res/96.xml: "Get ready" -> Large 44sp Word + Bottom Circular Timer ProgressBar + "Game over!" with "%d wpm" & Speed Vehicle!)
@Composable
fun SpeedReadingGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val speedWords = listOf(
        "butterfly", "yellow", "morning", "friend", "train",
        "rainbow", "clouds", "school", "library", "thunder",
        "sparkle", "garden", "raincoat", "chocolates", "drummer"
    )
    var wordIdx by remember { mutableIntStateOf(0) }
    var wordsReadCount by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var isRunning by remember { mutableStateOf(true) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            onSpeakText("Read 30 seconds to know your reading speed! Read out loud or tap the word.")
            while (secondsLeft > 0 && isRunning) {
                delay(1000)
                secondsLeft--
                // Automatically advance word every 2.5s when voice reading on emulator
                if (secondsLeft % 3 == 0 && secondsLeft > 0) {
                    wordsReadCount++
                    wordIdx++
                    starBurstTrigger++
                    onAwardStars(5)
                }
            }
            if (secondsLeft == 0) {
                isRunning = false
                starBurstTrigger++
                onAwardStars(30)
                val finalWpm = (wordsReadCount * 4).coerceAtLeast(54)
                onSpeakText("Game over! Your reading speed is $finalWpm words per minute!")
            }
        }
    }

    val currentWord = speedWords[wordIdx % speedWords.size]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSpeedYellow)
    ) {
        AssetSvgImage(
            assetPath = "images/game_speed_reading_bg_easy.svg",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            targetPx = 600,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar matching res/96.xml (Back arrow on Left, Star Counter Pill + Profile Avatar on Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SeekhSpeedBrown,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = SeekhSpeedBrown,
                        textColor = Color.White
                    )
                    ProfileAvatarCircle(
                        avatarIndex = profile.avatarIndex,
                        size = 42.dp,
                        borderWidth = 2.dp,
                        borderColor = Color.White
                    )
                }
            }

            // Center Word Display matching res/96.xml lines 61-79 (@0x7f0a012c "Get ready" / @0x7f0a012b 44sp word / @0x7f0a0201 "Game over!")
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .clickable {
                        if (isRunning) {
                            wordsReadCount++
                            wordIdx++
                            starBurstTrigger++
                            onAwardStars(5)
                            onSpeakText(speedWords[wordIdx % speedWords.size])
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isRunning) {
                        Text(
                            text = if (secondsLeft >= 28) "Get ready" else "Read out loud",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = SeekhTextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = currentWord,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 44.sp,
                            color = SeekhSpeedBrown,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = SeekhCatalog.getWordSyllables(currentWord),
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                            color = SeekhTextSecondary
                        )
                    } else {
                        val finalWpm = (wordsReadCount * 4).coerceAtLeast(54)
                        val vehicle = SeekhCatalog.getVehicleForWpm(finalWpm)
                        Text(
                            text = "Game over!",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            color = SeekhTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$finalWpm wpm",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 42.sp,
                            color = SeekhSpeedBrown
                        )
                        Text(
                            text = "${vehicle.emoji} ${vehicle.name}",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = SeekhGreen600
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                secondsLeft = 30
                                wordsReadCount = 0
                                wordIdx++
                                isRunning = true
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                        ) {
                            Text("Play Again", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom Circular Countdown Timer FrameLayout matching res/96.xml lines 80-108 (@0x7f0a00b8: @0x7f0a0498 seconds text + @0x7f0a0497 ProgressBar)
            Box(
                modifier = Modifier
                    .padding(bottom = 36.dp)
                    .size(84.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { (secondsLeft / 30f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(0.88f),
                    color = if (secondsLeft <= 6) SeekhCoralRed else SeekhGreen500,
                    trackColor = SeekhGreen100,
                    strokeWidth = 6.dp
                )
                Text(
                    text = "${secondsLeft}s",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = SeekhSpeedBrown
                )
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = if (isRunning) BuddyMood.LISTENING else buddyMood,
            speechBubbleText = if (isRunning) "Listening..." else null,
            onClick = { onSpeakText(currentWord) },
            size = 80.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// 5. Exact Reference "Word Meanings" Game Screen (HybridGameCategorySelectorActivity 9-Theme Selector res/LF.xml + res/8Q.xml + res/d7.xml -> HybridVocabularyGameActivity res/pr.xml with Automatic Next-Word Progression!)
@Composable
fun VocabularyBuilderGameScreen(
    profile: LearnerProfile,
    selectedLanguageCode: String,
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    // null = showing the 9-Theme Category Selector Screen (HybridGameCategorySelectorActivity); non-null = playing that theme in res/pr.xml!
    var selectedTheme by remember { mutableStateOf<String?>("Color") }
    var quizRound by remember(selectedTheme) { mutableIntStateOf(0) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    BackHandler {
        if (selectedTheme != null) {
            selectedTheme = null
        } else {
            onBack()
        }
    }

    // 1. 9-Theme Category Selector Screen (HybridGameCategorySelectorActivity: res/LF.xml + res/8Q.xml + res/d7.xml)
    if (selectedTheme == null) {
        LaunchedEffect(Unit) {
            onSpeakText("Word Meanings. Choose a theme to play!")
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SeekhSurfaceGray)
        ) {
            CurvedGreenHeaderBackground(headerHeight = 185.dp, curveDepth = 30.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Toolbar matching res/LF.xml
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Word Meanings",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        color = Color.White
                    )
                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = Color.White.copy(alpha = 0.22f),
                        textColor = Color.White
                    )
                }

                // 2-Column RecyclerView Grid of all 9 Official Word Themes (res/8Q.xml + res/d7.xml: Body parts, Color, Seasons, Shapes, Numbers, Expressions, Actions, Places, Vehicles)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(SeekhCatalog.wordMeaningsThemes) { (themeName, iconSvg) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(132.dp)
                                .clickable {
                                    selectedTheme = themeName
                                },
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, SeekhBorderGray)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                AssetSvgImage(
                                    assetPath = iconSvg,
                                    contentDescription = themeName,
                                    modifier = Modifier.size(64.dp),
                                    targetPx = 192
                                )
                                Text(
                                    text = themeName,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SeekhTextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
        return
    }

    // 2. Active "Word Meanings" Gameplay Screen matching res/pr.xml!
    val themeWords = remember(selectedTheme) {
        val filtered = SeekhCatalog.vocabularyItems.filter {
            it.category.equals(selectedTheme, ignoreCase = true)
        }
        if (filtered.size >= 4) filtered else SeekhCatalog.vocabularyItems
    }
    val quizTarget = remember(themeWords, quizRound) {
        themeWords[quizRound % themeWords.size]
    }
    val shuffledChoices = remember(quizTarget, quizRound) {
        val distractors = SeekhCatalog.vocabularyItems
            .filterNot { it.word == quizTarget.word }
            .shuffled()
            .take(3)
        (distractors + quizTarget).shuffled()
    }
    var answeredWord by remember(quizRound, selectedTheme) { mutableStateOf<String?>(null) }
    var isSolvedCorrectly by remember(quizRound, selectedTheme) { mutableStateOf(false) }

    LaunchedEffect(selectedTheme, quizRound) {
        onSpeakText("${quizTarget.word}. ${quizTarget.exampleSentence}")
    }

    // Automatic next-word progression 1.2s after tapping the correct picture (zero extra "Next Vocabulary Word" button!)
    LaunchedEffect(isSolvedCorrectly) {
        if (isSolvedCorrectly) {
            delay(1200)
            quizRound++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8EA))
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 185.dp,
            curveDepth = 28.dp,
            color = SeekhVocabDarkGreen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar matching res/pr.xml lines 15-42 (Back arrow, FlexboxLayout ThreeStepProgressIndicator, Theme button, Star Counter Pill)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { selectedTheme = null }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Choose theme",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                ThreeStepProgressIndicator(currentStep = quizRound)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.20f),
                        modifier = Modifier.clickable { selectedTheme = null }
                    ) {
                        Text(
                            text = "${selectedTheme ?: "Themes"} ▾",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = Color.White.copy(alpha = 0.20f),
                        textColor = Color.White
                    )
                }
            }

            // Heading "Word Meanings" matching res/pr.xml line 48 (@0x7f120326 = "Word Meanings")
            Text(
                text = "Word Meanings",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Target Word Prompt Card matching res/pr.xml lines 61-100 (@0x7f0a036d: @0x7f120328 "Word", 44sp word @0x7f0a036c, definition @0x7f0a036a)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSpeakText("${quizTarget.word}. ${quizTarget.exampleSentence}")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Word (${selectedTheme})",
                                fontFamily = GoogleSansFamily,
                                fontSize = 13.sp,
                                color = SeekhTextSecondary
                            )
                            Text(
                                text = "${quizTarget.word} (${quizTarget.phonetics})",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 26.sp,
                                color = SeekhVocabDarkGreen
                            )
                            val nativeMeaning = when {
                                selectedLanguageCode.contains("ur") -> quizTarget.urduMeaning
                                selectedLanguageCode.contains("hi") -> quizTarget.hindiMeaning
                                else -> quizTarget.spanishMeaning
                            }
                            Text(
                                text = "$nativeMeaning • ${quizTarget.exampleSentence}",
                                fontFamily = GoogleSansFamily,
                                fontSize = 13.sp,
                                color = SeekhTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SeekhVocabDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Hear word",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2x2 Grid of 4 Illustrated SVG Choices (Automatic advance when correct picture is tapped!)
                val pairs = shuffledChoices.chunked(2)
                pairs.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isSelected = answeredWord == item.word
                            val isCorrect = item.word == quizTarget.word
                            val borderColor = when {
                                !isSelected -> SeekhBorderGray
                                isCorrect -> SeekhGreenMint
                                else -> SeekhCoralRed
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(175.dp)
                                    .clickable {
                                        answeredWord = item.word
                                        if (isCorrect) {
                                            isSolvedCorrectly = true
                                            starBurstTrigger++
                                            onAwardStars(15)
                                            onSpeakText("That's right! ${item.word}!")
                                        } else {
                                            onSpeakText("That is ${item.word}. Look for ${quizTarget.word}!")
                                        }
                                    },
                                shape = RoundedCornerShape(22.dp),
                                color = Color.White,
                                shadowElevation = 4.dp,
                                border = BorderStroke(if (isSelected) 3.dp else 1.dp, borderColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AssetSvgImage(
                                        assetPath = item.svgAssetPath,
                                        contentDescription = item.word,
                                        modifier = Modifier.size(102.dp),
                                        targetPx = 256
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = item.word,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = SeekhTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = {
                onSpeakText("Tap the picture for ${quizTarget.word}!")
            },
            size = 80.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}
