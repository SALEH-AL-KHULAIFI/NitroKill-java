plugins {
    id("com.android.application")
}

android {
    namespace = "com.isx3i.nitrokill"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.isx3i.nitrokill"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// No dependencies block: the app uses only the Android framework SDK —
// no AndroidX, no Kotlin, no Compose, no DataStore. Everything the app
// needs (Activity, Service, TrafficStats, Notification, SharedPreferences,
// Toolbar, Switch, PopupMenu...) already ships inside the platform itself.
