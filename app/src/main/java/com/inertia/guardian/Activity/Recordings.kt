package com.inertia.guardian.Activity

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.inertia.guardian.R
import com.inertia.guardian.adapters.recordingAdapter
import com.inertia.guardian.models.uris
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Recordings : AppCompatActivity() {
    var rc:RecyclerView ?= null
    var recList:MutableList<uris> ?= null
    var adapter:recordingAdapter ?= null
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordings)

        rc = findViewById(R.id.record_list)
        val dont = findViewById<TextView>(R.id.dont)
        rc!!.layoutManager = GridLayoutManager(this,1)
        recList = ArrayList()

        supportActionBar?.hide()


        var sf: SharedPreferences = getSharedPreferences("uri", MODE_PRIVATE)
        var edit: SharedPreferences.Editor = sf.edit()

        val gsoon: Gson = Gson()

        val jsoon:String ?= sf.getString("uris",null)


        val path = externalCacheDir?.absolutePath
        val directory = File(path)
        val files = directory.listFiles()
        for (i in files.indices) {
            val name = files[i].name
            val uri = files[i].path
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val times:Date = Date(files[i].lastModified())
            val finaltimes:String = formatter.format(times)
            val u = uris(name,uri,finaltimes)
            recList?.add(u)
        }

        if (recList?.size!! > 0){
            adapter = recordingAdapter(this, recList as ArrayList<uris>)
            rc!!.adapter = adapter
        }
        else{
            rc?.visibility = View.GONE
            dont.visibility = View.VISIBLE
        }

//        if (jsoon != null){
//            val type: Type = object : TypeToken<MutableList<uris>>(){}.type
//            recList = gsoon.fromJson(jsoon,type)
//            adapter = recordingAdapter(this, recList as ArrayList<uris>)
//            rc!!.adapter = adapter
//
//        }
//        else{
//            rc?.visibility = View.GONE
//            dont.visibility = View.VISIBLE
//        }


        val back:FloatingActionButton = findViewById(R.id.back)
        back.setOnClickListener{
            super.onBackPressed()
        }

    }
}