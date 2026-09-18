package org.beacon.companion

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/** Displays only server-authenticated FCM commands addressed to this paired installation. */
class BeaconMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val text = message.data["ownerMessage"] ?: return
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel("lost_mode", "Lost mode", NotificationManager.IMPORTANCE_HIGH))
        manager.notify(1001, NotificationCompat.Builder(this, "lost_mode")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("This phone is marked lost")
            .setContentText(text.take(180)).setStyle(NotificationCompat.BigTextStyle().bigText(text.take(500)))
            .setAutoCancel(false).build())
    }
}
