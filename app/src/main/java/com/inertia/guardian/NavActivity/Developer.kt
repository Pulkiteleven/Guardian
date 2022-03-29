package com.inertia.guardian.NavActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inertia.guardian.R

class Developer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        supportActionBar?.hide()

        val back:FloatingActionButton = findViewById(R.id.back)

        back.setOnClickListener {
            onBackPressed()
        }
    }
}