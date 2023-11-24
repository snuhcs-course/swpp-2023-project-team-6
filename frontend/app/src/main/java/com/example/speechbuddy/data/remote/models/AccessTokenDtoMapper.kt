package com.example.speechbuddy.data.remote.models

import com.example.speechbuddy.domain.models.AccessToken
import com.example.speechbuddy.domain.models.AuthToken
import com.example.speechbuddy.domain.utils.DomainMapper

class AccessTokenDtoMapper : DomainMapper<AccessTokenDto, AccessToken> {

    override fun mapToDomainModel(model: AccessTokenDto): AccessToken {
        return AccessToken(
            accessToken = model.accessToken
        )
    }

    override fun mapFromDomainModel(domainModel: AccessToken): AccessTokenDto {
        return AccessTokenDto(
            accessToken = domainModel.accessToken
        )
    }

}