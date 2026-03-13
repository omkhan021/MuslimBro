plugins {
    alias(libs.plugins.muslimbro.android.feature)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.feature.qibla"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.adhan)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
