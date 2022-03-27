package com.skillw.attsystem.api.attribute.status

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.operation.Operation
import com.skillw.attsystem.api.operation.Plus
import com.skillw.attsystem.api.read.ReadGroup
import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagType

class AttributeStatus : LowerMap<Double>(), ConfigurationSerializable {

    override fun toString(): String {
        return map.toString()
    }

    fun clone(): AttributeStatus {
        val attributeStatus = AttributeStatus()
        this.forEach {
            attributeStatus.register(it.key, it.value)
        }
        return attributeStatus
    }

    override fun get(key: String): Double {
        return super.get(key) ?: 0.0
    }

    fun opposite(): AttributeStatus {
        this.replaceAll { k, _ -> -get(k) }
        return this
    }

    fun operation(key: String, value: Double, readGroup: ReadGroup): AttributeStatus {
        return operation(key, value, readGroup.operations[key] ?: Plus)
    }

    fun operation(attributeStatus: AttributeStatus, readGroup: ReadGroup): AttributeStatus {
        for (key in attributeStatus.keys) {
            if (this.containsKey(key)) {
                this.register(key, readGroup.operations[key]!!.run(get(key), attributeStatus[key]))
            } else {
                this.register(key, attributeStatus[key])
            }
        }
        return this
    }

    fun operation(key: String, value: Double, operation: Operation): AttributeStatus {
        if (this.containsKey(key)) {
            this.register(key, operation.run(get(key), value))
        } else {
            this.register(key, value)
        }
        return this
    }

    fun operation(attributeStatus: AttributeStatus, operation: Operation): AttributeStatus {
        for (key in attributeStatus.keys) {
            if (this.containsKey(key)) {
                this.register(key, operation.run(get(key), attributeStatus[key]))
            } else {
                this.register(key, attributeStatus[key])
            }
        }
        return this
    }

    fun get(
        attribute: Attribute,
        key: String,
        livingEntity: LivingEntity? = null
    ): Double {
        return attribute.get(key, this, livingEntity)
    }

    companion object {
        @JvmStatic
        fun deserialize(itemTag: ItemTag): AttributeStatus? {
            return try {
                val status = AttributeStatus()
                itemTag.forEach {
                    val value = it.value
                    if (value.type == ItemTagType.DOUBLE) {
                        status[it.key] = value.asDouble()
                    }
                }
                return status
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return HashMap(this)
    }

}