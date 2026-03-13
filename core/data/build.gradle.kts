plugins {
    alias(libs.plugins.muslimbro.android.library)
    alias(libs.plugins.muslimbro.hilt)
    alias(libs.plugins.muslimbro.room)
}

android {
    namespace = "com.muslimbro.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.datastore.preferences)
    implementation(libs.workmanager.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    implementation(libs.play.services.location)
    implementation(libs.adhan)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.room.testing)
}
