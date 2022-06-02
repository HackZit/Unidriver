package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.directions.route.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_active_ride.*
import kotlinx.android.synthetic.main.activity_rideshows.*
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class ActiveRide : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
    RoutingListener {


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
    var id: String? = null
    var iddest: String? = null
    var idpart: String? = null
    var hora: String? = null

    //current and destination location objects
    var myLocation: Location? = null
    var destinationLocation: Location? = null
    protected var start: LatLng? = null
    protected var end: LatLng? = null

    //polyline object
    private var polylines: MutableList<Polyline>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_ride)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Get a handle to the fragment and register the callback.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val rides = intent.getSerializableExtra("rides") as rideshowev
        id = rides.id
        iddest = rides.dir_destino
        idpart = rides.dir_comienzo
        hora = rides.hora
        println("Ubicacion latlong"+ getLocationFromAddress(this, idpart))
        findViewById<TextView>(R.id.LugarPartida).text = idpart
        findViewById<TextView>(R.id.LugarDestino).text = iddest
        findViewById<TextView>(R.id.Hora).text = hora
        query()
    }

    fun unirse(view: View){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            PackageManager.PERMISSION_GRANTED
        )
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName(Classes)
            connection = DriverManager.getConnection(url, username, password)
            val sql1 = "SELECT * FROM viajes WHERE IDviajes = $id"
            val rs1 = connection?.createStatement()?.executeQuery(sql1)
            var pasajeros = ""
            var numpasajeros = 1
                if (rs1 != null) {
                while (!rs1.isLast) {
                    rs1.next()
                    pasajeros = rs1.getString(5)
                    numpasajeros = rs1.getInt(7)+1
                    if(rs1.getInt(6) == rs1.getInt(7)+1 ){
                        val sql3= "UPDATE viajes SET activo ='false'  WHERE IDViajes = $id"
                        with(connection) {
                            this?.createStatement()?.execute(sql3)
                            //this?.commit()
                        }
                    }
                }
            }
            val  username= (this.application as GlobalClass).getSomeVariable()
            val sql = "UPDATE viajes SET pasajeros = '$pasajeros,$username' WHERE IDViajes = $id"
            val sql2 = "UPDATE viajes SET numactual_pasajeros =$numpasajeros  WHERE IDViajes = $id"
            with(connection) {
                this?.createStatement()?.execute(sql)
                this?.createStatement()?.execute(sql2)
                //this?.commit()
            }
            button6.setVisibility(View.INVISIBLE)
            query()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
        }
    }

    fun query() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            PackageManager.PERMISSION_GRANTED
        )
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName(Classes)
            connection = DriverManager.getConnection(url, username, password)
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            val  username= (this.application as GlobalClass).getSomeVariable()

            var person = persona("test", "test")
            var DetailsRide = listOf(person)
            DetailsRide = DetailsRide.minus(person)

            val sql1 = "SELECT Pasajeros FROM viajes WHERE idviajes = $id"
            val rs1 = connection?.createStatement()?.executeQuery(sql1)
            var list1: List<String>? = null
            if (rs1 != null) {
                rs1.next()
                list1 = rs1.getString(1).split(",")
                list1.forEach {
                    val sql = "SELECT * FROM users WHERE username = '$it'"
                    val rs = connection?.createStatement()?.executeQuery(sql)
                    println("NOMBRE DE USUARI: " + it)
                    if(it == username){
                        button6.setVisibility(View.INVISIBLE)
                    }
                    if (rs != null) {
                        rs.next()

                        var person = persona(
                            rs.getString(2)+ " " + rs.getString(3),
                            rs.getString(5)
                        )
                        Toast.makeText(this, "comienso " + person.fullname, Toast.LENGTH_LONG).show()
                        DetailsRide = DetailsRide.plus(person)

                    }
                }

            }

            val recycle = findViewById<RecyclerView>(R.id.RecyclerViewPersonas)
            val adapter = TextPersonasAdapter(DetailsRide)
            recycle.adapter = adapter



        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
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
                    ?.let { map.moveCamera(it) }
            }

        end = getLocationFromAddress(this, iddest)
        map.clear()
        start = getLocationFromAddress(this, idpart)
        //start route finding
        Findroutes(start, end)
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
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
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

        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || PermissionUtils.isPermissionGranted(
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
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun Findroutes(Start: LatLng?, End: LatLng?) {
        if (Start == null || End == null) {
            Toast.makeText(this@ActiveRide, "Unable to get location", Toast.LENGTH_LONG).show()
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
        Toast.makeText(this@ActiveRide, "Finding Route...", Toast.LENGTH_LONG).show()
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
        }
        return p1
    }


}