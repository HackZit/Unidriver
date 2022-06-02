package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.directions.route.*
import com.example.myapplication.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.myapplication.PermissionUtils.isPermissionGranted
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// Implement OnMapReadyCallback.
class SecondActivity: AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnRequestPermissionsResultCallback, RoutingListener {

    private val ip = "ec2-54-165-184-219.compute-1.amazonaws.com" // this is the host ip that your data base exists on you can use 10.0.2.2 for local host                                                    found on your pc. use if config for windows to find the ip if the database exists on                                                    your pc

    private val port = "5432" // the port sql server runs on

    private val Classes = "net.sourceforge.jtds.jdbc.Driver" // the driver that is required for this connection use                                                                           "org.postgresql.Driver" for connecting to postgresql

    private val database = "d47r312ehrchj" // the data base name

    private val username = "ysugackagnmvja" // the user name

    private val password = "d4907e1eaacb044bee14a4e58e951584db64c73c4664712cbb450e49b7e418d9" // the password

    private val url = "jdbc:postgresql://$ip:$port/$database?ssl=true&sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory" // the connection url string


    private var connection: Connection? = null
    private var permissionDenied = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //current and destination location objects
    var myLocation: Location? = null
    var destinationLocation: Location? = null
    protected var start: LatLng? = null
    protected var end: LatLng? = null

    //polyline object
    private var polylines: MutableList<Polyline>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout file as the content view.
        setContentView(R.layout.activity_second)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Get a handle to the fragment and register the callback.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        findViewById<EditText>(R.id.editTextTextPersonName).setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                map.clear()
                start = getLocationFromAddress(this, findViewById<EditText>(R.id.editTextTextPersonName).text.toString() )
                Findroutes(start, end)
            }
        })
        findViewById<EditText>(R.id.editTextTextPersonName2).setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                map.clear()
                end = getLocationFromAddress(this, findViewById<EditText>(R.id.editTextTextPersonName2).text.toString())
                Findroutes(start, end)
            }
        })

        dir()
    }
    fun dir (){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            PackageManager.PERMISSION_GRANTED
        )
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName(Classes)
            connection = (this.application as GlobalClass).getConnection()
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            val destinationview = findViewById<AutoCompleteTextView>(R.id.editTextTextPersonName2)
            val username= (this.application as GlobalClass).getSomeVariable()

            val sql1 = "SELECT COUNT(*) as count FROM direcciones WHERE USERNAME ='$username'"
            val rs1 = connection?.createStatement()?.executeQuery(sql1)
            println("RS1")
            if (rs1 != null) {
                rs1.next()
                val count: Int = rs1.getInt("count")
                println("Count "+ count)
                if (count == 0) {

                } else {
                    val sql = "SELECT * FROM direcciones WHERE USERNAME ='$username'"
                    val rs = connection?.createStatement()?.executeQuery(sql)
                    var destinos = arrayOf("")
                    if (rs != null) {
                        while (!rs.isLast) {
                            rs.next()
                            println("Destino " + rs.getString(3))
                            destinos = destinos.plus(rs.getString(3))
                        }
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, destinos)
                    destinationview.setAdapter(adapter)
                }
            }

        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            //Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            //Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
        }


    }
    // Get a handle to the GoogleMap object and display marker.
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val curren = location?.let { LatLng(location.latitude, it.longitude) }
                curren?.let { CameraUpdateFactory.newLatLngZoom(it, 15.0F) }
                    ?.let { map.animateCamera(it) }
            }
    }
    fun rideshowsscreen(view: View?) {
        val intent= Intent(this, rideshows::class.java)
        val hora = findViewById<EditText>(R.id.editTextTextPersonName4).text.toString()
        intent.putExtra("hora",hora)
        startActivity(intent)
    }
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT) .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        //Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG) .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun createride(view: View?) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            PackageManager.PERMISSION_GRANTED
        )
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName(Classes)
            connection = (this.application as GlobalClass).getConnection()
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            val  dir_inicio= findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
            val  dir_destino= findViewById<EditText>(R.id.editTextTextPersonName2).text.toString()
            val  username= (this.application as GlobalClass).getSomeVariable()
            val  hora= findViewById<EditText>(R.id.editTextTextPersonName4).text.toString()
            val  pasajeros= (this.application as GlobalClass).getSomeVariable()
            val  num_pasajeros= findViewById<EditText>(R.id.editTextTextPersonName5).text.toString()
            val sql = "INSERT INTO viajes (idviajes, dir_destino, dir_inicio, usermain, pasajeros, num_pasajeros, numactual_pasajeros, hora_destino, activo) VALUES (DEFAULT, '$dir_destino', '$dir_inicio', '$username', '$pasajeros', $num_pasajeros, 1, '$hora',TRUE)"
            val sql2 = "INSERT INTO direcciones (IDdir, USERNAME, dir_destino) VALUES (DEFAULT, '$username', '$dir_destino')"
            with(connection) {
                this?.createStatement()?.execute(sql)
                this?.createStatement()?.execute(sql2)
                //this?.commit()
            }
            val sql1 = "SELECT idviajes FROM viajes ORDER BY idviajes DESC LIMIT 1;"
            val rs1 = connection?.createStatement()?.executeQuery(sql1)
            if (rs1 != null) {
                rs1.next()
                val intent= Intent(this, ActiveRide::class.java)
                val ride = rideshowev(rs1.getString(1),dir_destino,dir_inicio,hora, R.drawable.coche)
                intent.putExtra("rides", ride)
                startActivity(intent)
            }


        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            //Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            //Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
        }
    }


    fun Findroutes(Start: LatLng?, End: LatLng?) {
        if (Start == null || End == null) {
            //Toast.makeText(this@ActiveRide, "Unable to get location", Toast.LENGTH_LONG).show()
        } else {
            val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(Start, End)
                .key("AIzaSyDx10nffpWYVeQYqCbiTDlRRD2TwQjYtHg") //also define your api key here.
                .build()
            routing.execute()
        }
    }

    //Routing call back functions.
    override fun onRoutingFailure(e: RouteException) {
        val parentLayout = findViewById<View>(android.R.id.content)
        val snackbar: Snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG)
        snackbar.show()
