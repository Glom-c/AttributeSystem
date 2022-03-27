package com.skillw.attsystem.api.read

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.status.Status
import com.skillw.pouvoir.api.able.Keyable
import org.bukkit.entity.LivingEntity
import taboolib.module.chat.TellrawJson

abstract class ReadPattern(
    override val key: String
) : Keyable<String> {
    var release = false
    abstract fun read(
        string: String,
        attribute: Attribute,
        livingEntity: LivingEntity?,
        slot: String
    ): Status?

    fun read(
        string: String,
        attribute: Attribute,
        livingEntity: LivingEntity?
    ): Status? {
        return read(string, attribute, livingEntity, "null")
    }

    open fun readNBT(
        map: Map<String, Any>,
        attribute: Attribute
    ): Status? {
        return null
    }

    abstract fun placeholder(
        key: String,
        attribute: Attribute,
        status: Status,
        livingEntity: LivingEntity? = null
    ): Any


    open fun stat(
        attribute: Attribute,
        status: Status,
        livingEntity: LivingEntity?
    ): TellrawJson {
        return TellrawJson()
    }

    override fun register() {
        AttributeSystem.readPatternManager.register(this)
    }
}