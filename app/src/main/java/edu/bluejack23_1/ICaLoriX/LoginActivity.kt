package edu.bluejack23_1.ICaLoriX

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import edu.bluejack23_1.ICaLoriX.databinding.ActivityLoginBinding
import firestore.Firestore
import model.Food
import model.FoodManager
import view_model.FoodMenuViewModel
import view_model.LoginViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var linkRegister: TextView
    private lateinit var loginBtn: Button
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        emailEdit = binding.emailEdit
        passwordEdit = binding.passwordEdit
        linkRegister = binding.linkRegister
        loginBtn = binding.loginBtn
        viewModel = LoginViewModel(emailEdit,passwordEdit)
        viewModel.checkUser()

        linkRegister.setOnClickListener {
            viewModel.redirectLiveData.value = RegisterActivity::class.java
        }

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

        loginBtn.setOnClickListener{
            viewModel.login()
        }
    }
}