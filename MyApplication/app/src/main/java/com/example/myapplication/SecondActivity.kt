package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// Implement OnMapReadyCallback.
class SecondActivity: AppCompatActivity(), OnMapReadyCallback {

    private val ip = "ec2-54-165-184-219.compute-1.amazonaws.com" // this is the host ip that your data base exists on you can use 10.0.2.2 for local host                                                    found on your pc. use if config for windows to find the ip if the database exists on                                                    your pc

    private val port = "5432" // the port sql server runs on

    private val Classes = "net.sourceforge.jtds.jdbc.Driver" // the driver that is required for this connection use                                                                           "org.postgresql.Driver" for connecting to postgresql

    private val database = "d47r312ehrchj" // the data base name

    private val username = "ysugackagnmvja" // the user name

    private val password = "d4907e1eaacb044bee14a4e58e951584db64c73c4664712cbb450e49b7e418d9" // the password

    private val url = "jdbc:postgresql://$ip:$port/$database?ssl=true&sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory" // the connection url string


    private var connection: Connection? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout file as the content view.
        setContentView(R.layout.activity_second)

        // Get a handle to the fragment and register the callback.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    // Get a handle to the GoogleMap object and display marker.
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
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
            connection = DriverManager.getConnection(url, username, password)
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            val  dir_inicio= findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
            val  dir_destino= findViewById<EditText>(R.id.editTextTextPersonName2).text.toString()
            val  username= MainActivity().user
            val  hora= findViewById<EditText>(R.id.editTextTextPersonName4).text.toString()
            val  pasajeros= MainActivity().
            val  num_pasajeros= findViewById<EditText>(R.id.editTextTextPersonName5).text.toString()
            val sql = "INSERT INTO viajes (dir_destino, dir_inicio, usermain, pasajeros, num_pasajeros, numactual_pasajeros, hora_destino,activo) VALUES ('$dir_destino', '$dir_inicio', '$username', '$pasajeros', $num_pasajeros, 1, '$hora',TRUE)"
            with(connection) {
                this?.createStatement()?.execute(sql)
                //this?.commit()

            }
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
        }
    }
}