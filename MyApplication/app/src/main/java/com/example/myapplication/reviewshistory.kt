package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.sql.Connection


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
    }
}