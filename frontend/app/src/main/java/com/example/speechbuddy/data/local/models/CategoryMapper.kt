package com.example.speechbuddy.data.local.models

import com.example.speechbuddy.domain.models.Category
import com.example.speechbuddy.domain.utils.DomainMapper

class CategoryMapper : DomainMapper<CategoryEntity, Category> {

    override fun mapToDomainModel(model: CategoryEntity): Category {
        return Category(
            id = model.id,
            text = model.text
        )
    }

    override fun mapFromDomainModel(domainModel: Category): CategoryEntity {
        return CategoryEntity(
            id = domainModel.id,
            text = domainModel.text
        )
    }

}