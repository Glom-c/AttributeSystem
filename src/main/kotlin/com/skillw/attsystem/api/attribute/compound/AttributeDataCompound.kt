package com.skillw.attsystem.api.attribute.compound

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.status.Status
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

    fun register(uuid: UUID) {
        AttributeSystem.attributeDataManager.register(uuid, this)
    }

    fun getAttributeStatus(attribute: Attribute): Status? {
        return this.getAttributeStatus(attribute.key)
    }

    fun getAttributeStatus(attributeKey: String): Status? {
        var attributeStatus: Status? = null
        for (attributeData in this.values) {
            for (attribute in attributeData.keys) {
                if (attribute.key == attributeKey) {
                    val other = attributeData[attribute.key]?.clone() ?: continue
                    if (attributeStatus == null) attributeStatus = other
                    else attributeStatus.operation(other)
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

    operator fun get(key: String, attribute: String): Status? {
        val attribute1: Attribute = attributeManager[attribute] as Attribute
        return this[key]?.get(attribute1)
    }

    operator fun get(key: String, attribute: Attribute): Status? {
        return this[key]?.get(attribute)
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
            itemTag: ItemTag
        ): AttributeDataCompound {
            val attributeDataCompound = AttributeDataCompound()
            for ((key, value) in itemTag) {
                if (value.type != ItemTagType.COMPOUND) continue
                val subTag = value.asCompound()
                attributeDataCompound[key] = AttributeData.fromItemTag(subTag).release()
            }
            return attributeDataCompound
        }

        fun fromItem(
            itemStack: ItemStack
        ): AttributeDataCompound? {
            val itemTag = itemStack.getItemTag()["ATTRIBUTE_DATA"]?.asCompound() ?: return null
            return fromItemTag(itemTag)
        }
    }

}