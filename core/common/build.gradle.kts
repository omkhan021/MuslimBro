plugins {
    alias(libs.plugins.muslimbro.android.library)
}

android {
    namespace = "com.muslimbro.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
