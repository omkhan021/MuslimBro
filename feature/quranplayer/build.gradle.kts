plugins {
    alias(libs.plugins.muslimbro.android.feature)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.feature.quranplayer"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.bundles.media3)
    implementation(libs.media3.exoplayer.dash)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
