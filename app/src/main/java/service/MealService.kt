package service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import edu.bluejack23_1.ICaLoriX.MainActivity
import edu.bluejack23_1.ICaLoriX.R
import model.NotifManager

class MealService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        val currentCalorie = params?.extras?.getDouble("curCal", 0.0) ?: 0.0
        val targetCalorie = params?.extras?.getDouble("tarCal", 0.0) ?: 0.0

        if (currentCalorie < targetCalorie) {
            NotifManager.addNotif("Calorie Reminder", "you need more calorie")
            sendNotification("Calorie Reminder", "you need more calorie")
        }

        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "MealServiceChannel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Meal Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.profile_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
