package view_model

import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import firestore.Firestore
import model.User
import model.UserManager

class EditPorfileViewModel(nameEditText: EditText,
                           heightEditText: EditText,
                           weightEditText: EditText,
                           genderSpinner: Spinner,
saveBtn:Button) {
    private var name = "";
    private var height = 0.0;
    private var weight = 0.0;
    private var gender = "";
    private var dob = "";

    init {
        nameEditText.addTextChangedListener {
            name = it.toString()
        }

        heightEditText.addTextChangedListener {
            height = it.toString().toDouble()
        }

        weightEditText.addTextChangedListener {
            height = it.toString().toDouble()
        }

        saveBtn.setOnClickListener {
            gender = genderSpinner.selectedItem.toString()
            UserManager.getUserData {
                val user = it
                if (user != null) {
                    user.weight = this.weight
                    user.height = this.height
                    user.dob = this.dob
                    user.gender = this.gender
                    user.name = this.name
                    UserManager.updateUser(user)
                }
            }

        }
    }

    fun updateUser(user:User) {
        UserManager.updateUser(user)
    }


}