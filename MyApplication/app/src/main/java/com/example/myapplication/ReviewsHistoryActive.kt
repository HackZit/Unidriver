package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_active_ride.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ReviewsHistoryActive : AppCompatActivity() {
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
    var id: String? = null
    var iddest: String? = null
    var idpart: String? = null
    var hora: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews_history_active)
        val rides = intent.getSerializableExtra("rides") as rideshowev
        id = rides.id
        iddest = rides.dir_destino
        idpart = rides.dir_comienzo
        hora = rides.hora
        findViewById<TextView>(R.id.part).text = idpart
        findViewById<TextView>(R.id.dest).text = iddest
        findViewById<TextView>(R.id.houru).text = hora
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
}