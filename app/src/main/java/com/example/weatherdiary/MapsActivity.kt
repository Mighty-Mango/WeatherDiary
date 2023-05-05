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
    private lateinit var locButton : Button
    private lateinit var listButton : Button

    //currently testing permissions like we did in class

    private var permission : String = Manifest.permission.ACCESS_COARSE_LOCATION
    private lateinit var launcher : ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TESTING BETWEEN HERE AND Obtain the .....

        // check if permission to use the coarse loc has already been granted
        var grantedPermission : Int =
            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
        if( grantedPermission == PackageManager.PERMISSION_GRANTED ) {
            // permission has been granted, start using the camera
            Log.w( "MainActivity", "permission was previously granted" )
        } else {
            // need to ask for permission ton use the camera
            Log.w( "MainActivity", "permission was NOT previously granted" )
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

        //needed for geocoder
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //init buttons
        locButton = findViewById(R.id.gotoview2) //button to transfer to second view + pass cityState
        listButton = findViewById(R.id.gotoview3) //button to go directly to diary list

        locButton.setOnClickListener{
            Log.w("BUTTON", "Go to second view")
          //  Log.w("BUTTON", "City, State is " + cityState)
            Log.w("BUTTON", "City State Companion: " + CITY_STATE)
            var myIntent : Intent = Intent( this, SecondActivity::class.java )
            myIntent.putExtra("location", CITY_STATE)
            startActivity( myIntent )
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true //allow zoom buttons
//        getLocationPermission()
        updateLocationUI()
//        getDeviceLocation()

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

    inner class Results : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean?) {
            if( result != null && result == true ) {
                Log.w("MainActivity", "Permission Granted !!!")
                locationPermissionGranted = true
            }else {
                Log.w("MainActivity", "Sorry, permission NOT granted")
                locationPermissionGranted = false
            }

            updateLocationUI()
        }
    }

    private fun updateLocationUI() {
        if (mMap == null) {
            Log.w("MapsActivity", "Somehow you managed to have a null map")
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

                //getLocationPermission()
                Log.w("TESTING", "NO Permission Granted")
            }

            getDeviceLocation()

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

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
                        setDefaultLoc()
                        //use default(UMD) if location permissions not allowed or last location not available
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

    private fun setDefaultLoc() : Unit{
        mMap.moveCamera(CameraUpdateFactory
            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))

        locMarker = mMap.addMarker(MarkerOptions().position(defaultLocation))!!

        mMap.uiSettings.isMyLocationButtonEnabled = false //i don't think this is needed
        CITY_STATE = "College Park, Maryland" //hardcoded for ease
    }

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