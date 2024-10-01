import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import edu.bluejack23_1.ICaLoriX.EditMealActivity
import edu.bluejack23_1.ICaLoriX.Home
import edu.bluejack23_1.ICaLoriX.R
import firestore.Firestore
import model.Food
import model.Global

class MealMenuAdapter(private val context: Context, private var dataList: MutableList<Food>) :
    RecyclerView.Adapter<MealMenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.MealCard)
        val imagePreview: ImageView = itemView.findViewById(R.id.imagePreview)
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodCal: TextView = itemView.findViewById(R.id.foodCal)
        val button: Button = itemView.findViewById(R.id.button)
        val logo:TextView = itemView.findViewById(R.id.logo_custom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_cv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        if (item.custom != "yes") {
            holder.logo.visibility = View.GONE
        }
        Glide.with(context).load(item.foodPic).apply(RequestOptions.bitmapTransform(CircleCrop())).into(holder.imagePreview)
        holder.foodName.text = item.name
        holder.foodCal.text = item.calories.toString() + " Cal/Serving"
        holder.button.text = "Add +"

        holder.cardView.setOnClickListener {

        }

        holder.button.setOnClickListener {
            showModal(item)
        }
    }

    private fun showModal(food: Food) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.meal_modal)

        val closeBtn = dialog.findViewById<ImageButton>(R.id.modal_closeBtn)
        val foodName = dialog.findViewById<TextView>(R.id.modal_title)
        val kal = dialog.findViewById<TextView>(R.id.modal_calories)
        val carb = dialog.findViewById<TextView>(R.id.modal_Carb)
        val fat = dialog.findViewById<TextView>(R.id.modal_Fat)
        val protein = dialog.findViewById<TextView>(R.id.modal_Protein)
        val editText = dialog.findViewById<EditText>(R.id.modal_edit_text)
        val header_cal = dialog.findViewById<TextView>(R.id.header_cal)

        Glide.with(context).load(food.foodPic).apply(RequestOptions.bitmapTransform(CircleCrop())).into(dialog.findViewById<ImageView>(R.id.modal_image_custom))
        foodName.text = food.name
        var quantity = food.serving

        editText.setText(food.serving.toString())
        val newKal = food.calories * quantity
        val newCarb = food.carb * quantity
        val newFat = food.fats * quantity
        val newProtein = food.proteins * quantity

        header_cal.text = newKal.toString() + " Kcal/serving"
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

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }


        val editBtn = dialog.findViewById<Button>(R.id.modal_edit)
        val deleteBtn = dialog.findViewById<Button>(R.id.modal_delete)

        editBtn.setOnClickListener {
            val intent = Intent(context, EditMealActivity::class.java)
            intent.putExtra("food", food)
            context.startActivity(intent)
            dialog.dismiss()
        }

        deleteBtn.setOnClickListener {
            val user = Firestore.getAuth().currentUser
            val collectionPath = "User/${user!!.email}/foods"
            Firestore.deleteDocumentById(collectionPath, food.foodID,
                onSuccess = {
                    showToast("Successfully deleted document with ID: ${food.foodID}")
                    val index = dataList.indexOf(food)
                    if (index != -1) {
                        dataList.removeAt(index)
                        notifyItemRemoved(index)
                    }
                    dialog.dismiss()
                },
                onFailure = { e ->
                    showToast("Error deleting document: $e")
                    dialog.dismiss()
                }
            )
        }

        val addBtn = dialog.findViewById<Button>(R.id.modal_addBtn)

        addBtn.setOnClickListener {
            val servingQuantity: Double = quantity

            food.serving = servingQuantity
            val user = Firestore.getAuth().currentUser
            val path = "User/${user!!.email}/${Global.choosenMeal}"
            Firestore.addToCollectionWithId(path, food.foodID, food)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(dataList: MutableList<Food>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun addData(newFood: Food) {
        dataList.add(newFood)
        notifyItemInserted(dataList.size - 1)
    }
}
