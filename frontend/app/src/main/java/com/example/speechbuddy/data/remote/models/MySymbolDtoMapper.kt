package com.example.speechbuddy.data.remote.models

import com.example.speechbuddy.domain.models.MySymbol
import com.example.speechbuddy.domain.utils.DomainMapper

class MySymbolDtoMapper: DomainMapper<MySymbolDto, MySymbol> {

    override fun mapToDomainModel(model: MySymbolDto): MySymbol {
        return MySymbol(
            id = model.id
        )
    }
    override fun mapFromDomainModel(domainModel: MySymbol): MySymbolDto {
        return MySymbolDto(
            id = domainModel.id
        )
    }
}
