package com.example.weatherdiary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    private var locationPermissionGranted : Boolean = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val defaultLocation = LatLng(38.9869, -76.9426) //UMD
    private lateinit var locButton : Button //button to go to second view and transfer location
    private lateinit var listButton : Button //button to go straight to third view
    private var permission : String = Manifest.permission.ACCESS_COARSE_LOCATION
    private lateinit var launcher : ActivityResultLauncher<String>
    private lateinit var geocoder : Geocoder //these two are used to get the city and state from LatLng
    private lateinit var handler : GeocodingHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if permission to use the coarse location has already been granted
        var grantedPermission : Int =
            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
        if( grantedPermission != PackageManager.PERMISSION_GRANTED ) {
            // if perm not already granted, need to ask for permission to use coarse location
            var contract : ActivityResultContracts.RequestPermission =
                ActivityResultContracts.RequestPermission( )
            var results : Results = Results( )
            launcher = registerForActivityResult( contract, results )
            launcher.launch( permission )
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //needed for getting the last location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //init buttons
        locButton = findViewById(R.id.gotoview2) //button to transfer to second view + pass cityState
        listButton = findViewById(R.id.gotoview3) //button to go directly to diary list

        //used to go to view 2 and pass location via companion object
        locButton.setOnClickListener{
            var myIntent : Intent = Intent( this, SecondActivity::class.java )
            startActivity( myIntent )
            overridePendingTransition(R.anim.anim_one,R.anim.anim_two)
        }


        //used to go to view 3
        listButton.setOnClickListener{

            var myIntent2 : Intent = Intent( this, Entries::class.java )
            startActivity( myIntent2 )
            overridePendingTransition(R.anim.anim_one,R.anim.anim_two)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker at UMD.
     * We do not check if Google Play services is installed since the required API is 33 or higher
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true //allow zoom buttons
        updateLocationUI()

        geocoder = Geocoder( this )  //init geocoder for entire activity
        handler = GeocodingHandler()

        //erase old marker and set new marker to location that user clicked.
        mMap.setOnMapClickListener {
            mMap.clear()
            locMarker = mMap.addMarker(MarkerOptions().position(it))!! //lets see if this causes issues
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it)) //center camera

            geocoder.getFromLocation(locMarker.position.latitude,
                locMarker.position.longitude, 5, handler)

        }
    }

    //permissions
    inner class Results : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean?) {
            locationPermissionGranted = result != null && result == true
            updateLocationUI()
        }
    }

    //updateUI and deviceLocation taken from Google Maps API sample modified to fit our app
    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
            }

            getDeviceLocation()

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    //this fun was taken from Google Maps API sample (edited to fit this)
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available. If not available (or permission not granted),
         * uses the default location which is College Park, MD / UMD
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
                            mMap.clear()
                            locMarker = mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)))!!

                            //adding geocoder code here to ensure phone location can be chosen w/o user clicking on screen
                            geocoder.getFromLocation(locMarker.position.latitude,
                                locMarker.position.longitude, 5, handler)
                        }
                    } else {
                        //loc perm allowed but last loc unavailable
                        setDefaultLoc()
                    }
                }
            }
            else{ //no loc permission
                setDefaultLoc()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    //function to set default location when loc perm not allowed
    //or last loc not available.
    private fun setDefaultLoc() : Unit{
        mMap.clear()
        mMap.moveCamera(CameraUpdateFactory
            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
        locMarker = mMap.addMarker(MarkerOptions().position(defaultLocation))!!
        CITY_STATE = "College Park, Maryland" //hardcoded for ease
    }

    inner class GeocodingHandler : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: MutableList<Address>) {
            if( addresses != null ) {
                var cityAdd : String = addresses[0].locality //city
                var stateAdd : String = addresses[0].adminArea //state
                CITY_STATE = "$cityAdd, $stateAdd" //combined to be: city, state
            }
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 16
        lateinit var CITY_STATE : String //used to get citystate in other activity
    }
}