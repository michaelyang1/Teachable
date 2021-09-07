package com.example.teachable

import android.util.Log
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import ir.drax.netwatch.NetWatch
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator

class TeachableApp : android.app.Application() {
    override fun onCreate() {
        Log.e("TAG", "first called")
        super.onCreate()
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }


}

