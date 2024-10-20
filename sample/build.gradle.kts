plugins {
    kotlin("jvm")
}

group = "systems.kuu.gtfs-k"
version = "1.0.0"

dependencies {
    implementation("io.github.fumiya-kume:gtfs_k:0.0.8")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}