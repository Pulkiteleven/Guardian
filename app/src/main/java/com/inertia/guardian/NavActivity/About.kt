package com.inertia.guardian.NavActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inertia.guardian.R

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.hide()

        val back:FloatingActionButton = findViewById(R.id.back)
        val email:TextView = findViewById(R.id.mail)

        email.movementMethod = LinkMovementMethod.getInstance()

        back.setOnClickListener {
            onBackPressed()
        }
    }
}