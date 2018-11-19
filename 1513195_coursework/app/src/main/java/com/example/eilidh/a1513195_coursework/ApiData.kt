package com.example.eilidh.a1513195_coursework

class ApiData {

    // all data is metric/celcius

    data class CoreData(
            var latitude: Float?,
            var longitude: Float?,
            var timezone: String?,
            var hourly: Hourly
    )

    data class Hourly(
            var summary: String?,
            var icon: String?,
            var data: Array<HourData>
    )

    data class HourData(
            var time: Long?,
            var precipProbability: Float?,
            var precipType: String?,
            var temperature: Float?,
            var summary: String?, // e.g. "Partly Cloudy"
            var icon: String?,
            var apparantTemperature: Float?, // 'feels like...'
            var humidity: Float?,
            var pressure: Float?,
            var windspeed: Float?,
            var windGust: Float?,
            var cloudCover: Float?,
            var uvIndex: Long?,
            var visibility: Float?
    )
}