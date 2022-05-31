package com.example.myapplication

import android.app.Application

class GlobalClass: Application() {
    private var username: String? = null

    fun getSomeVariable(): String? {
        return username
    }

    fun setSomeVariable(someVariable: String?) {
        this.username = someVariable
    }
}