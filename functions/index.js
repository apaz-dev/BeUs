const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onNotificationQueued = functions.firestore
    .document("notificationQueue/{docId}")
    .onCreate(async (snap, context) => {
      const data = snap.data();

      // Si ya fue enviada (por si acaso), salir
      if (data.sent === true) return null;

      const token = data.token;
      const title = data.title || "BeUs";
      const body = data.body || "";

      if (!token) {
        console.warn("Documento sin token FCM:", context.params.docId);
        return snap.ref.update({sent: true, error: "Sin token"});
      }

      const message = {
        token: token,
        notification: {
          title: title,
          body: body,
        },
        data: {
          teamId: data.teamId || "",
          eventId: data.eventId || "",
          role: data.role || "",
        },
        // Configuración para iOS
        apns: {
          payload: {
            aps: {
              sound: "default",
              badge: 1,
            },
          },
        },
        // Configuración para Android
        android: {
          priority: "high",
          notification: {
            sound: "default",
            channelId: "beus_roles",
          },
        },
      };

      try {
        await admin.messaging().send(message);
        console.log("Notificación enviada a:", token.substring(0, 20) + "...");
        return snap.ref.update({sent: true, sentAt: admin.firestore.FieldValue.serverTimestamp()});
      } catch (error) {
        console.error("Error al enviar notificación:", error.message);
        // Si el token no es válido, eliminarlo del perfil
        if (
          error.code === "messaging/invalid-registration-token" ||
          error.code === "messaging/registration-token-not-registered"
        ) {
          if (data.userId) {
            try {
              await admin.firestore()
                  .collection("profiles")
                  .doc(data.userId)
                  .update({fcmToken: admin.firestore.FieldValue.delete()});
            } catch (_) { /* ignore */ }
          }
        }
        return snap.ref.update({sent: true, error: error.message});
      }
    });

