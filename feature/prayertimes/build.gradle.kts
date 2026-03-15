plugins {
    alias(libs.plugins.muslimbro.android.feature)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.feature.prayertimes"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:alarms"))
    implementation(libs.play.services.location)
    implementation(libs.adhan)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
