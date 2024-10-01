package model

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import firestore.Firestore

data class User(
    var name: String,
    var email: String,
    var profilePic: String,
    var gender: String,
    var height: Double,
    var weight: Double,
    var dob: String,
    var goal: String
) {
    constructor() : this("", "", "", "", 0.0, 0.0, "", "")
}

object UserManager {
    private val auth = Firestore.getAuth()
    private var currentUser:  FirebaseUser? = auth.currentUser;
    var userData: User? = null

    private fun getUser(): FirebaseUser? {
        return currentUser
    }

    fun getUserData(onComplete: (User?) -> Unit) {
        if (currentUser == null) {
            onComplete(null)
            return
        } else {
            val colRef = Firestore.getFireStore().collection("User")
            colRef.document(currentUser!!.email!!).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("User Manager", error.message ?: "Error retrieving user data")
                    onComplete(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(User::class.java)
                    onComplete(data)
                } else {
                    onComplete(null)
                }
            }
        }
    }

    fun login(email: String,password: String, onComplete: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener() { log->
            if(log.isSuccessful) {
                currentUser = auth.currentUser
                onComplete("success")
            } else {
                onComplete("Failed to login!")
            }
        }
    }

    fun register(username: String, email: String,password: String, onComplete: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener() { log->
            if(log.isSuccessful) {
                currentUser = auth.currentUser
                val colRef = Firestore.getFireStore().collection("User");
                colRef.document(email).set(User(username, email,"", "",0.0,0.0,"", ""))
                Firestore.copyFood(email)
                onComplete("success")
            } else {
                onComplete("failed to register")
            }
        }
    }

    fun logout() {
        currentUser = null
        userData = null
        Firestore.getAuth().signOut()
    }

    fun updateUser(user: User) {
        if (currentUser == null) {
            return
        }
        val colRef = Firestore.getFireStore().collection("User")
        colRef.document(currentUser!!.email!!).set(user)
            .addOnSuccessListener {
                Log.d("User Manager", "success")
            }
            .addOnFailureListener { e ->
                e.message?.let { Log.e("User Manager", it) }
            }
    }
}