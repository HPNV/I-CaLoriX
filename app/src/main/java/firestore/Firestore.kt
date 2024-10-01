package firestore

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ExecutionException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import model.Food
import java.util.Date
import java.util.Locale
import kotlin.math.log

object Firestore {
    @SuppressLint("StaticFieldLeak")
    val firestore = Firebase.firestore
    val storage = FirebaseStorage.getInstance()
    fun getAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun getFireStore(): FirebaseFirestore {
        return firestore
    }
    fun saveToday() {
        val user = getAuth().currentUser
        if(user != null) {
            val userRef = firestore.collection("User").document(user.email!!)
            val historyRef = userRef.collection("history")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())
            val dateRef = historyRef.document(formattedDate)

            dateRef.set(mapOf("date" to formattedDate))

            getCollection<Food>("User/${user.email}/breakfast", onSuccess = { foods ->
                for (food in foods) {
                    dateRef.collection("foods").add(food)
                    userRef.collection("breakfast").document(food.foodID).delete()
                }
            })

            getCollection<Food>("User/${user.email}/lunch", onSuccess = { foods ->
                for (food in foods) {
                    dateRef.collection("foods").add(food)
                    userRef.collection("lunch").document(food.foodID).delete()
                }
            })

            getCollection<Food>("User/${user.email}/dinner", onSuccess = { foods ->
                for (food in foods) {
                    dateRef.collection("foods").add(food)
                    userRef.collection("dinner").document(food.foodID).delete()
                }
            })

        }
    }

    fun copyFood(email:String) {
        getCollection<Food>("Foods") {
            for(food in it) {
                addToCollectionWithId("User/$email/foods",food.foodID,food)
                Log.d("tes","hayo jadi")
            }
        }
    }

    fun getFoodHistory(from: String, to: String, onSuccess: (List<Food>) -> Unit) {
        val user = getAuth().currentUser
        val foodList = mutableListOf<Food>()

        if (user != null) {
            val path = "User/${user.email}/history"
            val historyRef: CollectionReference = firestore.collection(path)
            historyRef.get().addOnSuccessListener {
                Log.d("Firestore", "Food added: ${it.documents}")
            }

            historyRef.get().addOnSuccessListener { historySnapshot ->
                Log.d("Firestore", "Food added: ${historySnapshot.documents}")
                for (doc in historySnapshot) {
                    val date = doc.id
                    if (isDateInRange(date, from, to)) {
                        val dataRef = historyRef.document(date)
                        dataRef.collection("foods").get().addOnSuccessListener { foodsSnapshot ->
                            for (foodDoc in foodsSnapshot) {
                                val food = foodDoc.toObject<Food>()
                                foodList.add(food)
                                onSuccess(foodList)
                                Log.d("Firestore", "Food added: $food")
                            }
                        }.addOnFailureListener { e ->
                            Log.e("Firestore", "Error retrieving food: $e")
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving history: $e")
            }
        }
    }


    private fun isDateInRange(date: String, from: String, to: String): Boolean {

        return try {
            val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            val fromDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(from)
            val toDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(to)
            dateObj in fromDate..toDate
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
    }

    fun addToCollectionWithId(collectionName: String, documentId: String, data: Any) {
        val collectionRef: CollectionReference = firestore.collection(collectionName)
        val documentRef: DocumentReference = collectionRef.document(documentId)

        documentRef.set(data).addOnSuccessListener {
            println("Successfully added document with ID: $documentId")
        }.addOnFailureListener { e ->
            println("Error adding document: $e")
        }
    }

    fun deleteDocumentById(collectionName: String, documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val collectionRef: CollectionReference = firestore.collection(collectionName)
        val documentReference: DocumentReference = collectionRef.document(documentId)

        documentReference.delete().addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    inline fun <reified T> getDocumentFromCollection(collectionName: String, documentID: String, crossinline onSuccess: (T?) -> Unit) {
        val collectionRef: CollectionReference = firestore.collection(collectionName)
        val documentReference: DocumentReference = collectionRef.document(documentID)

        try {
            documentReference.get().addOnSuccessListener {
                val item = it.toObject(T::class.java)
                onSuccess(item)
            }.addOnFailureListener {
                onSuccess(null)
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    inline fun <reified T> getDocumentFromCollectionById(collectionName: String, documentId: String, crossinline onSuccess: (T?) -> Unit) {
        val collectionRef: CollectionReference = firestore.collection(collectionName)
        val documentReference: DocumentReference = collectionRef.document(documentId)

        documentReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val item = documentSnapshot.toObject(T::class.java)
                onSuccess(item)
            } else {
                onSuccess(null)
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
            onSuccess(null)
        }
    }

    inline fun <reified T> getCollection(collectionName: String, crossinline onSuccess: (List<T>) -> Unit) {
        val collectionRef: CollectionReference = firestore.collection(collectionName)
        collectionRef.get().addOnSuccessListener { querySnapshot ->
            val dataList = mutableListOf<T>()

            for (document in querySnapshot) {
                val item = document.toObject(T::class.java)
                dataList.add(item)
            }
            onSuccess(dataList)
        }.addOnFailureListener{ e ->
            println(e)
        }
    }

    fun uploadImage(path:String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef: StorageReference = storage.reference

        val imageRef = storageRef.child(path)
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                onFailure(task.exception ?: Exception("Unknown error"))
            }
        }
    }
}