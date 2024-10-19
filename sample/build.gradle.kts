plugins {
    kotlin("jvm")
}

group = "systems.kuu.gtfs-k"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.fumiya-kume:gtfs_k:0.0.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}