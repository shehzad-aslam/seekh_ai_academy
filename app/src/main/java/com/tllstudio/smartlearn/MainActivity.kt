package com.tllstudio.smartlearn

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

enum class AppScreen {
    PRIVACY_SPLASH,
    LANGUAGE_SELECTION,
    BUDDY_INTRO,
    PROFILE_SWITCHER,
    MAIN_HOME,
    STORY_READER,
    GAME_JUMBLE,
    GAME_BALLOON,
    GAME_SPEED,
    GAME_VOCAB
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: SeekhRepository
    private lateinit var voiceEngine: SeekhVoiceAndAudioEngine
    private var buddyMoodState by mutableStateOf(BuddyMood.IDLE)
    private var deepLinkScreen by mutableStateOf<String?>(null)

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = SeekhRepository(this)
        voiceEngine = SeekhVoiceAndAudioEngine(
            context = this,
            onMoodChanged = { mood -> buddyMoodState = mood }
        )

        deepLinkScreen = intent?.getStringExtra("screen")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            try {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } catch (_: Exception) {
            }
        }

        setContent {
            SeekhAIAcademyTheme {
                SeekhRootApp(
                    repository = repository,
                    voiceEngine = voiceEngine,
                    buddyMood = buddyMoodState,
                    externalScreenCommand = deepLinkScreen,
                    onConsumeExternalCommand = { deepLinkScreen = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkScreen = intent.getStringExtra("screen")
    }

    override fun onDestroy() {
        voiceEngine.release()
        super.onDestroy()
    }
}

@Composable
fun SeekhRootApp(
    repository: SeekhRepository,
    voiceEngine: SeekhVoiceAndAudioEngine,
    buddyMood: BuddyMood,
    externalScreenCommand: String?,
    onConsumeExternalCommand: () -> Unit
) {
    var profiles by remember { mutableStateOf(repository.loadProfiles()) }
    var activeProfileId by remember { mutableStateOf(repository.getActiveProfileId()) }
    val activeProfile = remember(profiles, activeProfileId) {
        profiles.firstOrNull { it.id == activeProfileId }
            ?: profiles.firstOrNull()
            ?: LearnerProfile(
                id = "temp_uncreated",
                name = "Reader",
                avatarIndex = 1,
                stars = 0,
                minutesReadToday = 0,
                dailyGoalMinutes = 10,
                streakDays = 0,
                wordsPerMinute = 0,
                completedBookIds = emptySet(),
                weeklyMinutes = listOf(0, 0, 0, 0, 0, 0, 0),
                bookPageProgress = emptyMap()
            )
    }

    var selectedLangCode by remember { mutableStateOf(repository.getSelectedLanguageCode()) }
    val selectedLanguage = remember(selectedLangCode) {
        SeekhCatalog.supportedLanguages.firstOrNull { it.code == selectedLangCode }
            ?: SeekhCatalog.supportedLanguages.first()
    }

    var customBooks by remember { mutableStateOf(repository.loadCustomStories()) }
    var blockedBookIds by remember { mutableStateOf(repository.getBlockedBookIds()) }
    val allBooks = remember(customBooks, blockedBookIds) {
        (SeekhCatalog.defaultBooks + customBooks).filterNot { blockedBookIds.contains(it.id) }
    }

    var accountState by remember { mutableStateOf(repository.loadAccountState()) }
    var turnedInAssignmentIds by remember { mutableStateOf(repository.getTurnedInAssignmentIds()) }
    var openedGiftBoxIds by remember { mutableStateOf(repository.getOpenedGiftBoxIds()) }
    var downloadedCloudBookIds by remember { mutableStateOf(repository.getDownloadedCloudBookIds()) }
    var downloadingBookIds by remember { mutableStateOf(emptySet<String>()) }

    var readingGroups by remember { mutableStateOf(repository.loadReadingGroups()) }
    var partnerCodes by remember { mutableStateOf(repository.getPartnerCodes()) }
    var isSoundEnabled by remember { mutableStateOf(voiceEngine.isSoundEnabled) }

    var currentScreen by remember {
        mutableStateOf(
            when {
                !repository.isOnboardingCompleted() -> AppScreen.PRIVACY_SPLASH
                profiles.isEmpty() -> AppScreen.PROFILE_SWITCHER
                else -> AppScreen.MAIN_HOME
            }
        )
    }
    val screenHistory = remember { mutableStateListOf<AppScreen>() }

    var isStandalonePrivacyView by remember { mutableStateOf(false) }
    var isLanguageFromDrawer by remember { mutableStateOf(false) }
    var selectedMainTab by remember { mutableStateOf(MainTab.LIBRARY) }
    var activeBook by remember {
        mutableStateOf(
            SeekhCatalog.defaultBooks.firstOrNull { it.id == "book_rained_fish" }
                ?: SeekhCatalog.defaultBooks.first()
        )
    }
    var forcedInitialPageIndex by remember { mutableStateOf<Int?>(null) }

    var showAiStoryDialog by remember { mutableStateOf(false) }
    var showPartnerCodesDialog by remember { mutableStateOf(false) }
    var showDeleteHistoryDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAccountMenuDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun navigateTo(target: AppScreen, replaceCurrent: Boolean = false) {
        voiceEngine.playTapSound()
        if (!replaceCurrent && currentScreen != target) {
            screenHistory.add(currentScreen)
        }
        currentScreen = target
    }

    fun navigateBack() {
        voiceEngine.stopSpeaking()
        voiceEngine.stopVoiceListening()
        voiceEngine.playTapSound()
        if (screenHistory.isNotEmpty()) {
            val prev = screenHistory.removeAt(screenHistory.lastIndex)
            currentScreen = prev
        } else {
            currentScreen = when (currentScreen) {
                AppScreen.PROFILE_SWITCHER -> {
                    if (repository.isOnboardingCompleted()) AppScreen.MAIN_HOME else AppScreen.BUDDY_INTRO
                }
                AppScreen.BUDDY_INTRO -> AppScreen.LANGUAGE_SELECTION
                AppScreen.LANGUAGE_SELECTION -> {
                    if (isLanguageFromDrawer) AppScreen.MAIN_HOME else AppScreen.PRIVACY_SPLASH
                }
                else -> AppScreen.MAIN_HOME
            }
        }
    }

    fun updateActiveProfile(transform: (LearnerProfile) -> LearnerProfile) {
        if (profiles.isEmpty()) return
        val updatedList = profiles.map { p ->
            if (p.id == activeProfile.id) transform(p) else p
        }
        profiles = updatedList
        repository.saveProfiles(updatedList)
    }

    fun recordReadingTimeAndProgress(
        addedSeconds: Int,
        addedStars: Int = 0,
        incrementGames: Boolean = false
    ) {
        if (profiles.isEmpty()) return
        val todayIdx = repository.getTodayDayOfWeekIndex()
        val todayKey = repository.getTodayDateKey()
        updateActiveProfile { p ->
            val newSecs = p.readingSecondsToday + addedSeconds
            val calcMinFromSecs = newSecs / 60
            val newMinToday = maxOf(p.minutesReadToday, calcMinFromSecs)
            val newWeek = p.weeklyMinutes.toMutableList().apply {
                while (size < 7) add(0)
                this[todayIdx] = newMinToday
            }
            val newStreak = when {
                newMinToday >= p.dailyGoalMinutes && p.minutesReadToday < p.dailyGoalMinutes -> p.streakDays + 1
                newMinToday > 0 && p.streakDays == 0 -> 1
                else -> p.streakDays
            }
            p.copy(
                stars = p.stars + addedStars,
                readingSecondsToday = newSecs,
                minutesReadToday = newMinToday,
                weeklyMinutes = newWeek,
                streakDays = newStreak,
                gamesPlayedCount = p.gamesPlayedCount + (if (incrementGames) 1 else 0),
                lastActiveDateKey = todayKey
            )
        }
    }

    fun awardStarsToActiveProfile(amount: Int, isFromGame: Boolean = false) {
        voiceEngine.playSuccessChime()
        recordReadingTimeAndProgress(
            addedSeconds = if (isFromGame) 35 else 15,
            addedStars = amount,
            incrementGames = isFromGame
        )
    }

    // Real-time active learning timer: while the user is inside Story Reader or any Game, accumulate real reading seconds!
    LaunchedEffect(currentScreen, activeProfile.id) {
        val isLearningScreen = currentScreen == AppScreen.STORY_READER ||
            currentScreen == AppScreen.GAME_JUMBLE ||
            currentScreen == AppScreen.GAME_BALLOON ||
            currentScreen == AppScreen.GAME_SPEED ||
            currentScreen == AppScreen.GAME_VOCAB
        if (isLearningScreen && profiles.isNotEmpty()) {
            while (true) {
                delay(15000)
                recordReadingTimeAndProgress(addedSeconds = 15)
            }
        }
    }

    // Handle external intent commands for direct screen navigation
    LaunchedEffect(externalScreenCommand) {
        when (externalScreenCommand?.uppercase()) {
            "PRIVACY" -> {
                isStandalonePrivacyView = true
                screenHistory.clear()
                currentScreen = AppScreen.PRIVACY_SPLASH
                onConsumeExternalCommand()
            }
            "LANGUAGE" -> {
                isLanguageFromDrawer = true
                screenHistory.clear()
                currentScreen = AppScreen.LANGUAGE_SELECTION
                onConsumeExternalCommand()
            }
            "INTRO" -> {
                screenHistory.clear()
                currentScreen = AppScreen.BUDDY_INTRO
                onConsumeExternalCommand()
            }
            "PROFILES_EMPTY" -> {
                repository.clearUsageHistory()
                profiles = emptyList()
                activeProfileId = ""
                screenHistory.clear()
                currentScreen = AppScreen.PROFILE_SWITCHER
                onConsumeExternalCommand()
            }
            "PROFILES" -> {
                screenHistory.clear()
                currentScreen = AppScreen.PROFILE_SWITCHER
                onConsumeExternalCommand()
            }
            "LIBRARY" -> {
                scope.launch { drawerState.close() }
                screenHistory.clear()
                selectedMainTab = MainTab.LIBRARY
                currentScreen = AppScreen.MAIN_HOME
                onConsumeExternalCommand()
            }
            "PRIZES" -> {
                scope.launch { drawerState.close() }
                screenHistory.clear()
                selectedMainTab = MainTab.PRIZES
                currentScreen = AppScreen.MAIN_HOME
                onConsumeExternalCommand()
            }
            "ACTIVITY" -> {
                scope.launch { drawerState.close() }
                screenHistory.clear()
                selectedMainTab = MainTab.ACTIVITY
                currentScreen = AppScreen.MAIN_HOME
                onConsumeExternalCommand()
            }
            "DRAWER" -> {
                screenHistory.clear()
                selectedMainTab = MainTab.LIBRARY
                currentScreen = AppScreen.MAIN_HOME
                scope.launch { drawerState.open() }
                onConsumeExternalCommand()
            }
            "READER" -> {
                activeBook = allBooks.firstOrNull { it.id == "book_rained_fish" } ?: allBooks.first()
                forcedInitialPageIndex = 0
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.STORY_READER
                onConsumeExternalCommand()
            }
            "READER_LARA" -> {
                activeBook = allBooks.firstOrNull { it.id == "sw_13916" } ?: allBooks.first()
                forcedInitialPageIndex = 0
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.STORY_READER
                onConsumeExternalCommand()
            }
            "READER_QUIZ" -> {
                val b = allBooks.firstOrNull { it.id == "sw_13916" } ?: allBooks.first()
                activeBook = b
                forcedInitialPageIndex = (b.pages.size - 1).coerceAtLeast(0)
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.STORY_READER
                onConsumeExternalCommand()
            }
            "JUMBLE" -> {
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.GAME_JUMBLE
                onConsumeExternalCommand()
            }
            "BALLOON" -> {
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.GAME_BALLOON
                onConsumeExternalCommand()
            }
            "SPEED" -> {
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.GAME_SPEED
                onConsumeExternalCommand()
            }
            "VOCAB" -> {
                screenHistory.clear()
                screenHistory.add(AppScreen.MAIN_HOME)
                currentScreen = AppScreen.GAME_VOCAB
                onConsumeExternalCommand()
            }
            "AI_STORY" -> {
                screenHistory.clear()
                selectedMainTab = MainTab.LIBRARY
                currentScreen = AppScreen.MAIN_HOME
                showAiStoryDialog = true
                onConsumeExternalCommand()
            }
        }
    }

    fun triggerCloudBookDownload(book: StoryBook) {
        if (downloadingBookIds.contains(book.id)) return
        downloadingBookIds = downloadingBookIds + book.id
        voiceEngine.playTapSound()
        voiceEngine.speak("Downloading ${book.title} for offline reading...")
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val conn = URL("https://dl.google.com/seekh/v2/books/en").openConnection() as HttpURLConnection
                    conn.connectTimeout = 2500
                    conn.readTimeout = 2500
                    conn.requestMethod = "HEAD"
                    conn.responseCode
                    conn.disconnect()
                } catch (_: Exception) {
                }
                delay(1100)
            }
            repository.markCloudBookDownloaded(book.id)
            downloadedCloudBookIds = repository.getDownloadedCloudBookIds()
            downloadingBookIds = downloadingBookIds - book.id
            voiceEngine.playSuccessChime()
            voiceEngine.speak("${book.title} downloaded! Tap to read now.")
        }
    }

    // Main Home BackHandler: closes open Navigation Drawer first, then returns from Trophies/Progress tab to Stories tab!
    BackHandler(
        enabled = currentScreen == AppScreen.MAIN_HOME && (drawerState.isOpen || selectedMainTab != MainTab.LIBRARY)
    ) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (selectedMainTab != MainTab.LIBRARY) {
            voiceEngine.playTapSound()
            selectedMainTab = MainTab.LIBRARY
        }
    }

    // Fallback global BackHandler for any non-home screen
    BackHandler(enabled = currentScreen != AppScreen.MAIN_HOME) {
        navigateBack()
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            (fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.96f, animationSpec = tween(320, easing = FastOutSlowInEasing)))
                .togetherWith(
                    fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing))
                )
        },
        label = "root_screen_anim"
    ) { screen ->
        when (screen) {
            AppScreen.PRIVACY_SPLASH -> {
                PrivacyShieldSplashScreen(
                    isStandaloneView = isStandalonePrivacyView,
                    onContinue = {
                        if (isStandalonePrivacyView) {
                            isStandalonePrivacyView = false
                            navigateBack()
                        } else {
                            navigateTo(AppScreen.LANGUAGE_SELECTION)
                        }
                    }
                )
            }

            AppScreen.LANGUAGE_SELECTION -> {
                LanguageSelectionScreen(
                    selectedLangCode = selectedLangCode,
                    buddyMood = buddyMood,
                    onSelectLanguage = { lang ->
                        selectedLangCode = lang.code
                        repository.setSelectedLanguageCode(lang.code)
                        voiceEngine.playTapSound()
                        voiceEngine.speak("Selected ${lang.title}. Let's read stories together!")
                    },
                    onContinue = {
                        if (isLanguageFromDrawer) {
                            isLanguageFromDrawer = false
                            navigateBack()
                        } else {
                            navigateTo(AppScreen.BUDDY_INTRO)
                        }
                    },
                    onBack = {
                        isLanguageFromDrawer = false
                        navigateBack()
                    }
                )
            }

            AppScreen.BUDDY_INTRO -> {
                InteractiveBuddyIntroScreen(
                    buddyMood = buddyMood,
                    onSpeakText = { voiceEngine.speak(it) },
                    onPracticeListen = { onDone ->
                        voiceEngine.startVoiceListening(onFallbackTick = onDone)
                        awardStarsToActiveProfile(50)
                    },
                    onFinishOnboarding = {
                        repository.setOnboardingCompleted(true)
                        navigateTo(AppScreen.PROFILE_SWITCHER)
                    },
                    onBack = {
                        navigateBack()
                    }
                )
            }

            AppScreen.PROFILE_SWITCHER -> {
                ProfileSwitcherScreen(
                    profiles = profiles,
                    activeProfileId = activeProfile.id,
                    buddyMood = buddyMood,
                    accountState = accountState,
                    onUpdateAccountState = { updatedAcc ->
                        accountState = updatedAcc
                        repository.saveAccountState(updatedAcc)
                        voiceEngine.playSuccessChime()
                        if (updatedAcc.isSignedIn) {
                            voiceEngine.speak("Signed in as ${updatedAcc.email}. Profiles are synced.")
                        } else {
                            voiceEngine.speak("Using app without an account.")
                        }
                    },
                    onSelectProfile = { chosen ->
                        activeProfileId = chosen.id
                        repository.setActiveProfileId(chosen.id)
                        repository.setOnboardingCompleted(true)
                        voiceEngine.playSuccessChime()
                        voiceEngine.speak("Welcome back, ${chosen.name}! Let's continue your reading journey.")
                        screenHistory.clear()
                        currentScreen = AppScreen.MAIN_HOME
                    },
                    onCreateProfile = { newName, avatarIdx, goalMins ->
                        val newProf = LearnerProfile(
                            id = "prof_${System.currentTimeMillis()}",
                            name = newName,
                            avatarIndex = avatarIdx,
                            stars = 0,
                            minutesReadToday = 0,
                            dailyGoalMinutes = goalMins,
                            streakDays = 0,
                            wordsPerMinute = 0,
                            completedBookIds = emptySet(),
                            weeklyMinutes = listOf(0, 0, 0, 0, 0, 0, 0),
                            bookPageProgress = emptyMap(),
                            gamesPlayedCount = 0,
                            readingSecondsToday = 0,
                            lastActiveDateKey = repository.getTodayDateKey()
                        )
                        val updated = profiles + newProf
                        profiles = updated
                        repository.saveProfiles(updated)
                        activeProfileId = newProf.id
                        repository.setActiveProfileId(newProf.id)
                        repository.setOnboardingCompleted(true)
                        voiceEngine.playSuccessChime()
                        voiceEngine.speak("Welcome, $newName! Your profile is ready. Let's read your first story!")
                        screenHistory.clear()
                        currentScreen = AppScreen.MAIN_HOME
                    },
                    onEditProfile = { targetProf, newName, newAvatar ->
                        val updated = profiles.map { p ->
                            if (p.id == targetProf.id) p.copy(name = newName, avatarIndex = newAvatar) else p
                        }
                        profiles = updated
                        repository.saveProfiles(updated)
                        voiceEngine.playSuccessChime()
                        voiceEngine.speak("Profile updated to $newName!")
                    },
                    onDeleteProfile = { targetProf ->
                        val updated = profiles.filterNot { it.id == targetProf.id }
                        profiles = updated
                        repository.saveProfiles(updated)
                        if (activeProfileId == targetProf.id) {
                            val nextId = updated.firstOrNull()?.id ?: ""
                            activeProfileId = nextId
                            repository.setActiveProfileId(nextId)
                        }
                        voiceEngine.playTapSound()
                        voiceEngine.speak("Deleted profile ${targetProf.name}.")
                    },
                    onOpenParentsSection = {
                        selectedMainTab = MainTab.ACTIVITY
                        screenHistory.clear()
                        currentScreen = AppScreen.MAIN_HOME
                    },
                    onBuddyClick = {
                        voiceEngine.speak("Create or select your profile to track your reading progress!")
                    },
                    onBack = {
                        if (profiles.isNotEmpty()) {
                            navigateBack()
                        }
                    }
                )
            }

            AppScreen.MAIN_HOME -> {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        SeekhNavigationDrawerContent(
                            profile = activeProfile,
                            selectedLanguage = selectedLanguage,
                            accountState = accountState,
                            blockedBookCount = blockedBookIds.size,
                            isSoundEnabled = isSoundEnabled,
                            onToggleSound = {
                                val next = !isSoundEnabled
                                isSoundEnabled = next
                                voiceEngine.isSoundEnabled = next
                                if (next) {
                                    voiceEngine.playSuccessChime()
                                    voiceEngine.speak("Voice tutor and sound effects enabled!")
                                } else {
                                    voiceEngine.stopSpeaking()
                                }
                            },
                            onSwitchProfileClick = {
                                scope.launch { drawerState.close() }
                                navigateTo(AppScreen.PROFILE_SWITCHER)
                            },
                            onEditProfileClick = {
                                showEditProfileDialog = true
                            },
                            onAccountMenuClick = {
                                showAccountMenuDialog = true
                            },
                            onAiStoryStudioClick = {
                                showAiStoryDialog = true
                            },
                            onUnblockBooksClick = {
                                repository.unblockAllBooks()
                                blockedBookIds = repository.getBlockedBookIds()
                                voiceEngine.playSuccessChime()
                                voiceEngine.speak("All blocked stories have been restored to the library!")
                            },
                            onLanguageSettingsClick = {
                                isLanguageFromDrawer = true
                                navigateTo(AppScreen.LANGUAGE_SELECTION)
                            },
                            onReadingGroupsClick = {
                                selectedMainTab = MainTab.ACTIVITY
                            },
                            onPartnerCodesClick = {
                                showPartnerCodesDialog = true
                            },
                            onPrivacyShieldClick = {
                                isStandalonePrivacyView = true
                                navigateTo(AppScreen.PRIVACY_SPLASH)
                            },
                            onDeleteHistoryClick = {
                                showDeleteHistoryDialog = true
                            },
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedMainTab) {
                            MainTab.LIBRARY -> {
                                LibraryTabScreen(
                                    books = allBooks,
                                    profile = activeProfile,
                                    selectedLanguage = selectedLanguage,
                                    accountState = accountState,
                                    turnedInAssignmentIds = turnedInAssignmentIds,
                                    downloadedCloudBookIds = downloadedCloudBookIds,
                                    downloadingBookIds = downloadingBookIds,
                                    onDownloadCloudBook = { book -> triggerCloudBookDownload(book) },
                                    onDeleteCustomBook = { book ->
                                        repository.deleteCustomStory(book.id)
                                        customBooks = repository.loadCustomStories()
                                        voiceEngine.playSuccessChime()
                                        voiceEngine.speak("Deleted ${book.title} from your stories.")
                                    },
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navigateTo(AppScreen.PROFILE_SWITCHER) },
                                    onStarClick = {
                                        voiceEngine.playTapSound()
                                        selectedMainTab = MainTab.PRIZES
                                    },
                                    onOpenBook = { book ->
                                        activeBook = book
                                        forcedInitialPageIndex = null
                                        voiceEngine.speak("Let's read ${book.title}!")
                                        navigateTo(AppScreen.STORY_READER)
                                    },
                                    onOpenGame = { gameType ->
                                        val targetGame = when (gameType) {
                                            "JUMBLE" -> AppScreen.GAME_JUMBLE
                                            "BALLOON" -> AppScreen.GAME_BALLOON
                                            "SPEED" -> AppScreen.GAME_SPEED
                                            else -> AppScreen.GAME_VOCAB
                                        }
                                        navigateTo(targetGame)
                                    },
                                    onOpenAiStoryCreator = {
                                        voiceEngine.playTapSound()
                                        showAiStoryDialog = true
                                    },
                                    onOpenLanguageSettings = {
                                        isLanguageFromDrawer = true
                                        navigateTo(AppScreen.LANGUAGE_SELECTION)
                                    },
                                    onSpeakText = { voiceEngine.speak(it) }
                                )
                            }

                            MainTab.PRIZES -> {
                                PrizesTabScreen(
                                    profile = activeProfile,
                                    openedGiftBoxIds = openedGiftBoxIds,
                                    onOpenGiftBox = { boxId ->
                                        if (!openedGiftBoxIds.contains(boxId)) {
                                            repository.markGiftBoxOpened(boxId)
                                            openedGiftBoxIds = repository.getOpenedGiftBoxIds()
                                            awardStarsToActiveProfile(50)
                                        } else {
                                            voiceEngine.playSuccessChime()
                                        }
                                    },
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navigateTo(AppScreen.PROFILE_SWITCHER) },
                                    onStarClick = {
                                        voiceEngine.playSuccessChime()
                                        voiceEngine.speak("You have collected ${activeProfile.stars} golden stars!")
                                    },
                                    onSpeakText = { voiceEngine.speak(it) }
                                )
                            }

                            MainTab.ACTIVITY -> {
                                ActivityTabScreen(
                                    profile = activeProfile,
                                    allProfiles = profiles,
                                    allBooks = allBooks,
                                    readingGroups = readingGroups,
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onProfileClick = { navigateTo(AppScreen.PROFILE_SWITCHER) },
                                    onStarClick = {
                                        voiceEngine.playTapSound()
                                        selectedMainTab = MainTab.PRIZES
                                    },
                                    onOpenBook = { book ->
                                        activeBook = book
                                        forcedInitialPageIndex = null
                                        voiceEngine.speak("Let's read ${book.title} again!")
                                        navigateTo(AppScreen.STORY_READER)
                                    },
                                    onSwitchToLibraryTab = {
                                        voiceEngine.playTapSound()
                                        selectedMainTab = MainTab.LIBRARY
                                    },
                                    onUpdateDailyGoal = { newGoal ->
                                        voiceEngine.playSuccessChime()
                                        updateActiveProfile { p -> p.copy(dailyGoalMinutes = newGoal) }
                                    },
                                    onCreateReadingGroup = { gName, tName ->
                                        val code = "SEEKH-${(100..999).random()}"
                                        val newGroup = ReadingGroup(
                                            id = "rg_${System.currentTimeMillis()}",
                                            name = gName,
                                            adminName = tName,
                                            languageTitle = selectedLanguage.title,
                                            inviteCode = code,
                                            createdDate = "Nov 2026",
                                            members = listOf(
                                                GroupMemberStat(
                                                    name = activeProfile.name,
                                                    avatarIndex = activeProfile.avatarIndex,
                                                    minutesThisWeek = activeProfile.weeklyMinutes.sum(),
                                                    storiesRead = activeProfile.completedBookIds.size,
                                                    speedWpm = activeProfile.wordsPerMinute,
                                                    isActiveThisWeek = true
                                                )
                                            )
                                        )
                                        val updated = listOf(newGroup) + readingGroups
                                        readingGroups = updated
                                        repository.saveReadingGroups(updated)
                                        voiceEngine.playSuccessChime()
                                        voiceEngine.speak("Reading group $gName created! Share code $code with students.")
                                    },
                                    onJoinReadingGroup = { code ->
                                        val joinedGroup = ReadingGroup(
                                            id = "rg_join_${System.currentTimeMillis()}",
                                            name = "Joined Group (${code.uppercase()})",
                                            adminName = "Partner Teacher",
                                            languageTitle = selectedLanguage.title,
                                            inviteCode = code.uppercase(),
                                            createdDate = "Nov 2026",
                                            members = listOf(
                                                GroupMemberStat(
                                                    name = activeProfile.name,
                                                    avatarIndex = activeProfile.avatarIndex,
                                                    minutesThisWeek = activeProfile.weeklyMinutes.sum(),
                                                    storiesRead = activeProfile.completedBookIds.size,
                                                    speedWpm = activeProfile.wordsPerMinute,
                                                    isActiveThisWeek = true
                                                ),
                                                GroupMemberStat(name = "Amina", avatarIndex = 4, minutesThisWeek = 78, storiesRead = 9, speedWpm = 64, isActiveThisWeek = true),
                                                GroupMemberStat(name = "Bilal", avatarIndex = 7, minutesThisWeek = 65, storiesRead = 7, speedWpm = 58, isActiveThisWeek = true)
                                            )
                                        )
                                        val updated = listOf(joinedGroup) + readingGroups
                                        readingGroups = updated
                                        repository.saveReadingGroups(updated)
                                        voiceEngine.playSuccessChime()
                                        voiceEngine.speak("Successfully joined reading group $code!")
                                    },
                                    onUpdateReadingGroup = { updatedGroup ->
                                        voiceEngine.playSuccessChime()
                                        val updated = readingGroups.map { if (it.id == updatedGroup.id) updatedGroup else it }
                                        readingGroups = updated
                                        repository.saveReadingGroups(updated)
                                    },
                                    onDeleteReadingGroup = { groupId ->
                                        val updated = readingGroups.filterNot { it.id == groupId }
                                        readingGroups = updated
                                        repository.saveReadingGroups(updated)
                                    },
                                    onSpeakText = { voiceEngine.speak(it) }
                                )
                            }
                        }

                        // Compact Non-Overlapping Floating AI Companion Orb ("Noor") above the Floating Bottom Pill Bar
                        FloatingReadingBuddy(
                            mood = buddyMood,
                            speechBubbleText = null,
                            onClick = {
                                val msg = when (selectedMainTab) {
                                    MainTab.LIBRARY -> "Hi! I'm Noor. Tap any storybook to read with me, or play a word game!"
                                    MainTab.PRIZES -> "You have ${activeProfile.stars} golden stars! Keep reading to unlock more trophies!"
                                    MainTab.ACTIVITY -> "You read ${activeProfile.minutesReadToday} minutes today! Awesome job!"
                                }
                                voiceEngine.speak(msg)
                            },
                            size = 66.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .navigationBarsPadding()
                                .padding(end = 18.dp, bottom = 82.dp)
                        )

                        // Floating Soft Glass Pill Bottom Navigation Bar
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            SeekhBottomNavigationBar(
                                selectedTab = selectedMainTab,
                                onTabSelected = { tab ->
                                    voiceEngine.playTapSound()
                                    selectedMainTab = tab
                                }
                            )
                        }
                    }
                }
            }

            AppScreen.STORY_READER -> {
                val initialPage = forcedInitialPageIndex ?: (activeProfile.bookPageProgress[activeBook.id] ?: 0)
                InteractiveStoryReaderScreen(
                    book = activeBook,
                    profile = activeProfile,
                    selectedLanguageCode = selectedLangCode,
                    buddyMood = buddyMood,
                    initialPageIndex = initialPage,
                    onPageChanged = { newIdx ->
                        val prevMaxIdx = activeProfile.bookPageProgress[activeBook.id] ?: -1
                        if (newIdx > prevMaxIdx) {
                            updateActiveProfile { p ->
                                val currentMax = p.bookPageProgress[activeBook.id] ?: -1
                                if (newIdx > currentMax) {
                                    val newMap = p.bookPageProgress.toMutableMap()
                                    newMap[activeBook.id] = newIdx
                                    p.copy(bookPageProgress = newMap)
                                } else p
                            }
                            if (prevMaxIdx >= 0) {
                                recordReadingTimeAndProgress(addedSeconds = 20)
                            }
                        }
                    },
                    onSpeakText = { voiceEngine.speak(it) },
                    onPlayPhoneme = { idx -> voiceEngine.playPhonemeSound(idx) },
                    onStartListening = { onFallback -> voiceEngine.startVoiceListening(onFallback) },
                    onStopListening = { voiceEngine.stopVoiceListening() },
                    onAwardStars = { awardStarsToActiveProfile(it, isFromGame = false) },
                    onBookCompleted = { completedBook, newWpm ->
                        val wasAlreadyCompleted = activeProfile.completedBookIds.contains(completedBook.id)
                        voiceEngine.playClappingSound()
                        if (!wasAlreadyCompleted) {
                            voiceEngine.speak("Congratulations ${activeProfile.name}! You finished ${completedBook.title} and earned fifty golden stars!")
                        } else {
                            voiceEngine.speak("Awesome reading ${activeProfile.name}! You finished reading ${completedBook.title} again!")
                        }
                        if (completedBook.bookSource == "CLASSROOM" || completedBook.assignedByTeacher.isNotBlank()) {
                            repository.markAssignmentTurnedIn(completedBook.id)
                            turnedInAssignmentIds = repository.getTurnedInAssignmentIds()
                        }
                        val todayIdx = repository.getTodayDayOfWeekIndex()
                        val todayKey = repository.getTodayDateKey()
                        updateActiveProfile { p ->
                            val alreadyDone = p.completedBookIds.contains(completedBook.id)
                            val newMap = p.bookPageProgress.toMutableMap()
                            newMap.remove(completedBook.id)
                            val addedMin = if (alreadyDone) 0 else 2
                            val addedSec = if (alreadyDone) 0 else 120
                            val addedStars = if (alreadyDone) 0 else 50
                            val newMinToday = p.minutesReadToday + addedMin
                            val newWeek = p.weeklyMinutes.toMutableList().apply {
                                while (size < 7) add(0)
                                this[todayIdx] = newMinToday
                            }
                            val newStreak = when {
                                newMinToday >= p.dailyGoalMinutes && p.minutesReadToday < p.dailyGoalMinutes -> p.streakDays + 1
                                newMinToday > 0 && p.streakDays == 0 -> 1
                                else -> p.streakDays
                            }
                            val updatedWpm = if (p.wordsPerMinute == 0) newWpm else ((p.wordsPerMinute + newWpm) / 2).coerceIn(30, 120)
                            p.copy(
                                stars = p.stars + addedStars,
                                minutesReadToday = newMinToday,
                                readingSecondsToday = p.readingSecondsToday + addedSec,
                                wordsPerMinute = updatedWpm,
                                completedBookIds = p.completedBookIds + completedBook.id,
                                weeklyMinutes = newWeek,
                                streakDays = newStreak,
                                bookPageProgress = newMap,
                                lastActiveDateKey = todayKey
                            )
                        }
                    },
                    onBlockBook = { bookToBlock ->
                        repository.blockBook(bookToBlock.id)
                        blockedBookIds = repository.getBlockedBookIds()
                        voiceEngine.speak("Blocked ${bookToBlock.title} from library.")
                    },
                    onPlayPostBookGame = { gameType ->
                        val targetGame = when (gameType) {
                            "BALLOON" -> AppScreen.GAME_BALLOON
                            "SPEED" -> AppScreen.GAME_SPEED
                            "VOCAB" -> AppScreen.GAME_VOCAB
                            else -> AppScreen.GAME_JUMBLE
                        }
                        navigateTo(targetGame, replaceCurrent = true)
                    },
                    onBack = {
                        navigateBack()
                    }
                )
            }

            AppScreen.GAME_JUMBLE -> {
                JumbledWordsGameScreen(
                    profile = activeProfile,
                    buddyMood = buddyMood,
                    onSpeakText = { voiceEngine.speak(it) },
                    onPlayPhoneme = { idx -> voiceEngine.playPhonemeSound(idx) },
                    onPlayErrorTone = { voiceEngine.playErrorTone() },
                    onAwardStars = { awardStarsToActiveProfile(it, isFromGame = true) },
                    onBack = {
                        navigateBack()
                    }
                )
            }

            AppScreen.GAME_BALLOON -> {
                PopTheBalloonsGameScreen(
                    profile = activeProfile,
                    buddyMood = buddyMood,
                    onSpeakText = { voiceEngine.speak(it) },
                    onPlayPhoneme = { idx -> voiceEngine.playPhonemeSound(idx) },
                    onPlayBalloonPop = { voiceEngine.playBalloonPopSound() },
                    onPlayErrorTone = { voiceEngine.playErrorTone() },
                    onAwardStars = { awardStarsToActiveProfile(it, isFromGame = true) },
                    onBack = {
                        navigateBack()
                    }
                )
            }

            AppScreen.GAME_SPEED -> {
                SpeedReadingGameScreen(
                    profile = activeProfile,
                    buddyMood = buddyMood,
                    onSpeakText = { voiceEngine.speak(it) },
                    onAwardStars = { awardStarsToActiveProfile(it, isFromGame = true) },
                    onBack = {
                        navigateBack()
                    }
                )
            }

            AppScreen.GAME_VOCAB -> {
                VocabularyBuilderGameScreen(
                    profile = activeProfile,
                    selectedLanguageCode = selectedLangCode,
                    buddyMood = buddyMood,
                    onSpeakText = { voiceEngine.speak(it) },
                    onAwardStars = { awardStarsToActiveProfile(it, isFromGame = true) },
                    onBack = {
                        navigateBack()
                    }
                )
            }
        }
    }

    if (showAiStoryDialog) {
        AiStoryCreatorDialog(
            onDismiss = { showAiStoryDialog = false },
            onStoryGenerated = { newBook ->
                repository.saveCustomStory(newBook)
                customBooks = repository.loadCustomStories()
                showAiStoryDialog = false
                activeBook = newBook
                forcedInitialPageIndex = 0
                awardStarsToActiveProfile(25)
                voiceEngine.speak("Your new story ${newBook.title} is ready! Let's read it together!")
                navigateTo(AppScreen.STORY_READER)
            }
        )
    }

    if (showPartnerCodesDialog) {
        PartnerCodesDialog(
            partnerCodes = partnerCodes,
            onAddCode = { code ->
                repository.addPartnerCode(code)
                partnerCodes = repository.getPartnerCodes()
                voiceEngine.playSuccessChime()
                voiceEngine.speak("Partner code $code registered successfully!")
            },
            onRemoveCode = { code ->
                repository.removePartnerCode(code)
                partnerCodes = repository.getPartnerCodes()
            },
            onDismiss = { showPartnerCodesDialog = false }
        )
    }

    if (showEditProfileDialog) {
        ChangeProfileNameAndAvatarDialog(
            profile = activeProfile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { newName, newAvatar ->
                updateActiveProfile { p -> p.copy(name = newName, avatarIndex = newAvatar) }
                voiceEngine.playSuccessChime()
                voiceEngine.speak("Profile updated to $newName!")
                showEditProfileDialog = false
            }
        )
    }

    if (showAccountMenuDialog) {
        AccountMenuDialog(
            accountState = accountState,
            onUpdateAccountState = { updatedAcc ->
                accountState = updatedAcc
                repository.saveAccountState(updatedAcc)
                voiceEngine.playSuccessChime()
                if (updatedAcc.isSignedIn) {
                    voiceEngine.speak("Signed in as ${updatedAcc.email}. Profiles are synced.")
                } else {
                    voiceEngine.speak("Using app without an account.")
                }
                showAccountMenuDialog = false
            },
            onDismiss = { showAccountMenuDialog = false }
        )
    }

    if (showDeleteHistoryDialog) {
        DeleteHistoryDialog(
            onConfirmDelete = {
                repository.clearUsageHistory()
                profiles = repository.loadProfiles()
                activeProfileId = ""
                customBooks = repository.loadCustomStories()
                readingGroups = repository.loadReadingGroups()
                turnedInAssignmentIds = repository.getTurnedInAssignmentIds()
                openedGiftBoxIds = repository.getOpenedGiftBoxIds()
                voiceEngine.playSuccessChime()
                voiceEngine.speak("App usage history deleted. Create a new profile to begin!")
                showDeleteHistoryDialog = false
                screenHistory.clear()
                currentScreen = AppScreen.PROFILE_SWITCHER
            },
            onDismiss = { showDeleteHistoryDialog = false }
        )
    }
}
