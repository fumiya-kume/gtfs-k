package io.github.fumiya_kume.gtfs_k.lib

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.zip.ZipInputStream
import javax.sql.RowSetReader

@Suppress("unused", "RedundantVisibilityModifier")
public fun gtfsReader(url: String): GtfsData {
    val data = ByteArrayInputStream(URL(url).readBytes())
    val files = unzipGtfsInMemory(data)
    return GtfsData(
        agency = csvReader().readAllWithHeader(files["agency.txt"]!!).map { row ->
            Agency(
                agencyId = row["agency_id"]?.let { AgencyId(it) },
                agencyName = row["agency_name"],
                agencyUrl = row["agency_url"],
                agencyTimezone = row["agency_timezone"],
                agencyLang = row["agency_lang"],
                agencyPhone = row["agency_phone"],
                agencyFareUrl = row["agency_fare_url"],
                agencyEmail = row["agency_email"]
            )
        },
        agencyJapan = csvReader().readAllWithHeader(files["agency_jp.txt"]!!).map { row ->
            AgencyJapan(
                agencyId = (row["agency_id"])?.let { AgencyId(it) },
                agencyOfficialName = row["agency_official_name"],
                agencyZipCode = row["agency_zip_code"],
                agencyAddress = row["agency_address"],
                agencyPresidentPos = row["agency_president_pos"],
                agencyPresidentName = row["agency_president_name"],
            )
        }
    )
}

private fun unzipGtfsInMemory(inputStream: ByteArrayInputStream): Map<String, String> {
    val zipFile = ZipInputStream(inputStream)
    val files = mutableMapOf<String, String>()
    var entry = zipFile.nextEntry
    while (entry != null) {
        val content = zipFile.readAllBytes().decodeToString()
        files[entry.name] = content
        entry = zipFile.nextEntry
    }
    zipFile.close()
    return files
}

/**
The data can be
```
①8000020130001
②8000020130001_1
```
 */
@JvmInline
value class AgencyId(val id: String)

@JvmInline
value class RouteId(val id: String)

data class GtfsData(
    val agency: List<Agency> = emptyList(),
    val agencyJapan: List<AgencyJapan> = emptyList(),
    val routed: Routes? = null,
)

// Gtfs data structure generated by the GitHub Copilot
data class Agency(
    val agencyId: AgencyId?,
    val agencyName: String?,
    val agencyUrl: String?,
    val agencyTimezone: String?,
    val agencyLang: String?,
    val agencyPhone: String?,
    val agencyFareUrl: String?,
    val agencyEmail: String?
)

/**
 * 事業者追加情報
 */
data class AgencyJapan(
    val agencyId: AgencyId?,
    /**
     * 事業者正式名称
     */
    val agencyOfficialName: String?,
    /**
     * 事業者郵便番号
     */
    val agencyZipCode: String?,
    /**
     * 事業者住所
     */
    val agencyAddress: String?,
    /**
     * 代表者肩書
     */
    val agencyPresidentPos: String?,
    /**
     * 代表者氏名
     */
    val agencyPresidentName: String?
)

data class Routes(
    val routeId: RouteId?,
    val agencyId: AgencyId?,
    val routeShortName: String?,
    val routeLongName: String?,
    val routeDesc: String?,
    val routeType: String?,
    val routeUrl: String?,
    val routeColor: String?,
    val routeTextColor: String?
)

data class Trips(
    val routeId: RouteId?,
    val serviceId: String?,
    val tripId: String?,
    val tripHeadsign: String?,
    val tripShortName: String?,
    val directionId: String?,
    val blockId: String?,
    val shapeId: String?,
    val wheelchairAccessible: String?,
    val bikesAllowed: String?
)

data class StopTimes(
    val tripId: String?,
    val arrivalTime: String?,
    val departureTime: String?,
    val stopId: String?,
    val stopSequence: String?,
    val stopHeadsign: String?,
    val pickupType: String?,
    val dropOffType: String?,
    val shapeDistTraveled: String?,
    val timeline: String?
)

data class Stops(
    val stopId: String?,
    val stopCode: String?,
    val stopName: String,
    val stopDesc: String?,
    val stopLat: String?,
    val stopLon: String?,
    val zoneId: String?,
    val stopUrl: String?,
    val locationType: String?,
    val parentStation: String?,
    val stopTimezone: String?,
    val wheelchairBoarding: String?,
    val platformCode: String?
)

data class Calendar(
    val serviceId: String?,
    val monday: String?,
    val tuesday: String?,
    val wednesday: String?,
    val thursday: String?,
    val friday: String?,
    val saturday: String?,
    val sunday: String?,
    val startDate: String?, // Consider using a Date type
    val endDate: String? // Consider using a Date type
)

data class CalendarDates(
    val serviceId: String?,
    val date: String?, // Consider using a Date type
    val exceptionType: String?
)

data class Frequencies(
    val tripId: String?,
    val startTime: String?, // Consider using a Time type
    val endTime: String?, // Consider using a Time type
    val headwaySecs: String?,
    val exactTimes: String?
)

data class Transfers(
    val fromStopId: String?,
    val toStopId: String?,
    val transferType: String?,
    val minTransferTime: String?
)

data class FeedInfo(
    val feedPublisherName: String?,
    val feedPublisherUrl: String?,
    val feedLang: String?,
    val feedStartDate: String?, // Consider using a Date type
    val feedEndDate: String?, // Consider using a Date type
    val feedVersion: String?
)

data class Shapes(
    val shapeId: String,
    val shapePtLat: String?,
    val shapePtLon: String?,
    val shapePtSequence: String,
    val shapeDistTraveled: String??
)
