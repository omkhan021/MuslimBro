plugins {
    alias(libs.plugins.muslimbro.android.library)
}

android {
    namespace = "com.muslimbro.core.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
    // adhan for prayer calculation parameter types
    implementation(libs.adhan)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
