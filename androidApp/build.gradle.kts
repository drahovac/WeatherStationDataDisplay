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
        versionCode = 3
        versionName = "1.1.1-alpha"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile(Version.proguardDefaultRules),
                Version.proguardRules
            )
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
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
}

object Version {
    const val COMPOSE = "1.4.3"
    const val proguardDefaultRules = "proguard-android.txt"
    const val proguardRules = "proguard-rules.pro"
}
