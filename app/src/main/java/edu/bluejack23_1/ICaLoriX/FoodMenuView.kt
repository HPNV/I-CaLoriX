package edu.bluejack23_1.ICaLoriX

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.ICaLoriX.databinding.ActivityFoodMenuViewBinding
import model.Food
import kotlin.math.sqrt

class FoodMenuView : AppCompatActivity(), SensorEventListener {
    lateinit var binding: ActivityFoodMenuViewBinding
    lateinit var recyclerView: RecyclerView
    lateinit var textView: TextView
    private var sensonManager: SensorManager? = null
    private var running = false
    private var totalStep = 0
    private var accelerometerValues = FloatArray(3)
    private var threshold = 1.5f
    private var lastAcceleration = 0.0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodMenuViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textView = binding.tes
        sensonManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private fun tes(word: String) {
        Toast.makeText(this, "$word", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        running = true
        val accelerometerSensor: Sensor? = sensonManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometerSensor == null) {
            Toast.makeText(this, "No accelerometer sensor detected", Toast.LENGTH_SHORT).show()
        } else {
            tes("Accelerometer sensor detected")
            sensonManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensonManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running && event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone()
            handleAccelerometerData(accelerometerValues)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun handleAccelerometerData(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]
        val acceleration = sqrt(x * x + y * y + z * z.toDouble())

        tes("$acceleration")

        if (acceleration - lastAcceleration > threshold) {
            totalStep++
            textView.text = "Total Steps: $totalStep"
        }

        lastAcceleration = acceleration
    }
}
