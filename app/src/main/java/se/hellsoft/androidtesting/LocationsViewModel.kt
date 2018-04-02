package se.hellsoft.androidtesting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Room
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.RequiresPermission

class LocationHistoryViewModel(application: Application) : AndroidViewModel(application) {
  private val database = Room
      .databaseBuilder(application, LocationsDatabase::class.java, "location_database")
      .fallbackToDestructiveMigration()
      .build()
  private val repository: LocationsRepository
  private val worker: Handler

  init {
    val locationsManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    repository = LocationsRepository(locationsManager, database.locationHistoryDao())
    val workerThread = HandlerThread("locations-worker")
    workerThread.start()
    worker = Handler(workerThread.looper)
  }

  override fun onCleared() {
    super.onCleared()
    worker.looper.quit()
  }

  val allLocations: LiveData<List<LoggedLocation>> get() = repository.locations

  @SuppressLint("MissingPermission")
  @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
  fun logLocation() {
    worker.post { repository.logLocation() }
  }
}

