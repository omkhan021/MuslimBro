plugins {
    alias(libs.plugins.muslimbro.android.feature)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.feature.settings"
}

dependencies {
    implementation(project(":core:data"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
