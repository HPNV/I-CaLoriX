package edu.bluejack23_1.ICaLoriX

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.databinding.ActivityAddMealBinding
import edu.bluejack23_1.ICaLoriX.databinding.ActivityEditMealBinding
import model.Food
import view_model.AddMealViewModel
import view_model.EditMealViewModel

class EditMealActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMealBinding
    private lateinit var nameEditText: EditText
    private lateinit var calorieEditText: EditText
    private lateinit var fatEditText: EditText
    private lateinit var proteinEditText: EditText
    private lateinit var carbEditText: EditText
    private lateinit var addMealBtn: Button
    private lateinit var mealPhoto: ImageView
    private lateinit var changeBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var viewModel: EditMealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val food: Food? = intent.getSerializableExtra("food") as? Food

        nameEditText = binding.AddMealNameEditText
        calorieEditText = binding.AddMealCalorieEditText
        fatEditText = binding.AddMealFatEditText
        proteinEditText = binding.AddMealProteinEditText
        carbEditText = binding.AddMealCarbEditText
        addMealBtn = binding.AddMealBtn
        mealPhoto = binding.MealImagePreview
        changeBtn = binding.changeImageBtn
        backBtn = binding.backBtn

        viewModel = EditMealViewModel(
            this,
            nameEditText,
            calorieEditText,
            carbEditText,
            proteinEditText,
            fatEditText,
            addMealBtn,
            mealPhoto,
            changeBtn,
            backBtn,
            food!!.foodID
        )

        if (food != null) {
            nameEditText.setText(food.name)
            calorieEditText.setText(food.calories.toString())
            fatEditText.setText(food.fats.toString())
            proteinEditText.setText(food.proteins.toString())
            carbEditText.setText(food.carb.toString())
            Glide.with(this).load(food.foodPic)
                .apply(RequestOptions.bitmapTransform(CircleCrop())).into(mealPhoto)
        }

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
