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
import kotlinx.android.synthetic.main.activity_rideshows.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class rideshows : AppCompatActivity() {


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
        setContentView(R.layout.activity_rideshows)

        val intent  = getIntent()
        val hora = intent.getStringExtra("hora")
        //Toast.makeText(this, "intent "+hora, Toast.LENGTH_LONG).show()
        if(hora == "") {
            query()
        }else {
            queryFiltered()
        }

    }

    fun queryFiltered() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            PackageManager.PERMISSION_GRANTED
        )
        //Toast.makeText(this, "query filtered ", Toast.LENGTH_LONG).show()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName(Classes)
            connection = (this.application as GlobalClass).getConnection()



            var ride = rideshowev("test","test", "test", "test", R.drawable.coche)
            var listaRides = listOf(ride)
            listaRides = listaRides.minus(ride)
            val intent  = getIntent()
            val hora = intent.getStringExtra("hora")
            val sql1 = "SELECT COUNT(*) as count FROM viajes WHERE ACTIVO='true' AND HORA_DESTINO='$hora'"
            val rs1 = connection?.createStatement()?.executeQuery(sql1)

            if (rs1 != null) {
                rs1.next()
                val count: Int = rs1.getInt("count")
                //Toast.makeText(this, "count "+count, Toast.LENGTH_SHORT).show()
                if (count == 0) {

                    var ride = rideshowev(
                        "N/A",
                        "N/A",
                        "N/A",
                        "N/A",
                        R.drawable.coche
                    )
                    listaRides = listaRides.plus(ride)
                }else{
                    val sql = "SELECT  * FROM viajes WHERE ACTIVO='true' AND HORA_DESTINO='$hora'"
                    val rs = connection?.createStatement()?.executeQuery(sql)
                    if (rs != null) {

                        while (!rs.isLast) {
                            rs.next()
                            var ride = rideshowev(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(8),
                                R.drawable.coche
                            )
                            listaRides = listaRides.plus(ride)

                        }
                    }
                }
            }

            val adapter = rideAdapter(this, listaRides)
            lista.adapter = adapter

            lista.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(this,ActiveRide::class.java)
                intent.putExtra("rides",listaRides[position])
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
            connection = (this.application as GlobalClass).getConnection()
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()


            var ride = rideshowev("test","test", "test", "test", R.drawable.coche)
            var listaRides = listOf(ride)
            listaRides = listaRides.minus(ride)

            val sql = "SELECT  * FROM viajes WHERE ACTIVO='true'"
            val rs = connection?.createStatement()?.executeQuery(sql)

            if (rs != null) {
                while (!rs.isLast) {
                    rs.next()
                    //Toast.makeText(this, "comienso " + ride.dir_comienzo, Toast.LENGTH_LONG).show()
                    var ride = rideshowev(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(8),
                        R.drawable.coche
                    )
                    listaRides = listaRides.plus(ride)

                }
            }


            val adapter = rideAdapter(this, listaRides)
            lista.adapter = adapter

            lista.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(this,ActiveRide::class.java)
                intent.putExtra("rides",listaRides[position])
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

    fun secondscreen(view: View?) {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
    fun reviewsview(view: View?) {
        val intent = Intent(this, reviewshistory::class.java)
        startActivity(intent)
    }
}