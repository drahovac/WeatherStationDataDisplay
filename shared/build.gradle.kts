plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version "1.8.10"
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-9"
}

dependencies {
    commonMainApi("dev.icerock.moko:resources:0.22.0")

    commonTestImplementation("dev.icerock.moko:resources-test:0.21.2")
}

multiplatformResources {
    multiplatformResourcesPackage = "com.drahovac.weatherstationdisplay" // required
    multiplatformResourcesClassName = "MR" // optional, default MR
    iosBaseLocalizationRegion = "en" // optional, default "en"
    multiplatformResourcesSourceSet = "commonMain"  // optional, default "commonMain"
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
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
            export("dev.icerock.moko:resources:0.22.0")
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0-ALPHA-12")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation(Deps.Koin.core)
                implementation(Deps.Ktor.ktor)
                implementation(Deps.Ktor.ktorLogging)
                implementation(Deps.Ktor.ktorSerialization)
                implementation(Deps.Ktor.ktorContent)
                implementation(Deps.Ktor.jsonSerial)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(Deps.Koin.android)
                api(Deps.Koin.compose)
                implementation("androidx.security:security-crypto:1.1.0-alpha06")
                implementation(Deps.Ktor.ktorAndroidEngine)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
                implementation("io.mockk:mockk:1.13.5")
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
    const val koin = "3.4.0"
    const val sqlDelightVersion = "1.5.5"
    const val ktor = "2.1.3"
    const val serial = "1.4.1"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
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
