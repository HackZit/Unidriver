package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class MainActivity : AppCompatActivity() {


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
        setContentView(R.layout.activity_main)
    }

    fun start(view: View?) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            PackageManager.PERMISSION_GRANTED
        )
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            Class.forName(Classes)
            connection = DriverManager.getConnection(url, username, password)
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            val  user= findViewById<EditText>(R.id.usertxt).text.toString()
            val  pass= findViewById<EditText>(R.id.passwordtxt).text.toString()
            val sql = "SELECT COUNT(*) as count FROM A_TRAINER WHERE NAME='$user' AND ID=$pass"
            VerifyLogin(sql)

        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Class fail", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Connected no " + e, Toast.LENGTH_LONG).show()
        }
    }

    fun registerscreen(view: View?) {
        val intent= Intent(this, RegisterAtivity::class.java)
        startActivity(intent)
    }

    fun VerifyLogin(sql: String){
        val rs = connection?.createStatement()?.executeQuery(sql)
        if (rs != null) {
            rs.next()
            val count: Int = rs.getInt("count")
            if (count == 1) {
                Toast.makeText(this, "Verificado $count", Toast.LENGTH_SHORT).show()
                val intent= Intent(this, SecondActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error $count", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Es nulo", Toast.LENGTH_SHORT).show()
        }
    }
    fun insertRow(view: View?) {
        val sql = "INSERT INTO A_TRAINER (ID, NAME, NUM_BADGES) VALUES (3,'sebas',5)"
        with(connection) {
            this?.createStatement()?.execute(sql)
            //this?.commit()
        }
    }
}

