package com.example.speechbuddy.data.remote.models

import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.domain.utils.DomainMapper

class AuthTokenDtoMapper : DomainMapper<AuthTokenDto, AuthToken> {

    override fun mapToDomainModel(model: AuthTokenDto): AuthToken {
        return AuthToken(
            accessToken = model.accessToken,
            refreshToken = model.refreshToken
        )
    }

    override fun mapFromDomainModel(domainModel: AuthToken): AuthTokenDto {
        return AuthTokenDto(
            accessToken = domainModel.accessToken,
            refreshToken = domainModel.refreshToken
        )
    }

}