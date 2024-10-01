package model

import android.net.Uri
import firestore.Firestore
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Random

data class Food(
    var foodID: String = "",
    var name: String = "",
    var foodPic: String = "",
    var calories: Double = 0.0,
    var carb: Double = 0.0,
    var proteins: Double = 0.0,
    var fats: Double = 0.0,
    var custom: String = "",
    var serving: Double = 1.0
) : Serializable {

    private companion object {
        private const val serialVersionUID: Long = 123456789L
    }

    constructor() : this("", "", "", 0.0, 0.0, 0.0, 0.0, "")

    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(foodID)
        out.writeObject(name)
        out.writeObject(foodPic)
        out.writeDouble(calories)
        out.writeDouble(carb)
        out.writeDouble(proteins)
        out.writeDouble(fats)
        out.writeObject(custom)
    }

    private fun readObject(`in`: ObjectInputStream) {
        val foodIDObj = `in`.readObject()
        if (foodIDObj is String) {
            foodID = foodIDObj
        }

        name = `in`.readObject() as String
        foodPic = `in`.readObject() as String
        calories = `in`.readDouble()
        carb = `in`.readDouble()
        proteins = `in`.readDouble()
        fats = `in`.readDouble()
        custom = `in`.readObject() as String
    }
}

object FoodManager {
    fun addFood(
        name: String,
        foodPic: Uri,
        calories: Double,
        carbs: Double,
        proteins: Double,
        fats: Double,
        callback: (Food) -> Unit
    ) {
        val random = Random()
        var foodID: String
        var isUnique: Boolean = true

        do {
            val randomInt = random.nextInt(999) + 1
            foodID = String.format("F%03d", randomInt)
            isFoodIDUnique(foodID) { unique ->
                isUnique = unique
            }
        } while (!isUnique)

        val user = Firestore.getAuth().currentUser
        Firestore.uploadImage("${user!!.email}/foods/$foodID", foodPic,
            onSuccess = { downloadUrl ->
                val food = Food(foodID, name, downloadUrl, calories, carbs, proteins, fats, "yes")
                Firestore.addToCollectionWithId("User/${user.email}/foods", foodID, food)
                callback(food)
            },
            onFailure = { exception ->
                println("Error uploading image: $exception")
            }
        )
    }


    fun updateFood(foodID: String, name: String, foodPic: Uri?, calories: Double, carbs: Double, proteins: Double, fats: Double) {
        val user = Firestore.getAuth().currentUser
        if (foodPic != null) {
            Firestore.uploadImage("${user!!.email}/foods/$foodID", foodPic,
                onSuccess = { downloadUrl ->
                    val food = Food(foodID, name, downloadUrl, calories, carbs, proteins, fats, "yes")
                    Firestore.addToCollectionWithId("User/${user.email}/foods", foodID, food)
                },
                onFailure = { exception ->
                    println("Error uploading image: $exception")
                }
            )
        } else {
            Firestore.getDocumentFromCollection<Food>("User/${user!!.email}/foods", foodID) { existingFood ->
                if (existingFood != null) {
                    val updatedFood = existingFood.copy(
                        name = name,
                        calories = calories,
                        carb = carbs,
                        proteins = proteins,
                        fats = fats
                    )
                    Firestore.addToCollectionWithId("User/${user.email}/foods", foodID, updatedFood)
                } else {
                    println("Food not found for ID: $foodID")
                }
            }
        }
    }


    private fun isFoodIDUnique(foodID: String, onSuccess: (Boolean) -> Unit) {
        Firestore.getDocumentFromCollection<Food>("foods", foodID) { existingFood ->
            onSuccess(existingFood == null)
        }
    }

    fun getFoods(onSuccess: (List<Food>) -> Unit) {
        val user = Firestore.getAuth().currentUser
        if(user != null) {
            val path = "User/${user.email}/foods"

            Firestore.getCollection<Food>(path) { foodList ->
                onSuccess(foodList)
            }
        } else {
            onSuccess(emptyList())
        }
    }
}
