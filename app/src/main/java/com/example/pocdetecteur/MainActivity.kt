package com.example.pocdetecteur

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {

    companion object {
        const val BOUGE_KEY = "BOUGE_KEY" // const key to save/read value from bundle
        const val STATIONNAIRE_KEY = "STATIONNAIRE_KEY" // const key to save/read value from bundle
    }

    private var bougeTxt = "Aucun mouvement detécté"
    private var stationnaireTxt = "Aucune station detectée"
    private var roger = this



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)



        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
            when (mSensor) {
                null -> {
                    bougeTxt = "No significant motion sensor available"
                }
            }
        //val mStationarySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT)
            /*val deviceSensors = sensorManager.getSensorList(Sensor.TYPE_SIGNIFICANT_MOTION)

            deviceSensors.forEach {
                stationnaireTxt += " _ " + it.name
            }*/
            val mStationarySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT)
        if(mStationarySensor == null)
        {
            stationnaireTxt = "No stationary sensor available"
        }

        val myTextView = findViewById<TextView>(R.id.textView)
        val myButton = findViewById<Button>(R.id.button)

        // Now you can use 'myTextView' to manipulate the TextView
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())
        myTextView.text = "Application démarrée à : " + currentDate
        val tvBouge = findViewById<TextView>(R.id.tvBouge)
        tvBouge.text = bougeTxt

        val tvStationnaire = findViewById<TextView>(R.id.tvStationnaire)
        tvStationnaire.text = stationnaireTxt


            val triggerEventListener = object : TriggerEventListener() {
                override fun onTrigger(event: TriggerEvent?) {
                   val sdf = SimpleDateFormat("hh:mm:ss")
                    val currentDate = sdf.format(Date())
                    if(bougeTxt == "Aucun mouvement detécté")
                    {
                        bougeTxt = ""
                    }
                    if(!tvBouge.text.toString().contains(currentDate)) {
                        bougeTxt += currentDate + " _ "
                        tvBouge.text = bougeTxt

                        val builder = NotificationCompat.Builder(roger, "channel_id")
                            .setSmallIcon(android.R.drawable.stat_notify_chat)
                            .setContentTitle("New message")
                            .setContentText(bougeTxt)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                        // Create a notification manager object
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(43, builder.build())
                    }


                    //sensorManager.cancelTriggerSensor(this, mSensor)
                    //sensorManager.requestTriggerSensor(this, mSensor)
                    //mSensor?.also { sensor ->
                        //sensorManager.requestTriggerSensor(this, sensor)}

                }
            }

            val triggerStationnaireEventListener = object : TriggerEventListener() {
                override fun onTrigger(event: TriggerEvent?) {
                    // Do work
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())
                    stationnaireTxt = "Arret detecté ! à : " + currentDate
                    tvStationnaire.text = stationnaireTxt
                    mStationarySensor?.also { statSensor ->
                        sensorManager.requestTriggerSensor(
                            this,
                            statSensor
                        )

                    }
                }
            }
            mSensor?.also { sensor ->
                sensorManager.requestTriggerSensor(triggerEventListener, sensor)}

            mStationarySensor?.also { statSensor ->
                    sensorManager.requestTriggerSensor(triggerStationnaireEventListener, statSensor)
                }

            myButton.setOnClickListener {
                myTextView.text = "Bouton cliqué"

                // Create a notification builder object
                val builder = NotificationCompat.Builder(this, "channel_id")
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentTitle("New message")
                    .setContentText("You have a new message.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Create a notification manager object
                val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                // Check if the device is running Android Oreo or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create a notification channel
                    val channel = NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
                    channel.description = "Channel Description"
                    // Register the channel with the system
                    notificationManager.createNotificationChannel(channel)
                }
                // To see the message in logcat
                Log.i("Notify","$builder")
                // Issue the notification
                notificationManager.notify(42, builder.build())
            }


            insets
        }
    }


    override fun onSaveInstanceState(outState: Bundle) { // Here You have to save count value
        super.onSaveInstanceState(outState)
        outState.putString(BOUGE_KEY, bougeTxt)
        outState.putString(STATIONNAIRE_KEY, stationnaireTxt)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // Here You have to restore count value
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("MyTag", "onRestoreInstanceState")

        bougeTxt = savedInstanceState.getString(BOUGE_KEY).toString()
        stationnaireTxt = savedInstanceState.getString(STATIONNAIRE_KEY).toString()

    }


}