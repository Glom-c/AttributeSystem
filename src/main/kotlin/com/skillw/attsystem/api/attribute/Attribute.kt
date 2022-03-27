package com.skillw.attsystem.api.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.attribute.status.AttributeStatus
import com.skillw.attsystem.api.operation.Plus
import com.skillw.attsystem.api.read.ReadGroup
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.attsystem.internal.manager.ConditionManagerImpl.lineConditions
import com.skillw.pouvoir.api.`object`.BaseObject
import com.skillw.pouvoir.util.MessageUtils.wrong
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import taboolib.module.chat.uncolored
import taboolib.platform.util.sendLang
import java.util.*
import java.util.regex.Matcher

class Attribute(
    override val key: String,
    override val priority: Int,
    val names: List<String>,
    val readGroup: ReadGroup,
    val oriented: Oriented
) : BaseObject {
    val patternList = LinkedList<ReadPattern>()
    internal val statusKeys = HashSet<String>()

    var config = false

    init {
        patternList.addAll(readGroup.getPatterns(names))
        try {
            patternList[0].map.values.forEach {
                statusKeys.addAll(it)
            }
        } catch (e: Exception) {
            wrong("Are you sure the ReadGroup &6${readGroup.key} &eis workable?")
        }
    }

    fun orientedEntity() = oriented == Oriented.ENTITY || oriented == Oriented.ALL
    fun orientedItem() = oriented == Oriented.ENTITY || oriented == Oriented.ALL
    fun orientedAll() = oriented == Oriented.ALL
    fun isOriented(oriented: Oriented) = this.oriented == oriented || this.orientedAll() || oriented == Oriented.ALL
    override fun register() {
        AttributeSystem.attributeManager.register(this)
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["priority"] = priority
        map["oriented"] = oriented.name.lowercase()
        map["names"] = names.toList()
        map["read-group"] = readGroup.key
        return map
    }

    fun total(attributeDataCompound: AttributeDataCompound): Double {
        return readGroup.total(attributeDataCompound, this)
    }

    fun get(
        key: String,
        compound: AttributeDataCompound,
        livingEntity: LivingEntity? = null
    ): Double {
        return readGroup.get(key, compound.getAttributeStatus(this), this, livingEntity)
    }

    fun get(
        key: String,
        status: AttributeStatus,
        livingEntity: LivingEntity? = null
    ): Double {
        return readGroup.get(key, status, this, livingEntity)
    }

    fun isAttribute(string: String): Boolean {
        return patternList.any { it.patterns.any { pattern -> pattern.matcher(string).find() } }
    }


    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): Attribute? {
            try {
                val key = section.name
                val priority = section["priority"].toString().toInt()
                val names = section.getStringList("names")
                val oriented = try {
                    Oriented.valueOf(section["oriented"].toString().uppercase())
                } catch (e: Exception) {
                    Oriented.ENTITY
                }
                val readGroup = AttributeSystem.readGroupManager[section.getString("read-group") ?: "Default"]
                if (readGroup == null) {
                    wrong("The ReadGroup of &6$key &eis null!")
                    return null
                }
                val attribute = Attribute(key, priority, names, readGroup, oriented)
                attribute.config = true
                return attribute
            } catch (e: Exception) {
                Bukkit.getConsoleSender().sendLang("error.attribute-load", section["key"].toString())
                e.printStackTrace()
            }
            return null
        }
    }

    fun read(
        string: String,
        livingEntity: LivingEntity?,
        slot: String
    ): AttributeStatus? {
        if (AttributeSystem.configManager.ignores.any { string.contains(it) }) return null
        val attributeStatus = AttributeStatus()
        val temp = string.uncolored().replace(Regex("§#.{6}"), "")
        var isAttribute = false
        val keys = HashSet<String>()
        var matcher: Matcher? = null
        patternList@ for (readPattern in patternList) {
            val patterns = readPattern.patterns
            for (index in patterns.indices) {
                val pattern = patterns[index]
                val matcher1 = pattern.matcher(temp)
                if (!matcher1.find()) continue
                isAttribute = true
                matcher = matcher1
                keys.addAll(readPattern.map[index]!!)
                break@patternList
            }
        }
        if (!isAttribute || keys.isEmpty() || matcher == null) return null
        if (!lineConditions(slot, matcher, livingEntity)) return null
        try {
            for (key in keys) {
                val valueStr = matcher.group(key)
                val value = Coerce.asDouble(valueStr)
                if (!value.isPresent) {
                    wrong("The value &d$valueStr &ein &6$temp &emust be a Double!")
                    continue
                }
                val operation = readGroup.operations[key] ?: Plus
                attributeStatus.operation(key, value.get(), operation)
            }
        } catch (e: Exception) {
            wrong("Can't read the attribute &d${this.key} &ein &6$temp &e!(Wrong format / Wrong read group)")
            return attributeStatus
        }
        return attributeStatus
    }


    fun read(
        string: String,
        livingEntity: LivingEntity?
    ): AttributeStatus? {
        return read(string, livingEntity, "null")
    }


    override fun toString(): String {
        return "Attribute {key=$key}"
    }

    enum class Oriented {
        ENTITY, ITEM, ALL
    }
}
