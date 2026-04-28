package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.PhotoModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore

class FirebasePhotoService {

    private val firestore = Firebase.firestore

    private fun photosCol(teamId: String, eventId: String) =
        firestore.collection("teams")
            .document(teamId)
            .collection("events")
            .document(eventId)
            .collection("photos")

    suspend fun addPhoto(photo: PhotoModel): Result<Unit> {
        return try {
            photosCol(photo.teamId, photo.eventId).document(photo.id).set(
                mapOf(
                    "id"           to photo.id,
                    "teamId"       to photo.teamId,
                    "eventId"      to photo.eventId,
                    "uploadedBy"   to photo.uploadedBy,
                    "uploaderName" to photo.uploaderName,
                    "storagePath"  to photo.storagePath,
                    "publicUrl"    to photo.publicUrl,
                    "caption"      to photo.caption,
                    "createdAt"    to photo.createdAt
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar metadata: ${e.message}"))
        }
    }

    suspend fun getPhotos(teamId: String, eventId: String): Result<List<PhotoModel>> =
        getPhotosForEvent(teamId, eventId)

    suspend fun getPhotosForEvent(teamId: String, eventId: String): Result<List<PhotoModel>> {
        return try {
            val snapshot = photosCol(teamId, eventId).get()
            val photos = snapshot.documents.mapNotNull { doc: DocumentSnapshot ->
                try {
                    PhotoModel(
                        id           = doc.get("id"),
                        teamId       = doc.get("teamId"),
                        eventId      = doc.get("eventId"),
                        uploadedBy   = doc.get("uploadedBy"),
                        uploaderName = doc.get("uploaderName"),
                        storagePath  = doc.get("storagePath"),
                        publicUrl    = doc.get("publicUrl"),
                        caption      = doc.get("caption"),
                        createdAt    = doc.get("createdAt")
                    )
                } catch (_: Exception) { null }
            }.sortedByDescending { it.createdAt }
            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener fotos: ${e.message}"))
        }
    }

    suspend fun deletePhoto(teamId: String, eventId: String, photoId: String): Result<Unit> {
        return try {
            photosCol(teamId, eventId).document(photoId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al borrar metadata: ${e.message}"))
        }
    }
}