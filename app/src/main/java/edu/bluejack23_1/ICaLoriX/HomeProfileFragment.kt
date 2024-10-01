package edu.bluejack23_1.ICaLoriX

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import edu.bluejack23_1.ICaLoriX.databinding.FragmentHomeProfileBinding
import firestore.Firestore
import model.User
import model.UserManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profileImage: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var dobTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var wightLossBtn: Button
    private lateinit var maintainWeightBtn: Button
    private lateinit var gainWeightBtn: Button
    private lateinit var editProfileBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var binding: FragmentHomeProfileBinding

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
        binding = FragmentHomeProfileBinding.inflate(layoutInflater)
        profileImage = binding.profilePicture
        usernameTextView = binding.profileUsername
        emailTextView = binding.profileEmail
        dobTextView = binding.profileDob
        genderTextView = binding.profileGender
        heightTextView = binding.profileHeight
        weightTextView = binding.profileWeight
        wightLossBtn = binding.profileWightLoss
        maintainWeightBtn = binding.profileMaintainWeight
        gainWeightBtn = binding.profileWeightGain
        editProfileBtn = binding.profileEditBtn
        logoutBtn = binding.profileLogout


        fun setText(it:User) {

            if(it.profilePic.isNotEmpty()) {
                Glide.with(this).load(it.profilePic)
                    .apply(RequestOptions.bitmapTransform(CircleCrop())).into(profileImage)
            } else {
                Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/i-calorix.appspot.com/o/default_profile.jpg?alt=media&token=6006f67b-fefc-43a1-8f6d-11cfcd3d0f0b")
                    .apply(RequestOptions.bitmapTransform(CircleCrop())).into(profileImage)
            }
            usernameTextView.text = it.name
            emailTextView.text = it.email
            dobTextView.text = it.dob
            genderTextView.text = it.gender
            heightTextView.text = it.height.toString()
            weightTextView.text = it.weight.toString()

            updateButtonStyle(wightLossBtn, it.goal == "Weight Loss")
            updateButtonStyle(maintainWeightBtn, it.goal == "Maintain Weight")
            updateButtonStyle(gainWeightBtn, it.goal == "Weight Gain")
        }

        if(UserManager.userData == null) {
            UserManager.getUserData {
                if (it != null) {
                    setText(it)
                    UserManager.userData = it
                } else {
                    showToast("user not found")
                }
            }
        } else {
            setText(UserManager.userData!!)
        }

        logoutBtn.setOnClickListener {
            model.UserManager.logout()
            val intent = Intent(context,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            activity?.finish()
        }

        editProfileBtn.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.currentItem = 3
        }

        var goal = ""

        wightLossBtn.setOnClickListener {
            goal = "Weight Loss"
            updateGoal(goal)
        }
        maintainWeightBtn.setOnClickListener {
            goal = "Maintain Weight"
            updateGoal(goal)

        }
        gainWeightBtn.setOnClickListener {
            goal = "Weight Gain"
            updateGoal(goal)
        }

        return binding.root
    }

    private fun updateButtonStyle(button: Button, isGoalSelected: Boolean) {
        if (isGoalSelected) {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_green))
//            button.setTypeface(null, Typeface.BOLD)
//            button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        } else {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            button.setTypeface(null, Typeface.NORMAL)
//            button.paintFlags = button.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    fun updateGoal(goal:String){
        val user = UserManager.userData
        if (user != null) {
            user.goal = goal
            UserManager.updateUser(user)
            updateButtonStyle(wightLossBtn, goal == "Weight Loss")
            updateButtonStyle(maintainWeightBtn, goal == "Maintain Weight")
            updateButtonStyle(gainWeightBtn, goal == "Weight Gain")
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
         * @return A new instance of fragment HomeProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}