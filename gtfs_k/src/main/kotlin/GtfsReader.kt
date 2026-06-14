package io.github.fumiya_kume.gtfs_k.lib

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val isoDateFormatter = DateTimeFormatter.BASIC_ISO_DATE

private fun String?.toLocalDateOrNull(): LocalDate? {
    if (this.isNullOrBlank()) return null
    return try {
        LocalDate.parse(this, isoDateFormatter)
    } catch (e: Exception) {
        null
    }
}

private fun String?.toBooleanFromGtfs(): Boolean? {
    return when(this) {
        "1" -> true
        "0" -> false
        else -> null
    }
}

private class NonClosingInputStream(private val delegate: InputStream) : InputStream() {
    override fun read(): Int = delegate.read()
    override fun read(b: ByteArray): Int = delegate.read(b)
    override fun read(b: ByteArray, off: Int, len: Int): Int = delegate.read(b, off, len)
    override fun close() { /* do nothing */ }
    override fun available(): Int = delegate.available()
}

@Suppress("unused", "RedundantVisibilityModifier")
public suspend fun gtfsReader(url: String): GtfsData = withContext(Dispatchers.IO) {
    var agency: List<Agency> = emptyList()
    var agencyJapan: List<AgencyJapan> = emptyList()
    var routes: List<Route> = emptyList()
    var routesJapan: List<RoutesJapan> = emptyList()
    var feedInfo: List<FeedInfo> = emptyList()
    var trips: List<Trip> = emptyList()
    var officeJapan: List<OfficeJapan> = emptyList()
    var frequencies: List<Frequency> = emptyList()
    var calenders: List<Calendar> = emptyList()
    var calendarDates: List<CalendarDate> = emptyList()
    var shapes: List<Shape> = emptyList()
    var stopTimes: List<StopTime> = emptyList()
    var stops: List<Stop> = emptyList()
    var transfers: List<Transfer> = emptyList()
    var fareAttributes: List<FareAttribute> = emptyList()

    ZipInputStream(URL(url).openStream()).use { zis ->
        generateSequence { zis.nextEntry }.forEach { entry ->
            val nonClosingZis = NonClosingInputStream(zis)
            when (entry.name) {
                "agency.txt" -> agency = parseAgency(nonClosingZis)
                "agency_jp.txt" -> agencyJapan = parseAgencyJapan(nonClosingZis)
                "routes.txt" -> routes = parseRoutes(nonClosingZis)
                "routes_jp.txt" -> routesJapan = parseRoutesJapan(nonClosingZis)
                "feed_info.txt" -> feedInfo = parseFeedInfo(nonClosingZis)
                "trips.txt" -> trips = parseTrips(nonClosingZis)
                "office_jp.txt" -> officeJapan = parseOfficeJapan(nonClosingZis)
                "frequencies.txt" -> frequencies = parseFrequencies(nonClosingZis)
                "calendar.txt" -> calenders = parseCalendars(nonClosingZis)
                "calendar_dates.txt" -> calendarDates = parseCalendarDates(nonClosingZis)
                "shapes.txt" -> shapes = parseShapes(nonClosingZis)
                "stop_times.txt" -> stopTimes = parseStopTimes(nonClosingZis)
                "stops.txt" -> stops = parseStops(nonClosingZis)
                "transfers.txt" -> transfers = parseTransfers(nonClosingZis)
                "fare_attributes.txt" -> fareAttributes = parseFareAttributes(nonClosingZis)
            }
        }
    }

    GtfsData(
        agency = agency,
        agencyJapan = agencyJapan,
        routes = routes,
        routesJapan = routesJapan,
        feedInfo = feedInfo,
        trips = trips,
        officeJapan = officeJapan,
        frequencies = frequencies,
        calenders = calenders,
        calendarDates = calendarDates,
        shapes = shapes,
        stopTimes = stopTimes,
        stops = stops,
        transfers = transfers,
        fareAttributes = fareAttributes
    )
}

private fun parseAgency(stream: InputStream): List<Agency> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { row ->
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
    }.toList()
    }
}

