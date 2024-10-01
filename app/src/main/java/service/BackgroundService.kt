package service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import firestore.Firestore
import java.util.Calendar

class BackgroundService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {

        Log.d("lolo","testing")

        val user = Firestore.getAuth().currentUser
        if (user != null) {
            Firestore.saveToday()
        }

        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}
