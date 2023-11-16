package com.example.speechbuddy.utils

import android.util.Log
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

enum class ResponseCode(val value: Int) {
    SUCCESS(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    NO_INTERNET_CONNECTION(600)
}

data class ErrorResponse(
    val code: Int = -1,
    val key: String = "UNKNOWN",
    val description: String = "UNKNOWN"
)

class ResponseHandler {

    fun <T> getConnectionErrorResponse(): Response<T> {
        return Response.error(ResponseCode.NO_INTERNET_CONNECTION.value, ERROR_RESPONSE_BODY.toResponseBody())
    }

    fun parseErrorResponse(body: ResponseBody): ErrorResponse {
        try {
            val errorJson = JSONObject(body.charStream().readText()).optJSONObject("error")

            if (errorJson != null) {
                val code = errorJson.optInt("code", -1)
                val messageJson = errorJson.opt("message")

                // if "message" in the ResponseBody is a list
                if (messageJson is JSONArray) {
                    val firstMessage = messageJson.optJSONObject(0)
                    if (firstMessage != null) {
                        val keys = firstMessage.keys()
                        if (keys.hasNext()) {
                            val key = keys.next()
                            val description = firstMessage.optJSONArray(key)!!.optString(0)
                            return ErrorResponse(
                                code,
                                key,
                                description
                            )
                        }
                    }
                }

                // if "message" in the ResponseBody is a single json
                if (messageJson is JSONObject) {
                    val keys = messageJson.keys()
                    if (keys.hasNext()) {
                        val key = keys.next()
                        val description = messageJson.optString(key)
                        return ErrorResponse(
                            code,
                            key,
                            description
                        )
                    }
                }
            }

            // Return a default ErrorResponse if the responseBody structure doesn't match predefined cases
            return ErrorResponse()
        } catch (e: JSONException) {
            // Handle the JSON parsing error
            Log.e("ErrorResponseMapper", "JSON parsing error: ${e.message}")
            // Return a default ErrorResponse in case of JSON parsing error
            return ErrorResponse()
        }
    }

    companion object {
        const val ERROR_RESPONSE_BODY = "{\"code\": 600, \"message\": \"No Internet Connection\"}"
    }

}