private fun parseAgencyJapan(stream: InputStream): List<AgencyJapan> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { row ->
        AgencyJapan(
            agencyId = row["agency_id"]?.let { AgencyId(it) },
            agencyOfficialName = row["agency_official_name"],
            agencyZipCode = row["agency_zip_code"],
            agencyAddress = row["agency_address"],
            agencyPresidentPos = row["agency_president_pos"],
            agencyPresidentName = row["agency_president_name"],
        )
    }.toList()
    }
}

private fun parseRoutes(stream: InputStream): List<Route> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { row ->
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
            jpParentRouteId = row["jp_parent_route_id"]?.let { RouteId(it) },
        )
    }.toList()
    }
}

private fun parseRoutesJapan(stream: InputStream): List<RoutesJapan> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        RoutesJapan(
            routeId = it["route_id"]?.let { RouteId(it) },
            routeUpdateDate = it["route_update_date"],
            originStop = it["origin_stop"],
            destinationStop = it["destination_stop"],
            viaStop = it["via_stop"],
        )
    }.toList()
    }
}

private fun parseFeedInfo(stream: InputStream): List<FeedInfo> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        FeedInfo(
            feedPublisherName = it["feed_publisher_name"],
            feedPublisherUrl = it["feed_publisher_url"],
            feedLang = it["feed_lang"],
            feedStartDate = it["feed_start_date"].toLocalDateOrNull(),
            feedEndDate = it["feed_end_date"].toLocalDateOrNull(),
            feedVersion = it["feed_version"],
        )
    }.toList()
    }
}

private fun parseTrips(stream: InputStream): List<Trip> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        Trip(
            tripId = it["trip_id"]?.let { TripId(it) },
            routeId = it["route_id"]?.let { RouteId(it) },
            serviceId = it["service_id"]?.let { ServiceId(it) },
            jpOfficeId = it["jp_office_id"]?.let { OfficeId(it) },
            tripHeadSign = it["trip_headsign"],
            tripShortName = it["trip_short_name"],
            directionId = it["direction_id"],
            blockId = it["block_id"]?.let { BlockId(it) },
            shapeId = it["shape_id"]?.let { ShapeId(it) },
            wheelchairAccessible = it["wheelchair_accessible"],
            bikesAllowed = it["bikes_allowed"],
            jpTripDesc = it["jp_trip_desc"],
            jpTripDescSymbol = it["jp_trip_desc_symbol"],
        )
    }.toList()
    }
}

private fun parseOfficeJapan(stream: InputStream): List<OfficeJapan> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        OfficeJapan(
            officeId = it["office_id"]?.let { OfficeId(it) },
            officeName = it["office_name"],
            officeUrl = it["office_url"],
            officePhone = it["office_phone"],
        )
    }.toList()
    }
}

private fun parseFrequencies(stream: InputStream): List<Frequency> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        Frequency(
            tripId = it["trip_id"]?.let { TripId(it) },
            startTime = it["start_time"],
            endTime = it["end_time"],
            headwaySecs = it["headway_secs"],
            exactTimes = it["exact_times"].toBooleanFromGtfs(),
        )
    }.toList()
    }
}

private fun parseCalendars(stream: InputStream): List<Calendar> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        Calendar(
            serviceId = it["service_id"]?.let { ServiceId(it) },
            monday = it["monday"].toBooleanFromGtfs(),
            tuesday = it["tuesday"].toBooleanFromGtfs(),
            wednesday = it["wednesday"].toBooleanFromGtfs(),
            thursday = it["thursday"].toBooleanFromGtfs(),
            friday = it["friday"].toBooleanFromGtfs(),
            saturday = it["saturday"].toBooleanFromGtfs(),
            sunday = it["sunday"].toBooleanFromGtfs(),
            startDate = it["start_date"].toLocalDateOrNull(),
            endDate = it["end_date"].toLocalDateOrNull(),
        )
    }.toList()
    }
}

private fun parseShapes(stream: InputStream): List<Shape> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        Shape(
            shapeId = it["shape_id"]?.let { ShapeId(it) },
            shapePtLat = it["shape_pt_lat"],
            shapePtLon = it["shape_pt_lon"],
            shapePtSequence = it["shape_pt_sequence"],
            shapeDistTraveled = it["shape_dist_traveled"],
        )
    }.toList()
    }
}

