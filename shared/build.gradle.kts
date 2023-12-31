import Build_gradle.Versions.sqlDelightVersion
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-17"
    id("com.github.ben-manes.versions") version "0.47.0"
}

dependencies {
    commonMainApi("dev.icerock.moko:resources:0.23.0")

    commonTestImplementation("dev.icerock.moko:resources-test:0.23.0")
}

configureDependencyCheck()

// https://github.com/ben-manes/gradle-versions-plugin
// run gradle dependencyUpdates
fun configureDependencyCheck() {
    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

multiplatformResources {
    multiplatformResourcesPackage = "com.drahovac.weatherstationdisplay" // required
    multiplatformResourcesClassName = "MR" // optional, default MR
    iosBaseLocalizationRegion = "en" // optional, default "en"
    multiplatformResourcesSourceSet = "commonMain"  // optional, default "commonMain"
}

kotlin {
    jvmToolchain(20)
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "20"
            }
        }
    }

    sqldelight {
        database("AppDatabase") {
            packageName = "com.drahovac.weatherstationdisplay"
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            export("dev.icerock.moko:resources:0.23.0")
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0-ALPHA-13")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")
                implementation(Deps.Koin.core)
                implementation(Deps.Ktor.ktor)
                implementation(Deps.Ktor.ktorLogging)
                implementation(Deps.Ktor.ktorSerialization)
                implementation(Deps.Ktor.ktorContent)
                implementation(Deps.Ktor.jsonSerial)
                implementation("co.touchlab:kermit:1.2.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("co.touchlab:kermit-test:2.0.0-RC5")
            }
        }
        val androidMain by getting {
            dependencies {
                api(Deps.Koin.android)
                api(Deps.Koin.compose)
                implementation("androidx.security:security-crypto:1.1.0-alpha06")
                implementation(Deps.Ktor.ktorAndroidEngine)
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
                api("com.patrykandpatrick.vico:compose-m3:1.11.0")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation("io.mockk:mockk:1.13.7")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Deps.Ktor.ktorIOSEngine)
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.drahovac.weatherstationdisplay"
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }
}

object Versions {
    const val koin = "3.4.3"
    const val koinCompose = "3.4.6"
    const val sqlDelightVersion = "1.5.5"
    const val ktor = "2.3.3"
    const val serial = "1.6.0"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koinCompose}"
    }

    object Ktor {
        const val ktor = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val ktorAndroidEngine = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        const val ktorIOSEngine = "io.ktor:ktor-client-darwin:${Versions.ktor}"
        const val ktorLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val ktorContent = "io.ktor:ktor-client-content-negotiation:${Versions.ktor}"
        const val ktorSerialization = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"
        const val ktorAuth = "io.ktor:ktor-client-auth:${Versions.ktor}"
        const val ktorMock = "io.ktor:ktor-client-mock:${Versions.ktor}"
        const val jsonSerial = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serial}"
    }
}

tasks.matching { it.name == "kspKotlinIosSimulatorArm64" }.configureEach {
    dependsOn(tasks.getByName("generateMRiosSimulatorArm64Main"))
}
