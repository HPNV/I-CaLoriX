package view_model

import android.widget.Button
import android.widget.ImageButton
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import edu.bluejack23_1.ICaLoriX.AddMealActivity
import edu.bluejack23_1.ICaLoriX.Home
import edu.bluejack23_1.ICaLoriX.MealMenu

class HomeContainerViewModel {
    private lateinit var homeFragment:Home
    private lateinit var viewPager: ViewPager2
    private lateinit var fragments: List<Fragment>
    val errorLiveData = MutableLiveData<String>()
    val redirectLiveData = MutableLiveData<Class<*>>()
    constructor(homeBtn: ImageButton, historyBtn: ImageButton, profileBtn:ImageButton, fragments:List<Fragment>, viewPager: ViewPager2) {
        this.fragments = fragments
        this.viewPager = viewPager

        homeBtn.setOnClickListener {
            viewPager.setCurrentItem(0, false)
            val home = fragments.get(0) as Home
            home.refreshData()
        }

        profileBtn.setOnClickListener {
            viewPager.setCurrentItem(2, false)
        }

        historyBtn.setOnClickListener {
            viewPager.setCurrentItem(4, false)
        }

        this.fragments = fragments
        this.viewPager = viewPager
    }
}