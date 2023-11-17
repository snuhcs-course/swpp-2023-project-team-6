package com.example.speechbuddy.data.local.models

import com.example.speechbuddy.domain.models.WeightRow
import com.example.speechbuddy.domain.utils.DomainMapper

class WeightRowMapper : DomainMapper<WeightRowEntity, WeightRow>{

    override fun mapToDomainModel(model: WeightRowEntity): WeightRow {
        return WeightRow(
            id = model.id,
            weights = model.weights
        )

    }

    override fun mapFromDomainModel(domainModel: WeightRow): WeightRowEntity {
        return WeightRowEntity(
            id = domainModel.id,
            weights = domainModel.weights
        )
    }
}
