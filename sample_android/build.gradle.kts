plugins {
//    alias(libs.plugins.android.application)
    id("com.android.application")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "system.kuu.sample_android"
    compileSdk = 35

    defaultConfig {
        applicationId = "system.kuu.sample_android"
        minSdk = 27
        targetSdk = 35
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
    // kotlinOptions removed as AGP built-in Kotlin uses compileOptions directly
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
    implementation("io.github.fumiya-kume:gtfs_k:0.0.9")

    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espressoCore)
    testImplementation("org.jetbrains.kotlin:kotlin-test:${libs.versions.kotlin.get()}")
}