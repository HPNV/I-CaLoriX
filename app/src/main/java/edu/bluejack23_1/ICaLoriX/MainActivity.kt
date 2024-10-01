package edu.bluejack23_1.ICaLoriX

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.Observer
import firestore.Firestore
import view_model.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = MainViewModel()

        viewModel.redirectLiveData.observe(this, Observer {
            if(it != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val i = Intent(
                        this@MainActivity,
                        it
                    )
                    finishAndRemoveTask()
                    startActivity(i)
                }, 2000)
            }
        })
    }
}