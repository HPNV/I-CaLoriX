package edu.bluejack23_1.ICaLoriX

import adapter.SpinnerAdapter
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import edu.bluejack23_1.ICaLoriX.databinding.ActivityGettingStartedBinding
import edu.bluejack23_1.ICaLoriX.databinding.FragmentGSContent2Binding
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GSContent2.newInstance] factory method to
 * create an instance of this fragment.
 */
class GSContent2 : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentGSContent2Binding
    private lateinit var spinner: Spinner
    private lateinit var dobBtn: Button
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private var param1: String? = null
    private var param2: String? = null
    fun getGender(): String {
        return spinner.selectedItem.toString()
    }

    fun getWeight(): Double {
        return weightEditText.text.toString().toDoubleOrNull() ?: 0.0
    }

    fun getHeight(): Double {
        return heightEditText.text.toString().toDoubleOrNull() ?: 0.0
    }

    fun getDob(): String {
        return dobBtn.text.toString()
    }

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
        binding = FragmentGSContent2Binding.inflate(inflater, container, false)

        heightEditText = binding.heightEdit
        weightEditText = binding.weightEdit

        spinner = binding.genderSpinner
        spinner.onItemSelectedListener = this

        val genderArray = resources.getStringArray(R.array.gender_array)
        val spinnerAdapter = SpinnerAdapter(requireContext(), R.layout.spinner_item, genderArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        dobBtn = binding.dobButton
        dobBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.dialog_theme,
                { view, year, monthOfYear, dayOfMonth ->
                    dobBtn.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    dobBtn.setTextColor(requireContext().resources.getColor(R.color.black, requireContext().theme))
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
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
         * @return A new instance of fragment GSContent2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GSContent2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}