package com.example.speechbuddy.data.remote.models

import com.example.speechbuddy.domain.models.User
import com.example.speechbuddy.domain.utils.DomainMapper

class UserDtoMapper : DomainMapper<UserDto, User> {

    override fun mapToDomainModel(model: UserDto): User {
        return User(
            id = model.id!!,
            email = model.email!!,
            nickname = model.nickname!!
        )
    }

    override fun mapFromDomainModel(domainModel: User): UserDto {
        return UserDto(
            id = domainModel.id,
            email = domainModel.email,
            nickname = domainModel.nickname
        )
    }

}