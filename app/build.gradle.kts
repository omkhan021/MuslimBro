plugins {
    alias(libs.plugins.muslimbro.android.application)
    alias(libs.plugins.muslimbro.hilt)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.app"

    defaultConfig {
        applicationId = "com.muslimbro.app"
        versionCode = 1
        versionName = "1.0.0"
    }
}

// Android Studio looks for this JVM task which doesn't exist in Android projects
tasks.register("testClasses")

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:prayertimes"))
    implementation(project(":feature:alarms"))
    implementation(project(":feature:quran"))
    implementation(project(":feature:quranplayer"))
    implementation(project(":feature:qibla"))
    implementation(project(":feature:widget"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:masnoon"))

    // App-level deps
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.splashscreen)
    implementation(libs.compose.activity)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.workmanager.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.bundles.lifecycle)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
}
