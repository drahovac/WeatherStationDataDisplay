package com.drahovac.weatherstationdisplay

import com.drahovac.weatherstationdisplay.R
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.resources.AssetResource
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.FontResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.ResourceContainer
import dev.icerock.moko.resources.StringResource

// TODO remove shit when they fix moko...
public actual object MR {
  public actual object strings : ResourceContainer<StringResource> {
    public actual val app_name: StringResource = StringResource(R.string.app_name)

    public actual val setup_welcome: StringResource = StringResource(R.string.setup_welcome)

    public actual val setup_enter_device_id: StringResource =
        StringResource(R.string.setup_enter_device_id)

    public actual val setup_id_input_label: StringResource =
        StringResource(R.string.setup_id_input_label)

    public actual val setup_id_guid: StringResource = StringResource(R.string.setup_id_guid)

    public actual val setup_open_browser: StringResource =
        StringResource(R.string.setup_open_browser)

    public actual val setup_continue: StringResource = StringResource(R.string.setup_continue)

    public actual val setup_must_not_be_empty: StringResource =
        StringResource(R.string.setup_must_not_be_empty)

    public actual val setup_finalize: StringResource = StringResource(R.string.setup_finalize)

    public actual val setup_enter_api_key: StringResource =
        StringResource(R.string.setup_enter_api_key)

    public actual val setup_api_key: StringResource = StringResource(R.string.setup_api_key)

    public actual val setup_api_key_guide: StringResource =
        StringResource(R.string.setup_api_key_guide)

    public actual val current_temperature: StringResource =
        StringResource(R.string.current_temperature)

    public actual val current_degree_celsius: StringResource =
        StringResource(R.string.current_degree_celsius)

    public actual val current_feels_like: StringResource =
        StringResource(R.string.current_feels_like)

    public actual val current_dew_point: StringResource = StringResource(R.string.current_dew_point)

    public actual val current_humidity: StringResource = StringResource(R.string.current_humidity)

    public actual val current_uv: StringResource = StringResource(R.string.current_uv)

    public actual val current_north: StringResource = StringResource(R.string.current_north)

    public actual val current_west: StringResource = StringResource(R.string.current_west)

    public actual val current_south: StringResource = StringResource(R.string.current_south)

    public actual val current_east: StringResource = StringResource(R.string.current_east)

    public actual val current_km_h: StringResource = StringResource(R.string.current_km_h)

    public actual val current_gust: StringResource = StringResource(R.string.current_gust)

    public actual val current_wind_speed: StringResource =
        StringResource(R.string.current_wind_speed)

    public actual val current_solar_radiation: StringResource =
        StringResource(R.string.current_solar_radiation)

    public actual val current_radiation_units: StringResource =
        StringResource(R.string.current_radiation_units)

    public actual val current_pressure: StringResource = StringResource(R.string.current_pressure)

    public actual val current_hpa: StringResource = StringResource(R.string.current_hpa)

    public actual val current_prec_rate: StringResource = StringResource(R.string.current_prec_rate)

    public actual val current_prec_total: StringResource =
        StringResource(R.string.current_prec_total)

    public actual val current_mm: StringResource = StringResource(R.string.current_mm)

    public actual val current_mm_hr: StringResource = StringResource(R.string.current_mm_hr)

    public actual val current_error_title: StringResource =
        StringResource(R.string.current_error_title)

    public actual val current_error_content: StringResource =
        StringResource(R.string.current_error_content)

    public actual val current_error_new_api_key: StringResource =
        StringResource(R.string.current_error_new_api_key)

    public actual val current_error_new_api_key_message: StringResource =
        StringResource(R.string.current_error_new_api_key_message)

    public actual val current_error_new_station_id: StringResource =
        StringResource(R.string.current_error_new_station_id)

    public actual val current_error_station_id_message: StringResource =
        StringResource(R.string.current_error_station_id_message)

    public actual val current_error_quota: StringResource =
        StringResource(R.string.current_error_quota)

    public actual val current_nav: StringResource = StringResource(R.string.current_nav)

    public actual val history_nav: StringResource = StringResource(R.string.history_nav)

    public actual val history_no_data: StringResource = StringResource(R.string.history_no_data)

    public actual val history_no_data_message: StringResource =
        StringResource(R.string.history_no_data_message)

    public actual val history_start_date: StringResource =
        StringResource(R.string.history_start_date)

    public actual val history_start_download: StringResource =
        StringResource(R.string.history_start_download)

    public actual val history_start_select: StringResource =
        StringResource(R.string.history_start_select)

    public actual val history_tab_yesterday: StringResource =
        StringResource(R.string.history_tab_yesterday)

    public actual val history_tab_week: StringResource = StringResource(R.string.history_tab_week)

    public actual val history_tab_month: StringResource = StringResource(R.string.history_tab_month)

    public actual val history_temperature: StringResource =
        StringResource(R.string.history_temperature)

    public actual val history_max_temperature: StringResource =
        StringResource(R.string.history_max_temperature)

    public actual val history_min_temperature: StringResource =
        StringResource(R.string.history_min_temperature)

    public actual val history_avg_temperature: StringResource =
        StringResource(R.string.history_avg_temperature)

    public actual val history_min_on: StringResource = StringResource(R.string.history_min_on)

    public actual val history_max_solar_radiation: StringResource =
        StringResource(R.string.history_max_solar_radiation)

    public actual val history_high_uv_index: StringResource =
        StringResource(R.string.history_high_uv_index)

    public actual val history_high_presc_total: StringResource =
        StringResource(R.string.history_high_presc_total)

    public actual val history_high_wind: StringResource = StringResource(R.string.history_high_wind)

    public actual val history_max_pressure: StringResource =
        StringResource(R.string.history_max_pressure)

    public actual val history_min_pressure: StringResource =
        StringResource(R.string.history_min_pressure)

    public actual val history_trend: StringResource = StringResource(R.string.history_trend)

    public actual val history_next_month: StringResource =
        StringResource(R.string.history_next_month)

    public actual val history_previous_month: StringResource =
        StringResource(R.string.history_previous_month)

    public actual val history_humidity: StringResource = StringResource(R.string.history_humidity)

    public actual val history_humidity_min: StringResource =
        StringResource(R.string.history_humidity_min)

    public actual val history_humidity_max: StringResource =
        StringResource(R.string.history_humidity_max)

    public actual val history_humidity_avg: StringResource =
        StringResource(R.string.history_humidity_avg)

    public actual val forecast: StringResource = StringResource(R.string.forecast)

    public actual val weather_tornado: StringResource = StringResource(R.string.weather_tornado)

    public actual val weather_tropical_storm: StringResource =
        StringResource(R.string.weather_tropical_storm)

    public actual val weather_hurricane: StringResource = StringResource(R.string.weather_hurricane)

    public actual val weather_strong_storms: StringResource =
        StringResource(R.string.weather_strong_storms)

    public actual val weather_thunderstorms: StringResource =
        StringResource(R.string.weather_thunderstorms)

    public actual val weather_rain_snow: StringResource = StringResource(R.string.weather_rain_snow)

    public actual val weather_rain_sleet: StringResource =
        StringResource(R.string.weather_rain_sleet)

    public actual val weather_wintry_mix: StringResource =
        StringResource(R.string.weather_wintry_mix)

    public actual val weather_freezing_drizzle: StringResource =
        StringResource(R.string.weather_freezing_drizzle)

    public actual val weather_drizzle: StringResource = StringResource(R.string.weather_drizzle)

    public actual val weather_freezing_rain: StringResource =
        StringResource(R.string.weather_freezing_rain)

    public actual val weather_showers: StringResource = StringResource(R.string.weather_showers)

    public actual val weather_rain: StringResource = StringResource(R.string.weather_rain)

    public actual val weather_flurries: StringResource = StringResource(R.string.weather_flurries)

    public actual val weather_snow_showers: StringResource =
        StringResource(R.string.weather_snow_showers)

    public actual val weather_blowing_drifting_snow: StringResource =
        StringResource(R.string.weather_blowing_drifting_snow)

    public actual val weather_snow: StringResource = StringResource(R.string.weather_snow)

    public actual val weather_hail: StringResource = StringResource(R.string.weather_hail)

    public actual val weather_sleet: StringResource = StringResource(R.string.weather_sleet)

    public actual val weather_blowing_dust_sandstorm: StringResource =
        StringResource(R.string.weather_blowing_dust_sandstorm)

    public actual val weather_foggy: StringResource = StringResource(R.string.weather_foggy)

    public actual val weather_haze: StringResource = StringResource(R.string.weather_haze)

    public actual val weather_smoke: StringResource = StringResource(R.string.weather_smoke)

    public actual val weather_breezy: StringResource = StringResource(R.string.weather_breezy)

    public actual val weather_windy: StringResource = StringResource(R.string.weather_windy)

    public actual val weather_frigid_ice_crystals: StringResource =
        StringResource(R.string.weather_frigid_ice_crystals)

    public actual val weather_cloudy: StringResource = StringResource(R.string.weather_cloudy)

    public actual val weather_mostly_cloudy: StringResource =
        StringResource(R.string.weather_mostly_cloudy)

    public actual val weather_partly_cloudy: StringResource =
        StringResource(R.string.weather_partly_cloudy)

    public actual val weather_clear: StringResource = StringResource(R.string.weather_clear)

    public actual val weather_sunny: StringResource = StringResource(R.string.weather_sunny)

    public actual val weather_fair_mostly_clear: StringResource =
        StringResource(R.string.weather_fair_mostly_clear)

    public actual val weather_fair_mostly_sunny: StringResource =
        StringResource(R.string.weather_fair_mostly_sunny)

    public actual val weather_mixed_rain_hail: StringResource =
        StringResource(R.string.weather_mixed_rain_hail)

    public actual val weather_hot: StringResource = StringResource(R.string.weather_hot)

    public actual val weather_isolated_thunderstorms: StringResource =
        StringResource(R.string.weather_isolated_thunderstorms)

    public actual val weather_scattered_thunderstorms: StringResource =
        StringResource(R.string.weather_scattered_thunderstorms)

    public actual val weather_scattered_showers: StringResource =
        StringResource(R.string.weather_scattered_showers)

    public actual val weather_heavy_rain: StringResource =
        StringResource(R.string.weather_heavy_rain)

    public actual val weather_scattered_snow_showers: StringResource =
        StringResource(R.string.weather_scattered_snow_showers)

    public actual val weather_heavy_snow: StringResource =
        StringResource(R.string.weather_heavy_snow)

    public actual val weather_blizzard: StringResource = StringResource(R.string.weather_blizzard)

    public actual val weather_not_available: StringResource =
        StringResource(R.string.weather_not_available)

    public actual val weather_rain_outlook: StringResource =
        StringResource(R.string.weather_rain_outlook)

    public actual val weather_snowfall_outlook: StringResource =
        StringResource(R.string.weather_snowfall_outlook)

    public actual val weather_snowfall_cm: StringResource =
        StringResource(R.string.weather_snowfall_cm)

    public actual val weather_sunset: StringResource = StringResource(R.string.weather_sunset)

    public actual val weather_sunrise: StringResource = StringResource(R.string.weather_sunrise)

    public actual val weather_moonphase: StringResource = StringResource(R.string.weather_moonphase)

    public actual val weather_24_forecast: StringResource =
        StringResource(R.string.weather_24_forecast)

    public actual val weather_relative_humidity: StringResource =
        StringResource(R.string.weather_relative_humidity)

    public actual val weather_precip: StringResource = StringResource(R.string.weather_precip)
  }

  public actual object plurals : ResourceContainer<PluralsResource>

  public actual object images : ResourceContainer<ImageResource>

  public actual object fonts : ResourceContainer<FontResource>

  public actual object files : ResourceContainer<FileResource>

  public actual object colors : ResourceContainer<ColorResource>

  public actual object assets : ResourceContainer<AssetResource>
}
