plugins {
//    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "system.kuu.sample_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "system.kuu.sample_android"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(platform(libs.composeBom))
    implementation(libs.ui)
    implementation(libs.material)
    implementation(libs.uiToolingPreview)
    implementation(libs.activityCompose)
    implementation(libs.material3)
    implementation("io.github.fumiya-kume:gtfs_k:0.0.8")

    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espressoCore)
}