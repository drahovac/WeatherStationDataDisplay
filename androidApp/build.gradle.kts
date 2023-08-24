import Build_gradle.Version.COMPOSE

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.drahovac.weatherstationdisplay.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.drahovac.weatherstationdisplay.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.2.2-alpha"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
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
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlinOptions {
        jvmTarget = "20"
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
    implementation("androidx.navigation:navigation-compose:2.7.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
}

object Version {
    const val COMPOSE = "1.5.0"
    const val proguardDefaultRules = "proguard-android.txt"
    const val proguardRules = "proguard-rules.pro"
}
