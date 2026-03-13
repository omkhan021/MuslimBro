plugins {
    alias(libs.plugins.muslimbro.android.library)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.core.ktx)
    implementation(libs.coil.compose)
    implementation(libs.compose.material.icons.extended)
    testImplementation(libs.junit)
}
