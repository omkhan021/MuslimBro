plugins {
    `kotlin-dsl`
}

group = "com.muslimbro.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.plugins.android.application.get().let {
        "com.android.tools.build:gradle:${it.version}"
    })
    compileOnly(libs.plugins.kotlin.android.get().let {
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${it.version}"
    })
    implementation(libs.plugins.ksp.get().let {
        "com.google.devtools.ksp:symbol-processing-gradle-plugin:${it.version}"
    })
    implementation(libs.plugins.hilt.get().let {
        "com.google.dagger:hilt-android-gradle-plugin:${it.version}"
    })
    compileOnly(libs.plugins.room.get().let {
        "androidx.room:room-gradle-plugin:${it.version}"
    })
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "muslimbro.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "muslimbro.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidApplication") {
            id = "muslimbro.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("hilt") {
            id = "muslimbro.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("room") {
            id = "muslimbro.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("compose") {
            id = "muslimbro.compose"
            implementationClass = "ComposeConventionPlugin"
        }
    }
}
