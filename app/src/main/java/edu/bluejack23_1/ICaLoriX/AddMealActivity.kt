package edu.bluejack23_1.ICaLoriX

import MealMenuAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.databinding.ActivityAddMealBinding
import view_model.AddMealViewModel

class AddMealActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMealBinding
    private lateinit var nameEditText: EditText
    private lateinit var calorieEditText: EditText
    private lateinit var fatEditText: EditText
    private lateinit var proteinEditText: EditText
    private lateinit var carbEditText: EditText
    private lateinit var viewModel: AddMealViewModel
    private lateinit var addMealBtn: Button
    private lateinit var mealPhoto: ImageView;
    private lateinit var changeBtn: Button
    private lateinit var backBtn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nameEditText = binding.AddMealNameEditText
        calorieEditText = binding.AddMealCalorieEditText
        fatEditText = binding.AddMealFatEditText
        proteinEditText = binding.AddMealProteinEditText
        carbEditText = binding.AddMealCarbEditText
        addMealBtn = binding.AddMealBtn
        mealPhoto = binding.MealImagePreview
        changeBtn = binding.changeImageBtn
        backBtn = binding.backBtn

        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/i-calorix.appspot.com/o/default_meal.jpg?alt=media&token=9aeb2423-ce79-440e-ba1e-403392a3b380")
            .apply(RequestOptions.bitmapTransform(CircleCrop())).into(mealPhoto)
        
        viewModel = AddMealViewModel(this,nameEditText,calorieEditText,carbEditText,proteinEditText,fatEditText,addMealBtn,mealPhoto,changeBtn,backBtn)

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
}