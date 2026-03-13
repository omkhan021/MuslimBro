package com.muslimbro.feature.quranplayer

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuranPlayerService : MediaSessionService() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        exoPlayer.setAudioAttributes(audioAttributes, true)
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionActivity(buildSessionActivity())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    private fun buildSessionActivity(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
            ?: Intent()
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    companion object {
        fun buildAyahUrl(reciterIdentifier: String, surahNumber: Int, verseNumber: Int): String {
            val surahStr = surahNumber.toString().padStart(3, '0')
            val verseStr = verseNumber.toString().padStart(3, '0')
            return "https://everyayah.com/data/$reciterIdentifier/$surahStr$verseStr.mp3"
        }

        fun buildMediaItem(
            reciterIdentifier: String,
            surahNumber: Int,
            verseNumber: Int
        ): MediaItem {
            val url = buildAyahUrl(reciterIdentifier, surahNumber, verseNumber)
            return MediaItem.Builder()
                .setUri(url)
                .setMediaId("$surahNumber:$verseNumber")
                .build()
        }
    }
}
