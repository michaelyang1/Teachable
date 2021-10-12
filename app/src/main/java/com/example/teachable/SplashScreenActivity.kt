package com.example.teachable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import messages.NavHomeActivity
import registerlogin.LoginActivity
import registerlogin.RegisterActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("oncreate", "splash screen activity called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var handler = Handler()
        handler.postDelayed({
            verifyUserIsLoggedIn()
        }, 15)
    }

    private fun verifyUserIsLoggedIn() {
        // check if user is logged into the app
        val uid = FirebaseAuth.getInstance().uid
        lateinit var intent: Intent
        if (uid == null) {
            intent = Intent(this, LoginActivity::class.java)
            Log.e("TAG", "User is not logged in")
        }
        else {
            intent = Intent(this, NavHomeActivity::class.java)
            Log.e("TAG", "User is logged in")
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
