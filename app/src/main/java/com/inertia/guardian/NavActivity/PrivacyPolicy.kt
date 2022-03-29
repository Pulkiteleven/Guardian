package com.inertia.guardian.NavActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.R

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)


        supportActionBar?.hide()
        val back:FloatingActionButton = findViewById(R.id.back)

        back.setOnClickListener {
            onBackPressed()
        }

        val link = intent.getStringExtra("link")
        val webView:WebView = findViewById(R.id.web)
        webView.loadUrl(link!!)
    }
}