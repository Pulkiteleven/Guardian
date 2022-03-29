package com.inertia.guardian.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.speech.SpeechRecognizer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inertia.guardian.R
import com.inertia.guardian.adapters.contactadapter
import com.inertia.guardian.classes.ReactivateService
import com.inertia.guardian.classes.SensorService
import com.inertia.guardian.models.contacts
import java.lang.reflect.Type


class Protect_activity : AppCompatActivity() {

    var favList:MutableList<contacts> ?= null
    var conList:MutableList<contacts> ?= null
    var adaper:contactadapter ?= null
    var spc:SpeechRecognizer ?= null
    var reci:Intent ?= null
    var textr:TextView ?= null
    var truefalse:Boolean = true

    val REQUEST_AUDIO = 2
    val PERMISSIONS_AUDIO = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )


    private val IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protect)

        supportActionBar?.hide()

        newPermisiion(this)
        favList = ArrayList()
        conList = ArrayList()

//        if(!checkAccessibilityPermission()){
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//        }


        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                askIgnoreOptimization()
            }
        }




        val add:MaterialButton = findViewById(R.id.add_contacts)
        val rccon:RecyclerView = findViewById(R.id.cons)
        val guard:MaterialButton = findViewById(R.id.guard)
        val keyword:TextInputEditText = findViewById(R.id.keyword)
        val total:TextInputEditText = findViewById(R.id.number)
        val lock_Key:MaterialButton = findViewById(R.id.lock_key)
        val myrec:FloatingActionButton = findViewById(R.id.myrec)



        rccon!!.layoutManager = GridLayoutManager(this,1)

        adaper = contactadapter(this,favList as ArrayList<contacts>)
        rccon!!.adapter = adaper

        val shf:SharedPreferences = getSharedPreferences("con", MODE_PRIVATE)

        val gson:Gson = Gson()

        val json:String ?= shf.getString("cons",null)
        if (json != null){
            val type:Type = object : TypeToken<MutableList<contacts>>(){}.type
            conList = gson.fromJson(json,type)
        }
        else{
//            rccon.visibility = View.GONE
        }
        for(c in 0..conList!!.size-1){
            val ii:contacts = contacts(
            (conList as ArrayList<contacts>)[c].name,
            (conList as ArrayList<contacts>)[c].no
            )
            favList?.add(ii)
        }
        if (favList == null){
            rccon.visibility = View.GONE
        }
        adaper?.notifyDataSetChanged()



        add.setOnClickListener {
            val i = Intent(Protect_activity@this,ContactPicker::class.java)
            startActivity(i)

        }
        guard.setOnClickListener{
            var sf:SharedPreferences = getSharedPreferences("keyword",MODE_PRIVATE)
            val edit: SharedPreferences.Editor = sf.edit()
            if (!TextUtils.isEmpty(total.text.toString())) {
                var myNum = Integer.parseInt(total.getText().toString());
                edit.putInt("total",myNum)
                edit.apply()
            }

            if (!TextUtils.isEmpty(keyword.text.toString())){
                var sf:SharedPreferences = getSharedPreferences("keyword",MODE_PRIVATE)
                val edit: SharedPreferences.Editor = sf.edit()
                edit.putString("keys",keyword.text.toString())
                edit.apply()
                Snacker("Updated")
            }
            else{
                Snacker("Add Some Key")
            }

            val sensorService = SensorService()
            val intent = Intent(this, sensorService.javaClass)
            if (!isMyServiceRunning(sensorService.javaClass)) {
                startService(intent)
            }
        }

        lock_Key.setOnClickListener {
            if (!TextUtils.isEmpty(keyword.text.toString())){
                var sf:SharedPreferences = getSharedPreferences("keyword",MODE_PRIVATE)
                val edit: SharedPreferences.Editor = sf.edit()
                edit.putString("keys",keyword.text.toString())
                edit.apply()
               Snacker("Updated")
            }
            else{
                Snacker("Add Some Key")
            }
        }

        myrec.setOnClickListener {
            val i = Intent(this,Recordings::class.java)
            startActivity(i)
        }

        val back: FloatingActionButton = findViewById(R.id.back)
        back.setOnClickListener{
            super.onBackPressed()
        }

    }




    private fun askIgnoreOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @SuppressLint("BatteryLife") val intent =
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    override fun onDestroy() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, ReactivateService::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

    fun Snacker(s:String){
        val root: ConstraintLayout = findViewById(R.id.root)
        val snack = Snackbar.make(root,s, BaseTransientBottomBar.LENGTH_LONG)
        snack.view.setBackgroundColor(Color.parseColor("#4447BD"))
        snack.setTextColor(Color.parseColor("#FFFFFF"))
        snack.show()
    }

    fun newPermisiion(activity: Activity){
        val permission2 =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_AUDIO,
                REQUEST_AUDIO
            )
        }
    }

}