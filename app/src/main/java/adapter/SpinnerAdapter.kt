package adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import edu.bluejack23_1.ICaLoriX.R

class SpinnerAdapter(context: Context, resource: Int, items: Array<String>) : ArrayAdapter<String>(context, resource, items) {

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = super.getDropDownView(position, convertView, parent) as TextView

        if(position == 0) {
            val textColor = context.resources.getColor(R.color.dark_grey, context.theme)
            view.setTextColor(textColor)
        } else {
            val textColor = context.resources.getColor(R.color.black, context.theme)
            view.setTextColor(textColor)
        }
        return view
    }
}