package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.Entity
import java.util.*

abstract class AttributeDataManager : BaseMap<UUID, AttributeDataCompound>(), Manager {


    abstract var playerBaseAttribute: AttributeData
    abstract var entityBaseAttribute: AttributeData

    abstract fun updateAll()

    abstract fun update(entity: Entity): AttributeDataCompound?

    abstract fun update(uuid: UUID): AttributeDataCompound?

    abstract fun addAttribute(
        entity: Entity,
        key: String,
        attributes: List<String>,
        release: Boolean = false
    ): AttributeData?

    abstract fun addAttribute(
        entity: Entity, key: String, attributeData: AttributeData,
        release: Boolean = false
    ): AttributeData

    abstract fun addAttribute(
        uuid: UUID, key: String, attributes: List<String>,
        release: Boolean = false
    ): AttributeData?

    abstract fun addAttribute(
        uuid: UUID, key: String, attributeData: AttributeData,
        release: Boolean = false
    ): AttributeData

    abstract fun removeAttribute(entity: Entity, key: String)
    abstract fun removeAttribute(uuid: UUID, key: String)
}