import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Enable Compose build features for library modules
            extensions.findByType(LibraryExtension::class.java)?.apply {
                buildFeatures { compose = true }
            }
            // For application modules
            extensions.findByType(com.android.build.api.dsl.ApplicationExtension::class.java)?.apply {
                buildFeatures { compose = true }
            }

            dependencies {
                val bom = libs.findLibrary("compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                add("implementation", libs.findBundle("compose").get())
                add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
                add("androidTestImplementation", libs.findLibrary("compose-ui-test-junit4").get())
                add("debugImplementation", libs.findLibrary("compose-ui-test-manifest").get())
            }
        }
    }
}
