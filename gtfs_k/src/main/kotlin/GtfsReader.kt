package io.github.fumiya_kume.gtfs_k.lib

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.zip.ZipInputStream

@Suppress("unused", "RedundantVisibilityModifier")
public fun gtfsReader(url: String): GtfsData {
    return downloadGtfsFile(url)
        .unzipFile()
        .parseFileList()
}

private fun downloadGtfsFile(url: String): ZipInputStream = ZipInputStream(ByteArrayInputStream(URL(url).readBytes()))

private fun ZipInputStream.unzipFile(): Map<String, String> {
    val files = mutableMapOf<String, String>()
    var entry = nextEntry
    while (entry != null) {
        val content = readAllBytes().decodeToString()
        files[entry.name] = content
        entry = nextEntry
    }
    close()
    return files
}

private fun Map<String, String>.parseFileList(): GtfsData {
    return GtfsData(
        agency = parseAgency(),
        agencyJapan = parseAgencyJapan(),
        routes = parseRoutes(),
        routesJapan = parseRoutesJapan(),
        feedInfo = parseFeedInfo(),
        trips = parseTrips(),
        officeJapan = parseOfficeJapan(),
        frequencies = parseFrequencies(),
        calenders = parseCalendars(),
        shapes = parseShapes(),
        stopTimes = parseStopTimes()
    )
}

private fun Map<String, String>.parseAgency(): List<Agency> {
    val data = get("agency.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map { row ->
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
}

private fun Map<String, String>.parseAgencyJapan(): List<AgencyJapan> {
    val data = get("agency_jp.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map { row ->
        AgencyJapan(
            agencyId = row["agency_id"]?.let { AgencyId(it) },
            agencyOfficialName = row["agency_official_name"],
            agencyZipCode = row["agency_zip_code"],
            agencyAddress = row["agency_address"],
            agencyPresidentPos = row["agency_president_pos"],
            agencyPresidentName = row["agency_president_name"],
        )
    }
}

private fun Map<String, String>.parseRoutes(): List<Routes> {
    val data = get("routes.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map { row ->
        Routes(
            routeId = row["route_id"]?.let { RouteId(it) },
            agencyId = row["agency_id"]?.let { AgencyId(it) },
            routeShortName = row["route_short_name"],
            routeLongName = row["route_long_name"],
            routeDesc = row["route_desc"],
            routeType = row["route_type"],
            routeUrl = row["route_url"],
            routeColor = row["route_color"],
            routeTextColor = row["route_text_color"],
            jpParentRouteId = row["jp_parent_route_id"],
        )
    }
}

private fun Map<String, String>.parseRoutesJapan(): List<RoutesJapan> {
    val data = get("routes_jp.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        RoutesJapan(
            routeId = it["route_id"]?.let { RouteId(it) },
            routeUpdateDate = it["route_update_date"],
            originStop = it["origin_stop"],
            destinationStop = it["destination_stop"],
            viaStop = it["via_stop"],
        )
    }
}

private fun Map<String, String>.parseFeedInfo(): List<FeedInfo> {
    val data = get("feed_info.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        FeedInfo(
            feedPublisherName = it["feed_publisher_name"],
            feedPublisherUrl = it["feed_publisher_url"],
            feedLang = it["feed_lang"],
            feedStartDate = it["feed_start_date"],
            feedEndDate = it["feed_end_date"],
            feedVersion = it["feed_version"],
        )
    }
}

private fun Map<String, String>.parseTrips(): List<Trips> {
    val data = get("trips.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Trips(
            tripId = it["trip_id"]?.let { TripId(it) },
            routeId = it["route_id"]?.let { RouteId(it) },
            serviceId = it["service_id"]?.let { ServiceId(it) },
            jpOfficeId = it["jp_office_id"]?.let { OfficeId(it) },
            tripHeadSign = it["trip_headsign"],
            tripShortName = it["trip_short_name"],
            directionId = it["direction_id"],
            blockId = it["block_id"],
            shapeId = it["shape_id"],
            wheelchairAccessible = it["wheelchair_accessible"],
            bikesAllowed = it["bikes_allowed"],
            jpTripDesc = it["jp_trip_desc"],
            jpTripDescSymbol = it["jp_trip_desc_symbol"],
        )
    }
}

private fun Map<String, String>.parseOfficeJapan(): List<OfficeJapan> {
    val data = get("office_jp.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        OfficeJapan(
            officeId = it["office_id"]?.let { OfficeId(it) },
            officeName = it["office_name"],
            officeUrl = it["office_url"],
            officePhone = it["office_phone"],
        )
    }
}

private fun Map<String, String>.parseFrequencies(): List<Frequencies> {
    val data = get("frequencies.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Frequencies(
            tripId = it["trip_id"]?.let { TripId(it) },
            startTime = it["start_time"],
            endTime = it["end_time"],
            headwaySecs = it["headway_secs"],
            exactTimes = it["exact_times"],
        )
    }
}

