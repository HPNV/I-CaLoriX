package edu.bluejack23_1.ICaLoriX

import MealHomeAdapter
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import edu.bluejack23_1.ICaLoriX.databinding.FragmentHomeBinding
import firestore.Firestore
import model.Food
import model.Global
import service.MealService
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var addBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealHomeAdapter: MealHomeAdapter
    private lateinit var breakfastBtn: Button
    private lateinit var lunchBtn: Button
    private lateinit var dinnerBtn: Button
    private lateinit var currentWeight:TextView
    private lateinit var targetWeight:TextView
    private lateinit var currentDate:TextView
    private lateinit var username:TextView

    private lateinit var stepTracker:TextView
    private lateinit var targetStep: TextView
    private lateinit var targetCalorie:TextView
    private lateinit var currentCalorie:TextView

    private lateinit var targetCarb: TextView
    private lateinit var targetProtein: TextView
    private lateinit var targetFats: TextView
    private lateinit var currCarb: TextView
    private lateinit var currProtein: TextView
    private lateinit var currFats: TextView

    private lateinit var progressCarb: ProgressBar
    private lateinit var progressProtein: ProgressBar
    private lateinit var progressFat: ProgressBar
    private lateinit var progressCal: ProgressBar
    private lateinit var stepProgress: ProgressBar

    private var breakfast = mutableListOf<Food>()
    private var lunch = mutableListOf<Food>()
    private var dinner = mutableListOf<Food>()

    private var totalStep = 0
    private var accelerometerValues = FloatArray(3)
    private var threshold = 1.5f
    private var lastAcceleration = 0.0

    private var currentCalorieValue = 0.0
    private var currCarbValue = 0.0
    private var currProteinValue = 0.0
    private var currFatsValue = 0.0

    private var targetStepVal = 0.0
    private var targetCalories = 0.0
    private var targetCarbVal = 0.0
    private var targetProteinVal = 0.0
    private var targetFatVal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mealHomeAdapter = MealHomeAdapter(requireContext(), mutableListOf())
        refreshData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        addBtn = binding.addBtn
        recyclerView = binding.homeRecylerView
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mealHomeAdapter
        breakfastBtn = binding.homeBreakfast
        lunchBtn = binding.homeLunch
        dinnerBtn = binding.homeDinner
        currentWeight = binding.userWeight
        targetWeight = binding.targetWeight
        currentDate = binding.currentDate
        username = binding.username

        targetCarb = binding.targetCarbs
        targetProtein = binding.targetProtein
        targetFats = binding.targetFats
        currCarb = binding.currCarbs
        currProtein = binding.currProtein
        currFats = binding.currFats

        progressCarb = binding.progressCarb
        progressProtein = binding.progressProtein
        progressFat = binding.progressFat

        targetCalorie = binding.targetCalorie
        currentCalorie = binding.currentCalorie
        targetStep = binding.targetStep
        stepTracker = binding.stepTracker

        progressCal = binding.progressCal
        stepProgress = binding.stepProgress

        val sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            showToast("Accelerometer sensor not available.")
        }

        breakfastBtn.setOnClickListener {
            Global.choosenMeal = "breakfast"
            mealHomeAdapter.setData(breakfast)
            setButtonStyle(breakfastBtn,lunchBtn,dinnerBtn,"breakfast")
        }
        lunchBtn.setOnClickListener {
            Global.choosenMeal = "lunch"
            mealHomeAdapter.setData(lunch)
            setButtonStyle(breakfastBtn,lunchBtn,dinnerBtn,"lunch")
        }
        dinnerBtn.setOnClickListener {
            Global.choosenMeal = "dinner"
            mealHomeAdapter.setData(dinner)
            setButtonStyle(breakfastBtn,lunchBtn,dinnerBtn,"dinner")
        }
        addBtn.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.adapter?.notifyItemChanged(1)
            viewPager?.currentItem = 1
        }

        val user = model.UserManager.userData
        if (user != null) {
            username.text = "Hi, " + user.name
            currentWeight.text = user.weight.toInt().toString()

            if (user.goal == "Maintain Weight") {
                targetWeight.text = currentWeight.text
                targetStepVal = 10000.0
                targetStep.text = targetStepVal.toInt().toString()

            } else if (user.goal == "Weight Gain") {
                val height = user.height
                val target = (height - 100) + ((height - 100) * 13 / 100)
                targetWeight.text = target.toInt().toString()
                targetStepVal = 10000.0
                targetStep.text = targetStepVal.toInt().toString()

            } else if (user.goal == "Weight Loss") {
                val height = user.height
                val target = (height - 100) - ((height - 100) * 13 / 100)
                targetWeight.text = target.toInt().toString()
                targetStepVal = 15000.0
                targetStep.text = targetStepVal.toInt().toString()
            }

            targetCalories = getCaloriePerDay(user.weight,user.height,user.gender,user.dob)
            val targetNutrients = getTargetMacronutrients(targetCalories)
            targetCarbVal = targetNutrients.first
            targetProteinVal = targetNutrients.second
            targetFatVal = targetNutrients.third

            targetCalorie.text = Math.floor(targetCalories).toInt().toString()
            targetCarb.text = targetCarbVal.toInt().toString() + "g"
            targetProtein.text = targetProteinVal.toInt().toString() + "g"
            targetFats.text = targetFatVal.toInt().toString() + "g"
        }
        currentDate.text = getCurrentDateFormatted()
        return binding.root
    }

    private fun updateCalorieText() {
        currentCalorieValue = 0.0
        currCarbValue = 0.0
        currProteinValue = 0.0
        currFatsValue = 0.0

        for (food in dinner) {
            currentCalorieValue += food.calories * food.serving
            currCarbValue += food.carb * food.serving
            currProteinValue += food.proteins * food.serving
            currFatsValue += food.fats * food.serving
        }
        for (food in lunch) {
            currentCalorieValue += food.calories * food.serving
            currCarbValue += food.carb * food.serving
            currProteinValue += food.proteins * food.serving
            currFatsValue += food.fats * food.serving
        }
        for (food in breakfast) {
            currentCalorieValue += food.calories * food.serving
            currCarbValue += food.carb * food.serving
            currProteinValue += food.proteins * food.serving
            currFatsValue += food.fats * food.serving
        }
        currentCalorie.text = currentCalorieValue.toInt().toString()
        currCarb.text = currCarbValue.toInt().toString() + "/"
        currProtein.text = currProteinValue.toInt().toString() + "/"
        currFats.text = currFatsValue.toInt().toString() + "/"

        updateProgressBar(progressCal, currentCalorieValue, targetCalories)
        updateProgressBar(progressCarb, currCarbValue, targetCarbVal)
        updateProgressBar(progressProtein, currProteinValue, targetProteinVal)
        updateProgressBar(progressFat, currFatsValue, targetFatVal)
    }

    private fun updateProgressBar(progressBar: ProgressBar, currentValue: Double, targetValue: Double) {
        val progress = ((currentValue / targetValue) * 100).toInt()
        progressBar.progress = progress
    }

    fun getCurrentDateFormatted(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)
        return dateFormat.format(currentDate)
    }

    fun getCaloriePerDay(weight: Double, height: Double, gender: String, dob:String): Double {
        val age = getAgeFromDob(dob)

        val bmr = if (gender.equals("Male", ignoreCase = true)) {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
        val activityFactor = 1.55
        val calorie = (bmr * activityFactor)
        scheduleJob(currentCalorieValue,calorie)
        return calorie
    }

    fun getTargetMacronutrients(targetCalories: Double): Triple<Double, Double, Double> {
        val targetCarbs = 0.45 * targetCalories / 4
        val targetProteins = 0.20 * targetCalories / 4
        val targetFats = 0.35 * targetCalories / 9
        return Triple(targetCarbs, targetProteins, targetFats)
    }

    private fun scheduleJob(curCal: Double, tarCal: Double) {
        val componentName = ComponentName(requireContext(), MealService::class.java)

        val targetTime1 = Calendar.getInstance()
        targetTime1.set(Calendar.HOUR_OF_DAY, 10)
        targetTime1.set(Calendar.MINUTE, 52)
        targetTime1.set(Calendar.SECOND, 0)

        val targetTime2 = Calendar.getInstance()
        targetTime2.set(Calendar.HOUR_OF_DAY, 13)
        targetTime2.set(Calendar.MINUTE, 0)
        targetTime2.set(Calendar.SECOND, 0)

        val targetTime3 = Calendar.getInstance()
        targetTime3.set(Calendar.HOUR_OF_DAY, 18)
        targetTime3.set(Calendar.MINUTE, 0)
        targetTime3.set(Calendar.SECOND, 0)

        val currentTime = Calendar.getInstance()
        var initialDelay: Long

        if (currentTime.before(targetTime1)) {
            initialDelay = targetTime1.timeInMillis - currentTime.timeInMillis
        } else if (currentTime.before(targetTime2)) {
            initialDelay = targetTime2.timeInMillis - currentTime.timeInMillis
        } else if (currentTime.before(targetTime3)) {
            initialDelay = targetTime3.timeInMillis - currentTime.timeInMillis
        } else {
            targetTime1.add(Calendar.DAY_OF_MONTH, 1)
            initialDelay = targetTime1.timeInMillis - currentTime.timeInMillis
        }

        val jobInfo1 = JobInfo.Builder(123, componentName)
            .setMinimumLatency(initialDelay)
            .setExtras(PersistableBundle().apply {
                putDouble("curCal", curCal)
                putDouble("tarCal", tarCal)
            })
            .setPersisted(true)
            .build()

        val jobScheduler = requireContext().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo1)
    }

    fun getAgeFromDob(dob: String): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID"))
        val currentDate = Date()
        val birthDate = dateFormat.parse(dob)
        val calendarCurrent = Calendar.getInstance()
        val calendarBirth = Calendar.getInstance()

        calendarCurrent.time = currentDate
        calendarBirth.time = birthDate!!

        var age = calendarCurrent.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR)
        if (calendarCurrent.get(Calendar.DAY_OF_YEAR) < calendarBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    fun refreshData() {
        val user = Firestore.getAuth().currentUser
        val path1 = "User/${user!!.email}/breakfast"
        Firestore.getCollection<Food>(path1, onSuccess = {
            breakfast.clear()
            breakfast.addAll(it)
            if (Global.choosenMeal == "breakfast") {
                mealHomeAdapter.setData(breakfast)
            }
            updateCalorieText()
        })

        val path2 = "User/${user!!.email}/lunch"
        Firestore.getCollection<Food>(path2, onSuccess = {
            lunch.clear()
            lunch.addAll(it)
            if (Global.choosenMeal == "lunch") {
                mealHomeAdapter.setData(lunch)
            }
            updateCalorieText()
        })

        val path3 = "User/${user!!.email}/dinner"
        Firestore.getCollection<Food>(path3, onSuccess = {
            dinner.clear()
            dinner.addAll(it)
            if (Global.choosenMeal == "dinner") {
                mealHomeAdapter.setData(dinner)
            }
            updateCalorieText()
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setButtonStyle(breakButton: Button, lunchButton: Button, dinnerButton: Button,choose:String) {
        if (choose == "breakfast") {

        } else if(choose == "lunch"){

        } else {

        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("totalStep", totalStep)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        totalStep = sharedPreferences.getInt("totalStep", 0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
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
        if (acceleration - lastAcceleration > threshold) {
            totalStep++
            stepTracker.text = totalStep.toString()
            updateProgressBar(stepProgress, totalStep.toDouble(), targetStepVal)
        }
        lastAcceleration = acceleration
    }
}