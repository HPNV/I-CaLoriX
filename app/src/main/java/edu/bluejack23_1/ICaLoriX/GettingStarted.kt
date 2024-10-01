package edu.bluejack23_1.ICaLoriX

import adapter.ViewPagerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import edu.bluejack23_1.ICaLoriX.databinding.ActivityGettingStartedBinding
import edu.bluejack23_1.ICaLoriX.databinding.ActivityLoginBinding
import view_model.GettingStartedViewModel


class GettingStarted : AppCompatActivity() {

    private lateinit var binding: ActivityGettingStartedBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var nextBtn: Button
    private lateinit var viewModel: GettingStartedViewModel

    private var fragments = listOf<Fragment>(
        GSContent1(),
        GSContent2(),
        GSContent3()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGettingStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        nextBtn = binding.nextBtn

        val viewPagerAdapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = viewPagerAdapter
        viewPager.isUserInputEnabled = false

        viewModel = GettingStartedViewModel(viewPager,nextBtn,fragments)

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