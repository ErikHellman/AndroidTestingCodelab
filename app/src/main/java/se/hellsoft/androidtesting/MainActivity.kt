package se.hellsoft.androidtesting

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val viewModel = ViewModelProviders.of(this)[LocationHistoryViewModel::class.java]
    val locationHistoryAdapter = LocationHistoryAdapter()
    viewModel.allLocations.observe(this, Observer {
      locationHistoryAdapter.locations = it ?: emptyList()
      locationHistoryAdapter.notifyDataSetChanged()
    })
    locationHistory.adapter = locationHistoryAdapter

    buttonLogLocation.setOnClickListener {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        viewModel.logLocation()
      } else {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10)
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == 10) {
      val message = if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        R.string.locationGranted
      } else {
        R.string.locationDenied
      }
      Snackbar.make(buttonLogLocation.rootView, message, Snackbar.LENGTH_SHORT).show()
    }
  }

  class LocationHistoryAdapter() : RecyclerView.Adapter<LocationItemViewHolder>() {
    var locations: List<LoggedLocation> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationItemViewHolder {
      val itemView = LayoutInflater.from(parent.context)
          .inflate(R.layout.item_location, parent, false)
      return LocationItemViewHolder(itemView)
    }

    override fun getItemCount(): Int = locations.size

    override fun onBindViewHolder(holder: LocationItemViewHolder, position: Int) {
      holder.bind(locations[position])
    }
  }

  class LocationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val coordinatesLabel = itemView.findViewById<TextView>(R.id.labelCoordinates)
    private val timestampLabel = itemView.findViewById<TextView>(R.id.labelTimestamp)

    fun bind(loggedLocation: LoggedLocation) {
      coordinatesLabel.text = String.format("%.3f, %.3f", loggedLocation.longitude, loggedLocation.latitude)
      val calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
      timestampLabel.text = Date(loggedLocation.timestamp).toString()
    }
  }
}
