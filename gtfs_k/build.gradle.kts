plugins {
    id("buildsrc.convention.kotlin-jvm")

    application
    `maven-publish`
    alias(libs.plugins.sonatypeCentralUpload)
}

group = "io.github.fumiya-kume"
version = "0.0.8"

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("gtfs-k")
                description.set("Parsing the GTFS for Kotlin")
                url.set("https://github.com/fumiya-kume")
                licenses {
                    license {
                        name.set("GPL License")
                        url.set("https://github.com/fumiya-kume/gtfs-k/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("fumiya-kume")
                        name.set("Kume Fumiya")
                        email.set("fumiya.kume@hotmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/fumiya-kume/gtfs-k")
                }
                from(components["java"])
            }
        }
    }
}
tasks {
    sonatypeCentralUpload {
        dependsOn("jar", "sourcesJar", "javadocJar", "generatePomFileForMavenPublication")
        username = System.getenv("sonatype_central_name")
        password = System.getenv("sonatype_central_password")
        archives = files(*jars())
        pom = file("build/publications/maven/pom-default.xml")
        doFirst { delete("build/sonatype-central-upload") }
        dependsOn("build")
        mustRunAfter("build")
        signingKey = System.getenv("PGP_SECRET_KEY").replace('$', '\n')
        signingKeyPassphrase = System.getenv("PGP_PASSWORD")
    }
}

private fun jars(): Array<String> {
    return arrayOf(
        jarName(),
        jarName("javadoc"),
        jarName("sources")
    )
}

private fun jarName(kind: String = ""): String {
    val suffix = if (kind.isNotBlank()) "-$kind" else ""
    return "build/libs/$name-%s$suffix.jar".format(version)
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(libs.kotlinCsvJvm)
}