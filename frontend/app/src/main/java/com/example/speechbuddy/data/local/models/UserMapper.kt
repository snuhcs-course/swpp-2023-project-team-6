package com.example.speechbuddy.data.local.models

import com.example.speechbuddy.domain.models.User
import com.example.speechbuddy.domain.utils.DomainMapper

class UserMapper : DomainMapper<UserEntity, User> {

    override fun mapToDomainModel(model: UserEntity): User {
        return User(
            id = model.id,
            email = model.email,
            nickname = model.nickname
        )
    }

    override fun mapFromDomainModel(domainModel: User): UserEntity {
        return UserEntity(
            id = domainModel.id,
            email = domainModel.email,
            nickname = domainModel.nickname
        )
    }

}