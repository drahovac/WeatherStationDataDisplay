plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-13"
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
                implementation(Deps.Koin.core)
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
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
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
    }
}

object Versions {
    const val koin = "3.4.0"
    const val sqlDelightVersion = "1.5.5"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
    }
}