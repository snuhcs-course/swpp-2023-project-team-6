package com.example.speechbuddy.data.remote.models

import com.example.speechbuddy.domain.models.SymbolId
import com.example.speechbuddy.domain.utils.DomainMapper

class SymbolIdDtoMapper: DomainMapper<SymbolIdDto, SymbolId> {

    override fun mapToDomainModel(model: SymbolIdDto): SymbolId {
        return SymbolId(
            id = model.id
        )
    }
    override fun mapFromDomainModel(domainModel: SymbolId): SymbolIdDto {
        return SymbolIdDto(
            id = domainModel.id
        )
    }
}
