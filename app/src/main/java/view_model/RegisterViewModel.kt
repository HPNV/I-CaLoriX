package view_model

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import edu.bluejack23_1.ICaLoriX.LoginActivity
import model.UserManager

class RegisterViewModel(usernameEditText: EditText, emailEditText: EditText, passwordEditText: EditText, conPasswordEditText: EditText) {
    private var username = ""
    private var email = ""
    private var pass = ""
    private var conPass = ""
    val errorLiveData = MutableLiveData<String>()
    val redirectLiveData = MutableLiveData<Class<*>>()
    init {
        usernameEditText.addTextChangedListener {
            username = it.toString()
        }

        emailEditText.addTextChangedListener {
            email = it.toString()
        }

        passwordEditText.addTextChangedListener {
            pass = it.toString()
        }

        conPasswordEditText.addTextChangedListener {
            conPass = it.toString()
        }
    }

    fun regis() {
        if(username.isEmpty() || email.isEmpty() || pass.isEmpty() || conPass.isEmpty()) {
            errorLiveData.value = "Please fill all the fields"
            return
        } else if(!email.endsWith("@gmail.com")){
            errorLiveData.value = "Email must ends with @gmail.com"
            return
        } else if(username.length < 3){
            errorLiveData.value = "Username must be more than 3 character"
            return
        } else if(pass.length < 6) {
            errorLiveData.value = "Password must be more than 5 character"
            return
        } else if(pass != conPass) {
            errorLiveData.value = "Wrong password confirmation"
            return
        }

        UserManager.register(username,email,pass) {
            if(it == "success") {
                redirectLiveData.value = LoginActivity::class.java
            } else {
                errorLiveData.value = it
            }
        }
    }
}