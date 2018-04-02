package se.hellsoft.androidtesting

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Entity(tableName = "logged_location")
data class LoggedLocation(@PrimaryKey(autoGenerate = true) val id: Int,
                          val longitude: Double,
                          val latitude: Double,
                          val timestamp: Long)

@Dao
interface LocationHistoryDao {
  @Query("SELECT * FROM logged_location ORDER BY timestamp DESC")
  fun loadLocations(): LiveData<List<LoggedLocation>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveLocation(loggedLocation: LoggedLocation)
}

@Database(entities = [LoggedLocation::class], version = 1, exportSchema = false)
abstract class LocationsDatabase : RoomDatabase() {
  abstract fun locationHistoryDao(): LocationHistoryDao
}
