package edu.bluejack23_1.ICaLoriX

import service.BackgroundService
import adapter.ViewPagerAdapter
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import edu.bluejack23_1.ICaLoriX.databinding.ActivityHomeContainerBinding
import view_model.HomeContainerViewModel
import kotlin.math.sqrt

class HomeContainerActivity : AppCompatActivity(){
    private lateinit var binding: ActivityHomeContainerBinding
    private lateinit var viewModel: HomeContainerViewModel
    private lateinit var homeButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var viewPager2: ViewPager2
    private var totalStep = 0
    private var accelerometerValues = FloatArray(3)
    private var threshold = 1.5f
    private var lastAcceleration = 0.0

    private var fragments = listOf<Fragment>(
        Home(),
        MealMenu(),
        HomeProfileFragment(),
        EditProfileFragment(),
        HistoryFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleJob()
        binding = ActivityHomeContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeButton = binding.btnHome
        historyButton = binding.btnHistory
        profileButton = binding.btnProfile
        viewPager2 = binding.viewPager
        val pagerAdapter = ViewPagerAdapter(this,fragments)
        viewPager2.adapter = pagerAdapter
        viewPager2.isUserInputEnabled = false

        viewModel = HomeContainerViewModel(homeButton,historyButton,profileButton,fragments,viewPager2)

        viewModel.redirectLiveData.observe(this, Observer {
            if(it != null) {
                val intent = Intent(this, it)
                finish()
                startActivity(intent)
            }
        })

        viewModel.errorLiveData.observe(this, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun scheduleJob() {
        val componentName = ComponentName(this, BackgroundService::class.java)

        val targetTime = Calendar.getInstance()
        targetTime.set(Calendar.HOUR_OF_DAY, 0)
        targetTime.set(Calendar.MINUTE, 5)
        targetTime.set(Calendar.SECOND, 0)

        val currentTime = Calendar.getInstance()
        var initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
        if (initialDelay < 0) {
            targetTime.add(Calendar.HOUR_OF_DAY, 24)
            initialDelay = targetTime.timeInMillis - currentTime.timeInMillis
        }

        val jobInfo = JobInfo.Builder(123, componentName)
            .setMinimumLatency(initialDelay)
            .setPersisted(true)
            .build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }
}