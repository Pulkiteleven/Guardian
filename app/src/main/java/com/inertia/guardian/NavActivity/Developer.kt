package com.inertia.guardian.NavActivity

import android.content.Intent
import android.net.Uri
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

        // Floating Action Button for profiles.

        val pulkit_linkedin:FloatingActionButton = findViewById(R.id.pulkit_linkedin)

        pulkit_linkedin.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/pulkit-dubey-75b703224/"))
            startActivity(intent)
        }

        val pulkit_github:FloatingActionButton = findViewById(R.id.pulkit_github)

        pulkit_github.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Pulkiteleven/"))
            startActivity(intent)
        }

        val pulkit_instagram:FloatingActionButton = findViewById(R.id.pulkit_instagram)

        pulkit_instagram.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/kylo_ren__20/"))
            startActivity(intent)
        }

        val satyam_linkedin:FloatingActionButton = findViewById(R.id.satyam_linkedin)

        satyam_linkedin.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/iamsatyam17/"))
            startActivity(intent)
        }

        val satyam_github:FloatingActionButton = findViewById(R.id.satyam_github)

        satyam_github.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/satyamsharma17/"))
            startActivity(intent)
        }

        val satyam_instagram:FloatingActionButton = findViewById(R.id.satyam_instagram)

        satyam_instagram.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/iamsatyam17/"))
            startActivity(intent)
        }

        val manish_linkedin:FloatingActionButton = findViewById(R.id.manish_linkedin)

        manish_linkedin.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/manish-singh-b96511229/"))
            startActivity(intent)
        }

        val manish_github:FloatingActionButton = findViewById(R.id.manish_github)

        manish_github.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MAnishSingh13275/"))
            startActivity(intent)
        }

        val manish_instagram:FloatingActionButton = findViewById(R.id.manish_instagram)

        manish_instagram.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/manish_singh0204/"))
            startActivity(intent)
        }

        val shubham_linkedin:FloatingActionButton = findViewById(R.id.shubham_linkedin)

        shubham_linkedin.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/shubham-mishra-362416226/"))
            startActivity(intent)
        }

        val shubham_github:FloatingActionButton = findViewById(R.id.shubham_github)

        shubham_github.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cyberdslayer/"))
            startActivity(intent)
        }

        val shubham_instagram:FloatingActionButton = findViewById(R.id.shubham_instagram)

        shubham_instagram.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/cyberdslayer/"))
            startActivity(intent)
        }
    }
}