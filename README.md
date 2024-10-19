# gtfs-k [![Maven Central Version](https://img.shields.io/maven-central/v/io.github.fumiya-kume/gtfs_k)](https://central.sonatype.com/artifact/io.github.fumiya-kume/gtfs_k) [![Java CI with Gradle](https://github.com/fumiya-kume/gtfs-k/actions/workflows/gradle.yml/badge.svg)](https://github.com/fumiya-kume/gtfs-k/actions/workflows/gradle.yml)

The library that parsing the GTFS format files.

> [!WARNING]  
> The library under the development, the data structure, hidden bugs still there.

# Points
- Made with pure Kotlin
- Easier to use
- Modern codebase

# Installation 

## Kotlin

```kt
implementation("io.github.fumiya-kume:gtfs_k:0.0.5")
```

## Apache Maven

```
<dependency>
    <groupId>io.github.fumiya-kume</groupId>
    <artifactId>gtfs_k</artifactId>
    <version>0.0.5</version>
</dependency>
```

# Usage Examples

```kt
fun main() {
    val gtfsUrl = "https://github.com/fumiya-kume/gtfs-k/raw/refs/heads/master/test-data/toyotetsu.zip"
    val result = gtfsReader(gtfsUrl)
    result.agency.forEach {
        println(it.agencyName)
        println(it.agencyLang)
    }
    result.agencyJapan.forEach { 
        println(it.agencyId)
        println(it.agencyAddress)
        println(it.agencyZipCode)
    }
}
```

# Copy rights

At the test data of GTFS, We'are using following content
- Toyotetsu - https://bus-viewer.jp/toyotetsu/view/opendataToyotetsu.html
