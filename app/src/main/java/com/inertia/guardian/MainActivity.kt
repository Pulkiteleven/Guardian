package com.inertia.guardian

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.Activity.AdminPanel
import com.inertia.guardian.Activity.Report
import com.inertia.guardian.Activity.SignUp
import com.inertia.guardian.Activity.Signin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val login:Button = findViewById(R.id.login)
        val signup:Button = findViewById(R.id.signup)

        login.setOnClickListener {
            val i = Intent(MainActivity@this,Signin::class.java)
            startActivity(i)
        }

        signup.setOnClickListener {
            val i = Intent(MainActivity@this,SignUp::class.java)
            startActivity(i)
        }

    }


}