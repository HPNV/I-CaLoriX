package edu.bluejack23_1.ICaLoriX

import MealHomeAdapter
import MealMenuAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.ICaLoriX.databinding.FragmentMealMenuBinding
import model.Food
import model.FoodManager
import model.Global

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MealMenu : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMealMenuBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: Button
    private lateinit var searchBar: SearchView
    private lateinit var mealMenuAdapter: MealMenuAdapter
    private lateinit var header:TextView
    private lateinit var filterCustom:Button
    private lateinit var filterFood:Button
    private lateinit var filterDrink:Button
    private val foods = mutableListOf<Food>()
    var filter = ""
    var headerText = "breakfast"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mealMenuAdapter = MealMenuAdapter(requireContext(), mutableListOf())
        refreshData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMealMenuBinding.inflate(inflater, container, false)
        recyclerView = binding.foodRecycler
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        header = binding.mealHeader
        filterCustom = binding.customFilter
        filterDrink = binding.drinkFilter
        filterFood = binding.foodFilter

        if(headerText == "breakfast") {
            header.text = "Breakfast"
        } else if(headerText == "dinner") {
            header.text = "Dinner"
        } else if(headerText == "lunch") {
            header.text = "Lunch"
        }

        filterCustom.setOnClickListener {
            filter = if(filter == "yes") {
                ""
            } else {
                "yes"
            }
            filterCustom(filter)
            setButtonStyle(filterCustom,filterDrink,filterCustom,filter)
        }

        filterDrink.setOnClickListener {
            filter = if(filter == "drink") {
                ""
            } else {
                "drink"
            }
            filterCustom(filter)
            setButtonStyle(filterCustom,filterDrink,filterCustom,filter)
        }

        filterFood.setOnClickListener {
            filter = if(filter == "food") {
                ""
            } else {
                "food"
            }

            filterCustom(filter)
            setButtonStyle(filterCustom,filterDrink,filterCustom,filter)
        }

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }

        recyclerView.adapter = mealMenuAdapter

        addBtn = binding.addBtn
        addBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddMealActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        searchBar = binding.searchFoodBar
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFoods(newText)
                return true
            }
        })
        refreshData()
        return binding.root
    }

    private fun filterFoods(query: String?) {
        val filteredFoods = mutableListOf<Food>()
        val newFood = foods.filter { food ->
            food.name.contains(query ?: "", ignoreCase = true)
        }
        filteredFoods.addAll(newFood)
        mealMenuAdapter.setData(filteredFoods)
    }

    private fun filterCustom(query: String?) {
        val filteredFoods = mutableListOf<Food>()
        val newFood = foods.filter { food ->
            food.custom.contains(query ?: "", ignoreCase = true)
        }
        filteredFoods.addAll(newFood)
        mealMenuAdapter.setData(filteredFoods)
    }

    private fun setButtonStyle(breakButton: Button, lunchButton: Button, dinnerButton: Button,choose:String) {
        if (choose == "drink") {

        } else if(choose == "food"){

        } else {

        }
    }

    private fun refreshData() {
        FoodManager.getFoods {
            foods.clear()
            foods.addAll(it)
            mealMenuAdapter.setData(foods)
        }
    }

    fun changeMeal() {
        if(Global.choosenMeal == "breakfast") {
            headerText = "Breakfast"
        } else if(Global.choosenMeal == "dinner") {
            headerText= "Dinner"
        } else if(Global.choosenMeal == "lunch") {
            headerText = "Lunch"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MealMenu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
