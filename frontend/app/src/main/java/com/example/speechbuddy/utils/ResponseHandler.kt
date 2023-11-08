package com.example.speechbuddy.utils

import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

enum class ResponseCode(val value: Int) {
    SUCCESS(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    NO_INTERNET_CONNECTION(600)
}

class ResponseHandler {

    fun getResponseBody(code: ResponseCode): ResponseBody {
        return when (code) {
            ResponseCode.SUCCESS -> "{\"code\": 200, \"message\": \"Success\"}".toResponseBody()
            ResponseCode.CREATED -> "{\"code\": 201, \"message\": \"Created\"}".toResponseBody()
            ResponseCode.BAD_REQUEST -> "{\"code\": 400, \"message\": \"Bad Request\"}".toResponseBody()
            ResponseCode.UNAUTHORIZED -> "{\"code\": 401, \"message\": \"Unauthorized\"}".toResponseBody()
            ResponseCode.FORBIDDEN -> "{\"code\": 403, \"message\": \"Forbidden\"}".toResponseBody()
            ResponseCode.NOT_FOUND -> "{\"code\": 404, \"message\": \"Not Found\"}".toResponseBody()
            ResponseCode.NO_INTERNET_CONNECTION -> "{\"code\": 600, \"message\": \"No Internet Connection\"}".toResponseBody()
        }
    }

}