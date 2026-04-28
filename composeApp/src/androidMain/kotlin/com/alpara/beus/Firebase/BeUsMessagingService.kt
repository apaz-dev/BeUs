package com.alpara.beus.Firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servicio de Firebase Cloud Messaging para Android.
 * Gestiona la recepción de notificaciones push y la actualización del token FCM.
 */
class BeUsMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Actualizar en Firestore si hay un usuario autenticado
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = dev.gitlive.firebase.Firebase.auth.currentUser
                if (user != null) {
                    Firebase.firestore
                        .collection("profiles")
                        .document(user.uid)
                        .update(mapOf("fcmToken" to token))
                }
            } catch (_: Exception) {
                // Silenciar errores — el token se registrará en el próximo login
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Las notificaciones con "notification" payload se muestran automáticamente
        // cuando la app está en background. Aquí podemos manejar "data" payloads.
        val title = message.notification?.title ?: message.data["title"] ?: return
        val body = message.notification?.body ?: message.data["body"] ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "beus_roles"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Crear canal para Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Roles de eventos",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de roles asignados en eventos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = android.app.Notification.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

