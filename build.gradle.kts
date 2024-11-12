tasks.register("clean") {
    doLast {
        println("clean task")
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
