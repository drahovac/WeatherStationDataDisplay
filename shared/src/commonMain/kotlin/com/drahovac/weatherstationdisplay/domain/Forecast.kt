package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val calendarDayTemperatureMax: List<Int?>,
    val calendarDayTemperatureMin: List<Int>,
    val dayOfWeek: List<String>,
    val expirationTimeUtc: List<Long>,
    val moonPhase: List<String>,
    val moonPhaseCode: List<String>,
    val moonPhaseDay: List<Int>,
    val moonriseTimeLocal: List<String>,
    val moonriseTimeUtc: List<Long?>,
    val moonsetTimeLocal: List<String?>,
    val moonsetTimeUtc: List<Long?>,
    val narrative: List<String>,
    val qpf: List<Double>,
    val qpfSnow: List<Double>,
    val sunriseTimeLocal: List<String?>,
    val sunriseTimeUtc: List<Long?>,
    val sunsetTimeLocal: List<String?>,
    val sunsetTimeUtc: List<Long?>,
    val temperatureMax: List<Double?>,
    val temperatureMin: List<Double>,
    val validTimeLocal: List<String>,
    val validTimeUtc: List<Long>,
    val daypart: List<Daypart>
) {

    val dateTimesUtc: List<Instant> = validTimeUtc.map {
        Instant.fromEpochSeconds(it)
    }

    val sunsetsUtc: List<Instant?> = sunsetTimeUtc.map {
        it?.let { Instant.fromEpochSeconds(it) }
    }

    val sunrisesUtc: List<Instant?> = sunriseTimeUtc.map {
        it?.let { Instant.fromEpochSeconds(it) }
    }
}

@Serializable
data class Daypart(
    val cloudCover: List<Int?>,
    val dayOrNight: List<String?>,
    val daypartName: List<String?>,
    val iconCode: List<Int?>,
    val iconCodeExtend: List<Int?>,
    val narrative: List<String?>,
    val precipChance: List<Int?>,
    val precipType: List<String?>,
    val qpf: List<Double?>,
    val qpfSnow: List<Double?>,
    val qualifierCode: List<String?>,
    val qualifierPhrase: List<String?>,
    val relativeHumidity: List<Int?>,
    val snowRange: List<String?>,
    val temperature: List<Double?>,
    val temperatureHeatIndex: List<Double?>,
    val temperatureWindChill: List<Double?>,
    val thunderCategory: List<String?>,
    val thunderIndex: List<Int?>,
    val uvDescription: List<String?>,
    val uvIndex: List<Int?>,
    val windDirection: List<Int?>,
    val windDirectionCardinal: List<String?>,
    val windPhrase: List<String?>,
    val windSpeed: List<Int?>,
    val wxPhraseLong: List<String?>,
    val wxPhraseShort: List<String?>,
)

