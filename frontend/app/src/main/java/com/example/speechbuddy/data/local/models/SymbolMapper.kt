package com.example.speechbuddy.data.local.models

import com.example.speechbuddy.domain.models.Symbol
import com.example.speechbuddy.domain.utils.DomainMapper

class SymbolMapper : DomainMapper<SymbolEntity, Symbol> {

    override fun mapToDomainModel(model: SymbolEntity): Symbol {
        return Symbol(
            id = model.id,
            text = model.text,
            imageUrl = model.imageUrl,
            categoryId = model.categoryId,
            isFavorite = model.isFavorite,
            isMine = model.isMine
        )
    }

    override fun mapFromDomainModel(domainModel: Symbol): SymbolEntity {
        return SymbolEntity(
            id = domainModel.id,
            text = domainModel.text,
            imageUrl = domainModel.imageUrl,
            categoryId = domainModel.categoryId,
            isFavorite = domainModel.isFavorite,
            isMine = domainModel.isMine
        )
    }

}