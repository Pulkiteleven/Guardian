package com.inertia.guardian.classes

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inertia.guardian.MainActivity
import com.inertia.guardian.R
import com.inertia.guardian.classes.ShakeDetector.OnShakeListener
import com.inertia.guardian.models.uris
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class SensorService: Service() {
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null
    var spc: SpeechRecognizer?= null
    var reci:Intent ?= null
    var recorder:MediaRecorder ?= null
    var s:String ?= null
    var fileName:String ?= null
    var uuid:String ?= null
    public var uriList:MutableList<uris> = ArrayList()
    var date:String ?= null
    var time:String ?= null




    fun SensorService() {}



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_BOOT_COMPLETED)
        registerReceiver(ReactivateService(),filter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground()
        }else{
            startForeground(1, Notification())
        }

        //Clicker
        // ShaeDetector initialization
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : OnShakeListener {
            @SuppressLint("MissingPermission")
            override fun onShake(count: Int) {
                // check if the user has shacked
                // the phone for 3 time in a row
                if (count >= 3) {
                    var sf:SharedPreferences = getSharedPreferences("keyword",MODE_PRIVATE)
                    s = sf.getString("keys","red")
                    vibrate()
                    listener()
                    spc?.startListening(reci)
                }
                if (count >= 5){
                    var sf:SharedPreferences = getSharedPreferences("keyword",MODE_PRIVATE)
                    var ii = sf.getInt("total",4)
                    vibrate()
                    Toast.makeText(this@SensorService, "Recording Started", Toast.LENGTH_SHORT).show()
                    loopRecorder(ii)
                }
            }
        })

        //register the listener
        mSensorManager?.registerListener(mShakeDetector,mAccelerometer,SensorManager.SENSOR_DELAY_UI)
    }

    public fun vibrate(){
        var vibrator:Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        var vibEff:VibrationEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            vibEff = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            vibrator.cancel()
            vibrator.vibrate(vibEff)
        }else{
            vibrator.vibrate(500)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground(){
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"

        var chan:NotificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,channelName,NotificationManager.IMPORTANCE_MIN
        )

//        var manger:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val notificationBuilder:NotificationCompat.Builder = NotificationCompat.Builder(
            this,NOTIFICATION_CHANNEL_ID
        )
        val notification:Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Guardain")
            .setContentText("I'm Your Guardain")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(2,notification)
    }

    override fun onDestroy() {

        val brodcastIntent = Intent()
        brodcastIntent.setAction("restartservice")
        brodcastIntent.setClass(this,ReactivateService::class.java)
        this.sendBroadcast(brodcastIntent)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    public fun listener(){
        reci = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        reci!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        spc = SpeechRecognizer.createSpeechRecognizer(this)
        spc?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
//                TODO("Not yet implemented")
            }

            override fun onBeginningOfSpeech() {
//                TODO("Not yet implemented")

            }

            override fun onRmsChanged(p0: Float) {
//                TODO("Not yet implemented")
            }

            override fun onBufferReceived(p0: ByteArray?) {
//                TODO("Not yet implemented")
            }

            override fun onEndOfSpeech() {
//                TODO("Not yet implemented")
            }

            override fun onError(p0: Int) {
//                TODO("Not yet implemented")
            }

            override fun onResults(p0: Bundle?) {
//                TODO("Not yet implemented")
                var sf:SharedPreferences = getSharedPreferences("keyword",MODE_PRIVATE)
                var ii = sf.getInt("total",4)
                var matches:ArrayList<String> ?= p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                var string:String = ""
                if (matches != null){
                    string = matches.get(0)
                    if (string.equals(s)) {
                        Toast.makeText(this@SensorService, string, Toast.LENGTH_SHORT).show()
                        loopRecorder(ii)
                    }
                }
            }

            override fun onPartialResults(p0: Bundle?) {
//                TODO("Not yet implemented")
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
//                TODO("Not yet implemented")
            }

        })
    }

    private fun startRecording(){
        uuid = UUID.randomUUID().toString()
       fileName = externalCacheDir?.absolutePath + "/" + uuid + ".mp3"
        date =  SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        time =  SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

//        val storage: File = File(Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOWNLOADS),"Guardian")
//        fileName = storage.toString() + "/" + uuid + ".mp3"


        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setOutputFile(fileName)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            recorder?.prepare()
        } catch (e: IOException) {
            Log.e(MainActivity::class.java.simpleName + ":startRecording()", "prepare() failed")
        }

        recorder?.start()
    }

    private fun stopRecording(){
        if (recorder != null){
            vibrate()
            recorder?.release()
            //sendfiles(Uri.parse(fileName))
            var a = date + " " + time
            Adder(uuid!!, fileName!!,a)
            recorder = null
        }
    }

    fun loopRecorder(i:Int){
        if (i > 0) {
            startRecording()
            Handler(Looper.getMainLooper()).postDelayed({
                stopRecording()
                loopRecorder(i - 1)
            }, 31000)
        }
    }

    fun Adder(uu:String,u:String,date_time:String){
        var sf:SharedPreferences = getSharedPreferences("uri", MODE_PRIVATE)
        var edit:SharedPreferences.Editor = sf.edit()

        val gsoon: Gson = Gson()

        val jsoon:String ?= sf.getString("uris",null)
        if (jsoon != null){
            val type: Type = object : TypeToken<MutableList<uris>>(){}.type
            uriList = gsoon.fromJson(jsoon,type)
        }
        val uuu:uris =  uris(uu,u,date_time)
        uriList.add(uuu)

        val gson:Gson = Gson()
        val json:String = gson.toJson(uriList)
        edit.putString("uris",json)
        edit.apply()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun sendfiles(uri: Uri){
//        var url:String = "api.whatsapp.com/send?phone=" + "919630916756"
        var a = "919630916756"
        var aa:String = "smsto:" + "" + a + "?body=" + ""

//        val i = Intent(Intent.ACTION_SEND)
//        i.setPackage("org.telegram.messenger")
//        i.setDataAndType(Uri.parse("telegram.me/@Sahilsarawagi"),"audio/*")
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        i.putExtra(Intent.EXTRA_STREAM, uri)
//
//        startActivity(i)

        val packageManager: PackageManager = this.getPackageManager()
        val i = Intent(Intent.ACTION_VIEW)


            val url =
                "api.whatsapp.com/send?phone=" + a
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            i.type = "audio/*"
            i.putExtra(Intent.EXTRA_STREAM,uri)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            startActivity(i)
                Toast.makeText(this, "HOGDIYa", Toast.LENGTH_SHORT).show()


    }

}
