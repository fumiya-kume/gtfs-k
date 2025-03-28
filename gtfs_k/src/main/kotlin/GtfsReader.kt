package io.github.fumiya_kume.gtfs_k.lib

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sun.rmi.server.Dispatcher

@Suppress("unused", "RedundantVisibilityModifier")
public suspend fun gtfsReader(url: String): GtfsData = withContext(Dispatchers.IO) {
    downloadGtfsFile(url)
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
        calendarDates = parseCalendarDates(),
        shapes = parseShapes(),
        stopTimes = parseStopTimes(),
        stops = parseStops(),
        transfers = parseTransfers(),
        fareAttributes = parseFareAttributes()
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

private fun Map<String, String>.parseRoutes(): List<Route> {
    val data = get("routes.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map { row ->
        Route(
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

private fun Map<String, String>.parseTrips(): List<Trip> {
    val data = get("trips.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Trip(
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

private fun Map<String, String>.parseFrequencies(): List<Frequency> {
    val data = get("frequencies.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Frequency(
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

private fun Map<String, String>.parseShapes(): List<Shape> {
    val data = get("shapes.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Shape(
            shapeId = it["shape_id"],
            shapePtLat = it["shape_pt_lat"],
            shapePtLon = it["shape_pt_lng"],
            shapePtSequence = it["shape_pt_sequence"],
            shapeDistTraveled = it["shape_dist_traveled"],
        )
    }
}

private fun Map<String, String>.parseStopTimes(): List<StopTime> {
    val data = get("stop_times.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        StopTime(
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

private fun Map<String, String>.parseStops(): List<Stop> {
    val data = get("stops.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Stop(
            stopId = it["stop_id"]?.let { StopId(it) },
            stopCode = it["stop_code"],
            stopName = it["stop_name"] ?: "",
            stopDesc = it["stop_desc"],
            stopLat = it["stop_lat"],
            stopLon = it["stop_lon"],
            zoneId = it["zone_id"],
            stopUrl = it["stop_url"],
            locationType = it["location_type"],
            parentStation = it["parent_station"],
            stopTimezone = it["stop_timezone"],
            wheelchairBoarding = it["wheelchair_boarding"],
            platformCode = it["platform_code"]
       )
    }
}

private fun Map<String, String>.parseCalendarDates(): List<CalendarDate> {
    val data = get("calendar_dates.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        CalendarDate(
            serviceId = it["service_id"]?.let { ServiceId(it) },
            date = it["date"],
            exceptionType = it["exception_type"],
        )
    }
}

private fun Map<String, String>.parseTransfers(): List<Transfer> {
    val data = get("transfers.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        Transfer(
            fromStopId = it["from_stop_id"],
            toStopId = it["to_stop_id"],
            transferType = it["transfer_type"],
            minTransferTime = it["min_transfer_time"]
        )
    }
}

private fun Map<String, String>.parseFareAttributes(): List<FareAttribute> {
    val data = get("fare_attributes.txt") ?: return emptyList()
    return csvReader().readAllWithHeader(data).map {
        FareAttribute(
            fareId = it["fare_id"]?.let { FareId(it) },
            price = it["price"],
            currencyType = it["currency_type"],
            paymentMethod = it["payment_method"],
            transfers = it["transfers"],
            transferDuration = it["transfer_duration"]
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
    val routes: List<Route> = emptyList(),
    val routesJapan: List<RoutesJapan> = emptyList(),
    val feedInfo: List<FeedInfo> = emptyList(),
    val trips: List<Trip> = emptyList(),
    val officeJapan: List<OfficeJapan> = emptyList(),
    val frequencies: List<Frequency> = emptyList(),
    val calenders: List<Calendar> = emptyList(),
    val calendarDates: List<CalendarDate> = emptyList(),
    val shapes: List<Shape> = emptyList(),
    val stopTimes: List<StopTime> = emptyList(),
    val stops: List<Stop> = emptyList(),
    val transfers: List<Transfer> = emptyList(),
    val fareAttributes: List<FareAttribute> = emptyList()
)

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

data class Route(
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

data class Trip(
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

data class Frequency(
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

data class Shape(
    val shapeId: String?,
    val shapePtLat: String?,
    val shapePtLon: String?,
    val shapePtSequence: String?,
    val shapeDistTraveled: String?
)

data class StopTime(
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

data class CalendarDate(
    val serviceId: ServiceId?,
    val date: String?, // Consider using a Date type
    val exceptionType: String?
)

data class Stop(
    val stopId: StopId?,
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

data class Transfer(
    val fromStopId: String?,
    val toStopId: String?,
    val transferType: String?,
    val minTransferTime: String?
)

data class FareAttribute(
    val fareId: FareId?,
    val price: String?,
    val currencyType: String?,
    val paymentMethod: String?,
    val transfers: String?,
    val transferDuration: String?
)
