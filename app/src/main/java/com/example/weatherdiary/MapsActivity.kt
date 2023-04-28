package com.example.weatherdiary

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.weatherdiary.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locMarker: Marker
    private var locationPermissionGranted : Boolean = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(38.9869, -76.9426) //UMD
    private lateinit var locButton : Button
    private lateinit var listButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //needed for geocoder
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //init buttons
        locButton = findViewById(R.id.gotoview2) //button to transfer to second view + pass cityState
        listButton = findViewById(R.id.gotoview3) //button to go directly to diary list

        locButton.setOnClickListener{
            Log.w("BUTTON", "Go to second view")
          //  Log.w("BUTTON", "City, State is " + cityState)
            Log.w("BUTTON", "City State Companion: " + CITY_STATE)
        }

        listButton.setOnClickListener{
            Log.w("BUTTON", "Go to third view")
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker at UMD.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true //allow zoom buttons
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()


        var geocoder : Geocoder = Geocoder( this )
        var handler : GeocodingHandler = GeocodingHandler()

        mMap.setOnMapClickListener {
            Log.w("TESTING", "LatLong is: " + it)
            mMap.clear()
            locMarker = mMap.addMarker(MarkerOptions().position(it))!! //lets see if this causes issues
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it)) //center camera

            geocoder.getFromLocation(locMarker.position.latitude,
                locMarker.position.longitude, 5, handler)

        }
    }


    //below functions are mostly from Google Maps API example/sample
    // code with added variables/UI stuff
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        //no need for fine location, just asking for COARSE_LOCATION perm

        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true

                Log.w("TESTING", "Permission Granted")
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                getLocationPermission()
                Log.w("TESTING", "NO Permission Granted")
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                            //below places default marker at lastknownlocation/currentlocation
                            mMap.clear()
                            locMarker = mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)))!!
                            Log.w("MapsActivity", "MARKER HAS BEEN SET TO LAST LOC")

                            //adding geocoder code here to ensure phone location can be chosen w/o user clicking on screen
                            var geocoder : Geocoder = Geocoder( this )
                            var handler : GeocodingHandler = GeocodingHandler()
                            geocoder.getFromLocation(locMarker.position.latitude,
                                locMarker.position.longitude, 5, handler)
                        }
                    } else {
                        Log.w("MapsActivity", "Last Location Unknown, using default loc")
                        mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                        CITY_STATE = "College Park, Maryland" //hardcoded for ease
                        //use default(UMD) if location permissions not allowed or last location not available
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    inner class GeocodingHandler : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: MutableList<Address>) {
            if( addresses != null ) {
                var cityAdd : String = addresses[0].locality //city
                var stateAdd : String = addresses[0].adminArea //state
                CITY_STATE = "$cityAdd, $stateAdd" //have both vars currently will fix once tested
                Log.w("MapsActivity", "City and state: " + CITY_STATE)
            } else
                Log.w( "MapsActivity", "Sorry, no results" )
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 16
        lateinit var CITY_STATE : String //can use this to get citystate in other activity
    }
}