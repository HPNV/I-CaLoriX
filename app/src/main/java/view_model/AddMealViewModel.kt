package view_model

import MealMenuAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.AddMealActivity
import edu.bluejack23_1.ICaLoriX.HomeContainerActivity
import firestore.Firestore
import model.Food
import model.FoodManager


class AddMealViewModel(
    private val activity: AddMealActivity,
    private val nameEditText: EditText,
    private val calorieEditText: EditText,
    private val carbEditText: EditText,
    private val proteinEditText: EditText,
    private val fatEditText:EditText,
    private val addBtn:Button,
    private val mealView: ImageView,
    private val mealImageButton: Button,
    private val backBtn: ImageButton,
) {
    private var name = ""
    private var calorie = 0.0
    private var carb = 0.0
    private var protein = 0.0
    private var fat = 0.0
    val menuAdapter: MealMenuAdapter = MealMenuAdapter(activity, emptyList<Food>().toMutableList())

    val errorLiveData = MutableLiveData<String>()
    val redirectLiveData = MutableLiveData<Class<*>>()

    private val gallery = Intent(Intent.ACTION_PICK)
    var selectedImage:Uri? = null
    private val imageLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImage = result.data?.data
            mealView.setImageURI(selectedImage)
            Firestore.uploadImage("addPreview",selectedImage!!, {
                Glide.with(activity).load(it)
                    .apply(RequestOptions.bitmapTransform(CircleCrop())).into(mealView)
            }, {})
        }
    }

    init {
        nameEditText.addTextChangedListener {
            name = it.toString()
        }

        calorieEditText.addTextChangedListener {
            calorie = it.toString().toDouble()
        }

        proteinEditText.addTextChangedListener {
            protein = it.toString().toDouble()
        }

        carbEditText.addTextChangedListener {
            carb = it.toString().toDouble()
        }

        fatEditText.addTextChangedListener {
            fat = it.toString().toDouble()
        }

        mealImageButton.setOnClickListener {
            gallery.type = "image/*"
            imageLauncher.launch(gallery)
        }

        backBtn.setOnClickListener {
            redirectLiveData.value = HomeContainerActivity::class.java
        }


        addBtn.setOnClickListener {
            if (name.isEmpty() || calorieEditText.text.equals("") || proteinEditText.text.equals("") || carbEditText.text.equals("") || carbEditText.text.equals("")) {
                errorLiveData.value = "Please fill all the fields!"
            } else {
                if (selectedImage != null) {
                    FoodManager.addFood(name, selectedImage!!, calorie, carb, protein, fat) { food ->
                        menuAdapter.addData(food)
                    }
                    errorLiveData.value = "$name has been added!"
                    redirectLiveData.value = HomeContainerActivity::class.java
                } else {
                    errorLiveData.value = "Image is not selected!"
                }
            }
//            errorLiveData.value = "Error"
        }
    }
}
