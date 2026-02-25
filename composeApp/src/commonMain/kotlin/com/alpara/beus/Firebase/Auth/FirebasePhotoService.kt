package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.PhotoModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore

/**
 * Gestiona la metadata de fotos en Firestore. (no se suben las fotos ahi)
 * Estructura: teams/{teamId}/events/{eventId}/photos/{photoId}
 * Así se garantiza que las fotos solo son accesibles si conoces el teamId y eventId,
 * y las reglas de seguridad de Firestore pueden validar la membresía al equipo.
 */
class FirebasePhotoService {

    private val firestore = Firebase.firestore

    private fun photosCol(teamId: String, eventId: String) =
        firestore.collection("teams")
            .document(teamId)
            .collection("events")
            .document(eventId)
            .collection("photos")

    /** Guarda la metadata de una foto recién subida. */
    suspend fun addPhoto(photo: PhotoModel): Result<Unit> {
        return try {
            photosCol(photo.teamId, photo.eventId).document(photo.id).set(
                mapOf(
                    "id" to photo.id,
                    "teamId" to photo.teamId,
                    "eventId" to photo.eventId,
                    "uploadedBy" to photo.uploadedBy,
                    "uploaderName" to photo.uploaderName,
                    "storagePath" to photo.storagePath,
                    "publicUrl" to photo.publicUrl,
                    "caption" to photo.caption,
                    "createdAt" to photo.createdAt
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar metadata: ${e.message}"))
        }
    }

    /** Devuelve todas las fotos de un equipo y evento, ordenadas por fecha desc. */
    suspend fun getPhotos(teamId: String, eventId: String): Result<List<PhotoModel>> {
        return try {
            val snapshot = photosCol(teamId, eventId).get()

            val photos = snapshot.documents.mapNotNull { doc: DocumentSnapshot ->
                try {
                    PhotoModel(
                        id = doc.get<String>("id"),
                        teamId = doc.get<String>("teamId"),
                        eventId = doc.get<String>("eventId"),
                        uploadedBy = doc.get<String>("uploadedBy"),
                        uploaderName = doc.get<String>("uploaderName"),
                        storagePath = doc.get<String>("storagePath"),
                        publicUrl = doc.get<String>("publicUrl"),
                        caption = doc.get<String>("caption"),
                        createdAt = doc.get<Long>("createdAt")
                    )
                } catch (_: Exception) { null }
            }.sortedByDescending { it.createdAt }

            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener fotos: ${e.message}"))
        }
    }

    /** Borra la metadata de una foto. Solo llamar si el usuario es el autor. */
    suspend fun deletePhoto(teamId: String, eventId: String, photoId: String): Result<Unit> {
        return try {
            photosCol(teamId, eventId).document(photoId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al borrar metadata: ${e.message}"))
        }
    }
}
