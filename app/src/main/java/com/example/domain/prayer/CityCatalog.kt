package com.example.domain.prayer

import com.example.domain.model.PrayerLocation
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object CityCatalog {

  val defaultLocation = PrayerLocation(
    name = "Makkah",
    region = "Saudi Arabia",
    latitude = 21.4225,
    longitude = 39.8262,
    timeZoneId = "Asia/Riyadh",
  )

  val allLocations: List<PrayerLocation> = listOf(
    // Middle East & North Africa
    defaultLocation,
    PrayerLocation("Madinah", "Saudi Arabia", 24.4672, 39.6111, "Asia/Riyadh"),
    PrayerLocation("Riyadh", "Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh"),
    PrayerLocation("Jeddah", "Saudi Arabia", 21.4858, 39.1925, "Asia/Riyadh"),
    PrayerLocation("Dubai", "United Arab Emirates", 25.2048, 55.2708, "Asia/Dubai"),
    PrayerLocation("Abu Dhabi", "United Arab Emirates", 24.4539, 54.3773, "Asia/Dubai"),
    PrayerLocation("Doha", "Qatar", 25.2854, 51.5310, "Asia/Qatar"),
    PrayerLocation("Kuwait City", "Kuwait", 29.3759, 47.9774, "Asia/Kuwait"),
    PrayerLocation("Muscat", "Oman", 23.5880, 58.3829, "Asia/Muscat"),
    PrayerLocation("Manama", "Bahrain", 26.2285, 50.5860, "Asia/Bahrain"),
    PrayerLocation("Amman", "Jordan", 31.9454, 35.9284, "Asia/Amman"),
    PrayerLocation("Jerusalem", "Palestine", 31.7683, 35.2137, "Asia/Jerusalem"),
    PrayerLocation("Baghdad", "Iraq", 33.3152, 44.3661, "Asia/Baghdad"),
    PrayerLocation("Cairo", "Egypt", 30.0444, 31.2357, "Africa/Cairo"),
    PrayerLocation("Alexandria", "Egypt", 31.2001, 29.9187, "Africa/Cairo"),
    PrayerLocation("Casablanca", "Morocco", 33.5731, -7.5898, "Africa/Casablanca"),
    PrayerLocation("Tunis", "Tunisia", 36.8065, 10.1815, "Africa/Tunis"),
    PrayerLocation("Algiers", "Algeria", 36.7538, 3.0588, "Africa/Algiers"),

    // Europe & Turkey
    PrayerLocation("Istanbul", "Turkey", 41.0082, 28.9784, "Europe/Istanbul"),
    PrayerLocation("Ankara", "Turkey", 39.9334, 32.8597, "Europe/Istanbul"),
    PrayerLocation("London", "United Kingdom", 51.5072, -0.1276, "Europe/London"),
    PrayerLocation("Birmingham", "United Kingdom", 52.4862, -1.8904, "Europe/London"),
    PrayerLocation("Manchester", "United Kingdom", 53.4808, -2.2426, "Europe/London"),
    PrayerLocation("Paris", "France", 48.8566, 2.3522, "Europe/Paris"),
    PrayerLocation("Berlin", "Germany", 52.5200, 13.4050, "Europe/Berlin"),
    PrayerLocation("Frankfurt", "Germany", 50.1109, 8.6821, "Europe/Berlin"),
    PrayerLocation("Amsterdam", "Netherlands", 52.3676, 4.9041, "Europe/Amsterdam"),
    PrayerLocation("Brussels", "Belgium", 50.8503, 4.3517, "Europe/Brussels"),
    PrayerLocation("Madrid", "Spain", 40.4168, -3.7038, "Europe/Madrid"),
    PrayerLocation("Rome", "Italy", 41.9028, 12.4964, "Europe/Rome"),
    PrayerLocation("Vienna", "Austria", 48.2082, 16.3738, "Europe/Vienna"),
    PrayerLocation("Stockholm", "Sweden", 59.3293, 18.0686, "Europe/Stockholm"),
    PrayerLocation("Moscow", "Russia", 55.7558, 37.6173, "Europe/Moscow"),

    // North & South America
    PrayerLocation("New York", "United States", 40.7128, -74.0060, "America/New_York"),
    PrayerLocation("Los Angeles", "United States", 34.0522, -118.2437, "America/Los_Angeles"),
    PrayerLocation("Chicago", "United States", 41.8781, -87.6298, "America/Chicago"),
    PrayerLocation("Houston", "United States", 29.7604, -95.3698, "America/Chicago"),
    PrayerLocation("Washington, D.C.", "United States", 38.9072, -77.0369, "America/New_York"),
    PrayerLocation("Toronto", "Canada", 43.6532, -79.3832, "America/Toronto"),
    PrayerLocation("Montreal", "Canada", 45.5017, -73.5673, "America/Toronto"),
    PrayerLocation("Vancouver", "Canada", 49.2827, -123.1207, "America/Vancouver"),
    PrayerLocation("Sao Paulo", "Brazil", -23.5505, -46.6333, "America/Sao_Paulo"),

    // South Asia
    PrayerLocation("Dhaka", "Bangladesh", 23.8103, 90.4125, "Asia/Dhaka"),
    PrayerLocation("Chattogram", "Bangladesh", 22.3569, 91.7832, "Asia/Dhaka"),
    PrayerLocation("Sylhet", "Bangladesh", 24.8949, 91.8687, "Asia/Dhaka"),
    PrayerLocation("Rajshahi", "Bangladesh", 24.3745, 88.6042, "Asia/Dhaka"),
    PrayerLocation("Khulna", "Bangladesh", 22.8456, 89.5403, "Asia/Dhaka"),
    PrayerLocation("Cox's Bazar", "Bangladesh", 21.4272, 92.0058, "Asia/Dhaka"),
    PrayerLocation("Karachi", "Pakistan", 24.8607, 67.0011, "Asia/Karachi"),
    PrayerLocation("Lahore", "Pakistan", 31.5204, 74.3587, "Asia/Karachi"),
    PrayerLocation("Islamabad", "Pakistan", 33.6844, 73.0479, "Asia/Karachi"),
    PrayerLocation("New Delhi", "India", 28.6139, 77.2090, "Asia/Kolkata"),
    PrayerLocation("Mumbai", "India", 19.0760, 72.8777, "Asia/Kolkata"),
    PrayerLocation("Kolkata", "India", 22.5726, 88.3639, "Asia/Kolkata"),
    PrayerLocation("Hyderabad", "India", 17.3850, 78.4867, "Asia/Kolkata"),

    // Southeast & East Asia
    PrayerLocation("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
    PrayerLocation("Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
    PrayerLocation("Surabaya", "Indonesia", -7.2575, 112.7521, "Asia/Jakarta"),
    PrayerLocation("Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
    PrayerLocation("Bandar Seri Begawan", "Brunei", 4.9031, 114.9398, "Asia/Brunei"),
    PrayerLocation("Tokyo", "Japan", 35.6762, 139.6503, "Asia/Tokyo"),
    PrayerLocation("Seoul", "South Korea", 37.5665, 126.9780, "Asia/Seoul"),
    PrayerLocation("Beijing", "China", 39.9042, 116.4074, "Asia/Shanghai"),

    // Sub-Saharan Africa & Oceania
    PrayerLocation("Lagos", "Nigeria", 6.5244, 3.3792, "Africa/Lagos"),
    PrayerLocation("Abuja", "Nigeria", 9.0765, 7.3986, "Africa/Lagos"),
    PrayerLocation("Nairobi", "Kenya", -1.2921, 36.8219, "Africa/Nairobi"),
    PrayerLocation("Johannesburg", "South Africa", -26.2041, 28.0473, "Africa/Johannesburg"),
    PrayerLocation("Cape Town", "South Africa", -33.9249, 18.4241, "Africa/Johannesburg"),
    PrayerLocation("Sydney", "Australia", -33.8688, 151.2093, "Australia/Sydney"),
    PrayerLocation("Melbourne", "Australia", -37.8136, 144.9631, "Australia/Melbourne"),
    PrayerLocation("Auckland", "New Zealand", -36.8509, 174.7645, "Pacific/Auckland"),
  )

  fun defaultForTimeZone(timeZoneId: String): PrayerLocation {
    return allLocations.firstOrNull { it.timeZoneId.equals(timeZoneId, ignoreCase = true) }
      ?: defaultLocation
  }

  fun findNearestCity(latitude: Double, longitude: Double): PrayerLocation {
    var best = defaultLocation
    var bestDist = Double.MAX_VALUE
    for (city in allLocations) {
      val d = haversineKm(latitude, longitude, city.latitude, city.longitude)
      if (d < bestDist) {
        bestDist = d
        best = city
      }
    }
    return if (bestDist <= 65.0) {
      best.copy(
        latitude = latitude,
        longitude = longitude,
        timeZoneId = java.util.TimeZone.getDefault().id,
      )
    } else {
      PrayerLocation(
        name = "Current Location",
        region = "GPS Detected",
        latitude = latitude,
        longitude = longitude,
        timeZoneId = java.util.TimeZone.getDefault().id,
      )
    }
  }

  private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
      sin(dLon / 2) * sin(dLon / 2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
  }
}
