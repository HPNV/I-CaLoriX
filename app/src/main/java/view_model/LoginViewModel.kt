package view_model

import android.util.Log
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import edu.bluejack23_1.ICaLoriX.FoodMenuView
import edu.bluejack23_1.ICaLoriX.GettingStarted
import edu.bluejack23_1.ICaLoriX.HomeContainerActivity
import edu.bluejack23_1.ICaLoriX.LoginActivity
import firestore.Firestore
import model.UserManager
import kotlin.math.log

class LoginViewModel(private val emailEditText: EditText, private val passEditText: EditText) {
    private var email = ""
    private var pass = ""
    val errorLiveData = MutableLiveData<String>()
    val redirectLiveData = MutableLiveData<Class<*>>()

    init {
        emailEditText.addTextChangedListener {
            email = it.toString()
        }

        passEditText.addTextChangedListener {
            pass = it.toString()
        }

        checkUser()
    }

    fun login() {
        if(email.isEmpty() || pass.isEmpty()) {
            errorLiveData.value = "Please fill all the field"
        } else {
            UserManager.login(email,pass){
                if(it == "success") {
                    checkUser()
                } else {
                    errorLiveData.value = it
                }
            }
        }
    }

     fun checkUser() {
        return UserManager.getUserData {
            if (it != null) {
                if(it.goal.isEmpty()){
                    redirectLiveData.value = GettingStarted::class.java
                } else {
                    redirectLiveData.value = HomeContainerActivity::class.java
                }
            }
        }
    }
}