package com.inertia.guardian.classes

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaRecorder
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inertia.guardian.MainActivity
import com.inertia.guardian.models.uris
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class ReactivateService: BroadcastReceiver() {

    var count=0
    var recorder:MediaRecorder ?= null
    var s:String ?= null
    var fileName:String ?= null
    var uuid:String ?= null
    public var uriList:MutableList<uris> = ArrayList()
    var date:String ?= null
    var time:String ?= null

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("Check : ","Receiver Started")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p0?.startForegroundService(Intent(p0, SensorService::class.java))
        } else {
            p0?.startService(Intent(p0, SensorService::class.java))
        }
    }
}