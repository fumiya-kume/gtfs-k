plugins {
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

ktlint {
    version.set("0.41.0")
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
}

tasks.register("clean") {
    doLast {
        println("clean task")
    }
}