//        Findroutes(start,end);
    }
    override fun onRoutingStart() {
        //Toast.makeText(this@ActiveRide, "Finding Route...", Toast.LENGTH_LONG).show()
    }

    //If Route finding success..
    override fun onRoutingSuccess(route: ArrayList<Route>, shortestRouteIndex: Int) {
        val center = start?.let { CameraUpdateFactory.newLatLng(it) }
        val zoom = CameraUpdateFactory.zoomTo(16f)
        polylines?.clear()
        val polyOptions = PolylineOptions()
        var polylineStartLatLng: LatLng? = null
        var polylineEndLatLng: LatLng? = null
        polylines = ArrayList()
        //add route(s) to the map using polyline
        for (i in 0 until route.size) {
            if (i == shortestRouteIndex) {
                polyOptions.color(resources.getColor(R.color.teal_200))
                polyOptions.width(7f)
                polyOptions.addAll(route[shortestRouteIndex].getPoints())
                val polyline: Polyline = map.addPolyline(polyOptions)
                polylineStartLatLng = polyline.points[0]
                val k = polyline.points.size
                polylineEndLatLng = polyline.points[k - 1]
                (polylines as ArrayList<Polyline>).add(polyline)
            } else {
            }
        }

        //Add Marker on route starting position
        val startMarker = MarkerOptions()
        startMarker.position(polylineStartLatLng!!)
        startMarker.title("My Location")
        map.addMarker(startMarker)

        //Add Marker on route ending position
        val endMarker = MarkerOptions()
        endMarker.position(polylineEndLatLng!!)
        endMarker.title("Destination")
        map.addMarker(endMarker)
    }
    override fun onRoutingCancelled() {
        Findroutes(start, end)
    }
    fun onConnectionFailed(connectionResult: ConnectionResult) {
        Findroutes(start, end)
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = LatLng(location.getLatitude(), location.getLongitude())
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(this@SecondActivity, "Unable to get location", Toast.LENGTH_LONG).show()
        }
        return p1
    }





}