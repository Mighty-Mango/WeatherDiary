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

    private lateinit var geocoder : Geocoder //= Geocoder( this )
    private lateinit var handler : GeocodingHandler// = GeocodingHandler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if permission to use the coarse loc has already been granted
        var grantedPermission : Int =
            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
        if( grantedPermission == PackageManager.PERMISSION_GRANTED ) {
            // permission has been granted, start using the camera
            Log.w( "MapsActivity", "permission was previously granted" )
        } else {
            // need to ask for permission ton use the camera
            Log.w( "MapsActivity", "permission was NOT previously granted" )
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

        //needed for last location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //init buttons
        locButton = findViewById(R.id.gotoview2) //button to transfer to second view + pass cityState
        listButton = findViewById(R.id.gotoview3) //button to go directly to diary list

        //used to go to view 2
        locButton.setOnClickListener{
            Log.w("BUTTON", "Go to second view")
            Log.w("BUTTON", "City State Companion: " + CITY_STATE)

            var myIntent : Intent = Intent( this, SecondActivity::class.java )
            startActivity( myIntent )
            overridePendingTransition(R.anim.anim_one,R.anim.anim_two)
        }

        //used to go to view 3
        listButton.setOnClickListener{
            Log.w("BUTTON", "Go to third view")
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

//        var geocoder : Geocoder = Geocoder( this )
//        var handler : GeocodingHandler = GeocodingHandler()

        geocoder = Geocoder( this )
        handler = GeocodingHandler()

        mMap.setOnMapClickListener {
            Log.w("MapsActivity", "LatLong is: $it")
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
            if( result != null && result == true ) {
                Log.w("MapsActivity", "Permission Granted !!!")
                locationPermissionGranted = true
            }else {
                Log.w("MapsActivity", "Sorry, permission NOT granted")
                locationPermissionGranted = false
            }
            updateLocationUI()
        }
    }

    //updateUI and deviceLocation taken from Google Maps API sample
    private fun updateLocationUI() {
        if (mMap == null) {
            Log.w("MapsActivity", "Somehow you managed to have a null map")
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true

                Log.w("MapsActivity", "Permission Granted")
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false

                Log.w("MapsActivity", "NO Permission Granted")
            }

            getDeviceLocation()

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    //taken from Google Maps API sample (edited to fit this)
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
                            mMap.clear()
                            locMarker = mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)))!!
                            Log.w("MapsActivity", "MARKER HAS BEEN SET TO LAST LOC")

                            //adding geocoder code here to ensure phone location can be chosen w/o user clicking on screen
//                            var geocoder : Geocoder = Geocoder( this )
//                            var handler : GeocodingHandler = GeocodingHandler()
                            geocoder.getFromLocation(locMarker.position.latitude,
                                locMarker.position.longitude, 5, handler)
                        }
                    } else {
                        //loc perm allowed but last loc unavailable
                        Log.w("MapsActivity", "Last Location Unknown, using default loc")
                        setDefaultLoc()
                        //uses default location(UMD) if location permissions not allowed or last location not available
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
                Log.w("MapsActivity", "City and state: $CITY_STATE")
            } else
                Log.w( "MapsActivity", "Sorry, no results" )
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 16
        lateinit var CITY_STATE : String //used to get citystate in other activity
    }
}