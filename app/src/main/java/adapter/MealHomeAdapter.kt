import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.R
import firestore.Firestore
import model.Food
import model.Global

class MealHomeAdapter(private val context: Context, private var dataList: MutableList<Food>) :
    RecyclerView.Adapter<MealHomeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.meal_home_cv)
        val imageView: ImageView = itemView.findViewById(R.id.meal_home_image)
        val nameTextView: TextView = itemView.findViewById(R.id.meal_home_foodName)
        val foodKalTextView: TextView = itemView.findViewById(R.id.meal_home_foodKal)
        val totalKalTextView: TextView = itemView.findViewById(R.id.meal_hom_totalFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_home_cv, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        Glide.with(context).load(item.foodPic).apply(RequestOptions.bitmapTransform(CircleCrop())).into(holder.imageView)
        holder.nameTextView.text = item.name
        holder.foodKalTextView.text = item.serving.toString() + " serving(s)"
        val total = item.calories * item.serving
        holder.totalKalTextView.text = total.toString()

        holder.cardView.setOnClickListener {
            showModal(item)
        }
    }

    private fun showModal(food:Food) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.meal_home_modal)

        Glide.with(context).load(food.foodPic).apply(RequestOptions.bitmapTransform(CircleCrop())).into(dialog.findViewById<ImageView>(R.id.modal_home_image_custom))

        val closeBtn = dialog.findViewById<ImageButton>(R.id.modal_home_closeBtn)
        val foodName = dialog.findViewById<TextView>(R.id.modal_home_title)
        val kal = dialog.findViewById<TextView>(R.id.modal_home_calories)
        val carb = dialog.findViewById<TextView>(R.id.modal_home_Carb)
        val fat = dialog.findViewById<TextView>(R.id.modal_home_Fat)
        val protein = dialog.findViewById<TextView>(R.id.modal_home_Protein)
        val editText = dialog.findViewById<EditText>(R.id.modal_home_edit_text)
        val headCal = dialog.findViewById<TextView>(R.id.header_cal)

        foodName.text = food.name

        var quantity = food.serving

        editText.setText(food.serving.toString())
        val newKal = food.calories * quantity
        val newCarb = food.carb * quantity
        val newFat = food.fats * quantity
        val newProtein = food.proteins * quantity

        headCal.text = newKal.toString() + " Kcal/serving"
        kal.text = newKal.toString() + " Kcal"
        carb.text = newCarb.toString() + " g"
        fat.text = newFat.toString() + " g"
        protein.text = newProtein.toString() + " g"

        editText.addTextChangedListener {
            quantity = it.toString().toDoubleOrNull() ?: 1.0

            val newKal = food.calories * quantity
            val newCarb = food.carb * quantity
            val newFat = food.fats * quantity
            val newProtein = food.proteins * quantity

            kal.text = newKal.toString() + " Kcal"
            carb.text = newCarb.toString() + " g"
            fat.text = newFat.toString() + " g"
            protein.text = newProtein.toString() + " g"
        }

        val removeBtn = dialog.findViewById<Button>(R.id.modal_home_remove)
        val saveBtn = dialog.findViewById<Button>(R.id.modal_home_save)
        val exitBtn = dialog.findViewById<ImageButton>(R.id.modal_home_closeBtn)

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        removeBtn.setOnClickListener {
            val user = Firestore.getAuth().currentUser
            var path = "User/${user!!.email}/${Global.choosenMeal}"

            Firestore.deleteDocumentById(path, food.foodID,onSuccess = {
                dataList.remove(food)
                notifyDataSetChanged()
                dialog.dismiss()
            }, onFailure = {
                showToast("Error when deleting food: ${food.foodID}")
                dialog.dismiss()
            })

            dataList.remove(food)
            notifyDataSetChanged()
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            val newQuantity = editText.text.toString().toDoubleOrNull() ?: 1.0
            food.serving = newQuantity

            val user = Firestore.getAuth().currentUser
            var path = "User/${user!!.email}/${Global.choosenMeal}"
            Firestore.addToCollectionWithId(path,food.foodID,food)
            notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(newFood: Food) {
        dataList.add(newFood)
        notifyItemInserted(dataList.size - 1)
    }

    fun setData(dataList: MutableList<Food>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
