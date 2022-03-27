package com.skillw.attsystem.api.attribute.compound

import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.status.Status
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

class AttributeData : BaseMap<Attribute, Status> {
    constructor()
    constructor(release: Boolean) {
        this.release = release
    }

    constructor(attributeData: AttributeData, release: Boolean) {
        this.release = release
        for (attribute in attributeData.map.keys) {
            this[attribute] = attributeData.map[attribute]!!.clone()
        }
    }

    constructor(attributeData: AttributeData) : this(attributeData, false)

    var release = false

    fun release(): AttributeData {
        this.release = true
        return this
    }

    fun unRelease(): AttributeData {
        this.release = false
        return this
    }

    fun operation(attribute: Attribute, status: Status): AttributeData {
        if (!this.containsKey(attribute)) {
            this.register(attribute, status)
        } else {
            this[attribute] = this[attribute]!!.operation(status)
        }
        return this
    }

    fun operation(vararg attributeDataArray: AttributeData) {
        for (attributeData in attributeDataArray) {
            attributeData.map.forEach { (attribute, attributeStatus) ->
                this.operation(attribute, attributeStatus)
            }
        }
    }

    fun toCompound(key: String): AttributeDataCompound {
        val compound = AttributeDataCompound()
        compound.register(key, this)
        return compound
    }

    override fun toString(): String {
        return map.toString()
    }

    operator fun get(attributeKey: String): Status? {
        return this[attributeManager[attributeKey] ?: return null]
    }

    fun clone(): AttributeData {
        return AttributeData(this, release)
    }

    fun toItemTag(): ItemTag {
        val tag = ItemTag()
        for ((attribute, status) in this) {
            val value = ItemTag.toNBT(status.serialize())
            if (value.asCompound().isNullOrEmpty()) continue
            tag[attribute.key] = ItemTag.toNBT(status.serialize())
        }
        return tag
    }

    fun save(itemStack: ItemStack, key: String): ItemStack {
        val tag = itemStack.getItemTag()
        if (tag.containsKey("ATTRIBUTE_DATA")) {
            val compound = tag["ATTRIBUTE_DATA"]!!.asCompound()
            compound[key] = this.toItemTag()
            tag["ATTRIBUTE_DATA"] = compound
        } else {
            val compound = ItemTag()
            compound[key] = this.toItemTag()
            tag["ATTRIBUTE_DATA"] = compound
        }
        return itemStack.setItemTag(tag)
    }

    companion object {
        fun fromItemTag(itemTag: ItemTag): AttributeData {
            val attributeData = AttributeData()
            for ((attKey, value) in itemTag) {
                val attribute = attributeManager[attKey] ?: continue
                val attributeStatus =
                    attribute.readPattern.readNBT(value.asCompound().mapValues { it.value.unsafeData() }, attribute)
                        ?: continue
                attributeData.register(attribute, attributeStatus)
            }
            return attributeData
        }
    }
}