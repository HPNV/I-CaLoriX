package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.R
import model.Food

class MealHistoryAdapter(private val context: Context, private var dataList: MutableList<Food>) :
    RecyclerView.Adapter<MealHistoryAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        Glide.with(context).load(item.foodPic).apply(RequestOptions.bitmapTransform(CircleCrop())).into(holder.imageView)
        holder.nameTextView.text = item.name
        holder.foodKalTextView.text = item.serving.toString() + " Serving"
        val total = item.calories * item.serving
        holder.totalKalTextView.text = "$total Calories"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(dataList: MutableList<Food>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }
}