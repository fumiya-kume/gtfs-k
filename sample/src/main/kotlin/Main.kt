package systems.kuu

import io.github.fumiya_kume.gtfs_k.lib.gtfsReader

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