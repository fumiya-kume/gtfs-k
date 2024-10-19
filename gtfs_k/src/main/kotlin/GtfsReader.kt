package io.github.fumiya_kume.gtfs_k.lib

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.zip.ZipInputStream

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
        }
    )
}

fun unzipGtfsInMemory(inputStream: ByteArrayInputStream): Map<String, String> {
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
    val agencyId: AgencyId,
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
    val routeId: String?,
    val agencyId: AgencyId?,
    val routeShortName: String?,
    val route_long_name: String?,
    val route_desc: String?,
    val route_type: Int,
    val route_url: String?,
    val route_color: String?,
    val route_text_color: String?
)

data class Trips(
    val route_id: Int?,
    val service_id: Int?,
    val trip_id: Int,
    val trip_headsign: String?,
    val trip_short_name: String?,
    val direction_id: Int?,
    val block_id: Int?,
    val shape_id: Int?,
    val wheelchair_accessible: Int?,
    val bikes_allowed: Int?
)

data class StopTimes(
    val trip_id: Int?,
    val arrival_time: String,
    val departure_time: String,
    val stop_id: Int?,
    val stop_sequence: Int,
    val stop_headsign: String?,
    val pickup_type: Int?,
    val drop_off_type: Int?,
    val shape_dist_traveled: Float?,
    val timepoint: Int?
)

data class Stops(
    val stop_id: Int,
    val stop_code: String?,
    val stop_name: String,
    val stop_desc: String?,
    val stop_lat: Double,
    val stop_lon: Double,
    val zone_id: Int?,
    val stop_url: String?,
    val location_type: Int?,
    val parent_station: String?,
    val stop_timezone: String?,
    val wheelchair_boarding: Int?,
    val platform_code: String?
)

data class Calendar(
    val service_id: Int,
    val monday: Int,
    val tuesday: Int,
    val wednesday: Int,
    val thursday: Int,
    val friday: Int,
    val saturday: Int,
    val sunday: Int,
    val start_date: String, // Consider using a Date type
    val end_date: String // Consider using a Date type
)

data class CalendarDates(
    val service_id: Int?,
    val date: String, // Consider using a Date type
    val exception_type: Int
)

data class Frequencies(
    val trip_id: Int?,
    val start_time: String, // Consider using a Time type
    val end_time: String, // Consider using a Time type
    val headway_secs: Int,
    val exact_times: Int?
)

data class Transfers(
    val from_stop_id: Int?,
    val to_stop_id: Int?,
    val transfer_type: Int,
    val min_transfer_time: Int?
)

data class FeedInfo(
    val feed_publisher_name: String,
    val feed_publisher_url: String,
    val feed_lang: String,
    val feed_start_date: String, // Consider using a Date type
    val feed_end_date: String, // Consider using a Date type
    val feed_version: String?
)

data class Shapes(
    val shape_id: Int,
    val shape_pt_lat: Double,
    val shape_pt_lon: Double,
    val shape_pt_sequence: Int,
    val shape_dist_traveled: Float?
)
