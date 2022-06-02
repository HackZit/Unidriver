package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_reviewshistory.*
import kotlinx.android.synthetic.main.activity_rideshows.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class reviewshistory : AppCompatActivity() {
    private val ip =
        "ec2-54-165-184-219.compute-1.amazonaws.com" // this is the host ip that your data base exists on you can use 10.0.2.2 for local host                                                    found on your pc. use if config for windows to find the ip if the database exists on                                                    your pc

    private val port = "5432" // the port sql server runs on

    private val Classes =
        "net.sourceforge.jtds.jdbc.Driver" // the driver that is required for this connection use                                                                           "org.postgresql.Driver" for connecting to postgresql

    private val database = "d47r312ehrchj" // the data base name

    private val username = "ysugackagnmvja" // the user name

    private val password =
        "d4907e1eaacb044bee14a4e58e951584db64c73c4664712cbb450e49b7e418d9" // the password

    private val url =
        "jdbc:postgresql://$ip:$port/$database?ssl=true&sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory" // the connection url string

    private var connection: Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewshistory)
        query()
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

            val sql2 = "SELECT * FROM viajes"
            val rs2 = connection?.createStatement()?.executeQuery(sql2)
            var ride = rideshowev("test","test", "test", "test", R.drawable.coche)
            var listaRides = listOf(ride)
            listaRides = listaRides.minus(ride)

            if (rs2 != null){
                while (!rs2.isLast) {
                    //antes del while hacer query cantidad total viajes count, dentro del while cantidad total
                    //por cada viaje hay que leer los pasajeros arranca proceso hecho
                    rs2.next()
                    Toast.makeText(this, "esteesreviews " + rs2.getString(5), Toast.LENGTH_LONG)
                        .show()
                    var list1: List<String>? = null
                    list1 = rs2.getString(5).split(",")
                    list1.forEach {
                        if (username.equals(it)) {

                            var ride = rideshowev(
                                rs2.getString(1),
                                rs2.getString(2),
                                rs2.getString(3),
                                rs2.getString(8),
                                R.drawable.coche
                            )
                            listaRides = listaRides.plus(ride)
                        }
                    }
                }
            }


            val adapter = rideAdapter(this, listaRides)
            listareviewshist.adapter = adapter

            listareviewshist.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(this,ActiveRide::class.java)
                intent.putExtra("rides",listaRides[position])
                startActivity(intent)
            }



        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
        }
    }


}