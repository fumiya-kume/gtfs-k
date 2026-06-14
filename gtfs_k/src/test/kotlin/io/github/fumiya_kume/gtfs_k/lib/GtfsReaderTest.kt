package io.github.fumiya_kume.gtfs_k.lib

import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.time.LocalDate

class GtfsReaderTest {

    private fun createZipUrl(files: Map<String, String>): String {
        val tempFile = File.createTempFile("gtfs_test", ".zip")
        tempFile.deleteOnExit()
        ZipOutputStream(tempFile.outputStream()).use { zos ->
            for ((name, content) in files) {
                val entry = ZipEntry(name)
                zos.putNextEntry(entry)
                zos.write(content.toByteArray())
                zos.closeEntry()
            }
        }
        return tempFile.toURI().toURL().toString()
    }

    @Test
    fun testParseAgency() = runBlocking {
        val agencyCsv = """
            agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone,agency_fare_url,agency_email
            A1,Agency One,http://agency1.com,Asia/Tokyo,ja,000-0000,http://agency1.com/fare,info@agency1.com
        """.trimIndent()

        val url = createZipUrl(mapOf("agency.txt" to agencyCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.agency.size)
        val agency = gtfsData.agency[0]
        assertEquals("A1", agency.agencyId?.id)
        assertEquals("Agency One", agency.agencyName)
        assertEquals("http://agency1.com", agency.agencyUrl)
        assertEquals("Asia/Tokyo", agency.agencyTimezone)
        assertEquals("ja", agency.agencyLang)
        assertEquals("000-0000", agency.agencyPhone)
        assertEquals("http://agency1.com/fare", agency.agencyFareUrl)
        assertEquals("info@agency1.com", agency.agencyEmail)
    }

    @Test
    fun testParseAgencyJapan() = runBlocking {
        val agencyJpCsv = """
            agency_id,agency_official_name,agency_zip_code,agency_address,agency_president_pos,agency_president_name
            A1,Official Agency One,123-4567,Tokyo,President,John Doe
        """.trimIndent()

        val url = createZipUrl(mapOf("agency_jp.txt" to agencyJpCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.agencyJapan.size)
        val agencyJp = gtfsData.agencyJapan[0]
        assertEquals("A1", agencyJp.agencyId?.id)
        assertEquals("Official Agency One", agencyJp.agencyOfficialName)
        assertEquals("123-4567", agencyJp.agencyZipCode)
        assertEquals("Tokyo", agencyJp.agencyAddress)
        assertEquals("President", agencyJp.agencyPresidentPos)
        assertEquals("John Doe", agencyJp.agencyPresidentName)
    }

    @Test
    fun testParseRoutes() = runBlocking {
        val routesCsv = """
            route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color,jp_parent_route_id
            R1,A1,Short,Long Route,Desc,3,http://route.com,FFFFFF,000000,PR1
        """.trimIndent()

        val url = createZipUrl(mapOf("routes.txt" to routesCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.routes.size)
        val route = gtfsData.routes[0]
        assertEquals("R1", route.routeId?.id)
        assertEquals("A1", route.agencyId?.id)
        assertEquals("Short", route.routeShortName)
        assertEquals("Long Route", route.routeLongName)
        assertEquals("Desc", route.routeDesc)
        assertEquals("3", route.routeType)
        assertEquals("http://route.com", route.routeUrl)
        assertEquals("FFFFFF", route.routeColor)
        assertEquals("000000", route.routeTextColor)
        assertEquals("PR1", route.jpParentRouteId?.id)
    }

    @Test
    fun testParseRoutesJapan() = runBlocking {
        val routesJpCsv = """
            route_id,route_update_date,origin_stop,destination_stop,via_stop
            R1,20230101,Stop A,Stop Z,Stop B
        """.trimIndent()

        val url = createZipUrl(mapOf("routes_jp.txt" to routesJpCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.routesJapan.size)
        val routeJp = gtfsData.routesJapan[0]
        assertEquals("R1", routeJp.routeId?.id)
        assertEquals("20230101", routeJp.routeUpdateDate)
        assertEquals("Stop A", routeJp.originStop)
        assertEquals("Stop Z", routeJp.destinationStop)
        assertEquals("Stop B", routeJp.viaStop)
    }

    @Test
    fun testParseFeedInfo() = runBlocking {
        val feedInfoCsv = """
            feed_publisher_name,feed_publisher_url,feed_lang,feed_start_date,feed_end_date,feed_version
            Publisher,http://publisher.com,ja,20230101,20231231,1.0
        """.trimIndent()

        val url = createZipUrl(mapOf("feed_info.txt" to feedInfoCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.feedInfo.size)
        val feed = gtfsData.feedInfo[0]
        assertEquals("Publisher", feed.feedPublisherName)
        assertEquals("http://publisher.com", feed.feedPublisherUrl)
        assertEquals("ja", feed.feedLang)
        assertEquals(LocalDate.of(2023, 1, 1), feed.feedStartDate)
        assertEquals(LocalDate.of(2023, 12, 31), feed.feedEndDate)
        assertEquals("1.0", feed.feedVersion)
    }

    @Test
    fun testParseTrips() = runBlocking {
        val tripsCsv = """
            route_id,service_id,trip_id,trip_headsign,trip_short_name,direction_id,block_id,shape_id,wheelchair_accessible,bikes_allowed,jp_trip_desc,jp_trip_desc_symbol,jp_office_id
            R1,S1,T1,Headsign,Short,0,B1,SH1,1,2,Desc,Sym,O1
        """.trimIndent()

        val url = createZipUrl(mapOf("trips.txt" to tripsCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.trips.size)
        val trip = gtfsData.trips[0]
        assertEquals("R1", trip.routeId?.id)
        assertEquals("S1", trip.serviceId?.id)
        assertEquals("T1", trip.tripId?.id)
        assertEquals("Headsign", trip.tripHeadSign)
        assertEquals("Short", trip.tripShortName)
        assertEquals("0", trip.directionId)
        assertEquals("B1", trip.blockId?.id)
        assertEquals("SH1", trip.shapeId?.id)
        assertEquals("1", trip.wheelchairAccessible)
        assertEquals("2", trip.bikesAllowed)
        assertEquals("Desc", trip.jpTripDesc)
        assertEquals("Sym", trip.jpTripDescSymbol)
        assertEquals("O1", trip.jpOfficeId?.id)
    }

    @Test
    fun testParseOfficeJapan() = runBlocking {
        val officeCsv = """
            office_id,office_name,office_url,office_phone
            O1,Office Name,http://office.com,111-1111
        """.trimIndent()

        val url = createZipUrl(mapOf("office_jp.txt" to officeCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.officeJapan.size)
        val office = gtfsData.officeJapan[0]
        assertEquals("O1", office.officeId?.id)
        assertEquals("Office Name", office.officeName)
        assertEquals("http://office.com", office.officeUrl)
        assertEquals("111-1111", office.officePhone)
    }

    @Test
    fun testParseFrequencies() = runBlocking {
        val freqCsv = """
            trip_id,start_time,end_time,headway_secs,exact_times
            T1,08:00:00,10:00:00,600,1
        """.trimIndent()

        val url = createZipUrl(mapOf("frequencies.txt" to freqCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.frequencies.size)
        val freq = gtfsData.frequencies[0]
        assertEquals("T1", freq.tripId?.id)
        assertEquals("08:00:00", freq.startTime)
        assertEquals("10:00:00", freq.endTime)
        assertEquals("600", freq.headwaySecs)
        assertEquals(true, freq.exactTimes)
    }

    @Test
    fun testParseCalendars() = runBlocking {
        val calCsv = """
            service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
            S1,1,1,1,1,1,0,0,20230101,20231231
        """.trimIndent()

        val url = createZipUrl(mapOf("calendar.txt" to calCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.calenders.size)
        val cal = gtfsData.calenders[0]
        assertEquals("S1", cal.serviceId?.id)
        assertEquals(true, cal.monday)
        assertEquals(false, cal.saturday)
        assertEquals(LocalDate.of(2023, 1, 1), cal.startDate)
        assertEquals(LocalDate.of(2023, 12, 31), cal.endDate)
    }

    @Test
    fun testParseShapes() = runBlocking {
        val shapesCsv = """
            shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence,shape_dist_traveled
            SH1,35.0,135.0,1,0.0
        """.trimIndent()

        val url = createZipUrl(mapOf("shapes.txt" to shapesCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.shapes.size)
        val shape = gtfsData.shapes[0]
        assertEquals("SH1", shape.shapeId?.id)
        assertEquals("35.0", shape.shapePtLat)
        assertEquals("135.0", shape.shapePtLon)
        assertEquals("1", shape.shapePtSequence)
        assertEquals("0.0", shape.shapeDistTraveled)
    }

    @Test
    fun testParseStopTimes() = runBlocking {
        val stopTimesCsv = """
            trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled,timepoint
            T1,08:00:00,08:01:00,ST1,1,Sign,0,0,1.5,1
        """.trimIndent()

        val url = createZipUrl(mapOf("stop_times.txt" to stopTimesCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.stopTimes.size)
        val st = gtfsData.stopTimes[0]
        assertEquals("T1", st.tripId?.id)
        assertEquals("ST1", st.stopId?.id)
        assertEquals("08:00:00", st.arrivalTime)
        assertEquals("08:01:00", st.departureTime)
        assertEquals("1", st.stopSequence)
        assertEquals("Sign", st.stopHeadsign)
        assertEquals("0", st.pickupType)
        assertEquals("0", st.dropOffType)
        assertEquals("1.5", st.shapeDistTraveled)
        assertEquals("1", st.timePoint)
    }

    @Test
    fun testParseStops() = runBlocking {
        val stopsCsv = """
            stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station,stop_timezone,wheelchair_boarding,platform_code
            ST1,Code1,Stop Name,Desc,35.0,135.0,Z1,http://stop.com,0,P1,Asia/Tokyo,1,PL1
        """.trimIndent()

        val url = createZipUrl(mapOf("stops.txt" to stopsCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.stops.size)
        val stop = gtfsData.stops[0]
        assertEquals("ST1", stop.stopId?.id)
        assertEquals("Code1", stop.stopCode)
        assertEquals("Stop Name", stop.stopName)
        assertEquals("Desc", stop.stopDesc)
        assertEquals("35.0", stop.stopLat)
        assertEquals("135.0", stop.stopLon)
        assertEquals("Z1", stop.zoneId?.id)
        assertEquals("http://stop.com", stop.stopUrl)
        assertEquals("0", stop.locationType)
        assertEquals("P1", stop.parentStation?.id)
        assertEquals("Asia/Tokyo", stop.stopTimezone)
        assertEquals("1", stop.wheelchairBoarding)
        assertEquals("PL1", stop.platformCode)
    }

    @Test
    fun testParseCalendarDates() = runBlocking {
        val calDatesCsv = """
            service_id,date,exception_type
            S1,20230101,1
        """.trimIndent()

        val url = createZipUrl(mapOf("calendar_dates.txt" to calDatesCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.calendarDates.size)
        val cd = gtfsData.calendarDates[0]
        assertEquals("S1", cd.serviceId?.id)
        assertEquals(LocalDate.of(2023, 1, 1), cd.date)
        assertEquals("1", cd.exceptionType)
    }

    @Test
    fun testParseTransfers() = runBlocking {
        val transfersCsv = """
            from_stop_id,to_stop_id,transfer_type,min_transfer_time
            ST1,ST2,2,300
        """.trimIndent()

        val url = createZipUrl(mapOf("transfers.txt" to transfersCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.transfers.size)
        val t = gtfsData.transfers[0]
        assertEquals("ST1", t.fromStopId?.id)
        assertEquals("ST2", t.toStopId?.id)
        assertEquals("2", t.transferType)
        assertEquals("300", t.minTransferTime)
    }

    @Test
    fun testParseFareAttributes() = runBlocking {
        val faresCsv = """
            fare_id,price,currency_type,payment_method,transfers,transfer_duration
            F1,200,JPY,0,1,3600
        """.trimIndent()

        val url = createZipUrl(mapOf("fare_attributes.txt" to faresCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.fareAttributes.size)
        val f = gtfsData.fareAttributes[0]
        assertEquals("F1", f.fareId?.id)
        assertEquals("200", f.price)
        assertEquals("JPY", f.currencyType)
        assertEquals("0", f.paymentMethod)
        assertEquals("1", f.transfers)
        assertEquals("3600", f.transferDuration)
    }

    @Test
    fun testEmptyFiles() = runBlocking {
        // Zip with no GTFS txt files, should parse into empty lists safely
        val url = createZipUrl(emptyMap())
        val gtfsData = gtfsReader(url)
        
        assertTrue(gtfsData.agency.isEmpty())
        assertTrue(gtfsData.routes.isEmpty())
        assertTrue(gtfsData.stops.isEmpty())
        assertTrue(gtfsData.trips.isEmpty())
        assertTrue(gtfsData.stopTimes.isEmpty())
    }

    @Test
    fun testMissingOptionalFields() = runBlocking {
        // A minimal stops.txt with only required fields (stop_name, and let's say stop_id is common though sometimes optional)
        val stopsCsv = """
            stop_name,stop_id
            Minimal Stop,ST2
        """.trimIndent()

        val url = createZipUrl(mapOf("stops.txt" to stopsCsv))
        val gtfsData = gtfsReader(url)

        assertEquals(1, gtfsData.stops.size)
        val stop = gtfsData.stops[0]
        assertEquals("ST2", stop.stopId?.id)
        assertEquals("Minimal Stop", stop.stopName)
        assertEquals(null, stop.stopDesc)
        assertEquals(null, stop.stopLat)
    }
}
