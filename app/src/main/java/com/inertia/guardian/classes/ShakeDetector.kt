package com.inertia.guardian.classes

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector:SensorEventListener {
    private var shake_gravity:Float = 2.7F
    private var time_ms:Int = 500
    private var shake_count:Int = 3000

//    private var mListenr:ShakeDetector ?= null
    private var mListener:OnShakeListener ?= null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0

    fun setOnShakeListener(listener: OnShakeListener?) {
        mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }



    override fun onSensorChanged(p0: SensorEvent?) {
        if (mListener != null){
            var x:Float = p0!!.values[0]
            var y:Float = p0!!.values[1]
            var z:Float = p0!!.values[2]

            var gx:Float = x/SensorManager.GRAVITY_EARTH
            var gy:Float = y/SensorManager.GRAVITY_EARTH
            var gz:Float = z/SensorManager.GRAVITY_EARTH

            var f:Float = gx*gx + gy*gy + gz*gz
            var  d:Double = Math.sqrt(f.toDouble())
            var gForce:Float = d.toFloat()

            if (gForce > shake_gravity){
                var now:Long = System.currentTimeMillis()

                if (mShakeTimestamp + time_ms > now){
                    return
                }

                if (mShakeTimestamp + shake_count < now){
                    mShakeCount = 0
                }
                mShakeTimestamp = now
                mShakeCount++

                mListener!!.onShake(mShakeCount)
            }

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        TODO("Not yet implemented")
    }

}