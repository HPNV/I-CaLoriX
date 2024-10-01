package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import edu.bluejack23_1.ICaLoriX.R

class NotifAdapter(context: Context, private val data: Array<Pair<String, String>>) :
    ArrayAdapter<Pair<String, String>>(context, R.layout.notif_cv, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.notif_cv, parent, false)

        val headerText = itemView.findViewById<TextView>(R.id.notifHeader)
        val contentText = itemView.findViewById<TextView>(R.id.notifContent)

        val item = getItem(position)
        headerText.text = item?.first
        contentText.text = item?.second

        return itemView
    }
}