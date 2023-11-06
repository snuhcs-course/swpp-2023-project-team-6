package com.example.speechbuddy.data.remote.models

import android.util.Log
import com.example.speechbuddy.domain.models.ErrorResponse
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ErrorResponseMapper {
    fun mapToDomainModel(response: ResponseBody): ErrorResponse {
        try {
            val errorJson = JSONObject(response.charStream().readText()).optJSONObject("error")

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
                            val description = firstMessage.optJSONArray(key).optString(0)
                            return ErrorResponse(code, key, description)
                        }
                    }
                }
                // if "message" in the ResponseBody is a single json
                else if (messageJson is JSONObject) {
                    val keys = messageJson.keys()
                    if (keys.hasNext()) {
                        val key = keys.next()
                        val description = messageJson.optString(key)
                        return ErrorResponse(code, key, description)
                    }
                }
            }

            // Return a default ErrorResponse if the responseBody structure doesn't match predefined cases
            return ErrorResponse()
        } catch (e: JSONException) {
            // Handle the JSON parsing error
            Log.e("ErrorResponseMapper", "JSON parsing error: ${e.message}")
            return ErrorResponse() // Return a default ErrorResponse in case of JSON parsing error
        }
    }
}
