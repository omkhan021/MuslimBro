package com.muslimbro.feature.quranplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentSurah: Int = 0,
    val currentVerse: Int = 0,
    val currentWordIndex: Int = -1,
    val reciterIdentifier: String = "Alafasy_128kbps"
)

@HiltViewModel
class QuranPlayerViewModel @Inject constructor(
    val exoPlayer: ExoPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var wordHighlightJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                if (isPlaying) startWordHighlighting() else stopWordHighlighting()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.mediaId?.split(":")?.let { parts ->
                    if (parts.size == 2) {
                        val surah = parts[0].toIntOrNull() ?: return
                        val verse = parts[1].toIntOrNull() ?: return
                        _uiState.value = _uiState.value.copy(
                            currentSurah = surah,
                            currentVerse = verse,
                            currentWordIndex = -1
                        )
                    }
                }
            }
        })
    }

    fun playSurah(surahNumber: Int, startVerse: Int = 1, versesCount: Int) {
        val reciter = _uiState.value.reciterIdentifier
        val items = (startVerse..versesCount).map { verse ->
            QuranPlayerService.buildMediaItem(reciter, surahNumber, verse)
        }
        exoPlayer.setMediaItems(items)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun playPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun skipNext() {
        exoPlayer.seekToNextMediaItem()
    }

    fun skipPrevious() {
        exoPlayer.seekToPreviousMediaItem()
    }

    fun setReciter(identifier: String) {
        _uiState.value = _uiState.value.copy(reciterIdentifier = identifier)
    }

    private fun startWordHighlighting() {
        wordHighlightJob?.cancel()
        wordHighlightJob = viewModelScope.launch {
            while (exoPlayer.isPlaying) {
                val position = exoPlayer.currentPosition
                // Word timing would come from the words table audio_offset
                // For now, emit position-based index estimation
                _uiState.value = _uiState.value.copy(
                    currentWordIndex = (position / 300).toInt() // rough 300ms per word
                )
                delay(50)
            }
        }
    }

    private fun stopWordHighlighting() {
        wordHighlightJob?.cancel()
        _uiState.value = _uiState.value.copy(currentWordIndex = -1)
    }

    override fun onCleared() {
        wordHighlightJob?.cancel()
        super.onCleared()
    }
}
