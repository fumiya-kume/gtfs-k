// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used to configure some project-wide configuration, like plugins management, dependencies management, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains Plugin to automatically download JDKs required by subprojects
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    kotlin("android") version "2.2.0" apply false
    id("com.android.application") version "8.11.1" apply false
}

include(":gtfs_k")
include("sample")
include(":sample_android")

rootProject.name = "gtfs-k"
