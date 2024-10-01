package model

import android.icu.text.SimpleDateFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import firestore.Firestore
import java.util.Date
import java.util.Locale
import java.util.Random

data class Notif(
    val notifID: String,
    val header:String,
    val message:String,
    val date:String
){
    constructor() : this("","","","")
}

object NotifManager {
    fun addNotif(header: String, message: String) {
        val random = Random()
        var notifID: String
        var isUnique: Boolean = true

        do {
            val randomInt = random.nextInt(999) + 1
            notifID = String.format("N%03d", randomInt)
            isNotifIDUnique(notifID) {
                isUnique = it
            }
        } while (!isUnique)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("id", "ID"))
        val notif = Notif(notifID, header, message,dateFormat.format(currentDate))
        val user = Firestore.getAuth().currentUser
        val path = "User/${user!!.email}/notif"
        Firestore.addToCollectionWithId(path, notifID, notif)
    }

    private fun isNotifIDUnique(notifID: String, onSuccess: (Boolean) -> Unit) {
        val existingNotif = Firestore.getDocumentFromCollection<Notif>("notifs", notifID) {
            onSuccess(it == null)
        }
    }
}
