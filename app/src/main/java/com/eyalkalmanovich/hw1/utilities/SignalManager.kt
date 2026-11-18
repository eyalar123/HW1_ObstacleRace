package com.eyalkalmanovich.hw1.utilities

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

/** Toast + vibration notifications. */
class SignalManager(context: Context) {

    private val appContext = context.applicationContext
    private var lastToast: Toast? = null

    fun toast(text: String) {
        lastToast?.cancel()
        lastToast = Toast.makeText(appContext, text, Toast.LENGTH_SHORT).also { it.show() }
    }

    fun vibrate(durationMs: Long = Constants.Signals.VIBRATION_MS) {
        val vibrator: Vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
