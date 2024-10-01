package edu.bluejack23_1.ICaLoriX

import adapter.NotifAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import edu.bluejack23_1.ICaLoriX.databinding.ActivityNotifBinding

class NotifActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotifBinding
    private lateinit var notifView:ListView
    private lateinit var backBtn:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notifView = binding.list
        backBtn = binding.backBtn

        backBtn.setOnClickListener {
            val intent = Intent(this, HomeContainerActivity::class.java)
            startActivity(intent)
        }

        val notifData = arrayOf(
            Pair("Header 1", "Konten 1"),
            Pair("Header 2", "Konten 2"),
            Pair("Header 3", "Konten 3")
        )

        val adapter = NotifAdapter(this, notifData)
        notifView.adapter = adapter

        notifView.adapter = adapter
    }
}