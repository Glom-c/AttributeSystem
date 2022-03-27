package com.skillw.attsystem.api.attribute.compound

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.status.AttributeStatus
import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagType
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.*

class AttributeDataCompound : LowerMap<AttributeData> {
    constructor()
    constructor(attributeDataCompound: AttributeDataCompound) {
        for (attKey in attributeDataCompound.keys) {
            val attributeData = attributeDataCompound[attKey]!!
            this[attKey] = attributeData.clone()
        }
    }

    fun clone(): AttributeDataCompound {
        return AttributeDataCompound(this)
    }

    override fun toString(): String {
        return map.toString()
    }

    fun hasAttribute(key: String): Boolean {
        return hasAttribute(attributeManager[key] ?: return false)
    }

    fun hasAttribute(attribute: Attribute): Boolean {
        return this.any { it.value.containsKey(attribute) }
    }

    val combatValue: Double
        get() {
            var value = 0.0
            for (data in this.values) {
                value += data.combatValue
            }
            return value
        }

    fun register(uuid: UUID) {
        AttributeSystem.attributeDataManager.register(uuid, this)
    }

    fun getAttributeStatus(attribute: Attribute): AttributeStatus {
        return this.getAttributeStatus(attribute.key)
    }

    fun getAttributeStatus(attributeKey: String): AttributeStatus {
        val attributeStatus = AttributeStatus()
        for (attributeData in this.values) {
            for (attribute in attributeData.keys) {
                if (attribute.key == attributeKey) {
                    attributeStatus.operation(attributeData[attribute.key] ?: continue, attribute.readGroup)
                }
            }
        }
        return attributeStatus
    }

    fun toAttributeData(): AttributeData {
        val attributeData = AttributeData()
        this.forEach {
            attributeData.operation(it.value)
        }
        return attributeData
    }

    fun operation(attributeDataCompound: AttributeDataCompound): AttributeDataCompound {
        attributeDataCompound.forEach { (key, attributeData) ->
            if (this.containsKey(key)) {
                this[key]!!.operation(attributeData)
            } else {
                this[key] = attributeDataCompound[key]!!
            }
        }
        return this
    }

    fun getAttributeTotal(attribute: Attribute): Double {
        return attribute.total(this)
    }

    fun getAttributeTotal(attribute: String): Double {
        return attributeManager[attribute]?.total(this) ?: 0.0
    }


    operator fun get(key: String, attribute: String): AttributeStatus {
        val attribute1: Attribute = attributeManager[attribute] as Attribute
        return this[key]?.get(attribute1) ?: AttributeStatus()
    }

    operator fun get(key: String, attribute: Attribute): AttributeStatus {
        return this[key]?.get(attribute) ?: AttributeStatus()
    }

    fun toItemTag(): ItemTag {
        val tag = ItemTag()
        for ((key, attributeData) in this) {
            tag[key] = attributeData.toItemTag()
        }
        return tag
    }

    fun save(itemStack: ItemStack): ItemStack {
        val tag = itemStack.getItemTag()
        tag["ATTRIBUTE_DATA"] = toItemTag()
        return itemStack.setItemTag(tag)
    }

    companion object {
        fun fromItemTag(
            itemTag: ItemTag,
            oriented: Attribute.Oriented = Attribute.Oriented.ALL
        ): AttributeDataCompound {
            val attributeDataCompound = AttributeDataCompound()
            for ((key, value) in itemTag) {
                if (value.type != ItemTagType.COMPOUND) continue
                val subTag = value.asCompound()
                attributeDataCompound[key] = AttributeData.fromItemTag(subTag, oriented).release()
            }
            return attributeDataCompound
        }

        fun fromItem(
            itemStack: ItemStack,
            oriented: Attribute.Oriented = Attribute.Oriented.ALL
        ): AttributeDataCompound? {
            val itemTag = itemStack.getItemTag()["ATTRIBUTE_DATA"]?.asCompound() ?: return null
            return fromItemTag(itemTag, oriented)
        }
    }
}