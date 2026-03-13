package com.muslimbro.feature.quran.reader

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muslimbro.core.domain.model.Translation
import com.muslimbro.core.domain.model.Verse
import com.muslimbro.core.ui.components.ErrorScreen
import com.muslimbro.core.ui.components.LoadingScreen
import com.muslimbro.core.ui.theme.Gold500
import com.muslimbro.core.ui.theme.QuranFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuranReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(uiState.surah?.name ?: "Quran")
                        uiState.surah?.let {
                            Text(
                                text = it.nameTranslation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    uiState.surah?.let { surah ->
                        Text(
                            text = surah.nameArabic,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingScreen(Modifier.padding(paddingValues))
            uiState.error != null -> ErrorScreen(
                message = uiState.error!!,
                modifier = Modifier.padding(paddingValues)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Bismillah header (except Al-Fatiha and At-Tawbah)
                    val surahNum = uiState.surah?.number ?: 0
                    if (surahNum != 1 && surahNum != 9) {
                        item {
                            Text(
                                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
                                style = TextStyle(
                                    fontFamily = QuranFontFamily,
                                    fontSize = 26.sp,
                                    textAlign = TextAlign.Center,
                                    textDirection = TextDirection.Rtl
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    items(uiState.verses) { verse ->
                        val translation = uiState.translations
                            .firstOrNull { it.verseNumber == verse.verseNumber }
                        VerseCard(
                            verse = verse,
                            translation = translation,
                            highlightedWordIndex = uiState.highlightedWordIndex,
                            onLongPress = { viewModel.selectVerseForSheet(verse) }
                        )
                    }
                }

                // Verse action bottom sheet
                uiState.selectedVerseForSheet?.let { verse ->
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.selectVerseForSheet(null) },
                        sheetState = sheetState,
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        VerseActionsSheet(
                            verse = verse,
                            onBookmark = { viewModel.bookmarkVerse(verse) },
                            onDismiss = { viewModel.selectVerseForSheet(null) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerseCard(
    verse: Verse,
    translation: Translation?,
    highlightedWordIndex: Int,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() })
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Verse number badge
            Text(
                text = "\u06DD${verse.verseNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.End)
            )

            // Arabic text — RTL with Uthmanic font
            val arabicText = buildAnnotatedString {
                val words = verse.textUthmani.split(" ")
                words.forEachIndexed { index, word ->
                    if (index == highlightedWordIndex) {
                        pushStyle(SpanStyle(background = Gold500.copy(alpha = 0.3f)))
                        append(word)
                        pop()
                    } else {
                        append(word)
                    }
                    if (index < words.size - 1) append(" ")
                }
            }

            Text(
                text = arabicText,
                style = TextStyle(
                    fontFamily = QuranFontFamily,
                    fontSize = 24.sp,
                    textDirection = TextDirection.Rtl,
                    lineHeight = 44.sp,
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Translation
            translation?.let {
                Text(
                    text = it.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun VerseActionsSheet(
    verse: Verse,
    onBookmark: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "${verse.surahNumber}:${verse.verseNumber}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextButton(
            onClick = { onBookmark(); onDismiss() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bookmark")
        }
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