private fun Map<String, String>.parseCalendars(): List<Calendar> {
    val data = get("calendar.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Calendar(
            serviceId = it["service_id"]?.let { ServiceId(it) },
            monday = it["monday"],
            tuesday = it["tuesday"],
            wednesday = it["wednesday"],
            thursday = it["thursday"],
            friday = it["friday"],
            saturday = it["saturday"],
            sunday = it["sunday"],
            startDate = it["start_date"],
            endDate = it["end_date"],
        )
    }
}

private fun Map<String, String>.parseShapes(): List<Shapes> {
    val data = get("shapes.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Shapes(
            shapeId = it["shape_id"],
            shapePtLat = it["shape_pt_lat"],
            shapePtLon = it["shape_pt_lng"],
            shapePtSequence = it["shape_pt_sequence"],
            shapeDistTraveled = it["shape_dist_traveled"],
        )
    }
}

private fun Map<String, String>.parseStopTimes(): List<StopTimes> {
    val data = get("stop_times.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        StopTimes(
            tripId = it["trip_id"]?.let { TripId(it) },
            stopId = it["stop_id"]?.let { StopId(it) },
            arrivalTime = it["arrival_time"],
            departureTime = it["departure_time"],
            stopSequence = it["stop_sequence"],
            stopHeadsign = it["stop_headsign"],
            pickupType = it["pickup_type"],
            dropOffType = it["drop_off_type"],
            shapeDistTraveled = it["shape_dist_traveled"],
            timePoint = it["timepoint"],
        )
    }
}

/**
 * The data can be
 * ```
 * ①8000020130001
 * ②8000020130001_1
 * ```
 */
@JvmInline
value class AgencyId(val id: String)

/**
 * The data can be
 * ```
 * 1001
 * ```
 */
@JvmInline
value class RouteId(val id: String)

/**
 * The data can be
 * ```
 * S
 * ```
 */
@JvmInline
value class OfficeId(val id: String)

@JvmInline
value class ShapeId(val id: String)

@JvmInline
value class ServiceId(val id: String)

@JvmInline
value class TripId(val id: String)

@JvmInline
value class StopId(val id: String)

@JvmInline
value class FareId(val id: String)


data class GtfsData(
    val agency: List<Agency> = emptyList(),
    val agencyJapan: List<AgencyJapan> = emptyList(),
    val routes: List<Routes> = emptyList(),
    val routesJapan: List<RoutesJapan> = emptyList(),
    val feedInfo: List<FeedInfo> = emptyList(),
    val trips: List<Trips> = emptyList(),
    val officeJapan: List<OfficeJapan> = emptyList(),
    val frequencies: List<Frequencies> = emptyList(),
    val calenders: List<Calendar> = emptyList(),
    val shapes: List<Shapes> = emptyList(),
    val stopTimes: List<StopTimes> = emptyList(),
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
    val routeTextColor: String?,
    val jpParentRouteId: String?,
)

data class RoutesJapan(
    val routeId: RouteId?,
    /**
     * ダイヤ改正日
     */
    val routeUpdateDate: String?,
    /**
     * 起点
     */
    val originStop: String?,
    /**
     * 終点
     */
    val destinationStop: String?,
    /**
     * 経過地
     */
    val viaStop: String?,
)

data class FeedInfo(
    val feedPublisherName: String?,
    val feedPublisherUrl: String?,
    val feedLang: String?,
    val feedStartDate: String?, // Consider using a Date type
    val feedEndDate: String?, // Consider using a Date type
    val feedVersion: String?
)

data class Trips(
    val tripId: TripId?,
    val routeId: RouteId?,
    val serviceId: ServiceId?,
    val jpOfficeId: OfficeId?,
    val tripHeadSign: String?,
    val tripShortName: String?,
    val directionId: String?,
    val blockId: String?,
    val shapeId: String?,
    val wheelchairAccessible: String?,
    val bikesAllowed: String?,
    val jpTripDesc: String?,
    val jpTripDescSymbol: String?,
)

data class OfficeJapan(
    val officeId: OfficeId?,
    val officeName: String?,
    val officeUrl: String?,
    val officePhone: String?,
)

data class Frequencies(
    val tripId: TripId?,
    val startTime: String?,
    val endTime: String?,
    val headwaySecs: String?,
    val exactTimes: String?,
)

data class Calendar(
    val serviceId: ServiceId?,
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

data class Shapes(
    val shapeId: String?,
    val shapePtLat: String?,
    val shapePtLon: String?,
    val shapePtSequence: String?,
    val shapeDistTraveled: String?
)

data class StopTimes(
    val tripId: TripId?,
    val stopId: StopId?,
    val arrivalTime: String?,
    val departureTime: String?,
    val stopSequence: String?,
    val stopHeadsign: String?,
    val pickupType: String?,
    val dropOffType: String?,
    val shapeDistTraveled: String?,
    val timePoint: String?
)

// -- until here, checked --

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

data class CalendarDates(
    val serviceId: String?,
    val date: String?, // Consider using a Date type
    val exceptionType: String?
)

data class Transfers(
    val fromStopId: String?,
    val toStopId: String?,
    val transferType: String?,
    val minTransferTime: String?
)

