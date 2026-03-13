plugins {
    alias(libs.plugins.muslimbro.android.library)
    alias(libs.plugins.muslimbro.hilt)
}

android {
    namespace = "com.muslimbro.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
