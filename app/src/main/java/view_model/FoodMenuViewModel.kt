package view_model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import firestore.Firestore
import model.Food

class FoodMenuViewModel : ViewModel() {
    val foodLiveData:MutableLiveData<List<Food>> = MutableLiveData<List<Food>>()

    init {
        foodLiveData.value = emptyList()
        Firestore.getCollection<Food>("foods"){
            foodLiveData.postValue(it)
        }
    }

}