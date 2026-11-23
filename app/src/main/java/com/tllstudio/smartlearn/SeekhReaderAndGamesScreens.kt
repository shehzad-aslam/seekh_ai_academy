package com.tllstudio.smartlearn

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.launch

// 1. Exact Reference Full-Screen Snapping Vertical Pager Story Book Reader Flow (matching Read Along by Google!):
//    Every page occupies the full screen (no card borders around pages) inside a VerticalPager so as soon as you scroll vertically, the next full-screen illustrated page slides in and snaps cleanly into place!
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveStoryReaderScreen(
    book: StoryBook,
    profile: LearnerProfile,
    selectedLanguageCode: String,
    buddyMood: BuddyMood,
    initialPageIndex: Int = 0,
    voiceEngine: SeekhVoiceAndAudioEngine? = null,
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
    val coroutineScope = rememberCoroutineScope()
    val startPageIdx = remember(book.id) {
        initialPageIndex.coerceIn(0, (book.pages.size - 1).coerceAtLeast(0))
    }
    // VerticalPager has book.pages.size full-screen story pages + 1 final full-screen finish page at the end
    val totalPagerPages = (book.pages.size + 1).coerceAtLeast(2)
    val pagerState = rememberPagerState(initialPage = startPageIdx) { totalPagerPages }

    var pageIndex by remember(book.id) { mutableIntStateOf(startPageIdx) }
    val currentPage = book.pages.getOrElse(pageIndex) { book.pages.first() }
    val words = remember(currentPage.text) {
        currentPage.text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    }

    // Visual cursor word index per page: starts at 0 (Word 1) so the cursor is always visible on the first word and moves word-by-word!
    var pageReadWordCounts by remember(book.id) {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }
    val setPageReadWordCount: (Int, Int) -> Unit = { targetPageIdx, newCount ->
        pageReadWordCounts = pageReadWordCounts + (targetPageIdx to newCount)
    }

    // Tracks which pages have already awarded stars (prevents duplicate star farming when scrolling back/forth!)
    var rewardedPageIndices by remember(book.id) {
        mutableStateOf(
            if (profile.completedBookIds.contains(book.id)) {
                book.pages.indices.toSet()
            } else {
                (0 until initialPageIndex).toSet()
            }
        )
    }
    var rewardedQuizPageIndices by remember(book.id) {
        mutableStateOf(
            if (profile.completedBookIds.contains(book.id)) {
                book.pages.indices.toSet()
            } else {
                emptySet()
            }
        )
    }
    var masteredPracticeWordsByPage by remember(book.id) {
        mutableStateOf<Map<Int, Set<String>>>(emptyMap())
    }
    var quizAnswersByPage by remember(book.id) {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    // Tooltip state: (pageIndex, selectedWord)
    var selectedTooltipPageAndWord by remember(book.id) { mutableStateOf<Pair<Int, String>?>(null) }
    var activeQuizModalPageIndex by remember(book.id) { mutableStateOf<Int?>(null) }
    var isMicActive by remember(book.id) { mutableStateOf(false) }
    // Mobile only speaks when user taps "🔊 Listen" (and does NOT auto-advance pages when mobile is speaking!)
    var isAutoReadingActive by remember(book.id) { mutableStateOf(false) }
    var showBlockBookDialog by remember { mutableStateOf(false) }
    var readingMode by remember { mutableStateOf("Read aloud") } // "Read aloud" | "Listen" | "Silent"
    var showReadingModeDialog by remember { mutableStateOf(false) }
    var showFullCelebrationScreen by remember { mutableStateOf(false) }
    var completedWpm by remember { mutableIntStateOf(profile.wordsPerMinute) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }
    var hearLineTrigger by remember { mutableIntStateOf(0) }
    var lastRmsAdvanceTimeMs by remember { mutableLongStateOf(0L) }
    val startTimeMs = remember { System.currentTimeMillis() }

    val awardPageStarsOnce: (Int, Int) -> Unit = { targetPageIdx, starsToAward ->
        if (!rewardedPageIndices.contains(targetPageIdx)) {
            rewardedPageIndices = rewardedPageIndices + targetPageIdx
            starBurstTrigger++
            onAwardStars(starsToAward)
        }
    }

    // Connect live Android TTS word-range boundaries & SpeechRecognizer callbacks to move the cursor word-by-word as words are spoken!
    DisposableEffect(voiceEngine, pageIndex, isMicActive, isAutoReadingActive) {
        val activePgIdx = pageIndex
        val activePg = book.pages.getOrElse(activePgIdx) { book.pages.first() }
        val cleanText = activePg.text.replace("_", " ").trim()
        val activeWords = cleanText.split(Regex("\\s+")).filter { it.isNotEmpty() }

        // Precompute character start positions of each word in cleanText for exact TTS onRangeStart synchronization
        val wordCharStarts = mutableListOf<Int>()
        var searchCursor = 0
        for (w in activeWords) {
            val foundIdx = cleanText.indexOf(w, searchCursor)
            if (foundIdx >= 0) {
                wordCharStarts.add(foundIdx)
                searchCursor = foundIdx + w.length
            } else {
                wordCharStarts.add(searchCursor)
            }
        }

        voiceEngine?.onTtsWordRangeListener = { charStart, _ ->
            if (!isMicActive && isAutoReadingActive && activeWords.isNotEmpty()) {
                val matchedWordIdx = wordCharStarts.indexOfLast { it <= charStart }.coerceIn(0, activeWords.lastIndex)
                val currentCount = pageReadWordCounts[activePgIdx] ?: 0
                if (matchedWordIdx >= currentCount) {
                    setPageReadWordCount(activePgIdx, matchedWordIdx)
                }
            }
        }

        voiceEngine?.onRecognizedWordsListener = { spokenWords ->
            if (isMicActive && activeWords.isNotEmpty() && spokenWords.isNotEmpty()) {
                var currentCursor = pageReadWordCounts[activePgIdx] ?: 0
                val cleanSpoken = spokenWords.map { it.lowercase().replace(Regex("[^a-z]"), "") }.filter { it.isNotEmpty() }
                for (sw in cleanSpoken) {
                    if (currentCursor >= activeWords.size) break
                    val targetClean = activeWords[currentCursor].lowercase().replace(Regex("[^a-z]"), "")
                    val isMatch = targetClean.isEmpty() ||
                        sw == targetClean ||
                        (sw.length >= 2 && targetClean.startsWith(sw.take(2))) ||
                        (targetClean.length >= 2 && sw.startsWith(targetClean.take(2)))
                    if (isMatch) {
                        currentCursor++
                    }
                }
                // Even if child's pronunciation differed, advance by at least 1 word when a spoken word burst arrives
                val curr = pageReadWordCounts[activePgIdx] ?: 0
                val nextCursor = maxOf(currentCursor, curr + 1).coerceAtMost(activeWords.size)
                setPageReadWordCount(activePgIdx, nextCursor)
            }
        }

        voiceEngine?.onVoiceRmsPulseListener = { rmsdB ->
            if (isMicActive && activeWords.isNotEmpty() && rmsdB > 2.4f) {
                val now = System.currentTimeMillis()
                if (now - lastRmsAdvanceTimeMs >= 320L) {
                    lastRmsAdvanceTimeMs = now
                    val curr = pageReadWordCounts[activePgIdx] ?: 0
                    if (curr < activeWords.size) {
                        setPageReadWordCount(activePgIdx, curr + 1)
                    }
                }
            }
        }

        onDispose {
            voiceEngine?.onTtsWordRangeListener = null
            voiceEngine?.onRecognizedWordsListener = null
            voiceEngine?.onVoiceRmsPulseListener = null
        }
    }

    // Sync pageIndex whenever VerticalPager settles on a new full-screen page & reset cursor to Word 0 on that page!
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { settledPage ->
                selectedTooltipPageAndWord = null
                if (settledPage in 0 until book.pages.size) {
                    // Position the reading cursor at Word 0 (the first word) of the newly snapped page
                    setPageReadWordCount(settledPage, 0)
                    if (settledPage != pageIndex) {
                        isAutoReadingActive = false
                        pageIndex = settledPage
                        onPageChanged(settledPage)
                    }
                } else if (settledPage == book.pages.size) {
                    isAutoReadingActive = false
                    isMicActive = false
                    onStopListening()
                }
            }
    }

    // Sub-state aware BackHandler inside Story Reader!
    BackHandler {
        when {
            showReadingModeDialog -> showReadingModeDialog = false
            showBlockBookDialog -> showBlockBookDialog = false
            activeQuizModalPageIndex != null -> activeQuizModalPageIndex = null
            selectedTooltipPageAndWord != null -> selectedTooltipPageAndWord = null
            showFullCelebrationScreen -> {
                showFullCelebrationScreen = false
                onBack()
            }
            else -> {
                isAutoReadingActive = false
                isMicActive = false
                onStopListening()
                onBack()
            }
        }
    }

    // MOBILE SPEAKING MODE ("🔊 Listen"):
    // As the mobile speaks each word, the cursor moves word-by-word across the page, and when the page finishes, it STAYS on the current page (does NOT auto-scroll to next page!).
    LaunchedEffect(pageIndex, isAutoReadingActive, hearLineTrigger) {
        val activePgIdx = pagerState.currentPage
        if (activePgIdx !in 0 until book.pages.size) return@LaunchedEffect
        if (!isAutoReadingActive || hearLineTrigger == 0) return@LaunchedEffect
        if (isMicActive) return@LaunchedEffect

        val activePg = book.pages[activePgIdx]
        val activeWords = activePg.text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (activeWords.isEmpty()) return@LaunchedEffect

        // Start cursor cleanly at Word 0 of this page
        setPageReadWordCount(activePgIdx, 0)
        delay(160)
        onSpeakText(activePg.text)

        for (wIdx in activeWords.indices) {
            if (!isAutoReadingActive || isMicActive || pagerState.currentPage != activePgIdx) return@LaunchedEffect

            // Highlight word wIdx with the bouncing star cursor
            if ((pageReadWordCounts[activePgIdx] ?: 0) < wIdx) {
                setPageReadWordCount(activePgIdx, wIdx)
            }

            // Natural word duration based on syllable/character length & punctuation pause
            val rawWord = activeWords[wIdx]
            val cleanLen = rawWord.replace(Regex("[^a-zA-Z]"), "").length
            val hasPunctPause = rawWord.any { it in ",.;!?" }
            val wordDurationMs = (210L + cleanLen * 44L + if (hasPunctPause) 175L else 0L).coerceIn(260L, 620L)
            delay(wordDurationMs)

            // Advance cursor to next word (wIdx + 1) as this word finishes being spoken
            if ((pageReadWordCounts[activePgIdx] ?: 0) <= wIdx) {
                setPageReadWordCount(activePgIdx, wIdx + 1)
            }
        }

        // Wait briefly if TTS is still finishing the last syllable of the sentence
        var extraWait = 0
        while (voiceEngine?.isCurrentlySpeaking() == true && extraWait < 12) {
            delay(100)
            extraWait++
        }

        // Mark all words on the page completed, award stars once, and STAY on this page ("agr mobile bol rha ho tu next page auto na aya")!
        setPageReadWordCount(activePgIdx, activeWords.size)
        awardPageStarsOnce(activePgIdx, 10)
        isAutoReadingActive = false
    }

    // USER SPEAKING MODE ("🎤 Speak"):
    // When WE are speaking into the microphone, the cursor moves word-by-word, and as soon as the page completes, the next page comes up AUTOMATICALLY ("agr hm bol rha ho tu next page auto aa jya")!
    LaunchedEffect(isMicActive, pageIndex) {
        val activePgIdx = pagerState.currentPage
        if (isMicActive && activePgIdx in 0 until book.pages.size) {
            isAutoReadingActive = false
            val activeWords = book.pages[activePgIdx].text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            if ((pageReadWordCounts[activePgIdx] ?: 0) >= activeWords.size) {
                setPageReadWordCount(activePgIdx, 0)
            }
            onStartListening {}

            while (isMicActive && pagerState.currentPage == activePgIdx) {
                delay(330)
                val currentRead = pageReadWordCounts[activePgIdx] ?: 0
                if (currentRead < activeWords.size) {
                    val nextRead = (currentRead + 1).coerceAtMost(activeWords.size)
                    setPageReadWordCount(activePgIdx, nextRead)
                }
                val updatedRead = pageReadWordCounts[activePgIdx] ?: 0
                if (updatedRead >= activeWords.size) {
                    awardPageStarsOnce(activePgIdx, 15)
                    delay(360)
                    if (pagerState.currentPage == activePgIdx) {
                        if (activePgIdx < book.pages.size - 1) {
                            pagerState.animateScrollToPage(activePgIdx + 1)
                        } else {
                            isMicActive = false
                            onStopListening()
                            pagerState.animateScrollToPage(book.pages.size)
                        }
                    }
                    break
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
                pageIndex = 0
                pageReadWordCounts = emptyMap()
                coroutineScope.launch { pagerState.scrollToPage(0) }
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
            // Exact Reference Top Reader Bar (Back Arrow + Page Indicator on Left, Reading Mode Dropdown + Star Counter Pill + Profile Avatar + Menu on Right)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = {
                            onStopListening()
                            onBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Library",
                                tint = SeekhTextPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SeekhGreenSoft
                        ) {
                            Text(
                                text = "${(pageIndex + 1).coerceAtMost(book.pages.size)} / ${book.pages.size}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = SeekhGreen600,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = book.title,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SeekhTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StarCounterPill(
                            stars = profile.stars,
                            backgroundColor = SeekhGreenSoft,
                            textColor = SeekhTextPrimary
                        )
                        ProfileAvatarCircle(
                            avatarIndex = profile.avatarIndex,
                            size = 36.dp,
                            borderWidth = 1.5.dp,
                            borderColor = SeekhGreen500
                        )
                        IconButton(
                            onClick = { showBlockBookDialog = true },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Story options",
                                tint = SeekhTextSecondary
                            )
                        }
                    }
                }

                // Thin Animated Overall Story Progress Bar directly under top bar
                val totalPagesCount = book.pages.size.coerceAtLeast(1)
                val completedOrReached = maxOf(pageIndex + 1, rewardedPageIndices.size)
                val animatedStepProgress by animateFloatAsState(
                    targetValue = (completedOrReached.toFloat() / totalPagesCount.toFloat()).coerceIn(0.06f, 1f),
                    animationSpec = tween(320, easing = FastOutSlowInEasing),
                    label = "reader_step_progress"
                )
                LinearProgressIndicator(
                    progress = { animatedStepProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = SeekhGreen500,
                    trackColor = SeekhBorderGray.copy(alpha = 0.5f)
                )
            }

            // FULL-SCREEN SNAPPING VERTICAL PAGER (No Card boxes around pages — each page fills the full screen and snaps cleanly on vertical swipe!)
            VerticalPager(
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                beyondViewportPageCount = 1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.White)
            ) { pIdx ->
                if (pIdx < book.pages.size) {
                    val page = book.pages[pIdx]
                    val pageWords = remember(page.text) {
                        page.text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                    }
                    val pageReadCount = pageReadWordCounts[pIdx] ?: 0
                    val pageMasteredWords = masteredPracticeWordsByPage[pIdx] ?: emptySet()
                    val isCurrentActivePage = pagerState.currentPage == pIdx
                    val textScrollState = rememberScrollState()

                    // FULL-SCREEN PAGE CANVAS (No Card border, no card background, pure full-screen illustration + complete unclipped interactive words!)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 104.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top 37% of Full Screen: Clean Story Illustration
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.37f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(SeekhSurfaceGray)
                                .clickable {
                                    pageIndex = pIdx
                                    isMicActive = false
                                    onStopListening()
                                    isAutoReadingActive = true
                                    hearLineTrigger++
                                }
                        ) {
                            AssetWebpImage(
                                assetPath = page.imageAssetPath,
                                contentDescription = "Illustration ${pIdx + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom 63% of Full Screen: 100% Complete Unclipped Interactive Story Text + Bilingual Subtitle!
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.63f)
                                .verticalScroll(textScrollState),
                            verticalArrangement = Arrangement.Top
                        ) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                pageWords.forEachIndexed { wIdx, rawWord ->
                                    val cleanWord = rawWord.lowercase().replace(Regex("[^a-z]"), "")
                                    val isRead = wIdx < pageReadCount
                                    val isCurrentActiveWord = isCurrentActivePage && wIdx == pageReadCount
                                    val isPracticeWord = page.practiceWords.contains(cleanWord)
                                    val isMasteredPractice = pageMasteredWords.contains(cleanWord)

                                    val targetWordColor = when {
                                        isMasteredPractice -> SeekhGreenMint
                                        isRead -> SeekhBlueReading
                                        else -> SeekhTextPrimary
                                    }
                                    val wordColor by animateColorAsState(
                                        targetValue = targetWordColor,
                                        animationSpec = tween(200),
                                        label = "word_col_${pIdx}_$wIdx"
                                    )
                                    val wordScale by animateFloatAsState(
                                        targetValue = if (isCurrentActiveWord) 1.07f else 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "word_scale_${pIdx}_$wIdx"
                                    )

                                    Column(
                                        modifier = Modifier
                                            .padding(end = 7.dp)
                                            .scale(wordScale)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isCurrentActiveWord) SeekhGreenSoft.copy(alpha = 0.72f) else Color.Transparent
                                            )
                                            .bouncyClick {
                                                pageIndex = pIdx
                                                isAutoReadingActive = false
                                                isMicActive = false
                                                onStopListening()
                                                val details = SeekhCatalog.getDetailedWordInfo(rawWord, page.text, selectedLanguageCode)
                                                selectedTooltipPageAndWord = pIdx to rawWord
                                                // Speak complete word details out loud (word + syllables + part of speech + meaning!)
                                                onSpeakText(details.spokenExplanation)
                                                if (wIdx >= pageReadCount) {
                                                    val nextCount = wIdx + 1
                                                    setPageReadWordCount(pIdx, nextCount)
                                                    if (nextCount >= pageWords.size) {
                                                        awardPageStarsOnce(pIdx, 10)
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 4.dp, vertical = 1.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = rawWord,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = if (isCurrentActiveWord) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 23.sp,
                                            lineHeight = 30.sp,
                                            color = wordColor
                                        )

                                        // Dotted Red (or Green when mastered) Underline for Practice Words
                                        if (isPracticeWord) {
                                            val dotColor = if (isMasteredPractice) SeekhGreenMint else SeekhCoralRed
                                            val approxWidth = (rawWord.length * 12).coerceIn(28, 110).dp
                                            Canvas(
                                                modifier = Modifier
                                                    .width(approxWidth)
                                                    .height(5.dp)
                                            ) {
                                                drawLine(
                                                    color = dotColor,
                                                    start = Offset(0f, size.height / 2f),
                                                    end = Offset(size.width, size.height / 2f),
                                                    strokeWidth = 2.5.dp.toPx(),
                                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 7f), 0f)
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }

                                        // Moving Pointer under the current active word to read
                                        if (isCurrentActiveWord) {
                                            val pointerBounce by rememberInfiniteTransition(label = "ptr_$pIdx").animateFloat(
                                                initialValue = 0f,
                                                targetValue = 4f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(520),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "ptr_y_$pIdx"
                                            )
                                            CustomMagicPointerIcon(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .offset(y = pointerBounce.dp)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(18.dp))
                                        }
                                    }
                                }
                            }

                            // Clean Bilingual Translation Line directly on full screen (no card box, 100% visible!)
                            val bilingualSentence = when {
                                selectedLanguageCode.contains("ur") -> page.urduTranslation
                                selectedLanguageCode.contains("hi") -> page.hindiTranslation
                                selectedLanguageCode.contains("es") -> page.spanishTranslation
                                else -> ""
                            }
                            if (bilingualSentence.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bilingualSentence,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.5.sp,
                                    lineHeight = 23.sp,
                                    color = SeekhTextSecondary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            pageIndex = pIdx
                                            isMicActive = false
                                            onStopListening()
                                            isAutoReadingActive = true
                                            hearLineTrigger++
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                } else {
                    // Final Full-Screen Story End Page when user swipes up after the last page
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = 28.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(190.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(SeekhSurfaceGray)
                        ) {
                            AssetWebpImage(
                                assetPath = book.coverAssetPath,
                                contentDescription = book.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(22.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(5) { GoldenStarIcon(size = 32.dp) }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You read \"${book.title}\"!",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = SeekhTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All ${book.pages.size} pages completed • Claim your +50 Golden Stars!",
                            fontFamily = GoogleSansFamily,
                            fontSize = 15.sp,
                            color = SeekhTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                isMicActive = false
                                onStopListening()
                                val elapsedMin = ((System.currentTimeMillis() - startTimeMs) / 60000L).toInt().coerceAtLeast(1)
                                val totalWords = book.pages.sumOf { it.text.split(Regex("\\s+")).size }
                                val calcWpm = (totalWords / elapsedMin).coerceIn(54, 92)
                                completedWpm = calcWpm
                                onBookCompleted(book, calcWpm)
                                showFullCelebrationScreen = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                        ) {
                            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Claim +50 Stars & Play Game 🎉",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Floating Complete Word Tutor Overlay Card (appears cleanly above the bottom dock whenever any word is clicked!)
        val activeWordPair = selectedTooltipPageAndWord
        if (activeWordPair != null) {
            val wordPgIdx = activeWordPair.first
            val clickedRawWord = activeWordPair.second
            val pageForWord = book.pages.getOrElse(wordPgIdx) { currentPage }
            val wordDetails = remember(clickedRawWord, pageForWord.text, selectedLanguageCode) {
                SeekhCatalog.getDetailedWordInfo(clickedRawWord, pageForWord.text, selectedLanguageCode)
            }
            val pageMasteredWords = masteredPracticeWordsByPage[wordPgIdx] ?: emptySet()

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 14.dp, end = 14.dp, bottom = 104.dp)
                    .fillMaxWidth()
                    .shadow(14.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(2.dp, SeekhBlue500)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Row: Word + Phonetics + Part of Speech + Hear All + Practice Say It + Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = wordDetails.displayWord,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = SeekhTextPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SeekhBlueSoft
                                ) {
                                    Text(
                                        text = wordDetails.partOfSpeech,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = SeekhBlue600,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Pronunciation: ${wordDetails.phonetics}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = SeekhTextSecondary
                            )
                        }

                        IconButton(
                            onClick = { selectedTooltipPageAndWord = null },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close word tutor",
                                tint = SeekhTextSecondary
                            )
                        }
                    }

                    // Interactive Syllable Breakdown Chips (Tap any syllable to hear it!)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Syllables:",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SeekhTextSecondary
                        )
                        wordDetails.syllables.forEachIndexed { sIdx, syl ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SeekhBlueSoft,
                                border = BorderStroke(1.dp, SeekhBlue500),
                                modifier = Modifier.bouncyClick {
                                    onPlayPhoneme(sIdx * 3)
                                    onSpeakText(syl)
                                }
                            ) {
                                Text(
                                    text = syl,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = SeekhBlue600,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Complete English Explanation & Native Urdu/Hindi Meaning Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SeekhSurfaceGray,
                        border = BorderStroke(1.dp, SeekhBorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Meaning: ${wordDetails.englishMeaning}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = SeekhTextPrimary
                            )
                            Text(
                                text = "ترجمہ / अर्थ: ${wordDetails.nativeMeaning}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp,
                                color = SeekhGreen600
                            )
                        }
                    }

                    // Action Buttons: "🔊 Hear Explanation" & "🎤 Say Word (+10 ⭐)"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .bouncyClick {
                                    onSpeakText(wordDetails.spokenExplanation)
                                },
                            shape = RoundedCornerShape(50),
                            color = SeekhBlue600
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Hear word explanation",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hear Details",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .bouncyClick {
                                    val wordKey = wordDetails.cleanWord.lowercase()
                                    val alreadyMastered = pageMasteredWords.contains(wordKey)
                                    masteredPracticeWordsByPage = masteredPracticeWordsByPage + (wordPgIdx to (pageMasteredWords + wordKey))
                                    if (!alreadyMastered) {
                                        starBurstTrigger++
                                        onAwardStars(10)
                                    }
                                    onSpeakText("Great job! You practiced the word ${wordDetails.displayWord}!")
                                    selectedTooltipPageAndWord = null
                                },
                            shape = RoundedCornerShape(50),
                            color = SeekhGreenMint
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Say word",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Say It (+10 ⭐)",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Flying Stars Animation Overlay (StarAnimationHolderView)
        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        // Crystal-Clear 2-Button Voice Control Dock at Bottom-Left:
        //   1. "🔊 Listen (Hear Story)" -> Blue Speaker Button to listen to the story word-by-word
        //   2. "🎤 Speak (Read Yourself)" -> Green Microphone Button to speak into the mic yourself
        if (pagerState.currentPage < book.pages.size) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 98.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Live Mode Helper Strip + Optional Quiz Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when {
                            isMicActive -> Color(0xFFECFDF5)
                            isAutoReadingActive -> SeekhBlueSoft
                            else -> SeekhSurfaceGray
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isMicActive -> SeekhGreenMint.copy(alpha = 0.5f)
                                isAutoReadingActive -> SeekhBlue500.copy(alpha = 0.5f)
                                else -> SeekhBorderGray
                            }
                        )
                    ) {
                        Text(
                            text = when {
                                isMicActive -> "🎤 Speak Mode: Read the highlighted word aloud"
                                isAutoReadingActive -> "🔊 Listen Mode: AI reading word by word"
                                else -> "Tap 🔊 Listen to hear, or 🎤 Speak to read yourself"
                            },
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = when {
                                isMicActive -> SeekhVocabDarkGreen
                                isAutoReadingActive -> SeekhBlue600
                                else -> SeekhTextSecondary
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (currentPage.quiz != null) {
                        val isQuizSolved = rewardedQuizPageIndices.contains(pageIndex)
                        Surface(
                            modifier = Modifier.bouncyClick(scaleDown = 0.90f) {
                                isAutoReadingActive = false
                                voiceEngine?.stopSpeaking()
                                activeQuizModalPageIndex = pageIndex
                                onSpeakText(currentPage.quiz.question)
                            },
                            shape = RoundedCornerShape(50),
                            color = if (isQuizSolved) SeekhGreenMint else SeekhGreenSoft,
                            border = BorderStroke(1.dp, SeekhGreen500)
                        ) {
                            Text(
                                text = if (isQuizSolved) "Quiz ✓" else "💡 Quiz",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = if (isQuizSolved) Color.White else SeekhGreen600,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Side-by-Side Clearly Labeled "🔊 Listen" & "🎤 Speak" Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BUTTON 1: FOR LISTENING TO THE STORY (Sunna k lia)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .bouncyClick(scaleDown = 0.92f) {
                                if (isAutoReadingActive) {
                                    isAutoReadingActive = false
                                    voiceEngine?.stopSpeaking()
                                } else {
                                    isMicActive = false
                                    onStopListening()
                                    isAutoReadingActive = true
                                    hearLineTrigger++
                                }
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isAutoReadingActive) SeekhBlue600 else SeekhBlueSoft,
                        border = BorderStroke(1.5.dp, SeekhBlue600),
                        shadowElevation = if (isAutoReadingActive) 6.dp else 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isAutoReadingActive) Icons.Default.PauseCircleFilled else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen to Story",
                                tint = if (isAutoReadingActive) Color.White else SeekhBlue600,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAutoReadingActive) "Listening..." else "Listen",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp,
                                    color = if (isAutoReadingActive) Color.White else SeekhBlue600
                                )
                                Text(
                                    text = if (isAutoReadingActive) "Tap to Pause" else "Hear Story",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    color = if (isAutoReadingActive) Color.White.copy(alpha = 0.88f) else SeekhBlue600.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // BUTTON 2: FOR SPEAKING YOURSELF (Khud bolna k lia)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .bouncyClick(scaleDown = 0.92f) {
                                val nextMic = !isMicActive
                                isMicActive = nextMic
                                if (nextMic) {
                                    isAutoReadingActive = false
                                    voiceEngine?.stopSpeaking()
                                } else {
                                    onStopListening()
                                }
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isMicActive) SeekhGreenMint else Color(0xFFECFDF5),
                        border = BorderStroke(1.5.dp, SeekhGreenMint),
                        shadowElevation = if (isMicActive) 6.dp else 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isMicActive) Icons.Default.StopCircle else Icons.Default.Mic,
                                contentDescription = "Speak Yourself",
                                tint = if (isMicActive) Color.White else SeekhVocabDarkGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isMicActive) "Speaking..." else "Speak",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp,
                                    color = if (isMicActive) Color.White else SeekhVocabDarkGreen
                                )
                                Text(
                                    text = if (isMicActive) "Tap to Stop" else "Read Yourself",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    color = if (isMicActive) Color.White.copy(alpha = 0.88f) else SeekhVocabDarkGreen.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom-Right AI Reading Buddy (Tapping also replays / listens to the current page!)
        FloatingReadingBuddy(
            mood = when {
                isMicActive -> BuddyMood.LISTENING
                isAutoReadingActive -> BuddyMood.SPEAKING
                else -> buddyMood
            },
            speechBubbleText = null,
            onClick = {
                isMicActive = false
                onStopListening()
                isAutoReadingActive = true
                hearLineTrigger++
            },
            size = 74.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 12.dp, bottom = 12.dp)
        )
    }

    // Optional Quiz Dialog when user taps "💡 Quiz (+20 ⭐)" on a page
    val quizModalIdx = activeQuizModalPageIndex
    val modalQuiz = if (quizModalIdx != null) book.pages.getOrNull(quizModalIdx)?.quiz else null
    if (quizModalIdx != null && modalQuiz != null) {
        val chosenOpt = quizAnswersByPage[quizModalIdx]
        AlertDialog(
            onDismissRequest = { activeQuizModalPageIndex = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onSpeakText(modalQuiz.question) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Hear question",
                        tint = SeekhGreen600
                    )
                    Text(
                        text = modalQuiz.question,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = SeekhTextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    modalQuiz.options.forEachIndexed { optIdx, optionText ->
                        val isChosen = chosenOpt == optIdx
                        val isCorrect = optIdx == modalQuiz.correctIndex
                        val cardBg = when {
                            !isChosen -> SeekhSurfaceGray
                            isCorrect -> SeekhGreenMint
                            else -> SeekhCoralRed
                        }
                        val textCol = if (isChosen) Color.White else SeekhTextPrimary
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, if (isChosen) cardBg else SeekhBorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .bouncyClick {
                                    quizAnswersByPage = quizAnswersByPage + (quizModalIdx to optIdx)
                                    if (isCorrect) {
                                        if (!rewardedQuizPageIndices.contains(quizModalIdx)) {
                                            rewardedQuizPageIndices = rewardedQuizPageIndices + quizModalIdx
                                            starBurstTrigger++
                                            onAwardStars(20)
                                        }
                                        onSpeakText("Correct answer! $optionText!")
                                        coroutineScope.launch {
                                            delay(450)
                                            activeQuizModalPageIndex = null
                                        }
                                    } else {
                                        onSpeakText("Try again!")
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = optionText,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
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
            },
            confirmButton = {
                TextButton(onClick = { activeQuizModalPageIndex = null }) {
                    Text("Close", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                }
            }
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
                    .bouncyClick(scaleDown = 0.88f, onClick = onPrevious),
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
                .bouncyClick(scaleDown = 0.88f, onClick = onNext),
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

// 2. Jumbled Words Game Screen (with Clue & Bilingual Meaning Card, Smart Hint Button, Responsive Dynamic Tile Widths, 3-Word Round Celebration & Zero Duplicate Stars!)
@OptIn(ExperimentalLayoutApi::class)
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
    var showLevelPicker by remember { mutableStateOf(false) }
    var isHardLevel by remember { mutableStateOf(false) }

    BackHandler {
        if (showLevelPicker) {
            showLevelPicker = false
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
            onBack = { showLevelPicker = false }
        )
        return
    }

    val filteredChallenges = remember(isHardLevel) {
        val list = SeekhCatalog.jumbleChallenges.filter { it.isHard == isHardLevel }
        list.ifEmpty { SeekhCatalog.jumbleChallenges }
    }
    var challengeIdx by remember(isHardLevel) { mutableIntStateOf(0) }
    var wordsSolvedInSet by remember(isHardLevel) { mutableIntStateOf(0) }
    var showSetCompleteBanner by remember(isHardLevel) { mutableStateOf(false) }
    val challenge = filteredChallenges[challengeIdx % filteredChallenges.size]
    val targetWord = challenge.word.lowercase()
    val urduMeaning = remember(targetWord) { SeekhCatalog.getWordTranslation(targetWord, "en_ur") }

    data class LetterTile(val id: Int, val char: Char)

    var placedTiles by remember(challengeIdx, isHardLevel) {
        mutableStateOf(emptyList<LetterTile>())
    }

    var poolTiles by remember(challengeIdx, isHardLevel) {
        val rawTiles = targetWord.mapIndexed { idx, c -> LetterTile(idx, c) }
        var shuffled = rawTiles.shuffled()
        if (shuffled.map { it.char }.joinToString("") == targetWord && rawTiles.size > 1) {
            shuffled = rawTiles.reversed()
        }
        mutableStateOf(shuffled)
    }

    var hasWrongLastTile by remember(challengeIdx, isHardLevel) { mutableStateOf(false) }
    var isWordSolved by remember(challengeIdx, isHardLevel) { mutableStateOf(false) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    // Speak the target word & hint automatically when each round begins
    LaunchedEffect(challengeIdx, isHardLevel) {
        onSpeakText("Unscramble the letters to spell: ${challenge.word}. ${challenge.hint}")
    }

    // Auto-advance to next word (or show 3-word set celebration) 1.4s after solving
    LaunchedEffect(isWordSolved) {
        if (isWordSolved) {
            delay(1400)
            val nextSetCount = wordsSolvedInSet + 1
            if (nextSetCount >= 3) {
                wordsSolvedInSet = 3
                showSetCompleteBanner = true
            } else {
                wordsSolvedInSet = nextSetCount
                challengeIdx++
            }
        }
    }

    // Helper for Smart Hint Button: places the next correct letter automatically
    val useSmartHint: () -> Unit = {
        if (!isWordSolved) {
            var currentPlaced = placedTiles
            var currentPool = poolTiles
            if (hasWrongLastTile && currentPlaced.isNotEmpty()) {
                val lastWrong = currentPlaced.last()
                currentPlaced = currentPlaced.dropLast(1)
                currentPool = currentPool + lastWrong
                hasWrongLastTile = false
            }
            val nextSlotIdx = currentPlaced.size
            val neededChar = targetWord.getOrNull(nextSlotIdx)
            val matchingTile = currentPool.firstOrNull { it.char == neededChar }
            if (matchingTile != null) {
                onPlayPhoneme(matchingTile.char.code)
                val newPlaced = currentPlaced + matchingTile
                placedTiles = newPlaced
                poolTiles = currentPool.filterNot { it.id == matchingTile.id }
                if (newPlaced.size == targetWord.length) {
                    isWordSolved = true
                    starBurstTrigger++
                    onAwardStars(20)
                    onSpeakText("Excellent! You spelled ${challenge.word} correctly!")
                } else {
                    onSpeakText("Hint: starts with ${newPlaced.map { it.char }.joinToString("")}")
                }
            }
        }
    }

    val adaptiveSlotWidth = remember(targetWord.length) {
        (312 / targetWord.length.coerceAtLeast(4)).coerceIn(36, 52).dp
    }
    val adaptiveSlotHeight = remember(targetWord.length) {
        if (targetWord.length >= 8) 48.dp else 56.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhJumblePurple)
    ) {
        // Curved two-tone purple wave in the lower half
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val waveY = h * 0.58f
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
            // Top Bar (Back arrow, ThreeStepProgressView, Level pill, Star Counter Pill, Profile Avatar)
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

                ThreeStepProgressIndicator(currentStep = wordsSolvedInSet.coerceIn(0, 2))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0x38000000),
                        modifier = Modifier.bouncyClick { showLevelPicker = true }
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
                        size = 40.dp,
                        borderWidth = 2.dp,
                        borderColor = Color.White
                    )
                }
            }

            // Clue & Bilingual Meaning Prompt Card + Smart Hint Button
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhYellow500
                            ) {
                                Text(
                                    text = "${challenge.category} • ${targetWord.length} letters",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = SeekhTextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                            if (urduMeaning.isNotBlank() && !urduMeaning.startsWith("A word")) {
                                Text(
                                    text = urduMeaning,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SeekhYellow500
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Clue: ${challenge.hint}",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Hear Word Button
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .bouncyClick {
                                    onSpeakText("Spell the word: ${challenge.word}. ${challenge.hint}")
                                },
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.24f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Hear word",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Smart Hint Button (places next correct letter)
                        Surface(
                            modifier = Modifier
                                .height(42.dp)
                                .bouncyClick { useSmartHint() },
                            shape = RoundedCornerShape(50),
                            color = SeekhYellow500
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Hint",
                                    tint = SeekhTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Hint",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = SeekhTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Center Pool of White Letter Tiles (FlowRow so long Hard words never overflow!)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (isWordSolved) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SeekhGreenMint,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "Awesome! \"${challenge.word.uppercase()}\" +20 ⭐",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                } else if (hasWrongLastTile) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SeekhCoralRed
                    ) {
                        Text(
                            text = "Oops! Tap the red letter or Pick another tile",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Tap letters in order to spell the word",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    poolTiles.forEach { tile ->
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(width = 58.dp, height = 62.dp)
                                .shadow(6.dp, RoundedCornerShape(14.dp))
                                .bouncyClick {
                                    if (isWordSolved) return@bouncyClick
                                    var currentPlaced = placedTiles
                                    var currentPool = poolTiles
                                    if (hasWrongLastTile) {
                                        val lastWrong = currentPlaced.lastOrNull()
                                        if (lastWrong != null) {
                                            currentPlaced = currentPlaced.dropLast(1)
                                            currentPool = currentPool + lastWrong
                                        }
                                        hasWrongLastTile = false
                                    }
                                    onPlayPhoneme(tile.char.code)
                                    val nextSlotIndex = currentPlaced.size
                                    val expectedChar = targetWord.getOrNull(nextSlotIndex)
                                    val newPlaced = currentPlaced + tile
                                    placedTiles = newPlaced
                                    poolTiles = currentPool.filterNot { it.id == tile.id }

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
                                        onSpeakText("Try another letter here!")
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tile.char.toString(),
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp,
                                    color = SeekhJumbleDarkPurple
                                )
                            }
                        }
                    }
                }
            }

            // Lower Answer Slots Row with Responsive Widths + Clear / Skip Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    for (slotIdx in targetWord.indices) {
                        val placedTile = placedTiles.getOrNull(slotIdx)
                        val isLastPlaced = slotIdx == placedTiles.size - 1 && placedTiles.isNotEmpty()
                        val isWrongTile = isLastPlaced && hasWrongLastTile

                        Column(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isLastPlaced) {
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 6.dp)
                                        .size(30.dp)
                                        .shadow(3.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(if (isWrongTile) SeekhCoralRed else SeekhGreenMint)
                                        .border(2.dp, Color.White, CircleShape)
                                        .bouncyClick {
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
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(36.dp))
                            }

                            if (placedTile != null) {
                                Surface(
                                    modifier = Modifier
                                        .size(width = adaptiveSlotWidth, height = adaptiveSlotHeight)
                                        .shadow(4.dp, RoundedCornerShape(10.dp))
                                        .bouncyClick {
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
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = if (targetWord.length >= 8) 20.sp else 23.sp,
                                            color = if (isWrongTile) Color.White else SeekhJumbleDarkPurple
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(width = adaptiveSlotWidth, height = adaptiveSlotHeight)
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

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Clear & Next Word Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (placedTiles.isNotEmpty() && !isWordSolved) {
                        Surface(
                            modifier = Modifier.bouncyClick {
                                poolTiles = (poolTiles + placedTiles).shuffled()
                                placedTiles = emptyList()
                                hasWrongLastTile = false
                            },
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.20f)
                        ) {
                            Text(
                                text = "↺ Reset Letters",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.bouncyClick {
                            if (!isWordSolved) {
                                challengeIdx++
                            }
                        },
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "Skip Word →",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // 3-Word Set Completion Celebration Modal Overlay
        if (showSetCompleteBanner) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            repeat(3) { GoldenStarIcon(size = 36.dp) }
                        }
                        Text(
                            text = "3 / 3 Words Spelled!",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "You earned +60 Golden Stars in Jumbled Words (${if (isHardLevel) "Hard" else "Easy"} Level)!",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            color = SeekhTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                showSetCompleteBanner = false
                                wordsSolvedInSet = 0
                                challengeIdx++
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                        ) {
                            Text("Play Next 3 Words →", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                showSetCompleteBanner = false
                                wordsSolvedInSet = 0
                                isHardLevel = !isHardLevel
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (isHardLevel) "Switch to Easy Level" else "Try Hard Level ⭐",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                color = SeekhJumblePurple
                            )
                        }
                    }
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = if (hasWrongLastTile) "Tap undo ↶" else null,
            onClick = { onSpeakText("Spell the word: ${challenge.word}. ${challenge.hint}") },
            size = 78.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// 3. Pop The Balloons Phonics Game Screen (with Phonics Example Banner, Pause/Resume, Non-Cluttered Pop Effect & Round Cleared Celebration!)
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
    var isPaused by remember { mutableStateOf(false) }

    BackHandler {
        if (showLevelPicker) {
            showLevelPicker = false
        } else {
            onBack()
        }
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
            onBack = { showLevelPicker = false }
        )
        return
    }

    val easyTargets = listOf(
        "S" to "Sun ☀️",
        "B" to "Ball ⚽",
        "M" to "Mango 🥭",
        "P" to "Parrot 🦜",
        "T" to "Train 🚂",
        "C" to "Cat 🐱"
    )
    val hardTargets = listOf(
        "SH" to "Ship 🚢",
        "CH" to "Chair 🪑",
        "TH" to "Thumb 👍",
        "FL" to "Flower 🌸",
        "ST" to "Star ⭐",
        "TR" to "Tree 🌳"
    )
    val targets = if (isHardLevel) hardTargets else easyTargets

    var roundIdx by remember(isHardLevel) { mutableIntStateOf(0) }
    val (targetLetter, targetExample) = targets[roundIdx % targets.size]

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
        val xPositions = listOf(0.06f, 0.38f, 0.70f, 0.14f, 0.46f, 0.72f)
        val initialYs = listOf(0.20f, 0.36f, 0.24f, 0.64f, 0.78f, 0.68f)
        val speeds = listOf(0.0048f, 0.0056f, 0.0046f, 0.0054f, 0.0050f, 0.0058f)
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
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    // Continuous 60fps upward floating loop for balloons (respects isPaused)
    LaunchedEffect(roundIdx, isHardLevel, poppedCorrectCount, isPaused) {
        while (poppedCorrectCount < 3 && timeRemainingSec > 0) {
            delay(32)
            if (!isPaused) {
                balloons = balloons.map { b ->
                    if (b.isPopped) b
                    else {
                        val nextY = b.yFraction - b.speedPerTick
                        if (nextY < -0.15f) b.copy(yFraction = 1.02f, isWrongTapped = false)
                        else b.copy(yFraction = nextY)
                    }
                }
            }
        }
        if (poppedCorrectCount >= 3) {
            delay(1500)
            roundIdx++
        }
    }

    // Countdown timer loop (respects isPaused)
    LaunchedEffect(roundIdx, isHardLevel, isPaused) {
        if (!isPaused && timeRemainingSec == 25) {
            onSpeakText("Pop 3 balloons with the sound $targetLetter, as in $targetExample!")
        }
        while (timeRemainingSec > 0 && poppedCorrectCount < 3) {
            delay(1000)
            if (!isPaused) {
                timeRemainingSec = (timeRemainingSec - 1).coerceAtLeast(0)
            }
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
        // Soft Floating White Clouds Canvas Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cloudColor = Color.White.copy(alpha = 0.78f)
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
            // Top Bar (Back button, Timer Bar + Target Pill, Pause button, Level selector, Star Counter Pill)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SeekhBalloonDarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

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
                        modifier = Modifier.bouncyClick {
                            onSpeakText("Pop the balloons with the sound $targetLetter, as in $targetExample!")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "\"$targetLetter\" ($poppedCorrectCount/3) • ${timeRemainingSec}s",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Pause / Resume Icon Button
                Surface(
                    shape = CircleShape,
                    color = SeekhBalloonDarkBlue.copy(alpha = 0.14f),
                    modifier = Modifier
                        .size(34.dp)
                        .bouncyClick { isPaused = !isPaused }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = SeekhBalloonDarkBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = SeekhBalloonDarkBlue.copy(alpha = 0.14f),
                    modifier = Modifier.bouncyClick { showLevelPicker = true }
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

            // Target Phonics Sound & Example Word Helper Strip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.88f),
                border = BorderStroke(1.dp, SeekhBalloonDarkBlue.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 2.dp)
                    .bouncyClick {
                        onSpeakText("Find and pop 3 balloons with the sound $targetLetter, like in $targetExample!")
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Target sound: \"$targetLetter\" as in $targetExample",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SeekhBalloonDarkBlue
                    )
                    ThreeStepProgressIndicator(
                        currentStep = (poppedCorrectCount - 1).coerceAtLeast(0),
                        totalSteps = 3,
                        activeColor = if (poppedCorrectCount > 0) SeekhGreenMint else Color.LightGray,
                        inactiveColor = SeekhBorderGray
                    )
                }
            }

            // Continuous Rising Balloon Arena
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                val arenaW = maxWidth
                val arenaH = maxHeight

                if (timeRemainingSec == 0 && poppedCorrectCount < 3) {
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White,
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Time's up!",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 30.sp,
                                color = SeekhBalloonDarkBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "You popped $poppedCorrectCount of 3 \"$targetLetter\" balloons.",
                                fontFamily = GoogleSansFamily,
                                fontSize = 14.sp,
                                color = SeekhTextSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { roundIdx++ },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                            ) {
                                Text("Try Next Sound →", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    balloons.forEach { b ->
                        val xPos = arenaW * b.xFraction
                        val yPos = arenaH * b.yFraction

                        if (!b.isPopped) {
                            Box(
                                modifier = Modifier
                                    .offset(x = xPos, y = yPos)
                                    .size(width = 96.dp, height = 136.dp)
                                    .clickable {
                                        if (isPaused || poppedCorrectCount >= 3) return@clickable
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
                                                onSpeakText("Fantastic! You popped all 3 $targetLetter balloons!")
                                            }
                                        } else {
                                            onPlayErrorTone()
                                            timeRemainingSec = (timeRemainingSec - 2).coerceAtLeast(1)
                                            balloons = balloons.map {
                                                if (it.id == b.id) it.copy(isWrongTapped = true) else it
                                            }
                                            onSpeakText("That is ${b.letter}. Look for $targetLetter!")
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
                        }
                    }

                    // Celebratory Banner when all 3 target balloons are popped!
                    if (poppedCorrectCount >= 3) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            shadowElevation = 10.dp,
                            border = BorderStroke(2.dp, SeekhGreenMint),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    repeat(3) { GoldenStarIcon(size = 32.dp) }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "3 / 3 \"$targetLetter\" Balloons Popped!",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = SeekhBalloonDarkBlue
                                )
                                Text(
                                    text = "+30 Golden Stars • Next sound loading...",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SeekhGreen600
                                )
                            }
                        }
                    }
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = "Pop \"$targetLetter\" ($targetExample)!",
            onClick = { onSpeakText("Tap all the balloons that make the sound $targetLetter, as in $targetExample!") },
            size = 78.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// 4. Speed Reading Game Screen (User-Controlled "✓ I Read It!" / Optional Mic Mode — NO Unsolicited Auto-Advancing Free Stars!)
@Composable
fun SpeedReadingGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onUpdateWpm: (Int) -> Unit = {},
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val speedWords = listOf(
        "butterfly", "yellow", "morning", "friend", "train",
        "rainbow", "clouds", "school", "library", "thunder",
        "sparkle", "garden", "raincoat", "chocolates", "drummer",
        "adventure", "sunshine", "bicycle", "umbrella", "treasure"
    )
    var wordIdx by remember { mutableIntStateOf(0) }
    var wordsReadCount by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var isRunning by remember { mutableStateOf(true) }
    var isAutoVoiceMicOn by remember { mutableStateOf(false) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    val currentWord = speedWords[wordIdx % speedWords.size]
    val elapsedSec = (30 - secondsLeft).coerceAtLeast(1)
    val liveWpm = ((wordsReadCount * 60) / elapsedSec).coerceIn(0, 120)

    LaunchedEffect(isRunning) {
        if (isRunning) {
            onSpeakText("Read each word out loud and tap 'I Read It' to test your 30 second reading speed!")
            while (secondsLeft > 0 && isRunning) {
                delay(1000)
                secondsLeft--
                // Only auto-advance if user explicitly enabled Auto-Voice Mic mode!
                if (isAutoVoiceMicOn && secondsLeft % 2 == 0 && secondsLeft > 0) {
                    wordsReadCount++
                    wordIdx++
                    starBurstTrigger++
                    onAwardStars(5)
                }
            }
            if (secondsLeft == 0) {
                isRunning = false
                isAutoVoiceMicOn = false
                if (wordsReadCount > 0) {
                    val achievedWpm = (wordsReadCount * 4).coerceIn(28, 110)
                    starBurstTrigger++
                    onAwardStars(25)
                    onUpdateWpm(achievedWpm)
                    onSpeakText("Time's up! You read $wordsReadCount words with a reading speed of $achievedWpm words per minute!")
                } else {
                    onSpeakText("Time's up! Tap Play Again and press 'I Read It' after reading each word.")
                }
            }
        }
    }

    val markCurrentWordRead: () -> Unit = {
        if (isRunning) {
            wordsReadCount++
            wordIdx++
            starBurstTrigger++
            onAwardStars(5)
            onSpeakText(speedWords[wordIdx % speedWords.size])
        }
    }

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
            // Top Bar (Back arrow on Left, Auto-Voice Mic Toggle + Star Counter Pill + Profile Avatar on Right)
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isRunning) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isAutoVoiceMicOn) SeekhGreen500 else Color.White.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, if (isAutoVoiceMicOn) SeekhGreen500 else SeekhSpeedBrown.copy(alpha = 0.3f)),
                            modifier = Modifier.bouncyClick {
                                isAutoVoiceMicOn = !isAutoVoiceMicOn
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAutoVoiceMicOn) Icons.Default.Mic else Icons.Default.MicNone,
                                    contentDescription = "Voice Mic",
                                    tint = if (isAutoVoiceMicOn) Color.White else SeekhSpeedBrown,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isAutoVoiceMicOn) "Voice: ON" else "Tap Mode",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isAutoVoiceMicOn) Color.White else SeekhSpeedBrown
                                )
                            }
                        }
                    }

                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = SeekhSpeedBrown,
                        textColor = Color.White
                    )
                    ProfileAvatarCircle(
                        avatarIndex = profile.avatarIndex,
                        size = 40.dp,
                        borderWidth = 2.dp,
                        borderColor = Color.White
                    )
                }
            }

            // Center Interactive Word Card & Fluency Controls
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isRunning) {
                        // Live Words Read & WPM Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhGreenSoft
                            ) {
                                Text(
                                    text = "Words Read: $wordsReadCount",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SeekhGreen600,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhBlueSoft
                            ) {
                                Text(
                                    text = "Pace: $liveWpm wpm",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SeekhBlue600,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Read out loud, then tap below:",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = SeekhTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tap word to mark read or hear pronunciation
                        Text(
                            text = currentWord,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 42.sp,
                            color = SeekhSpeedBrown,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.bouncyClick { markCurrentWordRead() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Clickable Phonetic Syllables + Hear Word Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = SeekhCatalog.getWordSyllables(currentWord),
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SeekhTextSecondary
                            )
                            Surface(
                                shape = CircleShape,
                                color = SeekhSurfaceGray,
                                modifier = Modifier
                                    .size(34.dp)
                                    .bouncyClick { onSpeakText(currentWord) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Hear word",
                                        tint = SeekhSpeedBrown,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Action Buttons: Skip Word vs "✓ I Read It! (+5 ⭐)"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (isRunning) {
                                        wordIdx++
                                        onSpeakText(speedWords[wordIdx % speedWords.size])
                                    }
                                },
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.5.dp, SeekhBorderGray)
                            ) {
                                Text(
                                    text = "Skip →",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = SeekhTextSecondary
                                )
                            }

                            Button(
                                onClick = { markCurrentWordRead() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "I Read It! (+5 ⭐)",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        val finalWpm = if (wordsReadCount == 0) 0 else (wordsReadCount * 4).coerceIn(28, 110)
                        val vehicle = SeekhCatalog.getVehicleForWpm(finalWpm)
                        Text(
                            text = if (wordsReadCount > 0) "Awesome Reading!" else "Round Finished",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = SeekhTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$wordsReadCount words read in 30 seconds",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = SeekhTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$finalWpm wpm",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 40.sp,
                            color = SeekhSpeedBrown
                        )
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SeekhGreenSoft,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${vehicle.emoji} ${vehicle.name}",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = SeekhGreen600
                                )
                                Text(
                                    text = vehicle.description,
                                    fontFamily = GoogleSansFamily,
                                    fontSize = 12.sp,
                                    color = SeekhTextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                secondsLeft = 30
                                wordsReadCount = 0
                                wordIdx++
                                isRunning = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                        ) {
                            Text("Play Again", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // Bottom Circular Countdown Timer
            Box(
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .size(82.dp)
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
                    fontSize = 21.sp,
                    color = SeekhSpeedBrown
                )
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)

        FloatingReadingBuddy(
            mood = if (isRunning) BuddyMood.LISTENING else buddyMood,
            speechBubbleText = if (isAutoVoiceMicOn) "Listening..." else null,
            onClick = { onSpeakText(currentWord) },
            size = 78.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// 5. "Word Meanings" Vocabulary Game Screen (9-Theme Selector Hub -> 2x2 Picture Match where word labels reveal on tap + Smart Hint + Zero Duplicate Star Farming!)
@Composable
fun VocabularyBuilderGameScreen(
    profile: LearnerProfile,
    selectedLanguageCode: String,
    buddyMood: BuddyMood,
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf<String?>(null) }
    var quizRound by remember(selectedTheme) { mutableIntStateOf(0) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    BackHandler {
        if (selectedTheme != null) {
            selectedTheme = null
        } else {
            onBack()
        }
    }

    // 1. 9-Theme Category Selector Hub
    if (selectedTheme == null) {
        LaunchedEffect(Unit) {
            onSpeakText("Word Meanings. Choose one of the 9 illustrated themes to start!")
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SeekhSurfaceGray)
        ) {
            CurvedGreenHeaderBackground(headerHeight = 195.dp, curveDepth = 30.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Word Meanings",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Pick an illustrated theme to play",
                            fontFamily = GoogleSansFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    StarCounterPill(
                        stars = profile.stars,
                        backgroundColor = Color.White.copy(alpha = 0.22f),
                        textColor = Color.White
                    )
                }

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
                        val count = remember(themeName) {
                            SeekhCatalog.vocabularyItems.count { it.category.equals(themeName, ignoreCase = true) }.coerceAtLeast(4)
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(142.dp)
                                .bouncyClick {
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
                                    modifier = Modifier.size(62.dp),
                                    targetPx = 192
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = themeName,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SeekhTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "$count words • +15 ⭐",
                                        fontFamily = GoogleSansFamily,
                                        fontSize = 11.sp,
                                        color = SeekhGreen600
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        return
    }

    // 2. Active "Word Meanings" Gameplay Screen
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
    var tappedWords by remember(quizRound, selectedTheme) { mutableStateOf(emptySet<String>()) }
    var isSolvedCorrectly by remember(quizRound, selectedTheme) { mutableStateOf(false) }

    LaunchedEffect(selectedTheme, quizRound) {
        onSpeakText("Find the picture for: ${quizTarget.word}. ${quizTarget.exampleSentence}")
    }

    LaunchedEffect(isSolvedCorrectly) {
        if (isSolvedCorrectly) {
            delay(1300)
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
                        modifier = Modifier.bouncyClick { selectedTheme = null }
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

            Text(
                text = "Tap the picture that matches the word",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.92f),
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
                // Target Word Prompt Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClick {
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
                                text = "Theme: $selectedTheme • Round ${(quizRound % themeWords.size) + 1}/${themeWords.size}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SeekhGreen600
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
                                maxLines = 2,
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

                // 2x2 Grid of 4 Illustrated SVG Choices (Word label stays hidden until tapped so it's a real vocabulary challenge!)
                val pairs = shuffledChoices.chunked(2)
                pairs.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { item ->
                            val wasTapped = tappedWords.contains(item.word)
                            val isCorrect = item.word == quizTarget.word
                            val borderColor = when {
                                !wasTapped -> SeekhBorderGray
                                isCorrect -> SeekhGreenMint
                                else -> SeekhCoralRed
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(175.dp)
                                    .bouncyClick {
                                        if (isSolvedCorrectly) return@bouncyClick
                                        tappedWords = tappedWords + item.word
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
                                color = when {
                                    wasTapped && isCorrect -> SeekhGreenSoft
                                    wasTapped && !isCorrect -> Color(0xFFFFF1F2)
                                    else -> Color.White
                                },
                                shadowElevation = 4.dp,
                                border = BorderStroke(if (wasTapped) 3.dp else 1.dp, borderColor)
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
                                        modifier = Modifier.size(104.dp),
                                        targetPx = 256
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (wasTapped || isSolvedCorrectly) {
                                        Text(
                                            text = if (isCorrect) "✓ ${item.word}" else "✗ ${item.word}",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = if (isCorrect) SeekhGreen600 else SeekhCoralRed
                                        )
                                    } else {
                                        Text(
                                            text = "Tap to choose",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = SeekhTextSecondary
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
            speechBubbleText = null,
            onClick = {
                onSpeakText("Tap the picture for ${quizTarget.word}!")
            },
            size = 78.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}
