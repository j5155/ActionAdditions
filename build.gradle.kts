import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    kotlin("jvm") version "2.0.20"

    id("org.jetbrains.dokka") version "1.9.20"

    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
}

tasks.withType<DokkaMultiModuleTask>().configureEach {
    val dokkaDir = layout.buildDirectory.dir("dokka")
    outputDirectory.set(dokkaDir)
    pluginsMapConfiguration.set(
        mapOf(Pair("org.jetbrains.dokka.base.DokkaBase", """
            {
                "footerMessage": "Copyright © 2024 Expressway contributors, BSD-3-Clause"
            }"""))
    )
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

kotlin {
    jvmToolchain(18)
}
