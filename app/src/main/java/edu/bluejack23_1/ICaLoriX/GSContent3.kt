package edu.bluejack23_1.ICaLoriX

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import edu.bluejack23_1.ICaLoriX.databinding.FragmentGSContent3Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GSContent3.newInstance] factory method to
 * create an instance of this fragment.
 */
class GSContent3 : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentGSContent3Binding
    private lateinit var weightLossBtn: Button
    private lateinit var maintainBtn: Button
    private lateinit var weightGainBtn: Button
    private var param1: String? = null
    private var param2: String? = null
    private var goal:String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun getGoal():String {
        return goal
    }

    private fun setButtonStyle(button: Button, goal: String) {
        val selectedBG = requireContext().getDrawable(R.drawable.selected_button_group)
        val defaultBG = requireContext().getDrawable(R.drawable.default_button_group)

        val selectedColor = requireContext().getColorStateList(R.color.blue_green)
        val defaultColor = requireContext().getColorStateList(R.color.black)

        if (goal == button.text) {
            button.background = selectedBG
            button.setTextColor(selectedColor)
        } else {
            button.background = defaultBG
            button.setTextColor(defaultColor)
        }
    }

    private fun checkGoal() {
        setButtonStyle(weightLossBtn, goal)
        setButtonStyle(maintainBtn, goal)
        setButtonStyle(weightGainBtn, goal)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGSContent3Binding.inflate(inflater, container, false)
        weightLossBtn = binding.weightLossBtn
        maintainBtn = binding.maintainBtn
        weightGainBtn = binding.weightGainBtn

        weightLossBtn.setOnClickListener {
            goal = weightLossBtn.text.toString()
            checkGoal()
        }

        maintainBtn.setOnClickListener {
            goal = maintainBtn.text.toString()
            checkGoal()
        }

        weightGainBtn.setOnClickListener {
            goal = weightGainBtn.text.toString()
            checkGoal()
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GSContent3.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GSContent3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}