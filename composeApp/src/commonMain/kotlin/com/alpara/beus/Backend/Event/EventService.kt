package com.alpara.beus.Backend.Event

import com.alpara.beus.Models.EventPCreate
import com.alpara.beus.Models.EventRCreate
import com.alpara.beus.Backend.ApiClient
import com.alpara.beus.Utils.EventType
import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class EventService {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun addEvent(type: EventType): Result<EventRCreate>{

        return try {
            val response = client.put("$baseUrl/event/create"){
                contentType(ContentType.Application.Json)
                setBody(EventPCreate(
                    type = type
                ))
            }


            if (response.status == HttpStatusCode.Unauthorized) {
                return Result.failure(Exception("Token Expirado"))
            }

            val eventResponse = response.body<EventRCreate>()

            Result.success(eventResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}