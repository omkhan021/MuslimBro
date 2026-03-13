pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MuslimBro"

include(":app")

// Core modules
include(":core:common")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:ui")

// Feature modules
include(":feature:prayertimes")
include(":feature:alarms")
include(":feature:quran")
include(":feature:quranplayer")
include(":feature:qibla")
include(":feature:widget")
include(":feature:settings")
