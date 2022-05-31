package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_rideshows.*

class rideshows : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rideshows)

        val ride = rideshowev("uni","uninorte","12:39PM",R.drawable.coche)

        val listaRides = listOf(ride)

        val adapter = rideAdapter(this,listaRides)
        lista.adapter = adapter
    }
}