package com.skillw.attsystem.api

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import java.util.*

interface AttributeSystemAPI : Manager {

    fun read(oriented: Attribute.Oriented, livingEntity: LivingEntity?, vararg strings: String): AttributeData
    fun read(oriented: Attribute.Oriented, strings: List<String>, livingEntity: LivingEntity?): AttributeData
    fun read(oriented: Attribute.Oriented, uuid: UUID, vararg strings: String): AttributeData
    fun read(oriented: Attribute.Oriented, strings: List<String>, uuid: UUID): AttributeData
    fun read(
        oriented: Attribute.Oriented,
        slot: String,
        livingEntity: LivingEntity?,
        vararg strings: String
    ): AttributeData

    fun read(
        oriented: Attribute.Oriented, strings: List<String>, livingEntity: LivingEntity?,
        slot: String
    ): AttributeData

    fun read(
        oriented: Attribute.Oriented,
        slot: String, uuid: UUID, vararg strings: String
    ): AttributeData

    fun read(
        oriented: Attribute.Oriented, strings: List<String>, uuid: UUID,
        slot: String
    ): AttributeData

    fun update(entity: Entity)
    fun update(uuid: UUID)
    fun updateAll()
    fun remove(entity: Entity)
    fun remove(uuid: UUID)
}