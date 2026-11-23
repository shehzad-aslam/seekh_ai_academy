package com.tllstudio.smartlearn

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// 1. Exact Signed-In Reference Library Tab Screen (LibraryTabComponent: Search & Filter + Top Hero Card + Classroom Assignments + Level 1 stories + Play games + Phonics & Decodable + Level 2 stories + Story Series + Level 3 & 4 stories + Create your own story / My stories + Read again + Native language stories)
@Composable
fun LibraryTabScreen(
    books: List<StoryBook>,
    profile: LearnerProfile,
    selectedLanguage: LearningLanguageOption,
    accountState: SignedInAccountState = SignedInAccountState(),
    readingGroups: List<ReadingGroup> = emptyList(),
    turnedInAssignmentIds: Set<String> = emptySet(),
    downloadedCloudBookIds: Set<String>,
    downloadingBookIds: Set<String>,
    onDownloadCloudBook: (StoryBook) -> Unit,
    onDeleteCustomBook: (StoryBook) -> Unit = {},
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStarClick: () -> Unit,
    onOpenBook: (StoryBook) -> Unit,
    onOpenGame: (String) -> Unit,
    onOpenAiStoryCreator: () -> Unit = {},
    onOpenLanguageSettings: () -> Unit = {},
    onSpeakText: (String) -> Unit
) {
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var bookToDeleteConfirm by remember { mutableStateOf<StoryBook?>(null) }

    val filterChips = remember {
        listOf(
            "All",
            "Games",
            "New & Daily",
            "Level 1",
            "Level 2",
            "Level 3",
            "Level 4",
            "Animals & Nature",
            "Family & Friends",
            "School & Kindness",
            "Numbers & Rhymes",
            "Phonics",
            "Adventure & Travel",
            "Science & Weather",
            "Bedtime & Folktales",
            "Series",
            "Classroom",
            "My stories"
        )
    }

    // Sub-state BackHandler when searching or filtering
    BackHandler(enabled = showSearchBar || selectedCategoryFilter != "All" || bookToDeleteConfirm != null) {
        when {
            bookToDeleteConfirm != null -> bookToDeleteConfirm = null
            showSearchBar -> {
                showSearchBar = false
                searchQuery = ""
            }
            selectedCategoryFilter != "All" -> selectedCategoryFilter = "All"
        }
    }

    val uiStrings = remember(selectedLanguage.code) {
        SeekhCatalog.getAppStrings(selectedLanguage.code)
    }

    // Language-specific Basic Alphabets Book pinned at the VERY TOP of the Home screen!
    val alphabetHeroBook = remember(selectedLanguage.code) {
        SeekhCatalog.getAlphabetBookForLanguage(selectedLanguage.code)
    }
    val heroBook = alphabetHeroBook
    val booksWithAlphabet = remember(books, alphabetHeroBook) {
        listOf(alphabetHeroBook) + books.filterNot { it.id == alphabetHeroBook.id }
    }
    val nonHeroBooks = remember(books, heroBook) {
        books.filterNot { it.id == heroBook.id }
    }
    val groupAssignedLabels = remember(readingGroups) {
        buildMap<String, String> {
            readingGroups.forEach { g ->
                g.assignedBookIds.forEach { bId ->
                    put(bId, "${g.adminName} • ${g.name}")
                }
            }
        }
    }
    val classroomBooks = remember(books, groupAssignedLabels, accountState.isSignedIn) {
        books.filter { b ->
            groupAssignedLabels.containsKey(b.id) ||
                (accountState.isSignedIn && (b.bookSource == "CLASSROOM" || b.assignedByTeacher.isNotBlank()))
        }.map { b ->
            val groupLabel = groupAssignedLabels[b.id]
            if (groupLabel != null) b.copy(assignedByTeacher = "Assigned by $groupLabel") else b
        }
    }
    val dailyDropBooks = remember(books) {
        books.filter { it.isDailyDrop }
    }
    val animalsNatureBooks = remember(books) {
        books.filter { it.categoryTag == "Animals & Nature" }
    }
    val familyFriendsBooks = remember(books) {
        books.filter { it.categoryTag == "Family & Friends" }
    }
    val schoolKindnessBooks = remember(books) {
        books.filter { it.categoryTag == "School & Kindness" }
    }
    val numbersRhymesBooks = remember(booksWithAlphabet) {
        booksWithAlphabet.filter { it.categoryTag == "Numbers & Rhymes" || it.bookType == "ALPHABET" }
    }
    val adventureTravelBooks = remember(books) {
        books.filter { it.categoryTag == "Adventure & Travel" }
    }
    val scienceWeatherBooks = remember(books) {
        books.filter { it.categoryTag == "Science & Weather" }
    }
    val bedtimeFolktaleBooks = remember(books) {
        books.filter { it.categoryTag == "Bedtime & Folktales" }
    }
    val phonicsBooks = remember(booksWithAlphabet) {
        booksWithAlphabet.filter { it.bookType == "ALPHABET" || it.bookType == "DECODABLE" || it.phonicsSkillTag.isNotBlank() }
    }
    val seriesBooks = remember(books) {
        books.filter { it.bookType == "SERIES" || it.seriesTag.isNotBlank() }
    }
    val myCustomBooks = remember(books) {
        books.filter { it.isAiGenerated || it.bookType == "CYOC" }
    }
    val level1Books = remember(booksWithAlphabet) { booksWithAlphabet.filter { it.level == 1 && !it.isAiGenerated } }
    val level2Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 2 && !it.isAiGenerated } }
    val level3Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 3 && !it.isAiGenerated } }
    val level4Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 4 && !it.isAiGenerated } }

    // "Read again" shelf (readAgainBooks from flutter_ui_strings.txt & ref_live_01.png)
    val readAgainBooks = remember(booksWithAlphabet, profile.completedBookIds, profile.bookPageProgress) {
        booksWithAlphabet.filter { b ->
            profile.completedBookIds.contains(b.id) || (profile.bookPageProgress[b.id] ?: 0) > 0
        }
    }

    // Filtered books when user uses Search or selects a specific category filter chip
    val filteredBooks = remember(booksWithAlphabet, searchQuery, selectedCategoryFilter, groupAssignedLabels) {
        booksWithAlphabet.filter { b ->
            val matchesQuery = searchQuery.isBlank() ||
                b.title.contains(searchQuery, ignoreCase = true) ||
                b.nativeTitleUrdu.contains(searchQuery, ignoreCase = true) ||
                b.nativeTitleHindi.contains(searchQuery, ignoreCase = true) ||
                b.author.contains(searchQuery, ignoreCase = true) ||
                b.categoryTag.contains(searchQuery, ignoreCase = true) ||
                b.phonicsSkillTag.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedCategoryFilter) {
                "New & Daily" -> b.isDailyDrop || b.bookType == "ALPHABET"
                "Level 1" -> b.level == 1
                "Level 2" -> b.level == 2
                "Level 3" -> b.level == 3
                "Level 4" -> b.level == 4
                "Animals & Nature" -> b.categoryTag == "Animals & Nature"
                "Family & Friends" -> b.categoryTag == "Family & Friends"
                "School & Kindness" -> b.categoryTag == "School & Kindness"
                "Numbers & Rhymes" -> b.categoryTag == "Numbers & Rhymes" || b.bookType == "ALPHABET"
                "Adventure & Travel" -> b.categoryTag == "Adventure & Travel"
                "Science & Weather" -> b.categoryTag == "Science & Weather"
                "Bedtime & Folktales" -> b.categoryTag == "Bedtime & Folktales"
                "Phonics" -> b.bookType == "ALPHABET" || b.bookType == "DECODABLE" || b.phonicsSkillTag.isNotBlank()
                "Series" -> b.bookType == "SERIES" || b.seriesTag.isNotBlank()
                "Classroom" -> groupAssignedLabels.containsKey(b.id) || b.bookSource == "CLASSROOM" || b.assignedByTeacher.isNotBlank()
                "My stories" -> b.isAiGenerated || b.bookType == "CYOC"
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 200.dp, curveDepth = 34.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar (Hamburger Menu on Left, Search Button + Star Counter Pill + Profile Avatar on Right)
            SeekhMainTopBar(
                stars = profile.stars,
                avatarIndex = profile.avatarIndex,
                onMenuClick = onMenuClick,
                onStarClick = onStarClick,
                onProfileClick = onProfileClick,
                onSearchClick = {
                    showSearchBar = !showSearchBar
                    if (!showSearchBar) searchQuery = ""
                }
            )

            // Expandable Search Input Bar (SEARCH_BOOK / EventSearchBookResultDetails)
            if (showSearchBar) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SeekhGreen500
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = uiStrings.searchPlaceholder,
                                    fontFamily = GoogleSansFamily,
                                    fontSize = 13.sp
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = SeekhTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            val gameCatalogMeta = remember {
                listOf(
                    "JUMBLE" to SeekhJumblePurple,
                    "BALLOON" to Color(0xFF00838F),
                    "SENTENCE" to Color(0xFF1E40AF),
                    "RHYME" to Color(0xFFBE185D),
                    "MISSING_SOUND" to Color(0xFF0F766E),
                    "LISTEN_FIND" to Color(0xFF4338CA),
                    "OPPOSITES" to Color(0xFFD97706),
                    "COUNT_COLOR" to Color(0xFF047857),
                    "SPEED" to Color(0xFFF57F17),
                    "VOCAB" to SeekhVocabDarkGreen
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 135.dp)
            ) {
                // Horizontal Category & Level Filter Chips (LIBRARY_LEVEL_SELECTED / SEARCH_RESULTS_BOOK_TYPE_FILTER_APPLIED)
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filterChips) { chip ->
                            val isSelected = selectedCategoryFilter == chip
                            val displayChipLabel = uiStrings.filterChipLabels[chip] ?: chip
                            val chipScale by androidx.compose.animation.core.animateFloatAsState(
                                targetValue = if (isSelected) 1.06f else 1f,
                                animationSpec = androidx.compose.animation.core.spring(
                                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                    stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                                ),
                                label = "chip_scale_$chip"
                            )
                            val chipBg by androidx.compose.animation.animateColorAsState(
                                targetValue = if (isSelected) SeekhYellow500 else Color.White.copy(alpha = 0.94f),
                                animationSpec = androidx.compose.animation.core.tween(220),
                                label = "chip_bg_$chip"
                            )
                            Surface(
                                modifier = Modifier
                                    .softCardShadow(
                                        elevation = if (isSelected) 9.dp else 5.dp,
                                        shape = RoundedCornerShape(50),
                                        spotColor = if (isSelected) Color(0x55FFB800) else Color(0x301E1B4B)
                                    )
                                    .bouncyClick(scaleDown = 0.92f) {
                                        selectedCategoryFilter = if (isSelected && chip != "All") "All" else chip
                                        onSpeakText(displayChipLabel)
                                    },
                                shape = RoundedCornerShape(50),
                                color = chipBg,
                                shadowElevation = 0.dp,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color.White.copy(alpha = 0.6f) else Color(0xFFE2E6F7)
                                )
                            ) {
                                Text(
                                    text = displayChipLabel,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SeekhTextPrimary,
                                    modifier = Modifier.padding(
                                        horizontal = (14 * chipScale).dp,
                                        vertical = 7.dp
                                    )
                                )
                            }
                        }
                    }
                }

                // If user selects "Games" filter chip, show the dedicated 10 Kids Learning Games Hub with Visual Gameplay Previews!
                if (selectedCategoryFilter == "Games") {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiStrings.sectionPlayGames,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = SeekhTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SeekhGreenSoft
                            ) {
                                Text(
                                    text = "${uiStrings.playCta}: ${profile.gamesPlayedCount}",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SeekhGreen600,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            gameCatalogMeta.forEach { (gameKey, bgCol) ->
                                val gInfo = uiStrings.gameInfoByKey[gameKey]
                                    ?: Triple("Learning Game", "Interactive word challenge", "Game")
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .softCardShadow(
                                            elevation = 10.dp,
                                            shape = RoundedCornerShape(24.dp),
                                            spotColor = bgCol.copy(alpha = 0.45f)
                                        )
                                        .bouncyClick { onOpenGame(gameKey) },
                                    shape = RoundedCornerShape(24.dp),
                                    color = bgCol,
                                    shadowElevation = 0.dp,
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
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
                                                    color = Color.White.copy(alpha = 0.22f)
                                                ) {
                                                    Text(
                                                        text = gInfo.third,
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = gInfo.first,
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 19.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = gInfo.second,
                                                fontFamily = GoogleSansFamily,
                                                fontSize = 12.5.sp,
                                                color = Color.White.copy(alpha = 0.92f)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = Color.White
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = uiStrings.playCta,
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 12.5.sp,
                                                        color = bgCol
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = uiStrings.playCta,
                                                        tint = bgCol,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        // Distinct Visual Gameplay Preview Artwork right on the card!
                                        GameVisualPreviewArtwork(
                                            gameKey = gameKey,
                                            accentColor = bgCol,
                                            modifier = Modifier
                                                .width(136.dp)
                                                .height(64.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (searchQuery.isNotBlank() || selectedCategoryFilter != "All") {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        val filterTitle = uiStrings.filterChipLabels[selectedCategoryFilter] ?: selectedCategoryFilter
                        LibrarySectionHeader(
                            title = if (searchQuery.isNotBlank()) {
                                "${uiStrings.searchPlaceholder.take(14)} (${filteredBooks.size})"
                            } else {
                                "$filterTitle (${filteredBooks.size})"
                            },
                            onSpeak = { onSpeakText(filterTitle) }
                        )
                        if (selectedCategoryFilter == "My stories") {
                            Spacer(modifier = Modifier.height(6.dp))
                            CreateYourOwnStoryBannerCard(
                                onClick = onOpenAiStoryCreator
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (filteredBooks.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CustomSearchLensIllustration(modifier = Modifier.size(64.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No matching stories found",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = SeekhTextPrimary
                                    )
                                }
                            }
                        } else {
                            LibraryBooksThreeColumnRows(
                                sectionBooks = filteredBooks,
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                        }
                    }
                } else {
                    // 1. Pinned Top Card: Language-Specific Basic Alphabets Book (English A-Z, Urdu Alif-Be-Pe, Hindi Varnamala, Arabic, etc.)
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        FeaturedStoryHeroCard(
                            book = heroBook,
                            isCompleted = profile.completedBookIds.contains(heroBook.id),
                            selectedLanguageCode = selectedLanguage.code,
                            onClick = { onOpenBook(heroBook) }
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // 2. "Classroom & Parent assignments" Section
                    if (classroomBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionAssignedHomework,
                                onSpeak = { onSpeakText(uiStrings.sectionAssignedHomework) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 18.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(classroomBooks) { cBook ->
                                    val isTurnedIn = turnedInAssignmentIds.contains(cBook.id) || profile.completedBookIds.contains(cBook.id)
                                    ClassroomAssignmentBookCard(
                                        book = cBook,
                                        isTurnedIn = isTurnedIn,
                                        onClick = { onOpenBook(cBook) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }

                    // 2b. "Recommended stories" Section
                    if (dailyDropBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionRecommended,
                                onSpeak = { onSpeakText(uiStrings.sectionRecommended) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "New & Daily" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = dailyDropBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 3. "Level 1 stories" Section (Includes the language's Basic Alphabets Book at slot #1!)
                    if (level1Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionLevel1,
                                onSpeak = { onSpeakText(uiStrings.sectionLevel1) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Level 1" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level1Books.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 4. "Play games" Section (10 Kids Learning Games with Visual Gameplay Previews!)
                    item {
                        LibrarySectionHeader(
                            title = uiStrings.sectionPlayGames,
                            onSpeak = { onSpeakText(uiStrings.sectionPlayGames) },
                            actionLabel = uiStrings.seeAll,
                            onActionClick = { selectedCategoryFilter = "Games" }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(gameCatalogMeta) { (gameKey, bgCol) ->
                                val gInfo = uiStrings.gameInfoByKey[gameKey]
                                    ?: Triple("Learning Game", "Play now", "Game")
                                GameShelfCard(
                                    gameKey = gameKey,
                                    title = gInfo.first,
                                    subtitle = gInfo.second,
                                    containerColor = bgCol,
                                    badgeText = gInfo.third,
                                    playLabel = uiStrings.playCta,
                                    onClick = { onOpenGame(gameKey) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // 4b. "Animals & Nature" Category Shelf
                    if (animalsNatureBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionAnimalsNature,
                                onSpeak = { onSpeakText(uiStrings.sectionAnimalsNature) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Animals & Nature" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = animalsNatureBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 4c. "School & Kindness" Category Shelf
                    if (schoolKindnessBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionSchoolKindness,
                                onSpeak = { onSpeakText(uiStrings.sectionSchoolKindness) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "School & Kindness" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = schoolKindnessBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 5. "Phonics stories" Section
                    if (phonicsBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionPhonics,
                                onSpeak = { onSpeakText(uiStrings.sectionPhonics) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Phonics" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = phonicsBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 6. "Level 2 stories" Section
                    if (level2Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionLevel2,
                                onSpeak = { onSpeakText(uiStrings.sectionLevel2) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Level 2" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level2Books.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 6b. "Family & Friends" Category Shelf
                    if (familyFriendsBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionFamilyFriends,
                                onSpeak = { onSpeakText(uiStrings.sectionFamilyFriends) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Family & Friends" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = familyFriendsBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 6c. "Numbers & Rhymes" Category Shelf
                    if (numbersRhymesBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionNumbersRhymes,
                                onSpeak = { onSpeakText(uiStrings.sectionNumbersRhymes) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Numbers & Rhymes" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = numbersRhymesBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 7. "Story Series" Section
                    if (seriesBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionStorySeries,
                                onSpeak = { onSpeakText(uiStrings.sectionStorySeries) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Series" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = seriesBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 8. "Level 3 stories" Section
                    if (level3Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionLevel3,
                                onSpeak = { onSpeakText(uiStrings.sectionLevel3) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Level 3" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level3Books.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 8b. "Adventure & Travel" Category Shelf
                    if (adventureTravelBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionAdventureTravel,
                                onSpeak = { onSpeakText(uiStrings.sectionAdventureTravel) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Adventure & Travel" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = adventureTravelBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 8c. "Science & Weather" Category Shelf
                    if (scienceWeatherBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionScienceWeather,
                                onSpeak = { onSpeakText(uiStrings.sectionScienceWeather) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Science & Weather" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = scienceWeatherBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 9. "Level 4 stories" Section
                    if (level4Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionLevel4,
                                onSpeak = { onSpeakText(uiStrings.sectionLevel4) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Level 4" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level4Books.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 9b. "Bedtime & Folktales" Category Shelf
                    if (bedtimeFolktaleBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionBedtimeFolktales,
                                onSpeak = { onSpeakText(uiStrings.sectionBedtimeFolktales) },
                                actionLabel = uiStrings.seeAll,
                                onActionClick = { selectedCategoryFilter = "Bedtime & Folktales" }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = bedtimeFolktaleBooks.take(9),
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 10. Signed-In "Create your own story & My stories" Section
                    if (accountState.isSignedIn) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionCreateStory,
                                onSpeak = { onSpeakText(uiStrings.sectionCreateStory) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            CreateYourOwnStoryBannerCard(
                                onClick = onOpenAiStoryCreator
                            )
                            if (myCustomBooks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    myCustomBooks.forEach { myBook ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onOpenBook(myBook) },
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color.White,
                                            shadowElevation = 2.dp,
                                            border = BorderStroke(1.dp, SeekhBorderGray)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(50.dp)
                                                            .clip(RoundedCornerShape(12.dp))
                                                    ) {
                                                        AssetWebpImage(
                                                            assetPath = myBook.coverAssetPath,
                                                            contentDescription = myBook.title,
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }
                                                    Column {
                                                        Text(
                                                            text = SeekhCatalog.getBookPrimaryTitle(myBook, selectedLanguage.code),
                                                            fontFamily = GoogleSansFamily,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = SeekhTextPrimary
                                                        )
                                                        Text(
                                                            text = "${uiStrings.levelPrefix} ${myBook.level}",
                                                            fontFamily = GoogleSansFamily,
                                                            fontSize = 12.sp,
                                                            color = SeekhGreen600
                                                        )
                                                    }
                                                }
                                                IconButton(
                                                    onClick = { bookToDeleteConfirm = myBook }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeleteOutline,
                                                        contentDescription = "Delete my story",
                                                        tint = SeekhCoralRed
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }

                    // 11. "Read again" Section
                    if (readAgainBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = uiStrings.sectionReadAgain,
                                onSpeak = { onSpeakText(uiStrings.sectionReadAgain) }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = readAgainBooks,
                                profile = profile,
                                selectedLanguageCode = selectedLanguage.code,
                                downloadedCloudBookIds = downloadedCloudBookIds,
                                downloadingBookIds = downloadingBookIds,
                                onDownloadCloudBook = onDownloadCloudBook,
                                onOpenBook = onOpenBook
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 12. "Read stories in <Language>" / "More languages" Footer Card
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        val footerShape = RoundedCornerShape(22.dp)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .softCardShadow(
                                    elevation = 12.dp,
                                    shape = footerShape,
                                    spotColor = Color(0x403B28B0)
                                )
                                .bouncyClick(scaleDown = 0.96f, onClick = onOpenLanguageSettings),
                            shape = footerShape,
                            color = Color.White,
                            shadowElevation = 0.dp,
                            border = BorderStroke(1.2.dp, Color(0xFFE0E5F6))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color.White, Color(0xFFF6F4FF))
                                        )
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CustomLanguageGlobeBadge(
                                        assetHint = selectedLanguage.svgAsset,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Column {
                                        Text(
                                            text = selectedLanguage.title,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SeekhTextPrimary
                                        )
                                        Text(
                                            text = uiStrings.moreLanguagesLabel,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = SeekhGreen500
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = uiStrings.moreLanguagesLabel,
                                    tint = SeekhGreen500
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // MY_BOOK_CONTENT_DELETE_CONFIRMATION_DIALOG_SHOWN
    bookToDeleteConfirm?.let { targetBook ->
        AlertDialog(
            onDismissRequest = { bookToDeleteConfirm = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Delete story?",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${targetBook.title}\" from your created stories?",
                    fontFamily = GoogleSansFamily,
                    fontSize = 14.sp,
                    color = SeekhTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCustomBook(targetBook)
                        bookToDeleteConfirm = null
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhCoralRed)
                ) {
                    Text("Delete", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { bookToDeleteConfirm = null }) {
                    Text("Cancel", fontFamily = GoogleSansFamily)
                }
            }
        )
    }
}

@Composable
private fun ClassroomAssignmentBookCard(
    book: StoryBook,
    isTurnedIn: Boolean,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    Surface(
        modifier = Modifier
            .width(252.dp)
            .softCardShadow(
                elevation = 12.dp,
                shape = cardShape,
                spotColor = if (isTurnedIn) Color(0x4510B981) else Color(0x423B28B0)
            )
            .bouncyClick(scaleDown = 0.96f, onClick = onClick),
        shape = cardShape,
        color = Color.White,
        shadowElevation = 0.dp,
        border = BorderStroke(1.2.dp, if (isTurnedIn) SeekhGreenMint.copy(alpha = 0.6f) else SeekhGreen100)
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, if (isTurnedIn) Color(0xFFF0FDF4) else Color(0xFFF7F5FF))
                    )
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .softCardShadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AssetWebpImage(
                    assetPath = book.coverAssetPath,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isTurnedIn) SeekhGreenSoft else SeekhBlueSoft
                ) {
                    Text(
                        text = if (isTurnedIn) "Turned in ✓" else "Assigned • Classroom",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = if (isTurnedIn) SeekhGreen600 else SeekhBlue600,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.title,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = SeekhTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.assignedByTeacher.ifBlank { "Assigned by Teacher" },
                    fontFamily = GoogleSansFamily,
                    fontSize = 11.sp,
                    color = SeekhTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CreateYourOwnStoryBannerCard(
    onClick: () -> Unit
) {
    val bannerShape = RoundedCornerShape(24.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .softCardShadow(
                elevation = 14.dp,
                shape = bannerShape,
                spotColor = SeekhGreen500.copy(alpha = 0.55f),
                ambientColor = SeekhGreen900.copy(alpha = 0.35f)
            )
            .bouncyClick(scaleDown = 0.96f, onClick = onClick),
        shape = bannerShape,
        color = SeekhGreen500,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(SeekhGreen500, SeekhGreen700)
                    )
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    CustomAiSparkleBadge(modifier = Modifier.size(24.dp))
                }
                Column {
                    Text(
                        text = "Create your own story",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Pick a character and adventure to write a story",
                        fontFamily = GoogleSansFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.90f)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = SeekhYellow500
            ) {
                Text(
                    text = "Create",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SeekhTextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun LibrarySectionHeader(
    title: String,
    onSpeak: () -> Unit,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onSpeak() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                color = SeekhTextPrimary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Hear section title",
                tint = SeekhGreen500,
                modifier = Modifier.size(18.dp)
            )
        }
        if (!actionLabel.isNullOrBlank() && onActionClick != null) {
            Surface(
                shape = RoundedCornerShape(50),
                color = SeekhGreenSoft,
                border = BorderStroke(1.dp, SeekhGreen100),
                modifier = Modifier.bouncyClick(scaleDown = 0.92f, onClick = onActionClick)
            ) {
                Text(
                    text = actionLabel,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SeekhGreen600,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun LibraryBooksThreeColumnRows(
    sectionBooks: List<StoryBook>,
    profile: LearnerProfile,
    selectedLanguageCode: String,
    downloadedCloudBookIds: Set<String>,
    downloadingBookIds: Set<String>,
    onDownloadCloudBook: (StoryBook) -> Unit,
    onOpenBook: (StoryBook) -> Unit
) {
    val rows = sectionBooks.chunked(3)
    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { rowBooks ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 0 until 3) {
                    val b = rowBooks.getOrNull(i)
                    if (b != null) {
                        val savedPageIdx = profile.bookPageProgress[b.id] ?: 0
                        val progFrac = if (savedPageIdx > 0) {
                            ((savedPageIdx + 1).toFloat() / b.pages.size.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                        } else 0f
                        val needsDl = b.requiresDownload && !downloadedCloudBookIds.contains(b.id)
                        val isDl = downloadingBookIds.contains(b.id)

                        StoryBookCard(
                            book = b,
                            isCompleted = profile.completedBookIds.contains(b.id),
                            selectedLanguageCode = selectedLanguageCode,
                            progressFraction = progFrac,
                            needsDownload = needsDl,
                            isDownloading = isDl,
                            onClick = {
                                if (needsDl) {
                                    onDownloadCloudBook(b)
                                } else {
                                    onOpenBook(b)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun GameShelfCard(
    gameKey: String,
    title: String,
    subtitle: String,
    containerColor: Color,
    badgeText: String,
    playLabel: String = "Play",
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)
    Surface(
        modifier = Modifier
            .width(214.dp)
            .height(166.dp)
            .softCardShadow(
                elevation = 12.dp,
                shape = cardShape,
                spotColor = containerColor.copy(alpha = 0.55f),
                ambientColor = containerColor.copy(alpha = 0.30f)
            )
            .bouncyClick(scaleDown = 0.94f, onClick = onClick),
        shape = cardShape,
        color = containerColor,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            containerColor,
                            containerColor.copy(alpha = 0.88f),
                            Color(0x28000000)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.22f)
                    ) {
                        Text(
                            text = badgeText,
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = Color.White,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = playLabel,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp,
                                color = containerColor
                            )
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = playLabel,
                                tint = containerColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Self-Explanatory Visual Gameplay Preview Artwork right inside the Game Shelf Card!
                GameVisualPreviewArtwork(
                    gameKey = gameKey,
                    accentColor = containerColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.5.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        fontFamily = GoogleSansFamily,
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.90f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// 2. Play Store Screenshot #4: Prizes Tab Screen (PrizesTab + Signed-In Mystery Gift Boxes: prize_gift_box_stars, prize_gift_box_daily_goal, prize_gift_box_book_en)
@Composable
fun PrizesTabScreen(
    profile: LearnerProfile,
    openedGiftBoxIds: Set<String> = emptySet(),
    onOpenGiftBox: (String) -> Unit = {},
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStarClick: () -> Unit,
    onSpeakText: (String) -> Unit
) {
    var selectedBadge by remember { mutableStateOf<PrizeBadge?>(null) }
    var starBurstTrigger by remember { mutableIntStateOf(0) }

    val giftBoxes = remember {
        listOf(
            Triple("prize_gift_box_stars", "Prize for earning stars", "Star Gift Box"),
            Triple("prize_gift_box_daily_goal", "Prize for daily reading goal", "Streak Gift Box"),
            Triple("prize_gift_box_book_en", "Prize for reading stories", "Story Gift Box")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 205.dp, curveDepth = 36.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SeekhMainTopBar(
                stars = profile.stars,
                avatarIndex = profile.avatarIndex,
                onMenuClick = onMenuClick,
                onStarClick = onStarClick,
                onProfileClick = onProfileClick
            )

            val rows = SeekhCatalog.prizeBadges.chunked(3)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 135.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Signed-In Interactive Mystery Gift Boxes Row (prize_gift_box_stars, prize_gift_box_daily_goal, prize_gift_box_book_en)
                item {
                    val giftCardShape = RoundedCornerShape(24.dp)
                    Surface(
                        shape = giftCardShape,
                        color = Color.White,
                        shadowElevation = 0.dp,
                        border = BorderStroke(1.2.dp, SeekhGreen100),
                        modifier = Modifier
                            .fillMaxWidth()
                            .softCardShadow(
                                elevation = 14.dp,
                                shape = giftCardShape,
                                spotColor = Color(0x453B28B0)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Mystery Prize Gift Boxes • Tap to Unbox!",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SeekhTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                giftBoxes.forEach { (boxId, desc, label) ->
                                    val isOpened = openedGiftBoxIds.contains(boxId)
                                    Surface(
                                        modifier = Modifier
                                            .width(96.dp)
                                            .clickable {
                                                starBurstTrigger++
                                                onOpenGiftBox(boxId)
                                                onSpeakText("Congratulations! You opened $desc and earned fifty bonus stars!")
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isOpened) SeekhGreenSoft else Color(0xFFFFF8E1),
                                        border = BorderStroke(
                                            1.5.dp,
                                            if (isOpened) SeekhGreen500 else SeekhYellow500
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = if (isOpened) Icons.Default.EmojiEvents else Icons.Default.CardGiftcard,
                                                contentDescription = label,
                                                tint = if (isOpened) SeekhGreen500 else Color(0xFFD97706),
                                                modifier = Modifier.size(30.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (isOpened) "Opened ✓" else label,
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = SeekhTextPrimary,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                items(rows) { rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowBadges.forEach { badge ->
                            val isUnlocked = when (badge.category) {
                                "STARS" -> profile.stars >= badge.requiredStars
                                "STREAK" -> profile.streakDays >= badge.requiredStreak ||
                                    (badge.requiredStreak == 1 && profile.minutesReadToday >= profile.dailyGoalMinutes)
                                "BOOKS" -> profile.completedBookIds.size >= badge.requiredBooks
                                else -> false
                            }
                            val progFrac = when (badge.category) {
                                "STARS" -> (profile.stars.toFloat() / badge.requiredStars.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                "STREAK" -> (profile.streakDays.toFloat() / badge.requiredStreak.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                "BOOKS" -> (profile.completedBookIds.size.toFloat() / badge.requiredBooks.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                else -> 0f
                            }
                            PrizeMedallionItem(
                                badge = badge,
                                isUnlocked = isUnlocked,
                                progressFraction = progFrac,
                                onClick = {
                                    selectedBadge = badge
                                    onSpeakText("${badge.title}. ${badge.subtitle}")
                                }
                            )
                        }
                    }
                }
            }
        }

        FlyingStarsOverlay(triggerCount = starBurstTrigger)
    }

    selectedBadge?.let { badge ->
        val progText = when (badge.category) {
            "STARS" -> "Your stars: ${profile.stars} / ${badge.requiredStars} ⭐"
            "STREAK" -> "Your streak: ${profile.streakDays} / ${badge.requiredStreak} days 🔥"
            "BOOKS" -> "Stories finished: ${profile.completedBookIds.size} / ${badge.requiredBooks} 📚"
            else -> ""
        }
        val progFrac = when (badge.category) {
            "STARS" -> (profile.stars.toFloat() / badge.requiredStars.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
            "STREAK" -> (profile.streakDays.toFloat() / badge.requiredStreak.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
            "BOOKS" -> (profile.completedBookIds.size.toFloat() / badge.requiredBooks.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
            else -> 0f
        }
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomPrizeMedallionVector(
                        assetHint = badge.svgAsset,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = badge.title,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = SeekhTextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = badge.subtitle,
                        fontFamily = GoogleSansFamily,
                        fontSize = 14.sp,
                        color = SeekhTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    if (progText.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SeekhGreenSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = progText,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SeekhGreen600
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progFrac.coerceIn(0.05f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = if (progFrac >= 1f) SeekhGreenMint else SeekhGreen500,
                                    trackColor = SeekhGreen100
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedBadge = null },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text("OK", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun PrizeMedallionItem(
    badge: PrizeBadge,
    isUnlocked: Boolean,
    progressFraction: Float = 0f,
    onClick: () -> Unit
) {
    val animatedProg by androidx.compose.animation.core.animateFloatAsState(
        targetValue = progressFraction.coerceIn(0f, 1f),
        animationSpec = androidx.compose.animation.core.tween(500),
        label = "prize_prog"
    )
    Box(
        modifier = Modifier
            .width(106.dp)
            .height(122.dp)
            .bouncyClick(scaleDown = 0.93f, onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                CustomPrizeMedallionVector(
                    assetHint = badge.svgAsset,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProg.coerceAtLeast(0.04f) },
                        modifier = Modifier.size(92.dp),
                        color = SeekhGreen500,
                        trackColor = Color(0xFFCBD5E1),
                        strokeWidth = 4.dp
                    )
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = badge.title,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Overlapping Bottom Pill Label ("100", "300", "500", "1000", "3000", "1", "5") matching Play Store Screenshot #4 1:1
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(min = 68.dp),
            shape = RoundedCornerShape(50),
            color = if (isUnlocked) SeekhYellow500 else Color(0xFF1E1B4B),
            shadowElevation = 3.dp,
            border = BorderStroke(2.dp, Color.White)
        ) {
            Text(
                text = badge.badgeLabel,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isUnlocked) SeekhTextPrimary else Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
            )
        }
    }
}

// 3. Exact Reference Activity Tab Screen (with 3-Role Parent / Teacher / Student Hub, Live Homework Assignment & Turn-In, Family Profiles Coach & Classroom Admin!)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivityTabScreen(
    profile: LearnerProfile,
    allProfiles: List<LearnerProfile> = listOf(profile),
    allBooks: List<StoryBook>,
    readingGroups: List<ReadingGroup>,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStarClick: () -> Unit,
    onOpenBook: (StoryBook) -> Unit,
    onSwitchToLibraryTab: () -> Unit = {},
    onSelectProfile: (String) -> Unit = {},
    onUpdateDailyGoal: (Int) -> Unit,
    onCreateReadingGroup: (String, String) -> Unit,
    onJoinReadingGroup: (String) -> Unit,
    onJoinReadingGroupForProfile: (String, LearnerProfile) -> Unit = { code, _ -> onJoinReadingGroup(code) },
    onUpdateReadingGroup: (ReadingGroup) -> Unit,
    onDeleteReadingGroup: (String) -> Unit,
    onSpeakText: (String) -> Unit
) {
    var selectedWeekTab by remember { mutableStateOf("This week") } // "This week" | "Last week" (res/S2.xml)
    var selectedHubRole by remember { mutableStateOf("Student") } // "Student" | "Parent" | "Teacher"
    var showCreateGroupWizard by remember { mutableStateOf(false) }
    var showJoinGroupWizard by remember { mutableStateOf(false) }
    var showAllCompletedBooks by remember { mutableStateOf(false) }
    var selectedGroupForAdmin by remember { mutableStateOf<ReadingGroup?>(null) }
    var groupIntroPage by remember { mutableIntStateOf(0) }

    val completedBooks = remember(allBooks, profile.completedBookIds) {
        allBooks.filter { profile.completedBookIds.contains(it.id) }
    }
    val speedVehicle = remember(profile.wordsPerMinute) {
        SeekhCatalog.getVehicleForWpm(profile.wordsPerMinute)
    }

    // Sub-state BackHandler for open Reading Group screen or Wizards
    BackHandler(enabled = selectedGroupForAdmin != null || showCreateGroupWizard || showJoinGroupWizard) {
        when {
            selectedGroupForAdmin != null -> selectedGroupForAdmin = null
            showCreateGroupWizard -> showCreateGroupWizard = false
            showJoinGroupWizard -> showJoinGroupWizard = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SeekhSurfaceGray)
    ) {
        CurvedGreenHeaderBackground(headerHeight = 195.dp, curveDepth = 32.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SeekhMainTopBar(
                stars = profile.stars,
                avatarIndex = profile.avatarIndex,
                onMenuClick = onMenuClick,
                onStarClick = onStarClick,
                onProfileClick = onProfileClick
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 135.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top "This week" | "Last week" Toggle Pill Bar (matching res/S2.xml lines 51-85!)
                item {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        shadowElevation = 0.dp,
                        border = BorderStroke(1.dp, SeekhGreen100),
                        modifier = Modifier
                            .fillMaxWidth()
                            .softCardShadow(elevation = 10.dp, shape = RoundedCornerShape(50))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            listOf("This week", "Last week").forEach { tab ->
                                val active = selectedWeekTab == tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (active) SeekhGreen500 else Color.Transparent)
                                        .clickable {
                                            selectedWeekTab = tab
                                            onSpeakText(tab)
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (active) Color.White else SeekhTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 1. "Daily goal" Card matching res/tL.xml (CircularProgressIndicator + "Today: %d min" + Real-time status + Interactive Goal Selector + 7-day Mon-Sun checkmark strip)
                item {
                    val goalMetToday = profile.minutesReadToday >= profile.dailyGoalMinutes
                    val remainingMin = (profile.dailyGoalMinutes - profile.minutesReadToday).coerceAtLeast(0)
                    val goalStatusMessage = when {
                        selectedWeekTab != "This week" -> "You met your daily goal ${profile.weeklyMinutes.count { it >= profile.dailyGoalMinutes }} days this week"
                        goalMetToday -> "Yay! You have completed today’s ${profile.dailyGoalMinutes} min goal!"
                        profile.minutesReadToday > 0 -> "Keep going! Read $remainingMin more min to complete today’s goal."
                        else -> "Read a story or play a word game to start today’s ${profile.dailyGoalMinutes} min goal!"
                    }

                    val goalShape = RoundedCornerShape(24.dp)
                    Surface(
                        shape = goalShape,
                        color = Color.White,
                        shadowElevation = 0.dp,
                        border = BorderStroke(1.2.dp, SeekhGreen100),
                        modifier = Modifier
                            .fillMaxWidth()
                            .softCardShadow(elevation = 14.dp, shape = goalShape)
                            .bouncyClick(scaleDown = 0.98f) {
                                onSpeakText(goalStatusMessage)
                            }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daily goal",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = SeekhTextPrimary
                                )
                                // Quick Daily Goal Selector Pills (5m / 10m / 15m / 20m)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(5, 10, 15, 20).forEach { goalOpt ->
                                        val isCurrentGoal = profile.dailyGoalMinutes == goalOpt
                                        Surface(
                                            modifier = Modifier.bouncyClick { onUpdateDailyGoal(goalOpt) },
                                            shape = RoundedCornerShape(50),
                                            color = if (isCurrentGoal) SeekhGreen500 else SeekhSurfaceGray
                                        ) {
                                            Text(
                                                text = "${goalOpt}m",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (isCurrentGoal) Color.White else SeekhTextSecondary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left: CircularProgressIndicator around star/clock + "Today: %d min" (res/tL.xml lines 29-68)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(64.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            progress = {
                                                (profile.minutesReadToday.toFloat() / profile.dailyGoalMinutes.coerceAtLeast(1).toFloat()).coerceIn(0.04f, 1f)
                                            },
                                            modifier = Modifier.fillMaxSize(),
                                            color = if (goalMetToday) SeekhGreenMint else SeekhGreen500,
                                            trackColor = SeekhGreen100,
                                            strokeWidth = 6.dp
                                        )
                                        GoldenStarIcon(size = 28.dp)
                                    }
                                    Column {
                                        Text(
                                            text = "Today: ${profile.minutesReadToday} min",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp,
                                            color = SeekhTextPrimary
                                        )
                                        Text(
                                            text = "Your daily goal is ${profile.dailyGoalMinutes} min • Streak: ${profile.streakDays}d",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = SeekhGreen500
                                        )
                                    }
                                }

                                CustomScholarOwlIllustration(
                                    modifier = Modifier.size(72.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dynamic real-time goal status banner
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (goalMetToday) SeekhGreenMint.copy(alpha = 0.14f) else SeekhGreenSoft,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = goalStatusMessage,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (goalMetToday) SeekhVocabDarkGreen else SeekhGreen900,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 7-Day Mon-Sun Checkmark Strip (res/tL.xml line 115)
                            val weekDayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                weekDayLabels.forEachIndexed { idx, dayName ->
                                    val mins = profile.weeklyMinutes.getOrElse(idx) { 0 }
                                    val goalReached = mins >= profile.dailyGoalMinutes
                                    val hasRead = mins > 0
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        goalReached -> SeekhGreenMint
                                                        hasRead -> SeekhGreenSoft
                                                        else -> SeekhSurfaceGray
                                                    }
                                                )
                                                .border(
                                                    1.dp,
                                                    when {
                                                        goalReached -> SeekhGreenMint
                                                        hasRead -> SeekhGreen500
                                                        else -> SeekhBorderGray
                                                    },
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (goalReached) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Goal met",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else if (hasRead) {
                                                Text(
                                                    text = "${mins}m",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    color = SeekhGreen600
                                                )
                                            }
                                        }
                                        Text(
                                            text = dayName,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = if (goalReached || hasRead) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = if (goalReached || hasRead) SeekhTextPrimary else SeekhTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. "Weekly Activity" Card matching res/_m.xml ("Weekly Activity" + Real Total min + Speed Vehicle + 2 Stat Boxes)
                item {
                    val totalMin = profile.weeklyMinutes.sum()
                    val displayWpm = profile.wordsPerMinute

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Weekly Activity",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = SeekhTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Total $totalMin min read • ${profile.gamesPlayedCount} games played",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = SeekhTextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Speed Vehicle Row + LinearProgressIndicator (res/_m.xml lines 30-46)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${speedVehicle.emoji} ${speedVehicle.name}",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SeekhGreen500
                                )
                                Text(
                                    text = "$displayWpm wpm",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SeekhGreen600
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (displayWpm / 100f).coerceIn(0.15f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = SeekhGreen500,
                                trackColor = SeekhGreen100
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Two Side-by-Side Stat Boxes matching res/_m.xml lines 50-145 ("Time" in min & "Average reading speed this week" in wpm)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = SeekhGreenSoft,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "$totalMin",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 26.sp,
                                                color = SeekhGreen600
                                            )
                                            Text(
                                                text = "min",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SeekhGreen600,
                                                modifier = Modifier.padding(bottom = 3.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Time",
                                            fontFamily = GoogleSansFamily,
                                            fontSize = 12.sp,
                                            color = SeekhTextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = SeekhBlueSoft,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "$displayWpm",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 26.sp,
                                                color = SeekhBlue600
                                            )
                                            Text(
                                                text = "wpm",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SeekhBlue600,
                                                modifier = Modifier.padding(bottom = 3.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Reading speed",
                                            fontFamily = GoogleSansFamily,
                                            fontSize = 12.sp,
                                            color = SeekhTextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. "Stories completed" Card matching res/hn.xml ("Stories completed" + RecyclerView of covers or Empty State + "Read stories" button + "Show more" / "Show less")
                item {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Stories completed",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = SeekhTextPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SeekhGreenSoft
                                ) {
                                    Text(
                                        text = "${completedBooks.size}",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = SeekhGreen600,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (completedBooks.isEmpty()) {
                                // Empty state from res/hn.xml lines 28-55 ("No stories read this week" + "Read stories" button)
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (selectedWeekTab == "This week") "No stories read this week" else "No stories read last week",
                                        fontFamily = GoogleSansFamily,
                                        fontSize = 14.sp,
                                        color = SeekhTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = onSwitchToLibraryTab,
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                    ) {
                                        Text(
                                            text = "Read stories",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                val visibleCompleted = if (showAllCompletedBooks) completedBooks else completedBooks.take(3)
                                val rows = visibleCompleted.chunked(3)
                                rows.forEach { rBooks ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        for (i in 0 until 3) {
                                            val b = rBooks.getOrNull(i)
                                            if (b != null) {
                                                StoryBookCard(
                                                    book = b,
                                                    isCompleted = true,
                                                    onClick = { onOpenBook(b) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }

                                if (completedBooks.size > 3) {
                                    TextButton(
                                        onClick = { showAllCompletedBooks = !showAllCompletedBooks },
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text = if (showAllCompletedBooks) "Show less" else "Show more",
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

                // 4. "Parent, Teacher & Student Hub" & "Your reading groups" Section (3-Role Switcher: Student Homework | Parent Coach | Teacher Classroom Admin)
                item {
                    val assignedBookIdsSet = remember(readingGroups) {
                        readingGroups.flatMap { it.assignedBookIds }.toSet()
                    }
                    val assignedBooksList = remember(allBooks, assignedBookIdsSet) {
                        allBooks.filter { assignedBookIdsSet.contains(it.id) }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Divider Header ("Your reading groups & classroom")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SeekhBorderGray)
                            Text(
                                text = "Your reading groups",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SeekhTextPrimary
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SeekhBorderGray)
                        }

                        // 3-Role Switcher Pill Bar: Student | Parent | Teacher
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.dp, SeekhBorderGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(
                                    "Student" to "👨‍🎓 Student",
                                    "Parent" to "👨‍👩‍👧 Parent",
                                    "Teacher" to "👩‍🏫 Teacher"
                                ).forEach { (roleKey, roleLabel) ->
                                    val isSelectedRole = selectedHubRole == roleKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isSelectedRole) SeekhGreen500 else Color.Transparent)
                                            .bouncyClick {
                                                selectedHubRole = roleKey
                                                onSpeakText("$roleKey view")
                                            }
                                            .padding(vertical = 9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = roleLabel,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelectedRole) Color.White else SeekhTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        when (selectedHubRole) {
                            "Student" -> {
                                // STUDENT FLOW: My Assigned Homework Checklist + Join Classroom by Invite Code
                                Surface(
                                    shape = RoundedCornerShape(22.dp),
                                    color = Color.White,
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${profile.name}'s Assigned Homework",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 17.sp,
                                                    color = SeekhTextPrimary
                                                )
                                                val doneHomework = assignedBooksList.count { profile.completedBookIds.contains(it.id) }
                                                Text(
                                                    text = if (assignedBooksList.isEmpty()) {
                                                        "Join a teacher group or ask a parent to assign stories"
                                                    } else {
                                                        "Completed $doneHomework of ${assignedBooksList.size} assigned stories"
                                                    },
                                                    fontFamily = GoogleSansFamily,
                                                    fontSize = 12.sp,
                                                    color = SeekhGreen600
                                                )
                                            }
                                            OutlinedButton(
                                                onClick = { showJoinGroupWizard = true },
                                                shape = RoundedCornerShape(50),
                                                border = BorderStroke(1.5.dp, SeekhGreen500),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "🔑 Join Code",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = SeekhGreen600
                                                )
                                            }
                                        }

                                        if (assignedBooksList.isNotEmpty()) {
                                            val doneCount = assignedBooksList.count { profile.completedBookIds.contains(it.id) }
                                            val hwProg = (doneCount.toFloat() / assignedBooksList.size.coerceAtLeast(1).toFloat()).coerceIn(0.05f, 1f)
                                            LinearProgressIndicator(
                                                progress = { hwProg },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(7.dp)
                                                    .clip(RoundedCornerShape(50)),
                                                color = if (doneCount == assignedBooksList.size) SeekhGreenMint else SeekhGreen500,
                                                trackColor = SeekhGreen100
                                            )

                                            assignedBooksList.forEach { hwBook ->
                                                val isDone = profile.completedBookIds.contains(hwBook.id)
                                                val groupOwner = readingGroups.firstOrNull { it.assignedBookIds.contains(hwBook.id) }
                                                Surface(
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = if (isDone) SeekhGreenSoft else SeekhSurfaceGray,
                                                    border = BorderStroke(1.dp, if (isDone) SeekhGreen500 else SeekhBorderGray),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .bouncyClick { onOpenBook(hwBook) }
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.weight(1f),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(50.dp)
                                                                    .clip(RoundedCornerShape(12.dp))
                                                            ) {
                                                                AssetWebpImage(
                                                                    assetPath = hwBook.coverAssetPath,
                                                                    contentDescription = hwBook.title,
                                                                    modifier = Modifier.fillMaxSize(),
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                            }
                                                            Column {
                                                                Text(
                                                                    text = hwBook.title,
                                                                    fontFamily = GoogleSansFamily,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 14.sp,
                                                                    color = SeekhTextPrimary,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )
                                                                Text(
                                                                    text = if (groupOwner != null) {
                                                                        "From ${groupOwner.adminName} (${groupOwner.name}) • Lvl ${hwBook.level}"
                                                                    } else {
                                                                        "Level ${hwBook.level} Story"
                                                                    },
                                                                    fontFamily = GoogleSansFamily,
                                                                    fontSize = 11.sp,
                                                                    color = SeekhTextSecondary
                                                                )
                                                            }
                                                        }

                                                        Surface(
                                                            shape = RoundedCornerShape(50),
                                                            color = if (isDone) SeekhGreenMint else SeekhGreen500
                                                        ) {
                                                            Text(
                                                                text = if (isDone) "Turned in ✓" else "Read ▶",
                                                                fontFamily = GoogleSansFamily,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 11.sp,
                                                                color = Color.White,
                                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = SeekhSurfaceGray,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = "No active homework assignments yet.",
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = SeekhTextPrimary
                                                    )
                                                    Text(
                                                        text = "Enter a class invite code from your teacher, or switch to Parent / Teacher tab to assign stories!",
                                                        fontFamily = GoogleSansFamily,
                                                        fontSize = 12.sp,
                                                        color = SeekhTextSecondary,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                        Button(
                                                            onClick = { showJoinGroupWizard = true },
                                                            shape = RoundedCornerShape(50),
                                                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                                        ) {
                                                            Text("Join group", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        }
                                                        OutlinedButton(
                                                            onClick = { selectedHubRole = "Parent" },
                                                            shape = RoundedCornerShape(50)
                                                        ) {
                                                            Text("Parent Assign →", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            "Parent" -> {
                                // PARENT FLOW: Family Children Progress Comparison + 1-Tap Parent Homework Assigner
                                Surface(
                                    shape = RoundedCornerShape(22.dp),
                                    color = Color.White,
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "👨‍👩‍👧 Family Profiles Progress (${allProfiles.size})",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = SeekhTextPrimary
                                        )
                                        Text(
                                            text = "Monitor every child on this device, switch active learner, or assign daily homework stories:",
                                            fontFamily = GoogleSansFamily,
                                            fontSize = 12.sp,
                                            color = SeekhTextSecondary
                                        )

                                        allProfiles.forEach { childProf ->
                                            val isCurrent = childProf.id == profile.id
                                            val goalReached = childProf.minutesReadToday >= childProf.dailyGoalMinutes
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = if (isCurrent) SeekhGreenSoft else SeekhSurfaceGray,
                                                border = BorderStroke(1.5.dp, if (isCurrent) SeekhGreen500 else SeekhBorderGray),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .bouncyClick {
                                                        if (!isCurrent) onSelectProfile(childProf.id)
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        ProfileAvatarCircle(
                                                            avatarIndex = childProf.avatarIndex,
                                                            size = 44.dp,
                                                            borderWidth = 1.5.dp,
                                                            borderColor = if (isCurrent) SeekhGreen500 else SeekhBorderGray
                                                        )
                                                        Column {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                            ) {
                                                                Text(
                                                                    text = childProf.name,
                                                                    fontFamily = GoogleSansFamily,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 15.sp,
                                                                    color = SeekhTextPrimary
                                                                )
                                                                if (isCurrent) {
                                                                    Surface(
                                                                        shape = RoundedCornerShape(50),
                                                                        color = SeekhGreen500
                                                                    ) {
                                                                        Text(
                                                                            text = "Active",
                                                                            fontFamily = GoogleSansFamily,
                                                                            fontWeight = FontWeight.Bold,
                                                                            fontSize = 10.sp,
                                                                            color = Color.White,
                                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            Text(
                                                                text = "Today: ${childProf.minutesReadToday}/${childProf.dailyGoalMinutes}m • ${childProf.completedBookIds.size} stories • ${childProf.gamesPlayedCount} games",
                                                                fontFamily = GoogleSansFamily,
                                                                fontSize = 11.sp,
                                                                color = if (goalReached) SeekhGreen600 else SeekhTextSecondary
                                                            )
                                                        }
                                                    }
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Text(
                                                            text = "${childProf.stars} ⭐",
                                                            fontFamily = GoogleSansFamily,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 14.sp,
                                                            color = SeekhGreen600
                                                        )
                                                        Text(
                                                            text = "${childProf.wordsPerMinute} wpm",
                                                            fontFamily = GoogleSansFamily,
                                                            fontSize = 11.sp,
                                                            color = SeekhBlue600
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        HorizontalDivider(color = SeekhBorderGray)

                                        // Quick Parent Story Assigner
                                        Text(
                                            text = "📚 Assign Daily Stories to ${profile.name}",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SeekhTextPrimary
                                        )
                                        val parentGroup = readingGroups.firstOrNull()
                                        allBooks.take(4).forEach { bookItem ->
                                            val isAssigned = assignedBookIdsSet.contains(bookItem.id)
                                            val isCompletedByChild = profile.completedBookIds.contains(bookItem.id)
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = SeekhSurfaceGray,
                                                border = BorderStroke(1.dp, if (isAssigned) SeekhGreen500 else SeekhBorderGray),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        modifier = Modifier.weight(1f),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(42.dp)
                                                                .clip(RoundedCornerShape(10.dp))
                                                        ) {
                                                            AssetWebpImage(
                                                                assetPath = bookItem.coverAssetPath,
                                                                contentDescription = bookItem.title,
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                        }
                                                        Column {
                                                            Text(
                                                                text = bookItem.title,
                                                                fontFamily = GoogleSansFamily,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                color = SeekhTextPrimary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = if (isCompletedByChild) "Level ${bookItem.level} • Completed ✓" else "Level ${bookItem.level} Story",
                                                                fontFamily = GoogleSansFamily,
                                                                fontSize = 11.sp,
                                                                color = if (isCompletedByChild) SeekhGreen600 else SeekhTextSecondary
                                                            )
                                                        }
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(50),
                                                        color = if (isAssigned) SeekhGreen500 else Color.White,
                                                        border = BorderStroke(1.dp, SeekhGreen500),
                                                        modifier = Modifier.bouncyClick {
                                                            if (parentGroup != null) {
                                                                val updatedIds = if (parentGroup.assignedBookIds.contains(bookItem.id)) {
                                                                    parentGroup.assignedBookIds - bookItem.id
                                                                } else {
                                                                    parentGroup.assignedBookIds + bookItem.id
                                                                }
                                                                onUpdateReadingGroup(parentGroup.copy(assignedBookIds = updatedIds))
                                                            } else {
                                                                onCreateReadingGroup("Family Reading Plan", "Parent Coach")
                                                            }
                                                        }
                                                    ) {
                                                        Text(
                                                            text = if (isAssigned) "Assigned ✓" else "+ Assign",
                                                            fontFamily = GoogleSansFamily,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            color = if (isAssigned) Color.White else SeekhGreen600,
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else -> {
                                // TEACHER FLOW: 3-Slide Intro Carousel + Create / Join Reading Group Wizards
                                val introSlides = listOf(
                                    "Discover a new way to help children learn language.",
                                    "You can create reading group and invite multiple children to join.",
                                    "You’ll be able to monitor each child’s reading activity and speed to improve their reading."
                                )
                                Surface(
                                    shape = RoundedCornerShape(22.dp),
                                    color = Color.White,
                                    shadowElevation = 3.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = introSlides[groupIntroPage % introSlides.size],
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = SeekhTextPrimary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.clickable {
                                                groupIntroPage = (groupIntroPage + 1) % introSlides.size
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            introSlides.indices.forEach { idx ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (idx == groupIntroPage) 18.dp else 7.dp, 7.dp)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(if (idx == groupIntroPage) SeekhGreen500 else SeekhBorderGray)
                                                        .clickable { groupIntroPage = idx }
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { showJoinGroupWizard = true },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(50),
                                                border = BorderStroke(1.5.dp, SeekhGreen500)
                                            ) {
                                                Text(
                                                    text = "Join group",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = SeekhGreen500
                                                )
                                            }
                                            Button(
                                                onClick = { showCreateGroupWizard = true },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(50),
                                                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                            ) {
                                                Text(
                                                    text = "Create reading group",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color.White,
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
                }

                // Active Reading Group Cards (tapping opens the 3-Tab Reading Group Classroom Admin Screen)
                items(readingGroups) { group ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .bouncyClick {
                                selectedGroupForAdmin = group
                                onSpeakText("Opening ${group.name} reading group.")
                            }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = group.name,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = SeekhTextPrimary
                                    )
                                    Text(
                                        text = "Admin: ${group.adminName} • Code: ${group.inviteCode} • ${group.assignedBookIds.size} homework stories",
                                        fontFamily = GoogleSansFamily,
                                        fontSize = 12.sp,
                                        color = SeekhTextSecondary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SeekhGreenSoft
                                ) {
                                    Text(
                                        text = "Manage →",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SeekhGreen600,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Active children (${group.members.count { it.isActiveThisWeek }}/${group.members.size})",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SeekhGreen600
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            group.members.take(4).forEach { member ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        ProfileAvatarCircle(
                                            avatarIndex = member.avatarIndex,
                                            size = 36.dp,
                                            borderWidth = 1.dp,
                                            borderColor = SeekhBorderGray
                                        )
                                        Column {
                                            Text(
                                                text = member.name,
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SeekhTextPrimary
                                            )
                                            Text(
                                                text = "${member.storiesRead} stories • ${member.gamesPlayed} games • ${member.stars} ⭐",
                                                fontFamily = GoogleSansFamily,
                                                fontSize = 11.sp,
                                                color = SeekhTextSecondary
                                            )
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "${member.minutesThisWeek} min",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SeekhGreen600
                                        )
                                        Text(
                                            text = "• ${member.speedWpm} wpm",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = SeekhBlue600
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

    // 3-Tab Reading Group Classroom Admin Screen (Students Roster | Homework Assigner | Settings & Invite)
    selectedGroupForAdmin?.let { activeGroup ->
        val latestGroup = readingGroups.firstOrNull { it.id == activeGroup.id } ?: activeGroup
        ReadingGroupAdminDashboardDialog(
            group = latestGroup,
            allProfiles = allProfiles,
            allBooks = allBooks,
            onOpenBook = { book ->
                selectedGroupForAdmin = null
                onOpenBook(book)
            },
            onUpdateGroup = { updated ->
                selectedGroupForAdmin = updated
                onUpdateReadingGroup(updated)
            },
            onDeleteGroup = { groupId ->
                selectedGroupForAdmin = null
                onDeleteReadingGroup(groupId)
            },
            onSpeakText = onSpeakText,
            onDismiss = { selectedGroupForAdmin = null }
        )
    }

    // Multi-Step "Create reading group" Guided Wizard
    if (showCreateGroupWizard) {
        CreateReadingGroupWizardDialog(
            profiles = allProfiles,
            activeProfile = profile,
            onDismiss = { showCreateGroupWizard = false },
            onGroupCreated = { groupName, adminName ->
                onCreateReadingGroup(groupName, adminName)
                showCreateGroupWizard = false
            }
        )
    }

    // Multi-Step "Join a reading group" Guided Wizard
    if (showJoinGroupWizard) {
        JoinReadingGroupWizardDialog(
            profiles = allProfiles,
            activeProfile = profile,
            onDismiss = { showJoinGroupWizard = false },
            onGroupJoined = { inviteCode, chosenProfile ->
                onJoinReadingGroupForProfile(inviteCode, chosenProfile)
                showJoinGroupWizard = false
            }
        )
    }
}

// 3b. Authentic 5-Step "Create reading group" Guided Wizard (with Real Interactive Adult Math Gate in Step 3!)
@Composable
fun CreateReadingGroupWizardDialog(
    profiles: List<LearnerProfile>,
    activeProfile: LearnerProfile,
    onDismiss: () -> Unit,
    onGroupCreated: (String, String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Intro & Privacy, 2: Select Admin Profile, 3: Adult Verify Math Gate, 4: Select Language, 5: Name Group & Confirmation
    var selectedAdminProfile by remember { mutableStateOf(activeProfile) }
    var selectedLang by remember { mutableStateOf(SeekhCatalog.supportedLanguages.first()) }
    var groupName by remember { mutableStateOf("") }
    var selectedMathAnswer by remember { mutableIntStateOf(24) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        IconButton(onClick = { step-- }, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }
                    Text(
                        text = "Create reading group (Step $step/5)",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SeekhGreen600
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                when (step) {
                    1 -> {
                        Text(
                            text = "Your privacy is important to us.",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "• You can select a language for your reading group and track each child’s weekly reading in that language.\n• Your photo will not be visible to any child or parent.\n• Children can see your name and the number of active participants, but cannot view each other's activity or photo.\n• Only you can view the children activities and names.",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = SeekhTextSecondary
                        )
                    }
                    2 -> {
                        Text(
                            text = "Select your profile",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "This profile will have the admin rights of the reading group:",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            color = SeekhTextSecondary
                        )
                        profiles.forEach { p ->
                            val chosen = p.id == selectedAdminProfile.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (chosen) SeekhGreenSoft else SeekhSurfaceGray,
                                border = BorderStroke(1.5.dp, if (chosen) SeekhGreen500 else SeekhBorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedAdminProfile = p }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    ProfileAvatarCircle(avatarIndex = p.avatarIndex, size = 44.dp)
                                    Text(
                                        text = "${p.name} (Teacher / Admin)",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SeekhTextPrimary
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        Text(
                            text = "Adult Teacher / Parent Gate",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "Please solve this adult verification check to manage the reading group as a teacher or parent: What is 6 × 4?",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = SeekhTextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(18, 24, 28, 32).forEach { ans ->
                                val isChosen = selectedMathAnswer == ans
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isChosen) SeekhGreen500 else SeekhSurfaceGray,
                                    border = BorderStroke(1.dp, if (isChosen) SeekhGreen500 else SeekhBorderGray),
                                    modifier = Modifier
                                        .weight(1f)
                                        .bouncyClick { selectedMathAnswer = ans }
                                ) {
                                    Text(
                                        text = "$ans",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = if (isChosen) Color.White else SeekhTextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                        if (selectedMathAnswer == 24) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SeekhGreenSoft,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = SeekhGreen600)
                                    Text(
                                        text = "Adult Teacher / Parent Verified (${selectedAdminProfile.name})",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SeekhGreen900
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        Text(
                            text = "Select the language of stories for the reading group",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "You will be able to view the children's activity for the chosen language.",
                            fontFamily = GoogleSansFamily,
                            fontSize = 13.sp,
                            color = SeekhTextSecondary
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 210.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SeekhCatalog.supportedLanguages.forEach { lang ->
                                val chosen = lang.code == selectedLang.code
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (chosen) SeekhGreenSoft else SeekhSurfaceGray,
                                    border = BorderStroke(1.5.dp, if (chosen) SeekhGreen500 else SeekhBorderGray),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedLang = lang }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = lang.title,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SeekhTextPrimary
                                        )
                                        if (chosen) {
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SeekhGreen500)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Name your reading group",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("Enter group name here", fontFamily = GoogleSansFamily) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "${selectedLang.title} reading group will be created with admin: ${selectedAdminProfile.name}",
                            fontFamily = GoogleSansFamily,
                            fontSize = 13.sp,
                            color = SeekhGreen600
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step == 3 && selectedMathAnswer != 24) return@Button
                        if (step < 5) {
                            step++
                        } else {
                            val finalGroup = groupName.trim().ifEmpty { "Class 3 Readers" }
                            onGroupCreated(finalGroup, selectedAdminProfile.name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text(
                        text = when (step) {
                            3 -> "Verify & Continue"
                            5 -> "Go to the reading group and invite children"
                            else -> "Continue"
                        },
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// 3c. Authentic 3-Step "Join a reading group" Guided Wizard (Passes chosen Student Profile + Adult Verification!)
@Composable
fun JoinReadingGroupWizardDialog(
    profiles: List<LearnerProfile>,
    activeProfile: LearnerProfile,
    onDismiss: () -> Unit,
    onGroupJoined: (String, LearnerProfile) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Enter Code & Intro, 2: Select Child Profile & Privacy, 3: Adult Verify & Go to group now
    var inviteCode by remember { mutableStateOf("SEEKH-305") }
    var selectedChildProfile by remember { mutableStateOf(activeProfile) }
    var selectedMathAnswer by remember { mutableIntStateOf(15) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        IconButton(onClick = { step-- }, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }
                    Text(
                        text = "Join a reading group (Step $step/3)",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SeekhGreen600
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                when (step) {
                    1 -> {
                        Text(
                            text = "Your child is invited to join this reading group:",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "• Reading group is created by teacher / admin where multiple children can read stories.\n• The teacher / admin sets the language and assigns homework stories.\n• They can follow your child’s progress to help improve their reading.",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = SeekhTextSecondary
                        )
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = { inviteCode = it },
                            label = { Text("Enter invite code (e.g. SEEKH-305)", fontFamily = GoogleSansFamily) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        Text(
                            text = "Select a profile to join the reading group",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "Your child’s privacy is important for us: Only the teacher / admin can view your child’s name, activity and progress, but no one can see your child’s photo.",
                            fontFamily = GoogleSansFamily,
                            fontSize = 13.sp,
                            color = SeekhTextSecondary
                        )
                        profiles.forEach { p ->
                            val chosen = p.id == selectedChildProfile.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (chosen) SeekhGreenSoft else SeekhSurfaceGray,
                                border = BorderStroke(1.5.dp, if (chosen) SeekhGreen500 else SeekhBorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedChildProfile = p }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    ProfileAvatarCircle(avatarIndex = p.avatarIndex, size = 44.dp)
                                    Column {
                                        Text(
                                            text = p.name,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SeekhTextPrimary
                                        )
                                        Text(
                                            text = "${p.completedBookIds.size} stories read • ${p.stars} ⭐",
                                            fontFamily = GoogleSansFamily,
                                            fontSize = 12.sp,
                                            color = SeekhGreen600
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Parent / Adult Verification",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "Please verify that you are an adult authorizing ${selectedChildProfile.name} to join group (${inviteCode.uppercase()}): What is 5 × 3?",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            color = SeekhTextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(12, 15, 18, 20).forEach { ans ->
                                val isChosen = selectedMathAnswer == ans
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isChosen) SeekhGreen500 else SeekhSurfaceGray,
                                    border = BorderStroke(1.dp, if (isChosen) SeekhGreen500 else SeekhBorderGray),
                                    modifier = Modifier
                                        .weight(1f)
                                        .bouncyClick { selectedMathAnswer = ans }
                                ) {
                                    Text(
                                        text = "$ans",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = if (isChosen) Color.White else SeekhTextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (step == 3 && selectedMathAnswer != 15) return@Button
                        if (step < 3) {
                            step++
                        } else {
                            onGroupJoined(inviteCode.trim().ifEmpty { "SEEKH-305" }, selectedChildProfile)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                ) {
                    Text(
                        text = if (step < 3) "Continue" else "Go to the group now",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// 3d. Reading Group 3-Tab Classroom Admin Dashboard (Students Roster & Detailed Report | Homework Assigner | Settings & Invite)
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReadingGroupAdminDashboardDialog(
    group: ReadingGroup,
    allProfiles: List<LearnerProfile>,
    allBooks: List<StoryBook>,
    onOpenBook: (StoryBook) -> Unit,
    onUpdateGroup: (ReadingGroup) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tabs = listOf("Students", "Homework", "Settings")
    var selectedTab by remember { mutableStateOf("Students") }
    var metricFilter by remember { mutableStateOf("Overall activity") } // Overall activity | Reading | Speed
    var selectedStudentReport by remember { mutableStateOf<GroupMemberStat?>(null) }
    var editedName by remember(group.name) { mutableStateOf(group.name) }
    var newStudentName by remember { mutableStateOf("") }
    var linkCopiedBanner by remember { mutableStateOf(false) }

    val unEnrolledDeviceProfiles = remember(allProfiles, group.members) {
        allProfiles.filter { p ->
            group.members.none { m ->
                (m.profileId.isNotBlank() && m.profileId == p.id) || m.name.equals(p.name, ignoreCase = true)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header with Invite Code Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SeekhGreen500)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = group.name,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Teacher: ${group.adminName} • Invite Code: ${group.inviteCode}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // 3 Tabs Bar (Students | Homework | Settings)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SeekhSurfaceGray)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        val active = selectedTab == tab
                        val label = when (tab) {
                            "Students" -> "Students (${group.members.size})"
                            "Homework" -> "Homework (${group.assignedBookIds.size})"
                            else -> "Settings"
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (active) SeekhGreen500 else Color.White,
                            border = BorderStroke(1.dp, if (active) SeekhGreen500 else SeekhBorderGray),
                            modifier = Modifier
                                .weight(1f)
                                .bouncyClick { selectedTab = tab }
                        ) {
                            Text(
                                text = label,
                                fontFamily = GoogleSansFamily,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (active) Color.White else SeekhTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }

                // Tab Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (selectedTab) {
                        "Students" -> {
                            // Metric Filter Chips (Overall activity | Reading | Speed)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Overall activity", "Reading", "Speed").forEach { chip ->
                                    val chosen = metricFilter == chip
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (chosen) SeekhBlueSoft else SeekhSurfaceGray,
                                        border = BorderStroke(1.dp, if (chosen) SeekhBlue500 else SeekhBorderGray),
                                        modifier = Modifier.clickable { metricFilter = chip }
                                    ) {
                                        Text(
                                            text = chip,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = if (chosen) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = if (chosen) SeekhBlue600 else SeekhTextSecondary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Tap any student to view their detailed homework & reading speed report:",
                                fontFamily = GoogleSansFamily,
                                fontSize = 12.sp,
                                color = SeekhTextSecondary
                            )

                            group.members.forEachIndexed { index, member ->
                                val vehicle = SeekhCatalog.getVehicleForWpm(member.speedWpm)
                                val isExpanded = selectedStudentReport?.name == member.name
                                val hwDoneCount = group.assignedBookIds.count { member.completedBookIds.contains(it) }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isExpanded) SeekhGreenSoft else SeekhSurfaceGray,
                                    border = BorderStroke(if (isExpanded) 1.5.dp else 1.dp, if (isExpanded) SeekhGreen500 else SeekhBorderGray),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .bouncyClick {
                                            selectedStudentReport = if (isExpanded) null else member
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "#${index + 1}",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = SeekhGreen600
                                                )
                                                ProfileAvatarCircle(
                                                    avatarIndex = member.avatarIndex,
                                                    size = 42.dp,
                                                    borderWidth = 1.dp,
                                                    borderColor = SeekhGreen500
                                                )
                                                Column {
                                                    Text(
                                                        text = member.name,
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = SeekhTextPrimary
                                                    )
                                                    Text(
                                                        text = when (metricFilter) {
                                                            "Reading" -> "${member.storiesRead} stories • Homework: $hwDoneCount/${group.assignedBookIds.size}"
                                                            "Speed" -> "${vehicle.name} ${vehicle.emoji} • ${member.stars} ⭐"
                                                            else -> "Stories (${member.storiesRead}) • Games (${member.gamesPlayed}) • HW ($hwDoneCount/${group.assignedBookIds.size})"
                                                        },
                                                        fontFamily = GoogleSansFamily,
                                                        fontSize = 11.sp,
                                                        color = SeekhTextSecondary
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = if (metricFilter == "Speed") "${member.speedWpm} wpm" else "${member.minutesThisWeek} min",
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 15.sp,
                                                    color = SeekhGreen600
                                                )
                                                Text(
                                                    text = if (metricFilter == "Speed") "${member.minutesThisWeek} min" else "${member.speedWpm} wpm",
                                                    fontFamily = GoogleSansFamily,
                                                    fontSize = 11.sp,
                                                    color = SeekhBlue600
                                                )
                                            }
                                        }

                                        // Expanded Student Progress Report Card
                                        if (isExpanded) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            HorizontalDivider(color = SeekhGreen500.copy(alpha = 0.25f))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "📊 ${member.name}'s Classroom Report Card",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = SeekhGreen900
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "• Speed Rank: ${vehicle.emoji} ${vehicle.name} (${member.speedWpm} WPM)\n• Total Stars: ${member.stars} ⭐ • Games Played: ${member.gamesPlayed}\n• Homework Completed: $hwDoneCount of ${group.assignedBookIds.size} assigned stories",
                                                fontFamily = GoogleSansFamily,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                color = SeekhTextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            // 1-Tap Enroll Device Profiles + Clean Manual Student Addition (Starts at real 0 stats, never fake numbers!)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SeekhGreenSoft,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Enroll student in ${group.name}",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SeekhGreen900
                                    )

                                    if (unEnrolledDeviceProfiles.isNotEmpty()) {
                                        Text(
                                            text = "Quick-enroll child profiles on this device (Live Synced):",
                                            fontFamily = GoogleSansFamily,
                                            fontSize = 12.sp,
                                            color = SeekhGreen600
                                        )
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            unEnrolledDeviceProfiles.forEach { devProf ->
                                                Surface(
                                                    shape = RoundedCornerShape(50),
                                                    color = Color.White,
                                                    border = BorderStroke(1.dp, SeekhGreen500),
                                                    modifier = Modifier.bouncyClick {
                                                        val newMember = GroupMemberStat(
                                                            profileId = devProf.id,
                                                            name = devProf.name,
                                                            avatarIndex = devProf.avatarIndex,
                                                            minutesThisWeek = devProf.weeklyMinutes.sum().coerceAtLeast(devProf.minutesReadToday),
                                                            storiesRead = devProf.completedBookIds.size,
                                                            gamesPlayed = devProf.gamesPlayedCount,
                                                            speedWpm = devProf.wordsPerMinute,
                                                            stars = devProf.stars,
                                                            completedBookIds = devProf.completedBookIds,
                                                            isActiveThisWeek = true
                                                        )
                                                        onUpdateGroup(group.copy(members = group.members + newMember))
                                                        onSpeakText("${devProf.name} enrolled in ${group.name}!")
                                                    }
                                                ) {
                                                    Text(
                                                        text = "+ Enroll ${devProf.name}",
                                                        fontFamily = GoogleSansFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = SeekhGreen600,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = newStudentName,
                                            onValueChange = { newStudentName = it },
                                            placeholder = { Text("New student name", fontFamily = GoogleSansFamily, fontSize = 13.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                val sName = newStudentName.trim().ifEmpty { "Student ${group.members.size + 1}" }
                                                val added = GroupMemberStat(
                                                    name = sName,
                                                    avatarIndex = (group.members.size % 12) + 1,
                                                    minutesThisWeek = 0,
                                                    storiesRead = 0,
                                                    gamesPlayed = 0,
                                                    speedWpm = 0,
                                                    stars = 0,
                                                    isActiveThisWeek = false,
                                                    lastActiveLabel = "Just enrolled"
                                                )
                                                onUpdateGroup(group.copy(members = group.members + added))
                                                onSpeakText("$sName enrolled in ${group.name} starting at zero progress!")
                                                newStudentName = ""
                                            },
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                        ) {
                                            Text("+ Add", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        "Homework" -> {
                            // Teacher & Parent Story Homework Assigner Tab
                            Text(
                                text = "Assign stories to students in ${group.name}",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = SeekhTextPrimary
                            )
                            Text(
                                text = "Assigned stories appear immediately in the Student's Library & Activity Homework checklist:",
                                fontFamily = GoogleSansFamily,
                                fontSize = 12.sp,
                                color = SeekhTextSecondary
                            )

                            allBooks.forEach { book ->
                                val isAssigned = group.assignedBookIds.contains(book.id)
                                val completedByStudents = group.members.count { it.completedBookIds.contains(book.id) }
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isAssigned) SeekhGreenSoft else SeekhSurfaceGray,
                                    border = BorderStroke(1.dp, if (isAssigned) SeekhGreen500 else SeekhBorderGray),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { onOpenBook(book) },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                            ) {
                                                AssetWebpImage(
                                                    assetPath = book.coverAssetPath,
                                                    contentDescription = book.title,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = book.title,
                                                    fontFamily = GoogleSansFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = SeekhTextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Level ${book.level} • Completed by $completedByStudents/${group.members.size} students",
                                                    fontFamily = GoogleSansFamily,
                                                    fontSize = 11.sp,
                                                    color = if (isAssigned) SeekhGreen600 else SeekhTextSecondary
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = if (isAssigned) SeekhGreen500 else Color.White,
                                            border = BorderStroke(1.5.dp, SeekhGreen500),
                                            modifier = Modifier.bouncyClick {
                                                val updatedAssigned = if (isAssigned) {
                                                    group.assignedBookIds - book.id
                                                } else {
                                                    group.assignedBookIds + book.id
                                                }
                                                onUpdateGroup(group.copy(assignedBookIds = updatedAssigned))
                                                onSpeakText(
                                                    if (isAssigned) "Removed ${book.title} from homework."
                                                    else "Assigned ${book.title} to ${group.name}!"
                                                )
                                            }
                                        ) {
                                            Text(
                                                text = if (isAssigned) "Assigned ✓" else "+ Assign",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isAssigned) Color.White else SeekhGreen600,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        "Settings" -> {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SeekhSurfaceGray,
                                border = BorderStroke(1.dp, SeekhBorderGray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Reading group: ${group.name}", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Teacher / Admin: ${group.adminName}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                    Text("Invite Code: ${group.inviteCode}", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SeekhGreen600)
                                    Text("Language: ${group.languageTitle}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                    Text("Date created: ${group.createdDate}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                    Text("Members in this group: ${group.members.size}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                }
                            }

                            Text(
                                text = "Members in this group (Tap remove icon to remove member)",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SeekhTextSecondary
                            )
                            group.members.forEach { m ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        ProfileAvatarCircle(avatarIndex = m.avatarIndex, size = 32.dp)
                                        Text(m.name, fontFamily = GoogleSansFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    }
                                    if (group.members.size > 1) {
                                        IconButton(
                                            onClick = {
                                                onUpdateGroup(group.copy(members = group.members.filterNot { it.name == m.name }))
                                                onSpeakText("Removed ${m.name} from reading group.")
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PersonRemove,
                                                contentDescription = "Remove member",
                                                tint = SeekhCoralRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        linkCopiedBanner = true
                                        onSpeakText("Invitation code ${group.inviteCode} copied!")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                ) {
                                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Invite (${group.inviteCode})", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        linkCopiedBanner = true
                                        onSpeakText("Invitation link has been copied successfully")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (linkCopiedBanner) "Copied ✓" else "Copy invitation link",
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            Text(
                                text = "Edit group name",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SeekhTextPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = editedName,
                                    onValueChange = { editedName = it },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (editedName.isNotBlank()) {
                                            onUpdateGroup(group.copy(name = editedName.trim()))
                                            onSpeakText("Reading group renamed as ${editedName.trim()}")
                                        }
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = SeekhBlue600)
                                ) {
                                    Text("Done", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = SeekhBorderGray)

                            OutlinedButton(
                                onClick = {
                                    onSpeakText("Reading group ${group.name} deleted.")
                                    onDeleteGroup(group.id)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.5.dp, SeekhCoralRed)
                            ) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, tint = SeekhCoralRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Delete group",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = SeekhCoralRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. Change Profile Name & Avatar Dialog (matching HybridChangeProfileNameActivity: "Add name to this profile" / "Change name of this profile" / "Change profile picture")
@Composable
fun ChangeProfileNameAndAvatarDialog(
    profile: LearnerProfile,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var editedName by remember(profile.name) { mutableStateOf(profile.name) }
    var selectedAvatar by remember(profile.avatarIndex) { mutableIntStateOf(profile.avatarIndex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                text = "Change name of this profile",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SeekhTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Enter your name here", fontFamily = GoogleSansFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Change profile picture",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SeekhTextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
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
                                .clickable { selectedAvatar = idx },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = editedName.trim().ifEmpty { profile.name }
                    onSave(finalName, selectedAvatar)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text("Confirm", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = GoogleSansFamily)
            }
        }
    )
}

// 5. Partner Codes Modal ("Partner codes" from reference app drawer: "Add or remove partner organizations to start or stop sharing your app usage data")
@Composable
fun PartnerCodesDialog(
    partnerCodes: List<String>,
    onAddCode: (String) -> Unit,
    onRemoveCode: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Partner codes",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Add or remove partner organizations to start or stop sharing your app usage data.",
                    fontFamily = GoogleSansFamily,
                    fontSize = 14.sp,
                    color = SeekhTextSecondary
                )

                partnerCodes.forEach { code ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SeekhGreenSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = SeekhGreen600,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = code,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SeekhGreen900
                                )
                            }
                            IconButton(
                                onClick = { onRemoveCode(code) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = SeekhTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it },
                    label = { Text("Enter code here", fontFamily = GoogleSansFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputCode.isNotBlank()) {
                        onAddCode(inputCode)
                        inputCode = ""
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text(
                    text = if (inputCode.isNotBlank()) "Add a partner" else "Done",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = GoogleSansFamily)
            }
        }
    )
}

// 6. Left Navigation Drawer (Exact Reference HomeScreenDrawer items + Signed-In Account & AI Story Studio!)
@Composable
fun SeekhNavigationDrawerContent(
    profile: LearnerProfile,
    selectedLanguage: LearningLanguageOption,
    accountState: SignedInAccountState = SignedInAccountState(),
    blockedBookCount: Int = 0,
    isSoundEnabled: Boolean = true,
    onToggleSound: () -> Unit = {},
    onSwitchProfileClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
    onAccountMenuClick: () -> Unit = {},
    onAiStoryStudioClick: () -> Unit = {},
    onUnblockBooksClick: () -> Unit = {},
    onLanguageSettingsClick: () -> Unit,
    onReadingGroupsClick: () -> Unit,
    onPartnerCodesClick: () -> Unit,
    onPrivacyShieldClick: () -> Unit,
    onDeleteHistoryClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(310.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Profile & Signed-In Account Banner inside Drawer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SeekhGreen500)
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ProfileAvatarCircle(
                            avatarIndex = profile.avatarIndex,
                            size = 64.dp,
                            borderWidth = 2.dp,
                            borderColor = Color.White,
                            onClick = onSwitchProfileClick
                        )
                        Column {
                            Text(
                                text = profile.name,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StarCounterPill(
                                stars = profile.stars,
                                backgroundColor = Color(0x33000000),
                                textColor = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { onSwitchProfileClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwitchAccount,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Tap on your profile",
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (accountState.isSignedIn) "Linked to ${accountState.email}" else "Using without an account",
                        fontFamily = GoogleSansFamily,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.90f),
                        modifier = Modifier.clickable {
                            onCloseDrawer()
                            onAccountMenuClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 1. "Change name of this profile" / "Change profile picture" (LEFTNAV_TAP_CHANGE_PROFILE_NAME)
            DrawerMenuItem(
                icon = Icons.Default.Edit,
                title = "Change name of this profile",
                subtitle = "Edit name & change profile picture",
                onClick = {
                    onCloseDrawer()
                    onEditProfileClick()
                }
            )

            // 2. "Account menu" (Signed-in Google Account Sync / Switch Account)
            DrawerMenuItem(
                icon = if (accountState.isSignedIn) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                title = "Account menu",
                subtitle = if (accountState.isSignedIn) accountState.email else "Sign in to sync profiles",
                onClick = {
                    onCloseDrawer()
                    onAccountMenuClick()
                }
            )

            // 3. "Create your own story" (LUMOS_ADD_STORY_TAB_CLICKED / CYOC_CREATE_STORY_CLICKED)
            DrawerMenuItem(
                icon = Icons.Default.AutoAwesome,
                title = "Create your own story",
                subtitle = "Write and customize your own stories",
                onClick = {
                    onCloseDrawer()
                    onAiStoryStudioClick()
                }
            )

            // 4. "Language settings" (LEFTNAV_LANGUAGE_SELECTION_SHOW)
            DrawerMenuItem(
                icon = Icons.Default.Language,
                title = "Language settings",
                subtitle = selectedLanguage.title,
                onClick = {
                    onCloseDrawer()
                    onLanguageSettingsClick()
                }
            )

            // 5. "Partner codes" (PartnerCodeScreen)
            DrawerMenuItem(
                icon = Icons.Default.QrCode,
                title = "Partner codes",
                subtitle = "Add a partner",
                onClick = {
                    onCloseDrawer()
                    onPartnerCodesClick()
                }
            )

            // 6. "For teachers" / "Create reading group" (LEFTNAV_TAP_CREATE_READING_GROUP)
            DrawerMenuItem(
                icon = Icons.Default.Groups,
                title = "For teachers",
                subtitle = "Create reading group",
                onClick = {
                    onCloseDrawer()
                    onReadingGroupsClick()
                }
            )

            if (blockedBookCount > 0) {
                DrawerMenuItem(
                    icon = Icons.Default.Restore,
                    title = "Unblock stories ($blockedBookCount)",
                    subtitle = "Restore blocked stories to library",
                    onClick = {
                        onCloseDrawer()
                        onUnblockBooksClick()
                    }
                )
            }

            // 7. "Privacy" (Read Along Privacy Notice)
            DrawerMenuItem(
                icon = Icons.Default.Shield,
                title = "Privacy",
                subtitle = "Important information for parents",
                onClick = {
                    onCloseDrawer()
                    onPrivacyShieldClick()
                }
            )

            // 8. "Delete my app usage history" (DeleteHistoryDialog)
            DrawerMenuItem(
                icon = Icons.Default.DeleteOutline,
                title = "Delete my app usage history",
                subtitle = "Clear reading activities, stars & prizes",
                onClick = {
                    onCloseDrawer()
                    onDeleteHistoryClick()
                }
            )

            // 9. Voice & Sound toggle
            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = if (isSoundEnabled) "Sound & Voice: ON" else "Sound & Voice: OFF",
                subtitle = "Tap to mute or unmute voice assistant",
                onClick = {
                    onToggleSound()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SeekhBorderGray)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Seekh AI Academy",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SeekhTextPrimary
                )
                Text(
                    text = "Version 1.0 (1) • © TLL Studio",
                    fontFamily = GoogleSansFamily,
                    fontSize = 12.sp,
                    color = SeekhTextSecondary
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SeekhGreenSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SeekhGreen600,
                modifier = Modifier.size(22.dp)
            )
        }
        Column {
            Text(
                text = title,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = SeekhTextPrimary
            )
            Text(
                text = subtitle,
                fontFamily = GoogleSansFamily,
                fontSize = 12.sp,
                color = SeekhTextSecondary
            )
        }
    }
}

// 7. Signed-In AI Story Studio / CYOC ("Create Your Own Content / Story" — LUMOS_GENERATE_STORY_SHOWN / CYOC_GENERATE_STORY_CLICKED)
@Composable
fun AiStoryCreatorDialog(
    onDismiss: () -> Unit,
    onStoryGenerated: (StoryBook) -> Unit
) {
    val heroes = listOf("Noor the Wise Owl", "Zara the Explorer", "Leo the Brave Cub", "Bibi the Golden Butterfly")
    val settings = listOf("Starlight Library", "Enchanted Coral Reef", "Rainbow Mountain", "Sunny Botanical Garden")
    val lessons = listOf("Kindness & Sharing", "Curiosity & Science", "Teamwork & Friendship", "Courage & Honesty")

    var selectedHero by remember { mutableStateOf(heroes.first()) }
    var selectedSetting by remember { mutableStateOf(settings.first()) }
    var selectedLesson by remember { mutableStateOf(lessons.first()) }
    var selectedLevel by remember { mutableIntStateOf(2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SeekhGreen500),
                    contentAlignment = Alignment.Center
                ) {
                    CustomAiSparkleBadge(modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        text = "Create a story",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = SeekhTextPrimary
                    )
                    Text(
                        text = "Create your own bilingual story",
                        fontFamily = GoogleSansFamily,
                        fontSize = 12.sp,
                        color = SeekhTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Choose main character:",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SeekhTextPrimary
                )
                heroes.forEach { hero ->
                    val active = selectedHero == hero
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedHero = hero },
                        shape = RoundedCornerShape(14.dp),
                        color = if (active) SeekhGreenSoft else SeekhSurfaceGray,
                        border = BorderStroke(1.dp, if (active) SeekhGreen500 else SeekhBorderGray)
                    ) {
                        Text(
                            text = hero,
                            fontFamily = GoogleSansFamily,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = SeekhTextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                        )
                    }
                }

                Text(
                    text = "2. Choose story world:",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SeekhTextPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    settings.take(2).forEach { place ->
                        val active = selectedSetting == place
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSetting = place },
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) SeekhBlueSoft else SeekhSurfaceGray,
                            border = BorderStroke(1.dp, if (active) SeekhBlue500 else SeekhBorderGray)
                        ) {
                            Text(
                                text = place,
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SeekhTextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "3. Choose reading level:",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SeekhTextPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..4).forEach { lvl ->
                        val active = selectedLevel == lvl
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedLevel = lvl },
                            shape = RoundedCornerShape(50),
                            color = if (active) SeekhYellow500 else SeekhSurfaceGray
                        ) {
                            Text(
                                text = "Lvl $lvl",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SeekhTextPrimary,
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
                    val title = "$selectedHero at $selectedSetting"
                    val newStory = StoryBook(
                        id = "cyoc_${System.currentTimeMillis()}",
                        title = title,
                        nativeTitleUrdu = "$selectedHero کی نئی کہانی",
                        nativeTitleHindi = "$selectedHero की नई कहानी",
                        author = "Created by You",
                        illustrator = "Story Illustrator",
                        publisher = "My Created Stories",
                        level = selectedLevel,
                        coverAssetPath = "books/sw_13916/image_5.webp",
                        isAiGenerated = true,
                        bookType = "CYOC",
                        bookSource = "MY_STORY",
                        pages = listOf(
                            StoryPage(
                                pageNumber = 1,
                                imageAssetPath = "books/sw_13916/image_5.webp",
                                text = "One sunny morning, $selectedHero arrived at the magical $selectedSetting with a bright smile.",
                                urduTranslation = "ایک روشن صبح، $selectedHero مسکراتے ہوئے جادوئی $selectedSetting میں پہنچا۔",
                                hindiTranslation = "एक सुनहरी सुबह, $selectedHero मुस्कुराते हुए $selectedSetting में पहुँचा।",
                                practiceWords = setOf("morning", "bright", "special")
                            ),
                            StoryPage(
                                pageNumber = 2,
                                imageAssetPath = "books/sw_37273/image_14.webp",
                                text = "Together with wonderful friends, they discovered the secret of $selectedLesson and shared golden books!",
                                urduTranslation = "اپنے پیارے دوستوں کے ساتھ مل کر انہوں نے $selectedLesson کا راز جانا اور سنہری کتابیں پڑھیں!",
                                hindiTranslation = "अपने प्यारे दोस्तों के साथ मिलकर उन्होंने $selectedLesson सीखा और किताबें पढ़ीं!",
                                practiceWords = setOf("friends", "books", "yellow"),
                                quiz = StoryQuizQuestion(
                                    question = "Where did $selectedHero go on this adventure?",
                                    options = listOf(selectedSetting, "Inside a dark cave", "Under a sofa"),
                                    correctIndex = 0
                                )
                            )
                        )
                    )
                    onStoryGenerated(SeekhCatalog.enrichToFullIllustratedStory(newStory))
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text("Create story", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = GoogleSansFamily)
            }
        }
    )
}

// 8. Delete App Usage History Confirmation Dialog (matching DeleteHistoryDialog from flutter_ui_strings.txt lines 589 & 1437!)
@Composable
fun DeleteHistoryDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Delete my app usage history",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SeekhTextPrimary
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete your usage history?\n\nAll of your reading activities, collected stars and prizes will be permanently deleted.",
                fontFamily = GoogleSansFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SeekhTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhCoralRed)
            ) {
                Text("Delete history", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = GoogleSansFamily)
            }
        }
    )
}
