package com.example.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
//values for the geofence
const val GEOFENCE_RADIUS = 500
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 5 * 24 * 60 * 60 * 1000 //5 days
const val GEOFENCE_DWELL_DELAY = 5 * 1000 //5 SECS
const val  GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 15f
const val LOCATION_REQUEST_CODE = 123
private val TAG: String = MapsActivity::class.java.simpleName

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //permissions checker
        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                LOCATION_REQUEST_CODE
            )
        } else {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            this.map.isMyLocationEnabled = true

            // Zoom to last known location
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    with(map) {
                        val latLng = LatLng(it.latitude, it.longitude)
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL))
                    }
                } else {
                    with(map) {
                        moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(65.01355297927051, 25.464019811372978),
                                CAMERA_ZOOM_LEVEL
                            )
                        )
                    }
                }
            }
        }
        setLongClick(map)
    }

    //when map is long clicked this will go
    private fun setLongClick(googleMap: GoogleMap) {

        googleMap.setOnMapLongClickListener { latlng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Lng: %2$.5f",
                latlng.latitude,
                latlng.longitude
            )
            //ads marker when long clicked on the map with information about the location
            map.addMarker(
                MarkerOptions().position(latlng)
                    .title("Selected location")
                    .snippet(snippet)
            )
            //adds circle around the same dimensions what the geofence will be
            map.addCircle(
                CircleOptions()
                    .center(latlng)
                    .strokeColor(Color.argb(0, 0, 0, 0))
                    .fillColor(Color.argb(70, 25, 255, 25))
                    .radius(GEOFENCE_RADIUS.toDouble())
            )
            //move the camera to the center of the click
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, CAMERA_ZOOM_LEVEL))
            //we need reminderid for the database
            val reminderID = intent.getIntExtra("ReminderID", 0)

            createGeoFence(latlng, reminderID, geofencingClient)
            //notify the user that the geofence is done
            Toast.makeText(
                    this,
                    "Location set. You can exit by pressing back",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    //this creates the geofence (from exercises)
    private fun createGeoFence(location: LatLng, key: Int, geofencingClient: GeofencingClient) {
        val geofence = Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
                .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
                .build()

        val geofenceRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()
        //information for the notification from the newReminder window
        val reminderDate = intent.getLongExtra("reminderDate", 0)
        val heading = intent.getStringExtra("heading")
        val details = intent.getStringExtra("details")
        val date = intent.getStringExtra("date")
        val intent = Intent(this, ReminderReceiver::class.java)
                .putExtra("uid", key)
                //.putExtra("message", "Geofence alert - ${location.latitude}, ${location.longitude}")
                .putExtra("reminderDate", reminderDate)
                .putExtra("heading", heading)
                .putExtra("details",details)
                .putExtra("date", date)

        val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {
                geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            }
        }
    }


    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == GEOFENCE_LOCATION_REQUEST_CODE) {
            if (permissions.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                        this,
                        "This application needs background location to work on Android 10 and higher",
                        Toast.LENGTH_SHORT
                ).show()
            }

            if (requestCode == LOCATION_REQUEST_CODE) {
                if (
                        grantResults.isNotEmpty() && (
                                grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
                ) {
                    if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    map.isMyLocationEnabled = true
                    onMapReady(map)
                } else {
                    Toast.makeText(
                            this,
                            "The app needs location permission to function",
                            Toast.LENGTH_LONG
                    ).show()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (grantResults.isNotEmpty() && grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                                this,
                                "This application needs background location to work on Android 10 and higher",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    }