private fun parseStopTimes(stream: InputStream): List<StopTime> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
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
    }.toList()
    }
}

private fun parseStops(stream: InputStream): List<Stop> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { row: Map<String, String> ->
        Stop(
            stopId = row["stop_id"]?.let { StopId(it) },
            stopCode = row["stop_code"],
            stopName = row["stop_name"] ?: "",
            stopDesc = row["stop_desc"],
            stopLat = row["stop_lat"],
            stopLon = row["stop_lon"],
            zoneId = row["zone_id"]?.let { ZoneId(it) },
            stopUrl = row["stop_url"],
            locationType = row["location_type"],
            parentStation = row["parent_station"]?.let { StopId(it) },
            stopTimezone = row["stop_timezone"],
            wheelchairBoarding = row["wheelchair_boarding"],
            platformCode = row["platform_code"]
       )
    }.toList()
    }
}

private fun parseCalendarDates(stream: InputStream): List<CalendarDate> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        CalendarDate(
            serviceId = it["service_id"]?.let { ServiceId(it) },
            date = it["date"].toLocalDateOrNull(),
            exceptionType = it["exception_type"],
        )
    }.toList()
    }
}

private fun parseTransfers(stream: InputStream): List<Transfer> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        Transfer(
            fromStopId = it["from_stop_id"]?.let { StopId(it) },
            toStopId = it["to_stop_id"]?.let { StopId(it) },
            transferType = it["transfer_type"],
            minTransferTime = it["min_transfer_time"]
        )
    }.toList()
    }
}

private fun parseFareAttributes(stream: InputStream): List<FareAttribute> {
    return csvReader().open(stream) { readAllWithHeaderAsSequence().map { it ->
        FareAttribute(
            fareId = it["fare_id"]?.let { FareId(it) },
            price = it["price"],
            currencyType = it["currency_type"],
            paymentMethod = it["payment_method"],
            transfers = it["transfers"],
            transferDuration = it["transfer_duration"]
        )
    }.toList()
    }
}

@JvmInline value class AgencyId(val id: String)
@JvmInline value class RouteId(val id: String)
@JvmInline value class OfficeId(val id: String)
@JvmInline value class ServiceId(val id: String)
@JvmInline value class TripId(val id: String)
@JvmInline value class StopId(val id: String)
@JvmInline value class FareId(val id: String)
@JvmInline value class ShapeId(val id: String)
@JvmInline value class BlockId(val id: String)
@JvmInline value class ZoneId(val id: String)

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

data class AgencyJapan(
    val agencyId: AgencyId?,
    val agencyOfficialName: String?,
    val agencyZipCode: String?,
    val agencyAddress: String?,
    val agencyPresidentPos: String?,
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
    val jpParentRouteId: RouteId?,
)

data class RoutesJapan(
    val routeId: RouteId?,
    val routeUpdateDate: String?,
    val originStop: String?,
    val destinationStop: String?,
    val viaStop: String?,
)

data class FeedInfo(
    val feedPublisherName: String?,
    val feedPublisherUrl: String?,
    val feedLang: String?,
    val feedStartDate: LocalDate?,
    val feedEndDate: LocalDate?,
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
    val blockId: BlockId?,
    val shapeId: ShapeId?,
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
    val exactTimes: Boolean?,
)

data class Calendar(
    val serviceId: ServiceId?,
    val monday: Boolean?,
    val tuesday: Boolean?,
    val wednesday: Boolean?,
    val thursday: Boolean?,
    val friday: Boolean?,
    val saturday: Boolean?,
    val sunday: Boolean?,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)

data class Shape(
    val shapeId: ShapeId?,
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
    val date: LocalDate?,
    val exceptionType: String?
)

data class Stop(
    val stopId: StopId?,
    val stopCode: String?,
    val stopName: String,
    val stopDesc: String?,
    val stopLat: String?,
    val stopLon: String?,
    val zoneId: ZoneId?,
    val stopUrl: String?,
    val locationType: String?,
    val parentStation: StopId?,
    val stopTimezone: String?,
    val wheelchairBoarding: String?,
    val platformCode: String?
)

data class Transfer(
    val fromStopId: StopId?,
    val toStopId: StopId?,
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
