package se.hellsoft.androidtesting

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.RequiresPermission

class LocationsRepository(private val locationManager: LocationManager, private val locationsDao: LocationHistoryDao) {
  val locations = locationsDao.loadLocations()

  @SuppressLint("MissingPermission")
  @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
  fun logLocation() {
    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener {
      override fun onLocationChanged(location: Location?) {
        val now = System.currentTimeMillis()
        val loggedLocation = LoggedLocation(0,
            location?.longitude ?: 0.0,
            location?.latitude ?: 0.0,
            now)
        locationsDao.saveLocation(loggedLocation)
      }

      override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

      override fun onProviderEnabled(provider: String?) {}

      override fun onProviderDisabled(provider: String?) {}
    }, null)
  }
}