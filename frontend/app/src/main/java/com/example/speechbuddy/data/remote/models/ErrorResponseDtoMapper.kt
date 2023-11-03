package com.example.speechbuddy.data.remote.models

import com.example.speechbuddy.domain.models.ErrorResponse
import com.example.speechbuddy.domain.utils.DomainMapper

class ErrorResponseDtoMapper : DomainMapper<ErrorResponseDto, ErrorResponse> {

    override fun mapToDomainModel(model: ErrorResponseDto): ErrorResponse {
        val errorDto = model.error
        val code = errorDto?.code
        val key = errorDto?.message?.keys?.firstOrNull() // Get the first key from the message
        val description = errorDto?.message?.get(key)?.firstOrNull() // Get the first item in the error list

        return ErrorResponse(code = code, key = key, description = description)
    }

    override fun mapFromDomainModel(domainModel: ErrorResponse): ErrorResponseDto {
        val errorDto = ErrorDto(
            code = domainModel.code,
            message = if (domainModel.key != null && domainModel.description != null) {
                mapOf(domainModel.key to listOf(domainModel.description))
            } else {
                emptyMap()
            }
        )
        return ErrorResponseDto(error = errorDto)
    }

}