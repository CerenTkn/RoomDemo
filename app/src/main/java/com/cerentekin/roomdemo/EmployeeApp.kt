package com.cerentekin.roomdemo

import android.app.Application

class EmployeeApp: Application() {
    val db by lazy{
        //we are creating it lazily, which means that it loads the needed value
        //to our variable whenever it is needed. So not directly, but only it's needed.

        EmployeeDatabase.getInstance(this)
    }
}