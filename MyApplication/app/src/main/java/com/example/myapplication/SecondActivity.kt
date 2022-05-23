package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val actionBar = supportActionBar

        actionBar!!.title = "UniDriver"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}