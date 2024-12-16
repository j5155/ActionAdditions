import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.io.ByteArrayOutputStream

/* Gets the version name from the latest Git tag */
fun getVersionName(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--always")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

apply(plugin = "org.jetbrains.dokka")


repositories {
    mavenCentral()
    google()
    maven("https://maven.brott.dev/")
}

dependencies {
    compileOnly("com.android.support:appcompat-v7:28.0.0")
    compileOnly(project(":core"))

    compileOnly("org.firstinspires.ftc:RobotCore:10.1.1")
    compileOnly("org.firstinspires.ftc:Hardware:10.1.1")

    implementation("com.acmerobotics.dashboard:dashboard:0.4.16") {
        exclude(group="org.firstinspires.ftc")
    }

    implementation("com.acmerobotics.roadrunner:actions:1.0.0")
}

tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            includes.from("MODULE.md")
        }
    }
}

android {
    namespace = "page.j5155.roadrunner.expressway.ftc"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }
}
afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "dairyReleases"
                url = uri("https://repo.dairy.foundation/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
            maven {
                name = "dairySnapshots"
                url = uri("https://repo.dairy.foundation/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = "page.j5155.roadrunner.expressway"
                artifactId = "ftc"
                version = getVersionName()
                from(components["release"])
            }
        }
    }
}