val forecastPrototype = Forecast(
    calendarDayTemperatureMax = listOf(25, 27, 28, 29, 28),
    calendarDayTemperatureMin = listOf(16, 18, 19, 20, 19),
    dayOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
    expirationTimeUtc = listOf(1653084800, 1653171200, 1653257600, 1653344000, 1653430400),
    moonPhase = listOf(
        "Waxing Gibbous",
        "Waxing Gibbous",
        "Waxing Gibbous",
        "Waxing Gibbous",
        "Waxing Gibbous"
    ),
    moonPhaseCode = listOf("WXC", "WXC", "WXC", "WXC", "WXC"),
    moonPhaseDay = listOf(4, 5, 6, 7, 8),
    moonriseTimeLocal = listOf("20:34", "21:20", "22:06", "22:52", "23:38"),
    moonriseTimeUtc = listOf(1653092240, 1653178640, 1653265040, 1653351440, 1653437840),
    moonsetTimeLocal = listOf("04:12", "05:00", "05:48", "06:34", "07:20"),
    moonsetTimeUtc = listOf(1653087520, 1653174020, 1653260420, 1653346820, 1653433220),
    narrative = listOf(
        "Partly cloudy with a stray thunderstorm. Low around 16C. Winds WNW at 10 to 15 km/h. Chance of rain 60%.",
        "A shower or two possible early with partly cloudy skies in the afternoon. High 27C. Winds NW at 10 to 15 km/h. Chance of rain 30%.",
        "More sun than clouds. High 28C. Winds W at 10 to 15 km/h.",
        "Generally sunny despite a few afternoon clouds. High 29C. Winds NNW at 10 to 15 km/h.",
        "Scattered thunderstorms during the evening, then partly cloudy overnight. Low 19C. Winds WNW at 10 to 15 km/h. Chance of rain 50%."
    ),
    qpf = listOf(4.02, 0.14, 0.0, 0.0, 0.4),
    qpfSnow = listOf(0.0, 0.0, 0.0, 0.0, 0.0),
    sunriseTimeLocal = listOf("05:59:40", "06:01:08", "06:02:37", "06:04:05", "06:05:34"),
    sunriseTimeUtc = listOf(1653003980, 1653090468, 1653176957, 1653263445, 1653349934),
    sunsetTimeLocal = listOf("20:13:17", "20:11:19", "20:09:20", "20:07:20", "20:05:20"),
    sunsetTimeUtc = listOf(1653055197, 1653141579, 1653227960, 1653314340, 1653390720),
    temperatureMax = listOf(25.6, 27.8, 28.9, 29.2, 28.4),
    temperatureMin = listOf(16.8, 18.2, 19.4, 20.0, 19.6),
    validTimeLocal = listOf(
        "2023-08-21T07:00:00+0200",
        "2023-08-22T07:00:00+0200",
        "2023-08-23T07:00:00+0200",
        "2023-08-24T07:00:00+0200",
        "2023-08-25T07:00:00+0200"
    ),
    validTimeUtc = listOf(1653092800, 1653179200, 1653265600, 1653352000, 1653438400),
    daypart = listOf(
        Daypart(
            cloudCover = listOf(29),
            dayOrNight = listOf("N"),
            daypartName = listOf("Tonight"),
            iconCode = listOf(47),
            iconCodeExtend = listOf(3709),
            narrative = listOf(
                "Isolated thunderstorms during the evening. Mainly clear skies after midnight. Low around 16C. Winds WNW at 10 to 15 km/h. Chance of rain 60%."
            ),
            precipChance = listOf(60),
            precipType = listOf("Rain"),
            qpf = listOf(4.02),
            qpfSnow = listOf(0.0),
            qualifierCode = listOf(""),
            qualifierPhrase = listOf(""),
            relativeHumidity = listOf(80),
            snowRange = listOf(""),
            temperature = listOf(16.0),
            temperatureHeatIndex = listOf(18.0),
            temperatureWindChill = listOf(14.0),
            thunderCategory = listOf(""),
            thunderIndex = listOf(0),
            uvDescription = listOf("high","low","high","low","low","low","low","low","low","low",),
            uvIndex = listOf(0, 1, 0,5,0,6,0,7,0,8),
            windDirection = listOf(270),
            windDirectionCardinal = listOf("WNW"),
            windPhrase = listOf("Windy"),
            windSpeed = listOf(15),
            wxPhraseLong = listOf(""),
            wxPhraseShort = listOf("")
        ),
        Daypart(
            cloudCover = listOf(27),
            dayOrNight = listOf("D"),
            daypartName = listOf("Tomorrow"),
            iconCode = listOf(39),
            iconCodeExtend = listOf(6103),
            narrative = listOf(
                "A shower or two possible early with partly cloudy skies in the afternoon. High 27C. Winds NW at 10 to 15 km/h. Chance of rain 30%."
            ),
            precipChance = listOf(30),
            precipType = listOf("Rain"),
            qpf = listOf(0.14),
            qpfSnow = listOf(0.0),
            qualifierCode = listOf(""),
            qualifierPhrase = listOf(""),
            relativeHumidity = listOf(65),
            snowRange = listOf(""),
            temperature = listOf(27.0),
            temperatureHeatIndex = listOf(30.0),
            temperatureWindChill = listOf(25.0),
            thunderCategory = listOf(""),
            thunderIndex = listOf(0),
            uvDescription = listOf(""),
            uvIndex = listOf(10),
            windDirection = listOf(320),
            windDirectionCardinal = listOf("NW"),
            windPhrase = listOf("Breezy"),
            windSpeed = listOf(15),
            wxPhraseLong = listOf(""),
            wxPhraseShort = listOf("")
        ),
    )
)
