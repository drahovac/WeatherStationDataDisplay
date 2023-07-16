plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
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