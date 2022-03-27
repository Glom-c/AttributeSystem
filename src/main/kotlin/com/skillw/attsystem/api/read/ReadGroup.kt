package com.skillw.attsystem.api.read

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.attribute.status.AttributeStatus
import com.skillw.attsystem.api.operation.Operation
import com.skillw.attsystem.api.operation.Plus
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.util.Format.real
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.LowerMap
import com.skillw.pouvoir.util.CalculationUtils.resultDouble
import com.skillw.pouvoir.util.MapUtils.addSingle
import com.skillw.pouvoir.util.MessageUtils.wrong
import com.skillw.pouvoir.util.StringUtils.replacement
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class ReadGroup(
    override val key: String,
    val totalFormula: String,
    private val patternStrings: MutableList<String>,
    val placeholders: ReadPlaceholder
) : Keyable<String>, ConfigurationSerializable {

    private val keyPattern = Pattern.compile("<(?<key>.*?)>")
    val operations = LowerMap<Operation>()

    init {
        val tempList = LinkedList(patternStrings).replacement(mapOf("<name>" to "NULL"))
        for (index in tempList.indices) {
            val patternStr = tempList[index]
            val matcher = keyPattern.matcher(patternStr)
            if (!matcher.find()) continue
            do {
                var key = matcher.group("key") ?: continue
                var operation: Operation? = null
                if (key.contains(":")) {
                    val array = key.split(":")
                    operation = AttributeSystem.operationManager[array[0]]
                    if (operation != null) {
                        key = array[1]
                    }
                }
                if (operation == null) operation = Plus
                operations.put(key, operation)
            } while (matcher.find())
        }
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ReadGroup? {
            return try {
                return ReadGroup(
                    section.name,
                    section.getString("total").toString(),
                    section.getStringList("patterns"),
                    ReadPlaceholder(section.getConfigurationSection("placeholder"))
                )
            } catch (e: Exception) {
                wrong("An error occurred while loading ReadGroup ${section.name} ！")
                wrong("Cause: ${e.cause.toString()}")
                null
            }
        }
    }

    private fun getPatterns(name: String): ReadPattern {
        val tempList = LinkedList(patternStrings).replacement(mapOf("<name>" to name))
        val patterns = LinkedList<Pattern>()
        val keys = ConcurrentHashMap<Int, HashSet<String>>()
        val numberPattern = ASConfig.numberPattern
        for (index in tempList.indices) {
            val patternStr = tempList[index]
            val matcher = keyPattern.matcher(patternStr)
            if (!matcher.find()) continue
            val stringBuffer = StringBuffer()
            do {
                var key = matcher.group("key") ?: continue
                if (key.contains(":")) {
                    val array = key.split(":")
                    key = array[1]
                }
                matcher.appendReplacement(stringBuffer, numberPattern.replace("value", key))
                keys.addSingle(index, key)
            } while (matcher.find())
            val symbol = ASConfig.lineConditionSymbol
            patterns.add(
                Pattern.compile(
                    matcher.appendTail(stringBuffer)
                        .toString() + "((?!$symbol)|$symbol(?<requirement>.*))"
                )
            )
        }
        return ReadPattern(keys, patterns)
    }

    fun getPatterns(names: List<String>): List<ReadPattern> {
        val list = LinkedList<ReadPattern>()
        names.forEach {
            list.add(getPatterns(it))
        }
        return list
    }

    override fun register() {
        AttributeSystem.readGroupManager.register(this)
    }

    internal fun get(
        key: String,
        status: AttributeStatus,
        attribute: Attribute,
        livingEntity: LivingEntity? = null
    ): Double {
        val formula = placeholders[key] ?: return 0.0
        val replacement = ConcurrentHashMap<String, String>()
        attribute.statusKeys.forEach {
            replacement["<$it>"] = status[it].real()
        }
        replacement["<total>"] = totalFormula.replacement(replacement)
        return formula.resultDouble(livingEntity, replacement)
    }

    internal fun total(
        compound: AttributeDataCompound,
        attribute: Attribute,
        livingEntity: LivingEntity? = null
    ): Double {
        val status = compound.getAttributeStatus(attribute)
        val replacement = ConcurrentHashMap<String, String>()
        attribute.statusKeys.forEach {
            replacement["<$it>"] = status[it].real()
        }
        return totalFormula.resultDouble(livingEntity, replacement)
    }


    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["total"] = totalFormula
        map["patterns"] = patternStrings
        return map
    }

}

