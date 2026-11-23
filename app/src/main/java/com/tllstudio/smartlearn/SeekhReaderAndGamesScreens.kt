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

// Accent- & Phonetics-Aware Spoken Word Matcher for Live Microphone Reading:
// Matches the exact word spoken by the user (including common homophones, plural/tense stems, and 1-char phonetic variations) without ever advancing on noise or silence!
private fun isSpokenStoryWordMatch(spokenRaw: String, targetRaw: String): Boolean {
    val s = spokenRaw.lowercase().replace(Regex("[^a-z0-9]"), "")
    val t = targetRaw.lowercase().replace(Regex("[^a-z0-9]"), "")
    if (s.isEmpty() || t.isEmpty()) return false
    if (s == t) return true

    // Common sight-word & homophone equivalents in English speech recognition
    val homophones = mapOf(
        "a" to setOf("uh", "ah", "an", "the"),
        "the" to setOf("da", "tha", "thee", "a", "de"),
        "to" to setOf("too", "two", "2", "tu"),
        "for" to setOf("four", "4", "fur"),
        "our" to setOf("are", "hour"),
        "in" to setOf("inn", "en", "on"),
        "on" to setOf("in", "an"),
        "one" to setOf("1", "won"),
        "two" to setOf("2", "to", "too"),
        "three" to setOf("3", "tree"),
        "four" to setOf("4", "for"),
        "five" to setOf("5"),
        "eight" to setOf("8", "ate"),
        "hundred" to setOf("100"),
        "sun" to setOf("son"),
        "see" to setOf("sea", "c"),
        "red" to setOf("read"),
        "read" to setOf("red", "reed"),
        "here" to setOf("hear"),
        "there" to setOf("their", "theyre"),
        "right" to setOf("write"),
        "know" to setOf("no"),
        "by" to setOf("buy", "bye"),
        "lara" to setOf("laura", "lira", "larah"),
        "lulu" to setOf("lalu", "lou", "lolo"),
        "manto" to setOf("mento", "minto", "mantis"),
        "miya" to setOf("mia", "maya"),
        "sipho" to setOf("sifo", "sepho"),
        "sesa" to setOf("sisa", "ceca"),
        "bibi" to setOf("bb", "beebee", "baby")
    )
    if (homophones[t]?.contains(s) == true) return true

    // Singular/plural or common suffix stem match (e.g. "marigold" vs "marigolds", "walk" vs "walks")
    val sStem = s.removeSuffix("s").removeSuffix("es").removeSuffix("ed").removeSuffix("ing")
    val tStem = t.removeSuffix("s").removeSuffix("es").removeSuffix("ed").removeSuffix("ing")
    if (sStem.length >= 3 && sStem == tStem) return true

    // Prefix match for 4+ letter words as partial speech recognition streams in (e.g. "morn" -> "morning")
    if (t.length >= 4 && s.length >= 3 && (t.startsWith(s) || s.startsWith(t))) return true

    // Levenshtein edit distance <= 1 for 4-5 letter words, <= 2 for 6+ letter words
    if (t.length >= 4 && s.length >= 3 && kotlin.math.abs(s.length - t.length) <= 2) {
        val dp = Array(s.length + 1) { IntArray(t.length + 1) }
        for (i in 0..s.length) dp[i][0] = i
        for (j in 0..t.length) dp[0][j] = j
        for (i in 1..s.length) {
            for (j in 1..t.length) {
                val cost = if (s[i - 1] == t[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        val maxAllowedDist = if (t.length >= 6) 2 else 1
        if (dp[s.length][t.length] <= maxAllowedDist) return true
    }

    return false
}

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

    // Connect live Android TTS word-range boundaries & SpeechRecognizer callbacks to move the cursor word-by-word ONLY as words are actually spoken!
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

        // Tracks how many spoken tokens in the current SpeechRecognizer session have already been matched
        var consumedUtteranceTokens = 0

        voiceEngine?.onSpeechSessionResetListener = {
            consumedUtteranceTokens = 0
        }

        voiceEngine?.onRecognizedWordsListener = { spokenWords ->
            if (isMicActive && activeWords.isNotEmpty() && spokenWords.isNotEmpty()) {
                var currentCursor = pageReadWordCounts[activePgIdx] ?: 0
                val cleanSpoken = spokenWords.map { it.lowercase().replace(Regex("[^a-z0-9]"), "") }.filter { it.isNotEmpty() }
                if (cleanSpoken.isNotEmpty() && currentCursor < activeWords.size) {
                    val startIdx = if (cleanSpoken.size > consumedUtteranceTokens) {
                        consumedUtteranceTokens
                    } else {
                        (cleanSpoken.size - 1).coerceAtLeast(0)
                    }
                    for (tokenIdx in startIdx until cleanSpoken.size) {
                        if (currentCursor >= activeWords.size) break
                        val sw = cleanSpoken[tokenIdx]
                        val targetClean = activeWords[currentCursor].lowercase().replace(Regex("[^a-z0-9]"), "")
                        if (targetClean.isEmpty() || isSpokenStoryWordMatch(sw, targetClean)) {
                            currentCursor++
                            consumedUtteranceTokens = tokenIdx + 1
                        } else if (currentCursor + 1 < activeWords.size) {
                            // If a short sight word (like "a", "the", "in") was merged/skipped and the user spoke the next word:
                            val nextTargetClean = activeWords[currentCursor + 1].lowercase().replace(Regex("[^a-z0-9]"), "")
                            if (targetClean.length <= 3 && isSpokenStoryWordMatch(sw, nextTargetClean)) {
                                currentCursor += 2
                                consumedUtteranceTokens = tokenIdx + 1
                            }
                        }
                    }
                    if (currentCursor != (pageReadWordCounts[activePgIdx] ?: 0)) {
                        setPageReadWordCount(activePgIdx, currentCursor.coerceAtMost(activeWords.size))
                    }
                }
            }
        }

        voiceEngine?.onVoiceRmsPulseListener = null

        onDispose {
            voiceEngine?.onTtsWordRangeListener = null
            voiceEngine?.onSpeechSessionResetListener = null
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
    // Starts real microphone speech recognition — ZERO auto-timers! The pointer ONLY moves as you speak each word!
    LaunchedEffect(isMicActive, pageIndex) {
        val activePgIdx = pagerState.currentPage
        if (isMicActive && activePgIdx in 0 until book.pages.size) {
            isAutoReadingActive = false
            val activeWords = book.pages[activePgIdx].text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            if ((pageReadWordCounts[activePgIdx] ?: 0) >= activeWords.size) {
                setPageReadWordCount(activePgIdx, 0)
            }
            onStartListening {}
        }
    }

    // Auto-Next-Page Snapping when USER finishes speaking all words on the current page in "🎤 Speak" mode!
    val currentPageReadCount = pageReadWordCounts[pageIndex] ?: 0
    LaunchedEffect(isMicActive, pageIndex, currentPageReadCount) {
        val activePgIdx = pagerState.currentPage
        if (isMicActive && activePgIdx in 0 until book.pages.size && activePgIdx == pageIndex) {
            val activeWords = book.pages[activePgIdx].text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            if (activeWords.isNotEmpty() && currentPageReadCount >= activeWords.size) {
                awardPageStarsOnce(activePgIdx, 15)
                delay(360)
                if (isMicActive && pagerState.currentPage == activePgIdx) {
                    if (activePgIdx < book.pages.size - 1) {
                        pagerState.animateScrollToPage(activePgIdx + 1)
                    } else {
                        isMicActive = false
                        onStopListening()
                        pagerState.animateScrollToPage(book.pages.size)
                    }
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
                            text = SeekhCatalog.getBookPrimaryTitle(book, selectedLanguageCode),
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

            // FULL-SCREEN SNAPPING VERTICAL PAGER (Effortless low-threshold vertical swipe with zero nested scroll conflict!)
            VerticalPager(
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.14f,
                    snapAnimationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
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

                    // FULL-SCREEN PAGE CANVAS (No Card border, no nested verticalScroll stealing gestures, pure full-screen illustration + complete unclipped interactive words!)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 104.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top 36% of Full Screen: Clean Story Illustration
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.36f)
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom 64% of Full Screen: Interactive Alphabet Letter Tiles (if Alphabet Book) + 100% Complete Unclipped Interactive Story Text + Bilingual Subtitle!
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.64f),
                            verticalArrangement = Arrangement.Top
                        ) {
                            // Interactive Alphabet Letter Cards Banner for Basic Alphabets Books!
                            if (page.alphabetBadgeLetters.isNotBlank()) {
                                val letterTokens = remember(page.alphabetBadgeLetters) {
                                    page.alphabetBadgeLetters.split(Regex("\\s+")).filter { it.isNotBlank() && it != "•" }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    letterTokens.take(6).forEachIndexed { lIdx, ltrToken ->
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (lIdx % 2 == 0) SeekhGreenSoft else Color(0xFFFFF8E1),
                                            border = BorderStroke(
                                                1.5.dp,
                                                if (lIdx % 2 == 0) SeekhGreen500 else SeekhYellow500
                                            ),
                                            modifier = Modifier.bouncyClick {
                                                onPlayPhoneme(lIdx * 2)
                                                onSpeakText(ltrToken)
                                            }
                                        ) {
                                            Text(
                                                text = ltrToken,
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 24.sp,
                                                color = SeekhTextPrimary,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }

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

                            // Clean Bilingual Translation / Native Alphabet Line directly on full screen
                            val bilingualSentence = when {
                                book.bookType == "ALPHABET" -> page.urduTranslation.ifBlank { page.hindiTranslation.ifBlank { page.spanishTranslation } }
                                selectedLanguageCode.contains("ur") -> page.urduTranslation
                                selectedLanguageCode.contains("hi") || selectedLanguageCode.contains("mr") -> page.hindiTranslation
                                selectedLanguageCode.contains("es") -> page.spanishTranslation
                                else -> page.urduTranslation
                            }
                            if (bilingualSentence.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bilingualSentence,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = if (book.bookType == "ALPHABET") 17.5.sp else 15.5.sp,
                                    lineHeight = 24.sp,
                                    color = if (book.bookType == "ALPHABET") SeekhGreen600 else SeekhTextSecondary,
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
                                text = "Finish Story",
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
                                    text = "Practice Word",
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
        //   1. "Listen" -> Blue Speaker Button to listen to the story word-by-word
        //   2. "Speak" -> Green Microphone Button to speak into the mic yourself
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
                                isMicActive -> "Your turn to read aloud"
                                isAutoReadingActive -> "Listening to story"
                                else -> "Tap Listen to hear, or Speak to read"
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
                                text = if (isQuizSolved) "Done" else "Quiz",
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
                            val readerStrings = remember(selectedLanguageCode) {
                                SeekhCatalog.getAppStrings(selectedLanguageCode)
                            }
                            Column {
                                Text(
                                    text = if (isAutoReadingActive) readerStrings.listeningButton else readerStrings.listenButton,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp,
                                    color = if (isAutoReadingActive) Color.White else SeekhBlue600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = readerStrings.listenSubLabel,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    color = if (isAutoReadingActive) Color.White.copy(alpha = 0.88f) else SeekhBlue600.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                            val readerStrings = remember(selectedLanguageCode) {
                                SeekhCatalog.getAppStrings(selectedLanguageCode)
                            }
                            Column {
                                Text(
                                    text = if (isMicActive) readerStrings.speakingButton else readerStrings.speakButton,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp,
                                    color = if (isMicActive) Color.White else SeekhVocabDarkGreen,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = readerStrings.speakSubLabel,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    color = if (isMicActive) Color.White.copy(alpha = 0.88f) else SeekhVocabDarkGreen.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                            if (urduMeaning.isNotBlank() && !urduMeaning.any { it in 'A'..'Z' || it in 'a'..'z' }) {
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
                            text = "Correct! ${challenge.word.uppercase()}",
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
                                text = "Reset",
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
                            text = "Skip",
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
                            Text("Play Next 3 Words", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                                text = if (isHardLevel) "Switch to Easy Level" else "Try Hard Level",
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
            speechBubbleText = null,
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
        "S" to "Sun",
        "B" to "Ball",
        "M" to "Mango",
        "P" to "Parrot",
        "T" to "Train",
        "C" to "Cat"
    )
    val hardTargets = listOf(
        "SH" to "Ship",
        "CH" to "Chair",
        "TH" to "Thumb",
        "FL" to "Flower",
        "ST" to "Star",
        "TR" to "Tree"
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
            speechBubbleText = null,
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
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 92.dp),
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
                                text = "$selectedTheme • Question ${(quizRound % themeWords.size) + 1} of ${themeWords.size}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SeekhGreen600
                            )
                            Text(
                                text = quizTarget.word,
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
                                            text = item.word,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = if (isCorrect) SeekhGreen600 else SeekhCoralRed
                                        )
                                    } else {
                                        Text(
                                            text = "Select",
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

// Shared Clean End-of-Round Celebration Modal for All Learning Games
@Composable
private fun GameRoundSummaryModal(
    title: String,
    subtitle: String,
    accentColor: Color,
    onContinuePlaying: () -> Unit,
    onBackToLibrary: () -> Unit
) {
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
                    text = title,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = SeekhTextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = subtitle,
                    fontFamily = GoogleSansFamily,
                    fontSize = 14.sp,
                    color = SeekhTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onContinuePlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text(
                        text = "Play Next Round",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
                OutlinedButton(
                    onClick = onBackToLibrary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, accentColor)
                ) {
                    Text(
                        text = "Back to Library",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }
    }
}

// ============================================================================================
// 6. KIDS LEARNING GAME #5: SENTENCE BUILDER (Zero word-order spoilers + Scrollable + Round Summary)
// ============================================================================================
data class SentenceBuilderChallenge(
    val words: List<String>,
    val imageAssetPath: String,
    val urduTranslation: String,
    val hindiTranslation: String
)

private val sentenceBuilderChallenges = listOf(
    SentenceBuilderChallenge(
        words = listOf("Lara", "has", "bright", "yellow", "wings"),
        imageAssetPath = "books/sw_13916/image_3.webp",
        urduTranslation = "لارا کے چمکدار پیلے پر ہیں۔",
        hindiTranslation = "लारा के चमकीले पीले पंख हैं।"
    ),
    SentenceBuilderChallenge(
        words = listOf("The", "green", "frog", "jumps", "high"),
        imageAssetPath = "books/sw_37273/image_5.webp",
        urduTranslation = "ہرا مینڈک اونچی چھلانگ لگاتا ہے۔",
        hindiTranslation = "हरा मेंढक ऊँची छलांग लगाता है।"
    ),
    SentenceBuilderChallenge(
        words = listOf("I", "kick", "my", "big", "red", "ball"),
        imageAssetPath = "books/sw_15195/image_6.webp",
        urduTranslation = "میں اپنی بڑی سرخ گیند کو کک مارتا ہوں۔",
        hindiTranslation = "मैं अपनी बड़ी लाल गेंद को किक मारता हूँ।"
    ),
    SentenceBuilderChallenge(
        words = listOf("Nicole", "rides", "the", "silver", "train"),
        imageAssetPath = "books/sw_44485/image_4.webp",
        urduTranslation = "نکول چمکدار ٹرین میں سفر کرتی ہے۔",
        hindiTranslation = "निकोल चमकीली ट्रेन में सफ़र करती है।"
    ),
    SentenceBuilderChallenge(
        words = listOf("Lulu", "reads", "books", "in", "the", "library"),
        imageAssetPath = "books/sw_45764/cover_image.webp",
        urduTranslation = "لولو لائبریری میں کتابیں پڑھتی ہے۔",
        hindiTranslation = "लुलु लाइब्रेरी में किताबें पढ़ती है।"
    ),
    SentenceBuilderChallenge(
        words = listOf("My", "little", "sister", "is", "fast", "asleep"),
        imageAssetPath = "books/sw_15058/image_4.webp",
        urduTranslation = "میری چھوٹی بہن گہری نیند سو رہی ہے۔",
        hindiTranslation = "मेरी छोटी बहन गहरी नींद सो रही है।"
    ),
    SentenceBuilderChallenge(
        words = listOf("Spider", "wears", "eight", "tiny", "shoes"),
        imageAssetPath = "books/sw_37273/image_3.webp",
        urduTranslation = "مکڑی آٹھ ننھے جوتے پہنتی ہے۔",
        hindiTranslation = "मकड़ी आठ नन्हे जूते पहनती है।"
    ),
    SentenceBuilderChallenge(
        words = listOf("The", "drummers", "play", "a", "happy", "beat"),
        imageAssetPath = "books/sw_44485/image_11.webp",
        urduTranslation = "ڈھول بجانے والے خوشگوار دھن بجاتے ہیں۔",
        hindiTranslation = "ढोल वादक खुशहाल ताल बजाते हैं।"
    ),
    SentenceBuilderChallenge(
        words = listOf("Silver", "fish", "swim", "in", "the", "river"),
        imageAssetPath = "books/sw_45764/image_13.webp",
        urduTranslation = "چاندی جیسی مچھلیاں دریا میں تیرتی ہیں۔",
        hindiTranslation = "चमकीली मछलियाँ नदी में तैरती हैं।"
    ),
    SentenceBuilderChallenge(
        words = listOf("Stars", "twinkle", "in", "the", "night", "sky"),
        imageAssetPath = "books/sw_13916/image_19.webp",
        urduTranslation = "رات کے آسمان میں ستارے جگمگاتے ہیں۔",
        hindiTranslation = "रात के आसमान में तारे टिमटिमाते हैं।"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SentenceBuilderGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    selectedLanguageCode: String = "en_ur",
    onSpeakText: (String) -> Unit,
    onPlayPhoneme: (Int) -> Unit = {},
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    var solvedInSet by remember { mutableIntStateOf(0) }
    var showRoundCompleteModal by remember { mutableStateOf(false) }

    val challenge = sentenceBuilderChallenges[roundIdx % sentenceBuilderChallenges.size]
    val fullSentence = remember(challenge) { challenge.words.joinToString(" ") + "." }

    data class WordChip(val id: Int, val word: String)

    var placedWords by remember(roundIdx) { mutableStateOf(emptyList<WordChip>()) }
    var availableWords by remember(roundIdx) {
        val raw = challenge.words.mapIndexed { idx, w -> WordChip(idx, w) }
        var shuf = raw.shuffled()
        if (shuf.map { it.word } == challenge.words && raw.size > 1) {
            shuf = raw.reversed()
        }
        mutableStateOf(shuf)
    }
    var wrongTapFeedback by remember(roundIdx) { mutableStateOf<String?>(null) }
    var isSolved by remember(roundIdx) { mutableStateOf(false) }
    var starTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(roundIdx) {
        onSpeakText("Tap the words in order to build the sentence: $fullSentence")
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            kotlinx.coroutines.delay(1700)
            if (isSolved) {
                val nextCount = solvedInSet + 1
                if (nextCount >= 5) {
                    solvedInSet = 5
                    showRoundCompleteModal = true
                } else {
                    solvedInSet = nextCount
                    roundIdx++
                }
            }
        }
    }

    val headerBlue = Color(0xFF1E40AF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 195.dp,
            curveDepth = 32.dp,
            color = headerBlue
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                ThreeStepProgressIndicator(currentStep = solvedInSet.coerceIn(0, 4), totalSteps = 5)
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Text(
                text = "Sentence Builder",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Story Illustration & Bilingual Meaning Clue Card (Zero English word spoilers!)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(155.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            AssetWebpImage(
                                assetPath = challenge.imageAssetPath,
                                contentDescription = "Story scene",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguageCode.contains("hi")) challenge.hindiTranslation else challenge.urduTranslation,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = SeekhTextPrimary
                                )
                                Text(
                                    text = "${challenge.words.size} words • Tap Hear to listen",
                                    fontFamily = GoogleSansFamily,
                                    fontSize = 12.sp,
                                    color = SeekhTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhBlueSoft,
                                modifier = Modifier.bouncyClick {
                                    onSpeakText(fullSentence)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Hear sentence",
                                        tint = SeekhBlue600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Hear",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = SeekhBlue600
                                    )
                                }
                            }
                        }
                    }
                }

                // Sentence Construction Tray
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = if (isSolved) SeekhGreenSoft else Color.White,
                    border = BorderStroke(
                        2.dp,
                        when {
                            isSolved -> SeekhGreenMint
                            wrongTapFeedback != null -> SeekhCoralRed
                            else -> Color(0xFFBFDBFE)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 92.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (placedWords.isEmpty()) {
                            Text(
                                text = "Tap the words below in order to build the sentence",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = SeekhTextSecondary
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                placedWords.forEach { chip ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSolved) SeekhGreenMint else headerBlue,
                                        shadowElevation = 3.dp
                                    ) {
                                        Text(
                                            text = chip.word,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if (wrongTapFeedback != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = wrongTapFeedback!!,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = SeekhCoralRed
                            )
                        }
                    }
                }

                // Scrambled Word Chips Pool + Clean Hint Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose next word:",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SeekhTextPrimary
                    )
                    if (!isSolved && availableWords.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SeekhYellow500,
                            modifier = Modifier.bouncyClick {
                                val nextExpectedWord = challenge.words[placedWords.size]
                                val matchChip = availableWords.firstOrNull { it.word == nextExpectedWord }
                                if (matchChip != null) {
                                    wrongTapFeedback = null
                                    val newPlaced = placedWords + matchChip
                                    placedWords = newPlaced
                                    availableWords = availableWords.filterNot { it.id == matchChip.id }
                                    onSpeakText(matchChip.word)
                                    if (newPlaced.size == challenge.words.size) {
                                        isSolved = true
                                        starTrigger++
                                        onAwardStars(25)
                                        onSpeakText("Well done! $fullSentence")
                                    }
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Hint",
                                    tint = SeekhTextPrimary,
                                    modifier = Modifier.size(15.dp)
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

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    availableWords.forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.5.dp, headerBlue),
                            modifier = Modifier.bouncyClick {
                                if (isSolved) return@bouncyClick
                                val expectedWord = challenge.words[placedWords.size]
                                if (chip.word.equals(expectedWord, ignoreCase = true)) {
                                    wrongTapFeedback = null
                                    val newPlaced = placedWords + chip
                                    placedWords = newPlaced
                                    availableWords = availableWords.filterNot { it.id == chip.id }
                                    onPlayPhoneme(newPlaced.size)
                                    onSpeakText(chip.word)
                                    if (newPlaced.size == challenge.words.size) {
                                        isSolved = true
                                        starTrigger++
                                        onAwardStars(25)
                                        onSpeakText("Well done! $fullSentence")
                                    }
                                } else {
                                    wrongTapFeedback = "Try another word next!"
                                    onSpeakText("Listen to the sentence and try another word!")
                                }
                            }
                        ) {
                            Text(
                                text = chip.word,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = headerBlue,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showRoundCompleteModal) {
            GameRoundSummaryModal(
                title = "5 Sentences Completed!",
                subtitle = "Great job building complete English sentences!",
                accentColor = headerBlue,
                onContinuePlaying = {
                    showRoundCompleteModal = false
                    solvedInSet = 0
                    roundIdx++
                },
                onBackToLibrary = onBack
            )
        }

        FlyingStarsOverlay(triggerCount = starTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = { onSpeakText(fullSentence) },
            size = 76.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// ============================================================================================
// 7. KIDS LEARNING GAME #6: RHYMING WORDS MATCH (Zero Answer Spoilers + Story Illustrations)
// ============================================================================================
data class RhymeChallengeItem(
    val targetWord: String,
    val imageAssetPath: String,
    val rhymeFamily: String,
    val correctRhyme: String,
    val distractors: List<String>
)

private val rhymeChallenges = listOf(
    RhymeChallengeItem("TRAIN", "books/sw_44485/image_4.webp", "-AIN", "RAIN", listOf("BOOK", "FROG", "BALL")),
    RhymeChallengeItem("BALL", "books/sw_15195/image_2.webp", "-ALL", "TALL", listOf("FISH", "STAR", "SHOE")),
    RhymeChallengeItem("BRIGHT", "books/sw_13916/image_19.webp", "-IGHT", "NIGHT", listOf("GREEN", "CRAB", "SOUP")),
    RhymeChallengeItem("BOOK", "books/sw_15058/image_2.webp", "-OOK", "COOK", listOf("TREE", "CLOUD", "DRUM")),
    RhymeChallengeItem("FROG", "books/sw_37273/image_5.webp", "-OG", "LOG", listOf("SUN", "HAT", "BOAT")),
    RhymeChallengeItem("STAR", "books/sw_13916/image_20.webp", "-AR", "CAR", listOf("LEAF", "MOON", "CUP")),
    RhymeChallengeItem("BEE", "books/sw_13916/image_4.webp", "-EE", "TREE", listOf("RED", "SOCK", "FISH")),
    RhymeChallengeItem("SHOE", "books/sw_37273/image_3.webp", "-OE / -UE", "BLUE", listOf("CAT", "BED", "GATE")),
    RhymeChallengeItem("SUN", "books/sw_13916/image_1.webp", "-UN", "RUN", listOf("BOX", "BELL", "KITE")),
    RhymeChallengeItem("SAND", "books/sw_37273/image_9.webp", "-AND", "HAND", listOf("BIRD", "SHIP", "LAMP"))
)

@Composable
fun RhymingWordsGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    selectedLanguageCode: String = "en_ur",
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    var solvedInSet by remember { mutableIntStateOf(0) }
    var showRoundCompleteModal by remember { mutableStateOf(false) }

    val item = rhymeChallenges[roundIdx % rhymeChallenges.size]
    val choices = remember(roundIdx) {
        (listOf(item.correctRhyme) + item.distractors).shuffled()
    }
    var tappedWords by remember(roundIdx) { mutableStateOf(emptySet<String>()) }
    var isSolved by remember(roundIdx) { mutableStateOf(false) }
    var starTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(roundIdx) {
        onSpeakText("Which word rhymes with ${item.targetWord}?")
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            kotlinx.coroutines.delay(1600)
            if (isSolved) {
                val nextCount = solvedInSet + 1
                if (nextCount >= 5) {
                    solvedInSet = 5
                    showRoundCompleteModal = true
                } else {
                    solvedInSet = nextCount
                    roundIdx++
                }
            }
        }
    }

    val headerMagenta = Color(0xFFBE185D)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 195.dp,
            curveDepth = 32.dp,
            color = headerMagenta
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                ThreeStepProgressIndicator(currentStep = solvedInSet.coerceIn(0, 4), totalSteps = 5)
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Text(
                text = "Rhyming Words",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Target Word Card with Hand-Drawn Story Illustration
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClick {
                            onSpeakText("${item.targetWord}. Which word rhymes with ${item.targetWord}?")
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AssetWebpImage(
                                assetPath = item.imageAssetPath,
                                contentDescription = item.targetWord,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = item.targetWord,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = SeekhTextPrimary
                            )
                            Surface(
                                shape = CircleShape,
                                color = headerMagenta.copy(alpha = 0.14f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Hear word",
                                        tint = headerMagenta,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedLanguageCode.contains("hi")) {
                                "इस शब्द से मिलती-जुलती आवाज़ वाला शब्द चुनें"
                            } else {
                                "اس لفظ کے ہم قافیہ (ملتی جلتی آواز والا) لفظ کو چنیں"
                            },
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = SeekhTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 2x2 Grid of Clean Rhyming Word Choices
                choices.chunked(2).forEach { rowChoices ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowChoices.forEach { word ->
                            val tapped = tappedWords.contains(word)
                            val isCorrect = word == item.correctRhyme
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(104.dp)
                                    .bouncyClick {
                                        if (isSolved) return@bouncyClick
                                        tappedWords = tappedWords + word
                                        if (isCorrect) {
                                            isSolved = true
                                            starTrigger++
                                            onAwardStars(25)
                                            onSpeakText("Great job! ${item.targetWord} and $word rhyme!")
                                        } else {
                                            onSpeakText("$word. Try finding a word that sounds like ${item.targetWord}!")
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    tapped && isCorrect -> SeekhGreenMint
                                    tapped && !isCorrect -> Color(0xFFFFF1F2)
                                    else -> Color.White
                                },
                                shadowElevation = 4.dp,
                                border = BorderStroke(
                                    if (tapped) 2.5.dp else 1.5.dp,
                                    when {
                                        tapped && isCorrect -> SeekhGreenMint
                                        tapped && !isCorrect -> SeekhCoralRed
                                        else -> SeekhBorderGray
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = word,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        color = when {
                                            tapped && isCorrect -> Color.White
                                            tapped && !isCorrect -> SeekhCoralRed
                                            else -> SeekhTextPrimary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRoundCompleteModal) {
            GameRoundSummaryModal(
                title = "5 Rhymes Matched!",
                subtitle = "You mastered 5 rhyming word pairs!",
                accentColor = headerMagenta,
                onContinuePlaying = {
                    showRoundCompleteModal = false
                    solvedInSet = 0
                    roundIdx++
                },
                onBackToLibrary = onBack
            )
        }

        FlyingStarsOverlay(triggerCount = starTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = { onSpeakText("Find the word that rhymes with ${item.targetWord}!") },
            size = 76.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// ============================================================================================
// 8. KIDS LEARNING GAME #7: MISSING LETTERS & SOUNDS (Zero English word spoilers!)
// ============================================================================================
data class MissingPhonicsChallenge(
    val fullWord: String,
    val prefix: String,
    val missingSound: String,
    val suffix: String,
    val options: List<String>,
    val imageAssetPath: String,
    val urduMeaning: String,
    val hindiMeaning: String
)

private val missingPhonicsChallenges = listOf(
    MissingPhonicsChallenge("FROG", "FR", "O", "G", listOf("O", "A", "E", "U"), "books/sw_37273/image_5.webp", "تالاب کا مینڈک", "तालाब का मेंढक"),
    MissingPhonicsChallenge("BALL", "B", "A", "LL", listOf("A", "E", "I", "U"), "books/sw_15195/image_2.webp", "کھیلنے کی گیند", "खेलने की गेंद"),
    MissingPhonicsChallenge("FISH", "F", "I", "SH", listOf("I", "A", "O", "U"), "books/sw_45764/image_9.webp", "پانی کی مچھلی", "पानी की मछली"),
    MissingPhonicsChallenge("STAR", "ST", "A", "R", listOf("A", "E", "I", "O"), "books/sw_13916/image_19.webp", "آسمان کا ستارہ", "आसमान का तारा"),
    MissingPhonicsChallenge("YELLOW", "Y", "E", "LLOW", listOf("E", "A", "I", "U"), "books/sw_13916/image_2.webp", "پیلا رنگ", "पीला रंग"),
    MissingPhonicsChallenge("SPIDER", "SP", "I", "DER", listOf("I", "A", "O", "U"), "books/sw_13916/image_6.webp", "جالا بننے والی مکڑی", "जाला बुनने वाली मकड़ी"),
    MissingPhonicsChallenge("BUTTERFLY", "B", "U", "TTERFLY", listOf("U", "A", "E", "O"), "books/sw_37273/image_14.webp", "خوبصورت تتلی", "सुंदर तितली"),
    MissingPhonicsChallenge("TRAIN", "TR", "AI", "N", listOf("AI", "OO", "EA", "OU"), "books/sw_44485/image_4.webp", "ریل گاڑی", "रेलगाड़ी"),
    MissingPhonicsChallenge("CLOUDS", "CL", "OU", "DS", listOf("OU", "AI", "EE", "OA"), "books/sw_45764/image_3.webp", "آسمان کے بادل", "आसमान के बादल"),
    MissingPhonicsChallenge("SCHOOL", "SCH", "OO", "L", listOf("OO", "AI", "OU", "IE"), "books/sw_13916/image_7.webp", "پڑھنے کا اسکول", "पढ़ने का स्कूल"),
    MissingPhonicsChallenge("BOOKS", "B", "OO", "KS", listOf("OO", "AI", "OU", "EA"), "books/sw_15058/image_2.webp", "کہانیوں کی کتابیں", "कहानियों की किताबें"),
    MissingPhonicsChallenge("BEACH", "B", "EA", "CH", listOf("EA", "OO", "OU", "AI"), "books/sw_44485/image_16.webp", "ساحلِ سمندر", "समुद्र तट")
)

@Composable
fun MissingPhonicsSoundGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    selectedLanguageCode: String = "en_ur",
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    var solvedInSet by remember { mutableIntStateOf(0) }
    var showRoundCompleteModal by remember { mutableStateOf(false) }

    val item = missingPhonicsChallenges[roundIdx % missingPhonicsChallenges.size]
    val shuffledOptions = remember(roundIdx) { item.options.shuffled() }
    var tappedOptions by remember(roundIdx) { mutableStateOf(emptySet<String>()) }
    var isSolved by remember(roundIdx) { mutableStateOf(false) }
    var starTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(roundIdx) {
        onSpeakText("Find the missing letters to complete the word: ${item.fullWord}!")
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            kotlinx.coroutines.delay(1600)
            if (isSolved) {
                val nextCount = solvedInSet + 1
                if (nextCount >= 5) {
                    solvedInSet = 5
                    showRoundCompleteModal = true
                } else {
                    solvedInSet = nextCount
                    roundIdx++
                }
            }
        }
    }

    val headerTeal = Color(0xFF0F766E)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 195.dp,
            curveDepth = 32.dp,
            color = headerTeal
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                ThreeStepProgressIndicator(currentStep = solvedInSet.coerceIn(0, 4), totalSteps = 5)
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Text(
                text = "Missing Letters",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClick { onSpeakText(item.fullWord) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            AssetWebpImage(
                                assetPath = item.imageAssetPath,
                                contentDescription = "Word picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Word with Missing Letter Box (NEVER reveals the full English word until solved!)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.prefix,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = SeekhTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSolved) SeekhGreenMint else SeekhYellow500.copy(alpha = 0.25f),
                                border = BorderStroke(2.dp, if (isSolved) SeekhGreenMint else headerTeal),
                                modifier = Modifier.padding(horizontal = 5.dp)
                            ) {
                                Text(
                                    text = if (isSolved) item.missingSound else "_".repeat(item.missingSound.length),
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    color = if (isSolved) Color.White else headerTeal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = item.suffix,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = SeekhTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedLanguageCode.contains("hi")) item.hindiMeaning else item.urduMeaning,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SeekhGreen600
                        )
                    }
                }

                // 2x2 Grid of Missing Letter Tiles
                shuffledOptions.chunked(2).forEach { rowOpts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowOpts.forEach { opt ->
                            val tapped = tappedOptions.contains(opt)
                            val isCorrect = opt == item.missingSound
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(86.dp)
                                    .bouncyClick {
                                        if (isSolved) return@bouncyClick
                                        tappedOptions = tappedOptions + opt
                                        if (isCorrect) {
                                            isSolved = true
                                            starTrigger++
                                            onAwardStars(20)
                                            onSpeakText("Correct! $opt completes the word ${item.fullWord}!")
                                        } else {
                                            onSpeakText("Try another letter for ${item.fullWord}!")
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    tapped && isCorrect -> SeekhGreenMint
                                    tapped && !isCorrect -> Color(0xFFFFF1F2)
                                    else -> Color.White
                                },
                                shadowElevation = 4.dp,
                                border = BorderStroke(
                                    2.dp,
                                    when {
                                        tapped && isCorrect -> SeekhGreenMint
                                        tapped && !isCorrect -> SeekhCoralRed
                                        else -> headerTeal
                                    }
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = opt,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp,
                                        color = when {
                                            tapped && isCorrect -> Color.White
                                            tapped && !isCorrect -> SeekhCoralRed
                                            else -> headerTeal
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRoundCompleteModal) {
            GameRoundSummaryModal(
                title = "5 Words Completed!",
                subtitle = "You found all the missing letters!",
                accentColor = headerTeal,
                onContinuePlaying = {
                    showRoundCompleteModal = false
                    solvedInSet = 0
                    roundIdx++
                },
                onBackToLibrary = onBack
            )
        }

        FlyingStarsOverlay(triggerCount = starTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = { onSpeakText("Tap the missing letters to spell ${item.fullWord}!") },
            size = 76.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// ============================================================================================
// 9. KIDS LEARNING GAME #8: LISTEN & PICK (Zero English spoilers + Scrollable + Round Summary)
// ============================================================================================
data class ListenPickChallenge(
    val word: String,
    val spokenClue: String,
    val imageAssetPath: String,
    val urduLabel: String,
    val hindiLabel: String,
    val distractors: List<Triple<String, String, String>>
)

private val listenPickChallenges = listOf(
    ListenPickChallenge(
        word = "Ladybird",
        spokenClue = "Ladybird. Lara is a cute yellow ladybird sitting on a green leaf.",
        imageAssetPath = "books/sw_13916/image_2.webp",
        urduLabel = "پیلی لیڈی برڈ",
        hindiLabel = "पीली लेडीबर्ड",
        distractors = listOf(
            Triple("Train", "books/sw_44485/image_4.webp", "ٹرین"),
            Triple("Frog", "books/sw_37273/image_5.webp", "مینڈک"),
            Triple("Red Ball", "books/sw_15195/image_2.webp", "سرخ گیند")
        )
    ),
    ListenPickChallenge(
        word = "Drummers",
        spokenClue = "Drummers. The cheerful street drummers wear red and yellow shirts.",
        imageAssetPath = "books/sw_44485/image_11.webp",
        urduLabel = "ڈھول بجانے والے",
        hindiLabel = "ढोल वादक",
        distractors = listOf(
            Triple("Spider", "books/sw_13916/image_6.webp", "مکڑی"),
            Triple("Cocoon", "books/sw_37273/image_12.webp", "کوکون"),
            Triple("Library", "books/sw_45764/image_1.webp", "لائبریری")
        )
    ),
    ListenPickChallenge(
        word = "Butterfly",
        spokenClue = "Butterfly. Caterpillar transforms into a colorful butterfly with bright wings.",
        imageAssetPath = "books/sw_37273/image_14.webp",
        urduLabel = "خوبصورت تتلی",
        hindiLabel = "सुंदर तितली",
        distractors = listOf(
            Triple("Cupboard", "books/sw_45764/image_5.webp", "الماری"),
            Triple("Karate", "books/sw_44485/image_8.webp", "کراٹے"),
            Triple("Kitchen", "books/sw_15058/image_6.webp", "باورچی خانہ")
        )
    ),
    ListenPickChallenge(
        word = "Silver Fish",
        spokenClue = "Silver Fish. Lulu and her friends rescue the shiny silver fish in buckets.",
        imageAssetPath = "books/sw_45764/image_12.webp",
        urduLabel = "چمکدار مچھلیاں",
        hindiLabel = "चमकीली मछलियाँ",
        distractors = listOf(
            Triple("Moon", "books/sw_15195/image_17.webp", "چاند"),
            Triple("Grandfather", "books/sw_15058/image_8.webp", "دادا جان"),
            Triple("Bug School", "books/sw_13916/image_7.webp", "اسکول")
        )
    ),
    ListenPickChallenge(
        word = "Navy Sailor",
        spokenClue = "Navy Sailor. Captain Zara wears a crisp white navy sailor uniform and cap.",
        imageAssetPath = "books/sw_44485/image_10.webp",
        urduLabel = "بحریہ کی ملاح",
        hindiLabel = "नौसेना की नाविक",
        distractors = listOf(
            Triple("Crab", "books/sw_37273/image_9.webp", "کیکڑا"),
            Triple("Raincoat", "books/sw_15195/image_4.webp", "برساتی کوٹ"),
            Triple("Bubble Bath", "books/sw_13916/image_16.webp", "نہانا")
        )
    ),
    ListenPickChallenge(
        word = "Chocolates",
        spokenClue = "Chocolates. Golden chocolates and sweet treats shine in the corner shop.",
        imageAssetPath = "books/sw_44485/image_6.webp",
        urduLabel = "میٹھی چاکلیٹ",
        hindiLabel = "मीठी चॉकलेट",
        distractors = listOf(
            Triple("Grasshopper", "books/sw_37273/image_7.webp", "ٹڈا"),
            Triple("Sleeping Sister", "books/sw_15058/image_4.webp", "سوتی بہن"),
            Triple("Candles", "books/sw_45764/image_2.webp", "موم بتیاں")
        )
    ),
    ListenPickChallenge(
        word = "Full Moon",
        spokenClue = "Full Moon. The red ball flies high in the night sky over the glowing moon.",
        imageAssetPath = "books/sw_15195/image_17.webp",
        urduLabel = "چمکتا چاند",
        hindiLabel = "चमकता चाँद",
        distractors = listOf(
            Triple("Kitchen Soup", "books/sw_15058/image_6.webp", "گرم سوپ"),
            Triple("Frog's Boot", "books/sw_37273/image_6.webp", "مینڈک کا جوتا"),
            Triple("Curtain", "books/sw_45764/image_4.webp", "کھڑکی کا پردہ")
        )
    ),
    ListenPickChallenge(
        word = "Storybooks",
        spokenClue = "Storybooks. I love reading colorful storybooks every single day.",
        imageAssetPath = "books/sw_15058/image_2.webp",
        urduLabel = "کہانیوں کی کتابیں",
        hindiLabel = "कहानियों की किताबें",
        distractors = listOf(
            Triple("Red Crab", "books/sw_37273/image_10.webp", "سرخ کیکڑا"),
            Triple("Karate Kick", "books/sw_44485/image_9.webp", "کراٹے کک"),
            Triple("Storm Clouds", "books/sw_45764/image_3.webp", "طوفانی بادل")
        )
    )
)

@Composable
fun ListenAndPickGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    selectedLanguageCode: String = "en_ur",
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    var solvedInSet by remember { mutableIntStateOf(0) }
    var showRoundCompleteModal by remember { mutableStateOf(false) }

    val item = listenPickChallenges[roundIdx % listenPickChallenges.size]
    val cards = remember(roundIdx) {
        (listOf(Triple(item.word, item.imageAssetPath, if (selectedLanguageCode.contains("hi")) item.hindiLabel else item.urduLabel)) + item.distractors).shuffled()
    }
    var tappedWords by remember(roundIdx) { mutableStateOf(emptySet<String>()) }
    var isSolved by remember(roundIdx) { mutableStateOf(false) }
    var starTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(roundIdx) {
        onSpeakText("Listen carefully and tap the matching picture: ${item.spokenClue}")
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            kotlinx.coroutines.delay(1600)
            if (isSolved) {
                val nextCount = solvedInSet + 1
                if (nextCount >= 4) {
                    solvedInSet = 4
                    showRoundCompleteModal = true
                } else {
                    solvedInSet = nextCount
                    roundIdx++
                }
            }
        }
    }

    val headerIndigo = Color(0xFF4338CA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 195.dp,
            curveDepth = 32.dp,
            color = headerIndigo
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                ThreeStepProgressIndicator(currentStep = solvedInSet.coerceIn(0, 3), totalSteps = 4)
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Text(
                text = "Listen & Find",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Audio Speaker Prompt Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isSolved) {
                                "${item.word} • ${if (selectedLanguageCode.contains("hi")) item.hindiLabel else item.urduLabel}"
                            } else {
                                "Listen to the voice clue and select the matching picture"
                            },
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.5.sp,
                            color = if (isSolved) SeekhGreen600 else SeekhTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = headerIndigo,
                                modifier = Modifier.bouncyClick {
                                    onSpeakText(item.spokenClue)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Hear clue",
                                        tint = Color.White,
                                        modifier = Modifier.size(19.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hear Clue",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.5.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhYellow500,
                                modifier = Modifier.bouncyClick {
                                    val spelled = item.word.toCharArray().joinToString(" ")
                                    onSpeakText("${item.word}. Spelled: $spelled.")
                                }
                            ) {
                                Text(
                                    text = "Spell Word",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = SeekhTextPrimary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // 2x2 Grid of 4 Hand-Drawn Story Illustration Cards
                cards.chunked(2).forEach { rowCards ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCards.forEach { (cardWord, assetPath, nativeLbl) ->
                            val tapped = tappedWords.contains(cardWord)
                            val isCorrect = cardWord == item.word
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(175.dp)
                                    .bouncyClick {
                                        if (isSolved) return@bouncyClick
                                        tappedWords = tappedWords + cardWord
                                        if (isCorrect) {
                                            isSolved = true
                                            starTrigger++
                                            onAwardStars(25)
                                            onSpeakText("Wonderful! That is ${item.word}!")
                                        } else {
                                            onSpeakText("That picture is $cardWord. Listen again for ${item.word}!")
                                        }
                                    },
                                shape = RoundedCornerShape(22.dp),
                                color = when {
                                    tapped && isCorrect -> SeekhGreenSoft
                                    tapped && !isCorrect -> Color(0xFFFFF1F2)
                                    else -> Color.White
                                },
                                shadowElevation = 4.dp,
                                border = BorderStroke(
                                    if (tapped) 3.dp else 1.5.dp,
                                    when {
                                        tapped && isCorrect -> SeekhGreenMint
                                        tapped && !isCorrect -> SeekhCoralRed
                                        else -> SeekhBorderGray
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                    ) {
                                        AssetWebpImage(
                                            assetPath = assetPath,
                                            contentDescription = cardWord,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (tapped || isSolved) {
                                            if (isCorrect) "$cardWord ($nativeLbl)" else cardWord
                                        } else {
                                            "Select"
                                        },
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.5.sp,
                                        color = when {
                                            tapped && isCorrect -> SeekhGreen600
                                            tapped && !isCorrect -> SeekhCoralRed
                                            else -> SeekhTextSecondary
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRoundCompleteModal) {
            GameRoundSummaryModal(
                title = "4 Audio Clues Solved!",
                subtitle = "Great listening and picture matching!",
                accentColor = headerIndigo,
                onContinuePlaying = {
                    showRoundCompleteModal = false
                    solvedInSet = 0
                    roundIdx++
                },
                onBackToLibrary = onBack
            )
        }

        FlyingStarsOverlay(triggerCount = starTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = { onSpeakText(item.spokenClue) },
            size = 76.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// ============================================================================================
// 10. KIDS LEARNING GAME #9: OPPOSITE WORDS (Zero Spoilers + Story Illustrations)
// ============================================================================================
data class OppositePairItem(
    val baseWord: String,
    val imageAssetPath: String,
    val baseUrdu: String,
    val baseHindi: String,
    val oppositeWord: String,
    val oppositeUrdu: String,
    val oppositeHindi: String,
    val distractors: List<String>
)

private val oppositePairChallenges = listOf(
    OppositePairItem("BIG", "books/sw_37273/image_6.webp", "بڑا", "बड़ा", "SMALL", "چھوٹا", "छोटा", listOf("LOUD", "WET", "FAST")),
    OppositePairItem("DAY", "books/sw_13916/image_1.webp", "دن", "दिन", "NIGHT", "رات", "रात", listOf("TALL", "FULL", "OPEN")),
    OppositePairItem("HAPPY", "books/sw_13916/image_17.webp", "خوش", "ख़ुश", "SAD", "اداس", "उदास", listOf("COLD", "SHORT", "DRY")),
    OppositePairItem("HOT", "books/sw_15058/image_6.webp", "گرم", "गर्म", "COLD", "ٹھنڈا", "ठंडा", listOf("UP", "CLEAN", "SLOW")),
    OppositePairItem("FAST", "books/sw_44485/image_4.webp", "تیز", "तेज़", "SLOW", "آہستہ", "धीमा", listOf("EMPTY", "CLOSED", "SMALL")),
    OppositePairItem("UP", "books/sw_15195/image_14.webp", "اوپر", "ऊपर", "DOWN", "نیچے", "नीचे", listOf("NIGHT", "QUIET", "COLD")),
    OppositePairItem("OPEN", "books/sw_15058/image_2.webp", "کھلا ہوا", "खुला", "CLOSED", "بند", "बंद", listOf("SAD", "DRY", "SHORT")),
    OppositePairItem("WET", "books/sw_45764/image_9.webp", "گیلا", "गीला", "DRY", "خشک", "सूखा", listOf("SLOW", "DOWN", "SMALL")),
    OppositePairItem("TALL", "books/sw_15195/image_13.webp", "لمبا", "लंबा", "SHORT", "چھوٹا", "छोटा कद", listOf("COLD", "EMPTY", "NIGHT")),
    OppositePairItem("FULL", "books/sw_45764/image_11.webp", "بھرا ہوا", "भरा हुआ", "EMPTY", "خالی", "ख़ाली", listOf("SAD", "SLOW", "CLOSED"))
)

@Composable
fun OppositesQuestGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    selectedLanguageCode: String = "en_ur",
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    var solvedInSet by remember { mutableIntStateOf(0) }
    var showRoundCompleteModal by remember { mutableStateOf(false) }

    val item = oppositePairChallenges[roundIdx % oppositePairChallenges.size]
    val choices = remember(roundIdx) {
        (listOf(item.oppositeWord) + item.distractors).shuffled()
    }
    var tappedWords by remember(roundIdx) { mutableStateOf(emptySet<String>()) }
    var isSolved by remember(roundIdx) { mutableStateOf(false) }
    var starTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(roundIdx) {
        onSpeakText("What is the opposite of ${item.baseWord}?")
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            kotlinx.coroutines.delay(1600)
            if (isSolved) {
                val nextCount = solvedInSet + 1
                if (nextCount >= 5) {
                    solvedInSet = 5
                    showRoundCompleteModal = true
                } else {
                    solvedInSet = nextCount
                    roundIdx++
                }
            }
        }
    }

    val headerOrange = Color(0xFFD97706)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 195.dp,
            curveDepth = 32.dp,
            color = headerOrange
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                ThreeStepProgressIndicator(currentStep = solvedInSet.coerceIn(0, 4), totalSteps = 5)
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Text(
                text = "Opposite Words",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClick {
                            onSpeakText("What is the opposite of ${item.baseWord}?")
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AssetWebpImage(
                                assetPath = item.imageAssetPath,
                                contentDescription = item.baseWord,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Find the opposite of:",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SeekhTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = item.baseWord,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = SeekhTextPrimary
                            )
                            Text(
                                text = "(${if (selectedLanguageCode.contains("hi")) item.baseHindi else item.baseUrdu})",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = headerOrange
                            )
                        }
                        if (isSolved) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhGreenSoft
                            ) {
                                Text(
                                    text = "${item.baseWord} / ${item.oppositeWord} (${if (selectedLanguageCode.contains("hi")) item.oppositeHindi else item.oppositeUrdu})",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = SeekhGreen600,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 2x2 Grid of Clean Opposite Choices
                choices.chunked(2).forEach { rowChoices ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowChoices.forEach { word ->
                            val tapped = tappedWords.contains(word)
                            val isCorrect = word == item.oppositeWord
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(104.dp)
                                    .bouncyClick {
                                        if (isSolved) return@bouncyClick
                                        tappedWords = tappedWords + word
                                        if (isCorrect) {
                                            isSolved = true
                                            starTrigger++
                                            onAwardStars(25)
                                            onSpeakText("Correct! The opposite of ${item.baseWord} is ${item.oppositeWord}!")
                                        } else {
                                            onSpeakText("Not quite! Try finding the opposite of ${item.baseWord}.")
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    tapped && isCorrect -> SeekhGreenMint
                                    tapped && !isCorrect -> Color(0xFFFFF1F2)
                                    else -> Color.White
                                },
                                shadowElevation = 4.dp,
                                border = BorderStroke(
                                    if (tapped) 2.5.dp else 1.5.dp,
                                    when {
                                        tapped && isCorrect -> SeekhGreenMint
                                        tapped && !isCorrect -> SeekhCoralRed
                                        else -> SeekhBorderGray
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = word,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        color = when {
                                            tapped && isCorrect -> Color.White
                                            tapped && !isCorrect -> SeekhCoralRed
                                            else -> SeekhTextPrimary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRoundCompleteModal) {
            GameRoundSummaryModal(
                title = "5 Opposites Mastered!",
                subtitle = "You matched 5 pairs of opposite words!",
                accentColor = headerOrange,
                onContinuePlaying = {
                    showRoundCompleteModal = false
                    solvedInSet = 0
                    roundIdx++
                },
                onBackToLibrary = onBack
            )
        }

        FlyingStarsOverlay(triggerCount = starTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = { onSpeakText("What is the opposite of ${item.baseWord}?") },
            size = 76.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}

// ============================================================================================
// 11. KIDS LEARNING GAME #10: COUNT, NUMBERS & MATH (Custom Vector Shapes + Round Summary)
// ============================================================================================
enum class CountTokenType {
    LADYBIRD, RED_BALL, STAR, LEAF, RAINDROP, BOOK
}

@Composable
private fun CountTokenVectorIcon(
    tokenType: CountTokenType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(34.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = minOf(w, h) * 0.42f
        when (tokenType) {
            CountTokenType.LADYBIRD -> {
                drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.42f, center = Offset(cx, cy - r * 0.62f))
                drawCircle(color = Color(0xFFFFB800), radius = r, center = Offset(cx, cy + r * 0.1f))
                drawLine(color = Color(0xFF1E1B4B), start = Offset(cx, cy - r * 0.85f), end = Offset(cx, cy + r * 1.05f), strokeWidth = 2.dp.toPx())
                drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.18f, center = Offset(cx - r * 0.42f, cy))
                drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.18f, center = Offset(cx + r * 0.42f, cy))
                drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.16f, center = Offset(cx - r * 0.35f, cy + r * 0.5f))
                drawCircle(color = Color(0xFF1E1B4B), radius = r * 0.16f, center = Offset(cx + r * 0.35f, cy + r * 0.5f))
            }
            CountTokenType.RED_BALL -> {
                drawCircle(color = Color(0xFFE53935), radius = r, center = Offset(cx, cy))
                drawCircle(color = Color.White.copy(alpha = 0.45f), radius = r * 0.28f, center = Offset(cx - r * 0.32f, cy - r * 0.32f))
            }
            CountTokenType.STAR -> {
                val path = Path()
                for (i in 0 until 10) {
                    val angle = -Math.PI / 2.0 + i * (Math.PI / 5.0)
                    val rad = if (i % 2 == 0) r else r * 0.45f
                    val x = cx + (rad * kotlin.math.cos(angle)).toFloat()
                    val y = cy + (rad * kotlin.math.sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, color = Color(0xFFF59E0B))
            }
            CountTokenType.LEAF -> {
                drawOval(
                    color = Color(0xFF10B981),
                    topLeft = Offset(cx - r * 0.7f, cy - r),
                    size = Size(r * 1.4f, r * 2f)
                )
                drawLine(color = Color(0xFF047857), start = Offset(cx, cy - r * 0.8f), end = Offset(cx, cy + r), strokeWidth = 2.dp.toPx())
            }
            CountTokenType.RAINDROP -> {
                drawCircle(color = Color(0xFF0284C7), radius = r * 0.75f, center = Offset(cx, cy + r * 0.2f))
                val topTriangle = Path().apply {
                    moveTo(cx, cy - r * 0.95f)
                    lineTo(cx - r * 0.72f, cy + r * 0.1f)
                    lineTo(cx + r * 0.72f, cy + r * 0.1f)
                    close()
                }
                drawPath(topTriangle, color = Color(0xFF0284C7))
            }
            CountTokenType.BOOK -> {
                drawRoundRect(
                    color = Color(0xFF4F46E5),
                    topLeft = Offset(cx - r * 0.85f, cy - r * 0.95f),
                    size = Size(r * 1.7f, r * 1.9f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.85f),
                    topLeft = Offset(cx - r * 0.55f, cy - r * 0.55f),
                    size = Size(r * 1.1f, r * 0.35f)
                )
            }
        }
    }
}

data class CountAndColorChallenge(
    val questionEn: String,
    val questionUr: String,
    val questionHi: String,
    val tokenType: CountTokenType,
    val firstGroupCount: Int,
    val secondGroupCount: Int = 0,
    val correctNumber: String,
    val correctWord: String,
    val options: List<Pair<String, String>>,
    val spokenCountOut: String
)

private val countAndColorChallenges = listOf(
    CountAndColorChallenge(
        questionEn = "Count the yellow ladybirds in Lara's garden",
        questionUr = "باغ میں موجود پیلی لیڈی برڈز گنیں",
        questionHi = "बग़ीचे में मौजूद पीली लेडीबर्ड्स गिनें",
        tokenType = CountTokenType.LADYBIRD,
        firstGroupCount = 5,
        correctNumber = "5",
        correctWord = "Five",
        options = listOf("5" to "Five", "3" to "Three", "7" to "Seven", "9" to "Nine"),
        spokenCountOut = "One, two, three, four, five! Five yellow ladybirds!"
    ),
    CountAndColorChallenge(
        questionEn = "How many bouncy red balls are on the lawn?",
        questionUr = "لان میں کتنی سرخ گیندیں ہیں؟",
        questionHi = "घास पर कितनी लाल गेंदें हैं?",
        tokenType = CountTokenType.RED_BALL,
        firstGroupCount = 7,
        correctNumber = "7",
        correctWord = "Seven",
        options = listOf("7" to "Seven", "4" to "Four", "6" to "Six", "10" to "Ten"),
        spokenCountOut = "One, two, three, four, five, six, seven! Seven red balls!"
    ),
    CountAndColorChallenge(
        questionEn = "Add 4 green leaves and 4 more green leaves",
        questionUr = "4 ہرے پتے اور مزید 4 ہرے پتے ملا کر کل کتنے ہوئے؟",
        questionHi = "4 हरे पत्ते और 4 हरे पत्ते मिलाकर कुल कितने हुए?",
        tokenType = CountTokenType.LEAF,
        firstGroupCount = 4,
        secondGroupCount = 4,
        correctNumber = "8",
        correctWord = "Eight",
        options = listOf("8" to "Eight", "6" to "Six", "5" to "Five", "12" to "Twelve"),
        spokenCountOut = "Four plus four equals eight! Eight green leaves!"
    ),
    CountAndColorChallenge(
        questionEn = "Count the blue raindrops falling from the cloud",
        questionUr = "بادل سے گرتی نیلی بوندیں گنیں",
        questionHi = "बादल से गिरती नीली बूँदें गिनें",
        tokenType = CountTokenType.RAINDROP,
        firstGroupCount = 6,
        correctNumber = "6",
        correctWord = "Six",
        options = listOf("6" to "Six", "3" to "Three", "8" to "Eight", "11" to "Eleven"),
        spokenCountOut = "One, two, three, four, five, six! Six blue raindrops!"
    ),
    CountAndColorChallenge(
        questionEn = "Count the golden stars shining in the night sky",
        questionUr = "رات کے آسمان میں چمکتے سنہری ستارے گنیں",
        questionHi = "रात के आसमान में चमकते सुनहरे तारे गिनें",
        tokenType = CountTokenType.STAR,
        firstGroupCount = 9,
        correctNumber = "9",
        correctWord = "Nine",
        options = listOf("9" to "Nine", "6" to "Six", "8" to "Eight", "10" to "Ten"),
        spokenCountOut = "One, two, three, four, five, six, seven, eight, nine! Nine golden stars!"
    ),
    CountAndColorChallenge(
        questionEn = "Lulu borrowed 4 books and then 6 more books. How many in total?",
        questionUr = "لولو نے 4 کتابیں اور پھر 6 مزید کتابیں لیں۔ کل کتنی کتابیں ہوئیں؟",
        questionHi = "लुलु ने 4 किताबें और फिर 6 और किताबें लीं। कुल कितनी किताबें हुईं?",
        tokenType = CountTokenType.BOOK,
        firstGroupCount = 4,
        secondGroupCount = 6,
        correctNumber = "10",
        correctWord = "Ten",
        options = listOf("10" to "Ten", "8" to "Eight", "9" to "Nine", "12" to "Twelve"),
        spokenCountOut = "Four plus six equals ten! Ten library books!"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountAndColorsGameScreen(
    profile: LearnerProfile,
    buddyMood: BuddyMood,
    selectedLanguageCode: String = "en_ur",
    onSpeakText: (String) -> Unit,
    onAwardStars: (Int) -> Unit,
    onBack: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    var solvedInSet by remember { mutableIntStateOf(0) }
    var showRoundCompleteModal by remember { mutableStateOf(false) }

    val item = countAndColorChallenges[roundIdx % countAndColorChallenges.size]
    val shuffledOptions = remember(roundIdx) { item.options.shuffled() }
    var tappedOptions by remember(roundIdx) { mutableStateOf(emptySet<String>()) }
    var isSolved by remember(roundIdx) { mutableStateOf(false) }
    var starTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(roundIdx) {
        onSpeakText(item.questionEn)
    }

    LaunchedEffect(isSolved) {
        if (isSolved) {
            kotlinx.coroutines.delay(1700)
            if (isSolved) {
                val nextCount = solvedInSet + 1
                if (nextCount >= 4) {
                    solvedInSet = 4
                    showRoundCompleteModal = true
                } else {
                    solvedInSet = nextCount
                    roundIdx++
                }
            }
        }
    }

    val headerEmerald = Color(0xFF047857)
    val totalCount = item.firstGroupCount + item.secondGroupCount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(
            headerHeight = 195.dp,
            curveDepth = 32.dp,
            color = headerEmerald
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                ThreeStepProgressIndicator(currentStep = solvedInSet.coerceIn(0, 3), totalSteps = 4)
                StarCounterPill(
                    stars = profile.stars,
                    backgroundColor = Color.White.copy(alpha = 0.22f),
                    textColor = Color.White
                )
            }

            Text(
                text = "Count & Numbers",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClick { onSpeakText(item.questionEn) }
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.questionEn,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = SeekhTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedLanguageCode.contains("hi")) item.questionHi else item.questionUr,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SeekhGreen600,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Custom Vector Objects to Tap & Count!
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = SeekhSurfaceGray,
                            border = BorderStroke(1.5.dp, Color(0xFFD1FAE5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (idx in 0 until totalCount) {
                                    if (item.secondGroupCount > 0 && idx == item.firstGroupCount) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 26.sp,
                                                color = headerEmerald
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.White,
                                        shadowElevation = 2.dp,
                                        modifier = Modifier.bouncyClick {
                                            onSpeakText("${idx + 1}")
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CountTokenVectorIcon(tokenType = item.tokenType)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap any item above to count out loud",
                            fontFamily = GoogleSansFamily,
                            fontSize = 12.sp,
                            color = SeekhTextSecondary
                        )
                    }
                }

                // 2x2 Clean Number Answer Grid
                shuffledOptions.chunked(2).forEach { rowOpts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowOpts.forEach { (numStr, wordStr) ->
                            val tapped = tappedOptions.contains(numStr)
                            val isCorrect = numStr == item.correctNumber
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(94.dp)
                                    .bouncyClick {
                                        if (isSolved) return@bouncyClick
                                        tappedOptions = tappedOptions + numStr
                                        if (isCorrect) {
                                            isSolved = true
                                            starTrigger++
                                            onAwardStars(30)
                                            onSpeakText("Correct! ${item.spokenCountOut}")
                                        } else {
                                            onSpeakText("Let's count again carefully!")
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    tapped && isCorrect -> SeekhGreenMint
                                    tapped && !isCorrect -> Color(0xFFFFF1F2)
                                    else -> Color.White
                                },
                                shadowElevation = 4.dp,
                                border = BorderStroke(
                                    2.dp,
                                    when {
                                        tapped && isCorrect -> SeekhGreenMint
                                        tapped && !isCorrect -> SeekhCoralRed
                                        else -> headerEmerald
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = numStr,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 26.sp,
                                        color = when {
                                            tapped && isCorrect -> Color.White
                                            tapped && !isCorrect -> SeekhCoralRed
                                            else -> headerEmerald
                                        }
                                    )
                                    Text(
                                        text = wordStr,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = when {
                                            tapped && isCorrect -> Color.White.copy(alpha = 0.92f)
                                            tapped && !isCorrect -> SeekhCoralRed
                                            else -> SeekhTextSecondary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showRoundCompleteModal) {
            GameRoundSummaryModal(
                title = "4 Math Puzzles Solved!",
                subtitle = "Awesome counting and number skills!",
                accentColor = headerEmerald,
                onContinuePlaying = {
                    showRoundCompleteModal = false
                    solvedInSet = 0
                    roundIdx++
                },
                onBackToLibrary = onBack
            )
        }

        FlyingStarsOverlay(triggerCount = starTrigger)

        FloatingReadingBuddy(
            mood = buddyMood,
            speechBubbleText = null,
            onClick = { onSpeakText(item.questionEn) },
            size = 76.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 10.dp, bottom = 8.dp)
        )
    }
}
