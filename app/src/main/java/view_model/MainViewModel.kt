package view_model

import androidx.lifecycle.MutableLiveData
import edu.bluejack23_1.ICaLoriX.AddMealActivity
import edu.bluejack23_1.ICaLoriX.FoodMenuView
import edu.bluejack23_1.ICaLoriX.GettingStarted
import edu.bluejack23_1.ICaLoriX.HomeContainerActivity
import edu.bluejack23_1.ICaLoriX.LoginActivity
import firestore.Firestore
import model.UserManager

class MainViewModel {
    val redirectLiveData = MutableLiveData<Class<*>>()
    
    init {
//        Firestore.getAuth().signOut()
        checkUser()
    }

    private fun checkUser() {
        return UserManager.getUserData {
            if (it != null) {
                UserManager.userData = it
                if(it.goal.isEmpty()){
                    redirectLiveData.value = GettingStarted::class.java
                } else {
                    redirectLiveData.value = HomeContainerActivity::class.java
                }
            } else {
                redirectLiveData.value = LoginActivity::class.java
            }
        }
    }
}