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

    val filterChips = remember(accountState.isSignedIn) {
        if (accountState.isSignedIn) {
            listOf("All", "Level 1", "Level 2", "Level 3", "Level 4", "Phonics", "Series", "Classroom", "My stories")
        } else {
            listOf("All", "Level 1", "Level 2", "Level 3", "Level 4", "Phonics", "Series")
        }
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

    val heroBook = remember(books) {
        books.firstOrNull { it.isFeaturedHero } ?: books.first()
    }
    val nonHeroBooks = remember(books, heroBook) {
        books.filterNot { it.id == heroBook.id }
    }
    val classroomBooks = remember(books) {
        books.filter { it.bookSource == "CLASSROOM" || it.assignedByTeacher.isNotBlank() }
    }
    val phonicsBooks = remember(books) {
        books.filter { it.bookType == "DECODABLE" || it.phonicsSkillTag.isNotBlank() }
    }
    val seriesBooks = remember(books) {
        books.filter { it.bookType == "SERIES" || it.seriesTag.isNotBlank() }
    }
    val myCustomBooks = remember(books) {
        books.filter { it.isAiGenerated || it.bookType == "CYOC" }
    }
    val level1Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 1 && !it.isAiGenerated } }
    val level2Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 2 && !it.isAiGenerated } }
    val level3Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 3 && !it.isAiGenerated } }
    val level4Books = remember(nonHeroBooks) { nonHeroBooks.filter { it.level == 4 && !it.isAiGenerated } }

    // "Read again" shelf (readAgainBooks from flutter_ui_strings.txt & ref_live_01.png)
    val readAgainBooks = remember(books, profile.completedBookIds, profile.bookPageProgress) {
        books.filter { b ->
            profile.completedBookIds.contains(b.id) || (profile.bookPageProgress[b.id] ?: 0) > 0
        }
    }

    // Filtered books when user uses Search or selects a specific category filter chip
    val filteredBooks = remember(books, searchQuery, selectedCategoryFilter) {
        books.filter { b ->
            val matchesQuery = searchQuery.isBlank() ||
                b.title.contains(searchQuery, ignoreCase = true) ||
                b.nativeTitleUrdu.contains(searchQuery, ignoreCase = true) ||
                b.nativeTitleHindi.contains(searchQuery, ignoreCase = true) ||
                b.author.contains(searchQuery, ignoreCase = true) ||
                b.phonicsSkillTag.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedCategoryFilter) {
                "Level 1" -> b.level == 1
                "Level 2" -> b.level == 2
                "Level 3" -> b.level == 3
                "Level 4" -> b.level == 4
                "Phonics" -> b.bookType == "DECODABLE" || b.phonicsSkillTag.isNotBlank()
                "Series" -> b.bookType == "SERIES" || b.seriesTag.isNotBlank()
                "Classroom" -> b.bookSource == "CLASSROOM" || b.assignedByTeacher.isNotBlank()
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
                                    text = "Search stories by title, level, or phonics...",
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
                            Surface(
                                modifier = Modifier.clickable {
                                    selectedCategoryFilter = if (isSelected && chip != "All") "All" else chip
                                    onSpeakText(chip)
                                },
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) SeekhYellow500 else Color.White.copy(alpha = 0.92f),
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = chip,
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SeekhTextPrimary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // If user has active search query or category filter != "All", show the filtered results cleanly
                if (searchQuery.isNotBlank() || selectedCategoryFilter != "All") {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        LibrarySectionHeader(
                            title = if (searchQuery.isNotBlank()) {
                                "Search results (${filteredBooks.size})"
                            } else {
                                "$selectedCategoryFilter (${filteredBooks.size})"
                            },
                            onSpeak = { onSpeakText("$selectedCategoryFilter stories") }
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
                    // 1. Top Recommended Story Hero Card ("Read Along story for today • Colors on the street")
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        FeaturedStoryHeroCard(
                            book = heroBook,
                            isCompleted = profile.completedBookIds.contains(heroBook.id),
                            onClick = { onOpenBook(heroBook) }
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // 2. Signed-In "Classroom assignments" Section (BOOK_SOURCE_CLASSROOM_TEACHER / RA_AUDIT_EVENT_ATTACHMENT_BOOK_OPENED)
                    if (accountState.isSignedIn && classroomBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Classroom assignments",
                                onSpeak = { onSpeakText("Classroom assignments from your teacher") }
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

                    // 3. "Level 1 stories" Section (PROMPT_STORIES_OF_LEVEL_1)
                    if (level1Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Level 1 stories",
                                onSpeak = { onSpeakText("Level 1 stories") }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level1Books,
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

                    // 4. "Play games" Section (seekh: play_games — Jumbled Words, Pop The Balloons, Speed Reading, Word Meanings)
                    item {
                        LibrarySectionHeader(
                            title = "Play games",
                            onSpeak = { onSpeakText("Play games") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                GameShelfCard(
                                    title = "Jumbled Words",
                                    subtitle = "Unscramble letters",
                                    containerColor = SeekhJumblePurple,
                                    badgeText = "a • p • p • l • e • s",
                                    onClick = { onOpenGame("JUMBLE") }
                                )
                            }
                            item {
                                GameShelfCard(
                                    title = "Pop The Balloons",
                                    subtitle = "Phonics & sounds",
                                    containerColor = Color(0xFF00838F),
                                    badgeText = "🎈 S • B • M",
                                    onClick = { onOpenGame("BALLOON") }
                                )
                            }
                            item {
                                GameShelfCard(
                                    title = "Speed Reading",
                                    subtitle = "30s reading speed",
                                    containerColor = Color(0xFFF57F17),
                                    badgeText = "⏱ 30 sec WPM",
                                    onClick = { onOpenGame("SPEED") }
                                )
                            }
                            item {
                                GameShelfCard(
                                    title = "Word Meanings",
                                    subtitle = "9 illustrated themes",
                                    containerColor = SeekhVocabDarkGreen,
                                    badgeText = "🎨 9 Themes",
                                    onClick = { onOpenGame("VOCAB") }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // 5. "Phonics & Decodable stories" Section (BOOK_TYPE_DECODABLE / phonicSkillGroupsInfo)
                    if (phonicsBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Phonics & Decodable stories",
                                onSpeak = { onSpeakText("Phonics and decodable skill stories") }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = phonicsBooks,
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

                    // 6. "Level 2 stories" Section (PROMPT_STORIES_OF_LEVEL_2)
                    if (level2Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Level 2 stories",
                                onSpeak = { onSpeakText("Level 2 stories") }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level2Books,
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

                    // 7. "Story Series" Section (BOOK_SHOWN_SERIES / PROMPT_UNLOCK_SERIES_BOOK_TOMORROW)
                    if (seriesBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Story Series",
                                onSpeak = { onSpeakText("Multi-chapter story series") }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = seriesBooks,
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

                    // 8. "Level 3 stories" Section (PROMPT_STORIES_OF_LEVEL_3)
                    if (level3Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Level 3 stories",
                                onSpeak = { onSpeakText("Level 3 stories") }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level3Books,
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

                    // 9. "Level 4 stories" Section (PROMPT_STORIES_OF_LEVEL_4)
                    if (level4Books.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Level 4 stories",
                                onSpeak = { onSpeakText("Level 4 stories") }
                            )
                            LibraryBooksThreeColumnRows(
                                sectionBooks = level4Books,
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

                    // 10. Signed-In "Create your own story & My stories" Section (BOOK_SOURCE_CYOC / LUMOS_ADD_STORY_TAB_CLICKED / MY_BOOK_CONTENT_MENU_CLICKED)
                    if (accountState.isSignedIn) {
                        item {
                            LibrarySectionHeader(
                                title = "Create your own story",
                                onSpeak = { onSpeakText("Create your own story with AI Story Studio") }
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
                                                            text = myBook.title,
                                                            fontFamily = GoogleSansFamily,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = SeekhTextPrimary
                                                        )
                                                        Text(
                                                            text = "My created story • Level ${myBook.level}",
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

                    // 11. "Read again" Section (seekh: read_again — visible in ref_live_01.png!)
                    if (readAgainBooks.isNotEmpty()) {
                        item {
                            LibrarySectionHeader(
                                title = "Read again",
                                onSpeak = { onSpeakText("Read again") }
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

                    // 12. "Read stories in <Language>" / "More languages" Footer Card (read_stories_in_language, more_languages)
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                                .clickable { onOpenLanguageSettings() },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.dp, SeekhBorderGray)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                            text = "Read stories in ${selectedLanguage.title}",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SeekhTextPrimary
                                        )
                                        Text(
                                            text = "More languages",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = SeekhGreen500
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "More languages",
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
    Surface(
        modifier = Modifier
            .width(245.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, if (isTurnedIn) SeekhGreenMint else SeekhGreen100)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (isTurnedIn) SeekhGreen600 else SeekhBlue600,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = SeekhGreen500,
        shadowElevation = 5.dp
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
                        text = "AI Story Studio • Create a Story",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Pick a character & adventure to generate a new story",
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
                    text = "+ Create",
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
    onSpeak: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSpeak() }
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Bold,
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
    title: String,
    subtitle: String,
    containerColor: Color,
    badgeText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(168.dp)
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.22f)
            ) {
                Text(
                    text = badgeText,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontFamily = GoogleSansFamily,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 1
                )
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
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        border = BorderStroke(1.dp, SeekhGreen100),
                        modifier = Modifier.fillMaxWidth()
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
                            val isUnlocked = when (badge.id) {
                                "star_500", "star_3000", "streak_5", "star_5000" -> profile.stars >= 6000
                                else -> true
                            }
                            PrizeMedallionItem(
                                badge = badge,
                                isUnlocked = isUnlocked,
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
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
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
                Text(
                    text = "${badge.subtitle}\n\nEarn stars and badges by reading stories and playing word games!",
                    fontFamily = GoogleSansFamily,
                    fontSize = 15.sp,
                    color = SeekhTextSecondary,
                    textAlign = TextAlign.Center
                )
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(106.dp)
            .height(122.dp)
            .clickable { onClick() },
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
                            Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = badge.title,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(34.dp)
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

// 3. Exact Reference Activity Tab Screen (res/S2.xml + res/SG.xml + res/tL.xml + res/_m.xml + res/hn.xml + res/8O.xml + Guided Create/Join Reading Group Wizards!)
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
    onUpdateDailyGoal: (Int) -> Unit,
    onCreateReadingGroup: (String, String) -> Unit,
    onJoinReadingGroup: (String) -> Unit,
    onUpdateReadingGroup: (ReadingGroup) -> Unit,
    onDeleteReadingGroup: (String) -> Unit,
    onSpeakText: (String) -> Unit
) {
    var selectedWeekTab by remember { mutableStateOf("This week") } // "This week" | "Last week" (res/S2.xml)
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
                        shadowElevation = 3.dp,
                        modifier = Modifier.fillMaxWidth()
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

                // 1. "Daily goal" Card matching res/tL.xml (CircularProgressIndicator + "Today: %d min" + Mascot speech bubble + 7-day Mon-Sun checkmark strip)
                item {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSpeakText("Yay! You have completed today's goal. Your daily goal is ${profile.dailyGoalMinutes} minutes.")
                            }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Daily goal",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = SeekhTextPrimary
                            )
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
                                                (profile.minutesReadToday.toFloat() / profile.dailyGoalMinutes.toFloat()).coerceIn(0f, 1f)
                                            },
                                            modifier = Modifier.fillMaxSize(),
                                            color = SeekhGreenMint,
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
                                            text = "Your daily goal is ${profile.dailyGoalMinutes} min",
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = SeekhGreen500
                                        )
                                    }
                                }

                                CustomScholarOwlIllustration(
                                    modifier = Modifier.size(72.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Speech bubble from res/tL.xml lines 79-105 ("Yay! you have completed today's goal")
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SeekhGreenSoft,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedWeekTab == "This week") {
                                        "Yay! you have completed today’s goal"
                                    } else {
                                        "You met your daily goal ${profile.weeklyMinutes.count { it >= profile.dailyGoalMinutes }} days last week"
                                    },
                                    fontFamily = GoogleSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = SeekhGreen900,
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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (goalReached) SeekhGreenMint else SeekhSurfaceGray)
                                                .border(
                                                    1.dp,
                                                    if (goalReached) SeekhGreenMint else SeekhBorderGray,
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
                                            }
                                        }
                                        Text(
                                            text = dayName,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = if (goalReached) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = if (goalReached) SeekhTextPrimary else SeekhTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. "Weekly Activity" Card matching res/_m.xml ("Weekly Activity" + "Total %d min in English" + Speed Vehicle + 2 Stat Boxes: "%d min Time" & "%d wpm Average reading speed this week")
                item {
                    val totalMin = if (selectedWeekTab == "This week") {
                        profile.weeklyMinutes.sum()
                    } else {
                        (profile.weeklyMinutes.sum() - 14).coerceAtLeast(35)
                    }
                    val displayWpm = if (selectedWeekTab == "This week") {
                        profile.wordsPerMinute
                    } else {
                        (profile.wordsPerMinute - 4).coerceAtLeast(48)
                    }

                    Surface(
                        shape = RoundedCornerShape(22.dp),
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
                                text = "Total $totalMin min in English",
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

                // 4. "Your reading groups" Section matching res/SG.xml & res/8O.xml (Divider Header + 3-Slide Teacher Intro Carousel + Create/Join Wizards + Active Groups List)
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Divider Header matching res/SG.xml lines 25-52 ("Your reading groups")
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

                        // 3-Slide Intro Carousel matching res/SG.xml ViewPager2 & user_group_create_intro_1..3
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

                // Active Reading Group Cards (tapping opens the 3-Tab Reading Group Classroom Admin Screen res/_b.xml)
                items(readingGroups) { group ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
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
                                        text = "Teacher / Admin: ${group.adminName} • ${group.languageTitle}",
                                        fontFamily = GoogleSansFamily,
                                        fontSize = 13.sp,
                                        color = SeekhTextSecondary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SeekhGreenSoft
                                ) {
                                    Text(
                                        text = "View activity",
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

                            group.members.take(3).forEach { member ->
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
                                        Text(
                                            text = member.name,
                                            fontFamily = GoogleSansFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 15.sp,
                                            color = SeekhTextPrimary
                                        )
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

    // 3-Tab Reading Group Classroom Admin Screen (res/_b.xml: Activity | Popular stories | Settings)
    selectedGroupForAdmin?.let { activeGroup ->
        ReadingGroupAdminDashboardDialog(
            group = activeGroup,
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

    // Multi-Step "Create reading group" Guided Wizard (HybridUserGroupCreateOobeActivity -> Profile -> Verify -> Language -> Name -> Confirmation)
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

    // Multi-Step "Join a reading group" Guided Wizard (HybridUserGroupJoinOobeActivity -> Profile -> Verify -> Confirmation)
    if (showJoinGroupWizard) {
        JoinReadingGroupWizardDialog(
            profiles = allProfiles,
            activeProfile = profile,
            onDismiss = { showJoinGroupWizard = false },
            onGroupJoined = { inviteCode ->
                onJoinReadingGroup(inviteCode)
                showJoinGroupWizard = false
            }
        )
    }
}

// 3b. Authentic 5-Step "Create reading group" Guided Wizard (matching HybridUserGroupCreateOobeActivity & string_values.txt lines 912-980)
@Composable
fun CreateReadingGroupWizardDialog(
    profiles: List<LearnerProfile>,
    activeProfile: LearnerProfile,
    onDismiss: () -> Unit,
    onGroupCreated: (String, String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Intro & Privacy, 2: Select Admin Profile, 3: Adult Verify, 4: Select Language, 5: Name Group & Confirmation
    var selectedAdminProfile by remember { mutableStateOf(activeProfile) }
    var selectedLang by remember { mutableStateOf(SeekhCatalog.supportedLanguages.first()) }
    var groupName by remember { mutableStateOf("") }

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
                // Top step header
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
                        // Step 1: Intro & Privacy Explainer (user_group_create_intro_4..6 + user_group_create_privacy_1..3)
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
                        // Step 2: Select Admin Profile (user_group_create_select_profile_text)
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
                        // Step 3: Adult Teacher Verification (user_group_create_verify_text)
                        Text(
                            text = "Verify to create reading group",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "Please verify that you are an adult and can manage the reading group as an admin or teacher.",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = SeekhTextSecondary
                        )
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
                    4 -> {
                        // Step 4: Select Group Language (user_group_language_selection_text)
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
                        // Step 5: Name Reading Group & Confirmation (user_group_set_name_text + user_group_create_confirmation_text)
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

// 3c. Authentic 3-Step "Join a reading group" Guided Wizard (matching HybridUserGroupJoinOobeActivity & string_values.txt lines 985-1039)
@Composable
fun JoinReadingGroupWizardDialog(
    profiles: List<LearnerProfile>,
    activeProfile: LearnerProfile,
    onDismiss: () -> Unit,
    onGroupJoined: (String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Enter Code & Intro, 2: Select Child Profile & Privacy, 3: Adult Verify & Go to group now
    var inviteCode by remember { mutableStateOf("SEEKH-305") }
    var selectedChildProfile by remember { mutableStateOf(activeProfile) }

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
                            text = "• Reading group is created by teacher / admin where multiple children can read stories.\n• The teacher / admin sets the language for the reading group.\n• They can follow your child’s progress to help improve their reading.",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = SeekhTextSecondary
                        )
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = { inviteCode = it },
                            label = { Text("Enter code here", fontFamily = GoogleSansFamily) },
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
                                    Text(
                                        text = p.name,
                                        fontFamily = GoogleSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SeekhTextPrimary
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Verify & Confirm",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = SeekhTextPrimary
                        )
                        Text(
                            text = "Please verify that you are an adult who can make the decision for ${selectedChildProfile.name} to join this reading group (${inviteCode.uppercase()}).",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            color = SeekhTextSecondary
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            onGroupJoined(inviteCode.trim().ifEmpty { "SEEKH-305" })
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

// 3d. Reading Group 3-Tab Classroom Admin Dashboard (res/_b.xml: Activity | Popular stories | Settings + Member removal & Story Activity detail)
@Composable
private fun ReadingGroupAdminDashboardDialog(
    group: ReadingGroup,
    allBooks: List<StoryBook>,
    onOpenBook: (StoryBook) -> Unit,
    onUpdateGroup: (ReadingGroup) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tabs = listOf("Activity", "Popular stories", "Settings")
    var selectedTab by remember { mutableStateOf("Activity") }
    var metricFilter by remember { mutableStateOf("Overall activity") } // Overall activity | Reading | Speed (res/kc.xml)
    var editedName by remember(group.name) { mutableStateOf(group.name) }
    var newStudentName by remember { mutableStateOf("") }
    var linkCopiedBanner by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SeekhGreen500)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${group.adminName} • ${group.languageTitle} • Created ${group.createdDate}",
                                fontFamily = GoogleSansFamily,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
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

                // 3 Tabs Bar (Activity | Popular stories | Settings from res/_b.xml)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SeekhSurfaceGray)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        val active = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (active) SeekhGreen500 else Color.White,
                            border = BorderStroke(1.dp, if (active) SeekhGreen500 else SeekhBorderGray),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab }
                        ) {
                            Text(
                                text = tab,
                                fontFamily = GoogleSansFamily,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (active) Color.White else SeekhTextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (selectedTab) {
                        "Activity" -> {
                            // Metric Filter Chips (from res/kc.xml: Overall activity | Reading | Speed)
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
                                text = when (metricFilter) {
                                    "Reading" -> "Children reading"
                                    "Speed" -> "Average reading speed this week. In words per minute (wpm)"
                                    else -> "Children reading and playing games"
                                },
                                fontFamily = GoogleSansFamily,
                                fontSize = 12.sp,
                                color = SeekhTextSecondary
                            )

                            Text(
                                text = "Active children (${group.members.count { it.isActiveThisWeek }}/${group.members.size})",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SeekhGreen600
                            )

                            group.members.forEachIndexed { index, member ->
                                val vehicle = SeekhCatalog.getVehicleForWpm(member.speedWpm)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = SeekhSurfaceGray,
                                    border = BorderStroke(1.dp, SeekhBorderGray),
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
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                                                size = 40.dp,
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
                                                        "Reading" -> "${member.storiesRead} stories • Last active: ${member.lastActiveLabel}"
                                                        "Speed" -> "${vehicle.name} ${vehicle.emoji}"
                                                        else -> "Reading (${member.storiesRead}) • Game (${member.gamesPlayed})"
                                                    },
                                                    fontFamily = GoogleSansFamily,
                                                    fontSize = 12.sp,
                                                    color = SeekhTextSecondary
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = if (metricFilter == "Speed") "${member.speedWpm} wpm" else "${member.minutesThisWeek} min",
                                                fontFamily = GoogleSansFamily,
                                                fontWeight = FontWeight.Bold,
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
                                }
                            }

                            // Add Student to Classroom in Real Time
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = newStudentName,
                                            onValueChange = { newStudentName = it },
                                            placeholder = { Text("Student name", fontFamily = GoogleSansFamily, fontSize = 13.sp) },
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
                                                    minutesThisWeek = (45..95).random(),
                                                    storiesRead = (4..11).random(),
                                                    speedWpm = (52..82).random(),
                                                    isActiveThisWeek = true
                                                )
                                                onUpdateGroup(group.copy(members = group.members + added))
                                                onSpeakText("$sName added to ${group.name}!")
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

                        "Popular stories" -> {
                            // Matching res/Fk.xml ("Popular stories in this reading group" + "Reading this week (X/Y)")
                            Text(
                                text = "Popular stories in this reading group",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SeekhTextPrimary
                            )

                            val popularBooks = allBooks.take(6)
                            popularBooks.forEachIndexed { idx, book ->
                                val activeReaders = (group.members.size - (idx % 2)).coerceAtLeast(1)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = SeekhSurfaceGray,
                                    border = BorderStroke(1.dp, SeekhBorderGray),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenBook(book) }
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
                                                    .size(56.dp)
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
                                                    fontSize = 15.sp,
                                                    color = SeekhTextPrimary
                                                )
                                                Text(
                                                    text = "Reading this week ($activeReaders/${group.members.size})",
                                                    fontFamily = GoogleSansFamily,
                                                    fontSize = 12.sp,
                                                    color = SeekhGreen600
                                                )
                                            }
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Read",
                                            tint = SeekhGreen500
                                        )
                                    }
                                }
                            }
                        }

                        "Settings" -> {
                            // Matching res/Ns.xml ("Reading group", "Created by", "Language", "Date you created this group", "Members in this group", "Invite children", "Copy invitation link", "Edit group name", "Delete group")
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
                                    Text("Language: ${group.languageTitle}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                    Text("Date you created this group: ${group.createdDate}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                    Text("Members in this group: ${group.members.size}", fontFamily = GoogleSansFamily, fontSize = 13.sp, color = SeekhTextSecondary)
                                }
                            }

                            // Members list with remove option (remove_group_member)
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

                            // Invite children & Copy invitation link (fragment_settings_invite_children_action & fragment_settings_copy_invitation_link_action)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        linkCopiedBanner = true
                                        onSpeakText("Invitation link has been copied successfully! Code: ${group.inviteCode}")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
                                ) {
                                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Invite children", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

                            // Edit group name (fragment_settings_edit_group_action)
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

                            // Delete group / Leave reading group (fragment_settings_delete_group_action)
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
                subtitle = "Generate custom stories in AI Story Studio",
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
                        text = "AI Story Studio",
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
                        author = "AI Story Studio • You",
                        illustrator = "Seekh AI Illustrator",
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
                    onStoryGenerated(newStory)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SeekhGreen500)
            ) {
                Text("Generate Story ✨", fontFamily = GoogleSansFamily, fontWeight = FontWeight.Bold)
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
