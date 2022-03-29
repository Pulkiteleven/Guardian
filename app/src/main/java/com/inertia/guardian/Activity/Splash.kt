package com.inertia.guardian.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.MainActivity
import com.inertia.guardian.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null){
            var isAdmin:Boolean = false
            var collegename:String ?= null
            db.collection("admins").get()
                .addOnSuccessListener {
                    for (x in it){
                        if (user.email.toString().equals(x["name"].toString())){
                            isAdmin = true
                            collegename = x["college"].toString()
                            break
                        }
                    }
                    if (isAdmin){
                        val i = Intent(Splash@this,AdminPanel::class.java)
                        i.putExtra("college",collegename)
                        startActivity(i)
                        finish()

                    }
                    else{
                        val i = Intent(Splash@this,Report::class.java)
                        startActivity(i)
                        finish()
                    }
                }

        }
        else{
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}