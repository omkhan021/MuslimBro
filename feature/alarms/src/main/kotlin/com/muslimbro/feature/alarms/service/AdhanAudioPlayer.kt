package com.muslimbro.feature.alarms.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.muslimbro.core.domain.model.Prayer
import com.muslimbro.feature.alarms.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdhanAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAdhan(prayer: Prayer, onComplete: () -> Unit) {
        release()

        val rawResId = if (prayer == Prayer.FAJR) {
            R.raw.adhan_fajr
        } else {
            R.raw.adhan_regular
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                val afd = context.resources.openRawResourceFd(rawResId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setOnCompletionListener { onComplete() }
                setOnErrorListener { _, _, _ ->
                    onComplete()
                    true
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            onComplete()
        }
    }

    fun release() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }
}
