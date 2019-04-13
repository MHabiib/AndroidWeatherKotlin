package com.example.simpleweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.firebase.jobdispatcher.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val Tag = "MyDispatcherTag1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startJob.setOnClickListener { startDispatcher() }
        cancelJob.setOnClickListener { cancelDispatcher() }

        val kota = arrayOf("Medan", "Jakarta")

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,kota)

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinnerKota.adapter = adapter
    }
    private fun startDispatcher(){
        val mFirebaseJobDispatcher = FirebaseJobDispatcher( GooglePlayDriver(applicationContext));
        var myJobDispatcher = mFirebaseJobDispatcher.newJobBuilder()
            .setService(CuacaHariIni::class.java)
            .setTag(Tag)
            .setRecurring(true)
            .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
            .setTrigger(Trigger.executionWindow(0,60))
            .setReplaceCurrent(true)
            .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
            .setConstraints(Constraint.ON_ANY_NETWORK)
            .build()
        mFirebaseJobDispatcher.mustSchedule(myJobDispatcher)
        Toast.makeText(this,"Job Dispatcher Berjalan",
        Toast.LENGTH_SHORT).show()
    }
    private fun cancelDispatcher(){
        var mFirebaseJobDispatcher = FirebaseJobDispatcher(GooglePlayDriver(applicationContext))
        mFirebaseJobDispatcher.cancel(Tag)
        Toast.makeText(this,"Job Dispatcher Berhenti",
        Toast.LENGTH_SHORT).show()
    }

}
