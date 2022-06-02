package com.example.myapplication

import android.app.Application
import java.sql.Connection

class GlobalClass: Application() {
    private var username: String? = null
    private var connection: Connection? = null

    fun getSomeVariable(): String? {
        return username
    }

    fun setSomeVariable(someVariable: String?) {
        this.username = someVariable
    }

    fun getConnection(): Connection? {
        return connection
    }

    fun setConnection(someConnection: Connection?) {
        this.connection = someConnection
    }
}