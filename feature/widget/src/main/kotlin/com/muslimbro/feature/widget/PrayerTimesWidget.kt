package com.muslimbro.feature.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class PrayerTimesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.background)
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Prayer Times",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.primary
                )
            )
            // Widget state is pre-populated by WidgetUpdateWorker
            // In a full impl, read from GlanceStateDefinition-backed DataStore
        }
    }
}

class PrayerTimesWidgetMedium : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MediumWidgetContent()
        }
    }

    @Composable
    private fun MediumWidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.background)
                .padding(12.dp)
        ) {
            Text(
                text = "Next Prayer",
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
    }
}

// Widget Receivers
class PrayerTimesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PrayerTimesWidget()
}

class PrayerTimesWidgetMediumReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PrayerTimesWidgetMedium()
}
