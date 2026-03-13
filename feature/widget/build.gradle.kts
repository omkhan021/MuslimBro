plugins {
    alias(libs.plugins.muslimbro.android.feature)
    alias(libs.plugins.muslimbro.compose)
}

android {
    namespace = "com.muslimbro.feature.widget"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.workmanager.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.workmanager.testing)
}
