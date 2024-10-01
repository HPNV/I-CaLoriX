package edu.bluejack23_1.ICaLoriX

import adapter.SpinnerAdapter
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.databinding.FragmentEditProfileBinding
import firestore.Firestore
import model.User
import model.UserManager
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profilePic: ImageView
    private lateinit var username:EditText
    private lateinit var height:EditText
    private lateinit var weight:EditText
    private lateinit var gender:Spinner
    private lateinit var dob:Button
    private lateinit var binding:FragmentEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImage: Uri? = null
    private lateinit var saveBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        profilePic = binding.profilePicture
        username = binding.unameEdit
        height = binding.heightEdit
        weight = binding.weightEdit
        gender = binding.genderSpinner
        dob = binding.dobButton
        saveBtn = binding.saveBtn

        fun setText(it:User) {
            if(it.profilePic.isNotEmpty()) {
                Glide.with(this).load(it.profilePic)
                    .apply(RequestOptions.bitmapTransform(CircleCrop())).into(profilePic)
            } else {
                Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/i-calorix.appspot.com/o/default_profile.jpg?alt=media&token=6006f67b-fefc-43a1-8f6d-11cfcd3d0f0b")
                    .apply(RequestOptions.bitmapTransform(CircleCrop())).into(profilePic)
            }
            username.setText(it.name)
            dob.text = it.dob
            if(it.gender == "male") {
                gender.setSelection(1)
            } else {
                gender.setSelection(2)
            }
            height.setText(it.height.toString())
            weight.setText(it.weight.toString())
        }

        if(UserManager.userData == null) {
            UserManager.getUserData {
                if (it != null) {
                    setText(it)
                    UserManager.userData = it
                }
            }
        } else {
            setText(UserManager.userData!!)
        }

        profilePic.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }

        val genderArray = resources.getStringArray(R.array.gender_array)
        val spinnerAdapter = SpinnerAdapter(requireContext(), R.layout.spinner_item, genderArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gender.adapter = spinnerAdapter

        dob.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.dialog_theme,
                { view, year, monthOfYear, dayOfMonth ->
                    dob.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    dob.setTextColor(requireContext().resources.getColor(R.color.black, requireContext().theme))
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        saveBtn.setOnClickListener {
            val user = Firestore.getAuth().currentUser

            if (username.text.isNullOrBlank() || height.text.isNullOrBlank() || weight.text.isNullOrBlank() || dob.text.isNullOrBlank()) {
                showToast("Please fill in all required fields.")
                return@setOnClickListener
            }

            if (selectedImage == null) {
                val newUser = UserManager.userData
                if (newUser != null) {
                    newUser.height = height.text.toString().toDouble()
                    newUser.weight = weight.text.toString().toDouble()
                    newUser.name = username.text.toString()
                    newUser.gender = gender.selectedItem.toString()
                    newUser.dob = dob.text.toString()

                    Firestore.addToCollectionWithId("User", newUser.email, newUser)
                    UserManager.userData = newUser
                    val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
                    viewPager?.currentItem = 2
                }
            } else {
                Firestore.uploadImage("${user!!.email}/${user.email}", selectedImage!!,{ imageUrl ->
                    val newUser = UserManager.userData
                    if (newUser != null) {
                        newUser.profilePic = imageUrl
                        newUser.height = height.text.toString().toDouble()
                        newUser.weight = weight.text.toString().toDouble()
                        newUser.name = username.text.toString()
                        newUser.gender = gender.selectedItem.toString()
                        newUser.dob = dob.text.toString()

                        Firestore.addToCollectionWithId("User", newUser.email, newUser)
                        UserManager.userData = newUser
                        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
                        viewPager?.currentItem = 2
                    }
                }, {
                    showToast("Error uploading image.")
                })
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            selectedImage = data.data
            profilePic.setImageURI(selectedImage)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}