plugins {
    alias(libs.plugins.muslimbro.android.feature)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.feature.alarms"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.workmanager.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    implementation(libs.media3.exoplayer)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
