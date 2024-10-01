package view_model

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import edu.bluejack23_1.ICaLoriX.FoodMenuView
import edu.bluejack23_1.ICaLoriX.GSContent1
import edu.bluejack23_1.ICaLoriX.GSContent2
import edu.bluejack23_1.ICaLoriX.GSContent3
import edu.bluejack23_1.ICaLoriX.HomeContainerActivity
import edu.bluejack23_1.ICaLoriX.R
import model.User
import model.UserManager
import java.util.Calendar

class GettingStartedViewModel {
    private var weight = 0.0
    private var height = 0.0
    private var gender = ""
    private var dob = ""
    var goal = ""
    val errorLiveData = MutableLiveData<String>()
    val redirectLiveData = MutableLiveData<Class<*>>()

    constructor(viewPager: ViewPager2,nextBtn:Button,fragments: List<Fragment>){
        var currentItem = viewPager.currentItem

        fun next() {
            if (currentItem < fragments.size - 1) {
                currentItem += 1
                viewPager.setCurrentItem(currentItem,false)
            }
        }

        nextBtn.setOnClickListener {
            if (currentItem == 1) {
                val gsContent2Fragment = fragments[currentItem] as GSContent2

                gender = gsContent2Fragment.getGender()
                height = gsContent2Fragment.getHeight()
                weight = gsContent2Fragment.getWeight()
                dob = gsContent2Fragment.getDob()

                if (gender == "Gender"|| height == 0.0 || weight == 0.0 || dob == "Date of birth") {
                    errorLiveData.value = "Please fill all the fields"
                } else {
                    next()
                }
            } else if (currentItem == 2) {
                val gsContent3Fragment = fragments[currentItem] as GSContent3
                goal = gsContent3Fragment.getGoal()
                errorLiveData.value = "$goal"
                if(goal.isNotEmpty()) {
                    UserManager.getUserData {
                        val user = it as User
                        user.weight = weight
                        user.height = height
                        user.gender = gender
                        user.dob = dob
                        user.goal = goal
                        UserManager.updateUser(user)
                        redirectLiveData.value = HomeContainerActivity::class.java
                    }
                }
            } else {
                next()
            }
        }
    }
}