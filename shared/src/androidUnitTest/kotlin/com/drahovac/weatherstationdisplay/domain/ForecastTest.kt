package com.drahovac.weatherstationdisplay.domain

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class ForecastTest {

    @Test
    fun `map json to domain`() {
        val forecast = Json.decodeFromString<Forecast>(JSON)

        assertEquals("Sunday", forecast.dayOfWeek.first())
    }

    private companion object {
        val JSON = """{
    "calendarDayTemperatureMax": [
        33,
        32,
        32,
        30,
        30,
        28
    ],
    "calendarDayTemperatureMin": [
        21,
        20,
        18,
        18,
        18,
        19
    ],
    "dayOfWeek": [
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday"
    ],
    "expirationTimeUtc": [
        1692542013,
        1692542013,
        1692542013,
        1692542013,
        1692542013,
        1692542013
    ],
    "moonPhase": [
        "Waxing Crescent",
        "Waxing Crescent",
        "Waxing Crescent",
        "Waxing Crescent",
        "First Quarter",
        "Waxing Gibbous"
    ],
    "moonPhaseCode": [
        "WXC",
        "WXC",
        "WXC",
        "WXC",
        "FQ",
        "WXG"
    ],
    "moonPhaseDay": [
        3,
        4,
        5,
        6,
        7,
        8
    ],
    "moonriseTimeLocal": [
        "2023-08-20T10:02:47+0200",
        "2023-08-21T11:13:04+0200",
        "2023-08-22T12:25:29+0200",
        "2023-08-23T13:42:15+0200",
        "2023-08-24T15:01:16+0200",
        "2023-08-25T16:19:56+0200"
    ],
    "moonriseTimeUtc": [
        1692518567,
        1692609184,
        1692699929,
        1692790935,
        1692882076,
        1692973196
    ],
    "moonsetTimeLocal": [
        "2023-08-20T21:41:27+0200",
        "2023-08-21T21:53:58+0200",
        "2023-08-22T22:08:45+0200",
        "2023-08-23T22:27:58+0200",
        "2023-08-24T22:54:48+0200",
        "2023-08-25T23:33:29+0200"
    ],
    "moonsetTimeUtc": [
        1692560487,
        1692647638,
        1692734925,
        1692822478,
        1692910488,
        1692999209
    ],
    "narrative": [
        "Partly cloudy with a stray thunderstorm. Low 20C.",
        "A few morning showers. Highs 31 to 33C and lows 17 to 19C.",
        "More sun than clouds. Highs 31 to 33C and lows 17 to 19C.",
        "More sun than clouds. Highs 29 to 31C and lows 17 to 19C.",
        "Showers and thunderstorms late. Highs 29 to 31C and lows 18 to 20C.",
        "Thunderstorms. Highs 27 to 29C and lows 16 to 18C."
    ],
    "qpf": [
        4.02,
        0.14,
        0.4,
        0.0,
        2.39,
        6.5
    ],
    "qpfSnow": [
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0
    ],
    "sunriseTimeLocal": [
        "2023-08-20T05:59:40+0200",
        "2023-08-21T06:01:08+0200",
        "2023-08-22T06:02:37+0200",
        "2023-08-23T06:04:05+0200",
        "2023-08-24T06:05:34+0200",
        "2023-08-25T06:07:03+0200"
    ],
    "sunriseTimeUtc": [
        1692503980,
        1692590468,
        1692676957,
        1692763445,
        1692849934,
        1692936423
    ],
    "sunsetTimeLocal": [
        "2023-08-20T20:13:17+0200",
        "2023-08-21T20:11:19+0200",
        "2023-08-22T20:09:20+0200",
        "2023-08-23T20:07:20+0200",
        "2023-08-24T20:05:20+0200",
        "2023-08-25T20:03:19+0200"
    ],
    "sunsetTimeUtc": [
        1692555197,
        1692641479,
        1692727760,
        1692814040,
        1692900320,
        1692986599
    ],
    "temperatureMax": [
        null,
        32,
        32,
        30,
        30,
        28
    ],
    "temperatureMin": [
        20,
        18,
        18,
        18,
        19,
        17
    ],
    "validTimeLocal": [
        "2023-08-20T07:00:00+0200",
        "2023-08-21T07:00:00+0200",
        "2023-08-22T07:00:00+0200",
        "2023-08-23T07:00:00+0200",
        "2023-08-24T07:00:00+0200",
        "2023-08-25T07:00:00+0200"
    ],
    "validTimeUtc": [
        1692507600,
        1692594000,
        1692680400,
        1692766800,
        1692853200,
        1692939600
    ],
    "daypart": [
        {
            "cloudCover": [
                null,
                29,
                32,
                20,
                32,
                33,
                28,
                12,
                45,
                68,
                77,
                79
            ],
            "dayOrNight": [
                null,
                "N",
                "D",
                "N",
                "D",
                "N",
                "D",
                "N",
                "D",
                "N",
                "D",
                "N"
            ],
            "daypartName": [
                null,
                "Tonight",
                "Tomorrow",
                "Tomorrow night",
                "Tuesday",
                "Tuesday night",
                "Wednesday",
                "Wednesday night",
                "Thursday",
                "Thursday night",
                "Friday",
                "Friday night"
            ],
            "iconCode": [
                null,
                47,
                39,
                29,
                34,
                47,
                34,
                31,
                38,
                4,
                4,
                12
            ],
            "iconCodeExtend": [
                null,
                3709,
                6103,
                2900,
                3400,
                6200,
                3400,
                3100,
                7203,
                400,
                400,
                1240
            ],
            "narrative": [
                null,
                "Isolated thunderstorms during the evening. Mainly clear skies after midnight. Low around 20C. Winds WNW at 10 to 15 km/h. Chance of rain 60%.",
                "A shower or two possible early with partly cloudy skies in the afternoon. High 32C. Winds NW at 10 to 15 km/h. Chance of rain 30%.",
                "Partly cloudy skies. Low 18C. Winds NNW at 10 to 15 km/h.",
                "Generally sunny despite a few afternoon clouds. High 32C. Winds W at 10 to 15 km/h.",
                "Scattered thunderstorms during the evening, then partly cloudy overnight. Low 18C. Winds NW at 10 to 15 km/h. Chance of rain 50%.",
                "Generally sunny despite a few afternoon clouds. High near 30C. Winds NNW at 10 to 15 km/h.",
                "Clear. Low 18C. Winds NW at 10 to 15 km/h.",
                "Partly cloudy in the morning followed by scattered thunderstorms in the afternoon. High around 30C. Winds SSW at 10 to 15 km/h. Chance of rain 40%.",
                "Thunderstorms likely. Low 19C. Winds SSW at 10 to 15 km/h. Chance of rain 70%.",
                "Thunderstorms likely. High 28C. Winds WSW at 15 to 25 km/h. Chance of rain 80%.",
                "Showers and thundershowers during the evening giving way to periods of light rain overnight. Low 17C. Winds WSW at 10 to 15 km/h. Chance of rain 80%."
            ],
            "precipChance": [
                null,
                61,
                33,
                12,
                19,
                47,
                14,
                14,
                40,
                71,
                78,
                76
            ],
            "precipType": [
                null,
                "rain",
                "rain",
                "rain",
                "rain",
                "rain",
                "rain",
                "rain",
                "rain",
                "rain",
                "rain",
                "rain"
            ],
            "qpf": [
                null,
                4.02,
                0.14,
                0.0,
                0.0,
                0.4,
                0.0,
                0.0,
                0.4,
                1.99,
                3.9,
                2.6
            ],
            "qpfSnow": [
                null,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0
            ],
            "qualifierCode": [
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            ],
            "qualifierPhrase": [
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            ],
            "relativeHumidity": [
                null,
                76,
                61,
                73,
                57,
                74,
                55,
                68,
                56,
                76,
                69,
                84
            ],
            "snowRange": [
                null,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
            ],
            "temperature": [
                null,
                20,
                32,
                18,
                32,
                18,
                30,
                18,
                30,
                19,
                28,
                17
            ],
            "temperatureHeatIndex": [
                null,
                34,
                33,
                30,
                33,
                30,
                30,
                28,
                30,
                28,
                28,
                24
            ],
            "temperatureWindChill": [
                null,
                20,
                21,
                18,
                20,
                18,
                19,
                18,
                19,
                19,
                20,
                17
            ],
            "thunderCategory": [
                null,
                "Thunder expected",
                "No thunder",
                "No thunder",
                "No thunder",
                "Thunder expected",
                "No thunder",
                "No thunder",
                "Thunder expected",
                "Thunder expected",
                "Thunder expected",
                "Thunder possible"
            ],
            "thunderIndex": [
                null,
                2,
                0,
                0,
                0,
                2,
                0,
                0,
                2,
                2,
                2,
                1
            ],
            "uvDescription": [
                null,
                "Low",
                "High",
                "Low",
                "High",
                "Low",
                "High",
                "Low",
                "Moderate",
                "Low",
                "Moderate",
                "Low"
            ],
            "uvIndex": [
                null,
                0,
                6,
                0,
                6,
                0,
                6,
                0,
                5,
                0,
                4,
                0
            ],
            "windDirection": [
                null,
                301,
                314,
                343,
                266,
                321,
                342,
                307,
                196,
                207,
                237,
                240
            ],
            "windDirectionCardinal": [
                null,
                "WNW",
                "NW",
                "NNW",
                "W",
                "NW",
                "NNW",
                "NW",
                "SSW",
                "SSW",
                "WSW",
                "WSW"
            ],
            "windPhrase": [
                null,
                "Winds WNW at 10 to 15 km/h.",
                "Winds NW at 10 to 15 km/h.",
                "Winds NNW at 10 to 15 km/h.",
                "Winds W at 10 to 15 km/h.",
                "Winds NW at 10 to 15 km/h.",
                "Winds NNW at 10 to 15 km/h.",
                "Winds NW at 10 to 15 km/h.",
                "Winds SSW at 10 to 15 km/h.",
                "Winds SSW at 10 to 15 km/h.",
                "Winds WSW at 15 to 25 km/h.",
                "Winds WSW at 10 to 15 km/h."
            ],
            "windSpeed": [
                null,
                13,
                15,
                15,
                17,
                15,
                13,
                14,
                14,
                15,
                20,
                17
            ],
            "wxPhraseLong": [
                null,
                "Isolated Thunderstorms",
                "AM Showers",
                "Partly Cloudy",
                "Mostly Sunny",
                "Thunderstorms Early",
                "Mostly Sunny",
                "Clear",
                "PM Thunderstorms",
                "Thunderstorms",
                "Thunderstorms",
                "Rain/Thunder"
            ],
            "wxPhraseShort": [
                null,
                "Iso T-Storms",
                "AM Showers",
                "P Cloudy",
                "M Sunny",
                "T-Storms",
                "M Sunny",
                "Clear",
                "PM T-Storms",
                "T-Storms",
                "T-Storms",
                "Rain/Thunder"
            ]
        }
    ]
}"""
    }
}