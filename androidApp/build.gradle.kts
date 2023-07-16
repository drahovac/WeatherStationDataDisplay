import Build_gradle.Version.COMPOSE

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.drahovac.weatherstationdisplay.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.drahovac.weatherstationdisplay.android"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:$COMPOSE")
    implementation("androidx.compose.ui:ui-tooling:$COMPOSE")
    implementation("androidx.compose.ui:ui-tooling-preview:$COMPOSE")
    implementation("androidx.compose.foundation:foundation:$COMPOSE")
    implementation("androidx.compose.material:material:$COMPOSE")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.material3:material3:1.1.1")
}

object Version {
    const val COMPOSE = "1.4.3"
}