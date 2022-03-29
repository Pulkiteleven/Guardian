package com.inertia.guardian.NavActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inertia.guardian.R

class OurStory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_story)

        supportActionBar?.hide()
        val back: FloatingActionButton = findViewById(R.id.back)

        back.setOnClickListener {
            onBackPressed()
        }

        val link = intent.getStringExtra("story")
        val webView: WebView = findViewById(R.id.web)
        webView.loadUrl(link!!)
    }
}