package edu.bluejack23_1.ICaLoriX

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import edu.bluejack23_1.ICaLoriX.databinding.ActivityRegisterBinding
import view_model.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var unameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var confirmEdit: EditText
    private lateinit var linkLogin: TextView
    private lateinit var registerBtn: Button
    private lateinit var viewModel : RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unameEdit = binding.unameEdit
        emailEdit = binding.emailEdit
        passwordEdit = binding.passwordEdit
        confirmEdit = binding.confirmEdit
        linkLogin = binding.linkLogin
        registerBtn = binding.registerBtn
        viewModel = RegisterViewModel(unameEdit,emailEdit,passwordEdit,confirmEdit)

        linkLogin.setOnClickListener{
            viewModel.redirectLiveData.value = LoginActivity::class.java
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

        registerBtn.setOnClickListener{
            viewModel.regis()
        }
    }
}