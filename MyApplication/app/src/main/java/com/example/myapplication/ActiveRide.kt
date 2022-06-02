package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_rideshows.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ActiveRide : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {


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
            if (rs1 != null) {
                while (!rs1.isLast) {
                    rs1.next()
                    pasajeros = rs1.getString(5)
                }
            }
            val  username= (this.application as GlobalClass).getSomeVariable()
            val sql = "UPDATE viajes SET pasajeros = '$pasajeros,$username' WHERE IDViajes = $id"
            connection?.createStatement()?.executeQuery(sql)
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

}