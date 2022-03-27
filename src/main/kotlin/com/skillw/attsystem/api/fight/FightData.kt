package com.skillw.attsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.debug
import com.skillw.attsystem.internal.message.Message
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.MessageUtils.wrong
import com.skillw.pouvoir.util.StringUtils.parse
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.util.asList
import taboolib.common5.Coerce
import taboolib.module.nms.getI18nName
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FightData(val attacker: LivingEntity?, val defender: LivingEntity) : BaseMap<String, Any>() {
    constructor(attacker: LivingEntity?, defender: LivingEntity, run: FightData.() -> Unit) : this(
        attacker,
        defender
    ) {
        run.invoke(this)
    }

    val attackMessage = LinkedList<Message>()
    val defendMessage = LinkedList<Message>()

    override fun put(key: String, value: Any): Any {
        if (key == "result" && value is BigDecimal) {
            wrong("please use FightData#setDamage(Double) !")
            return value
        }
        return super.put(key, value)
    }

    val result: Double
        get() {
            if (!this.containsKey("result")) {
                this["result"] = 0.0
            }
            return this["result"] as Double
        }

    fun setResult(value: Double) {
        this["result"] = value
    }

    fun addResult(value: Double) {
        this["result"] = result + value
    }

    init {
        this["attacker-name"] =
            if (attacker is Player) attacker.displayName
            else
                (if (attacker?.customName == null) attacker?.getI18nName() else attacker.customName)
                    ?: "null"
        this["defender-name"] =
            if (defender is Player) defender.displayName
            else
                (if (defender.customName == null) defender.getI18nName() else defender.customName)!!
    }

    constructor(fightData: FightData) : this(fightData.attacker, fightData.defender) {
        this.putAll(fightData)
    }

    init {
        try {
            if (attacker != null && !AttributeSystem.attributeDataManager.containsKey(attacker.uniqueId))
                AttributeSystem.attributeSystemAPI.update(attacker.uniqueId)
            if (!AttributeSystem.attributeDataManager.containsKey(defender.uniqueId))
                AttributeSystem.attributeSystemAPI.update(defender.uniqueId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <K, V> handleMap(map: Map<K, V>): Map<String, Any> {
        val newMap = ConcurrentHashMap<String, Any>()
        map.forEach { entry ->
            newMap[handle(entry.key.toString())] = handle(entry.value!!)
        }
        return newMap
    }

    /**
     * 解析Any
     *
     * @param any 字符串/字符串集合/Map
     * @return
     */
    fun handle(any: Any): Any {
        if (any is String) {
            return handle(any)
        }
        if (any is List<*>) {
            if (any.isEmpty()) return "[]"
            if (any[0] is Map<*, *>) {
                val mapList = Coerce.toListOf(any, Map::class.java)
                val newList = LinkedList<Map<*, *>>()
                mapList.forEach {
                    newList.add(handleMap(it))
                }
                return newList
            }
            return handle(any.asList())
        }
        if (any is Map<*, *>) {
            return handleMap(any)
        }
        return any
    }

    fun handle(string: String): String {
        var formula = string
        val list = formula.parse('{', '}')
        for (str in list) {
            when {
                str.startsWith("a.") -> {
                    val value = Pouvoir.pouPlaceHolderAPI.replace(attacker, "%${str.split("a.")[1]}%")
                    formula = formula.replace(
                        "{$str}",
                        value
                    )
                    debug("      &3{$str} &7-> &9${value.replace("&", "")}")
                    continue
                }
                str.startsWith("d.") -> {
                    val value = Pouvoir.pouPlaceHolderAPI.replace(defender, "%${str.split("d.")[1]}%")
                    formula = formula.replace(
                        "{$str}",
                        value
                    )
                    debug("      &3{$str} &7-> &9${value.replace("&", "")}")
                    continue
                }
                else -> {
                    val replacement = this[str] ?: continue
                    formula = formula.replace("{$str}", replacement.toString())
                    debug("      &3{$str} &7-> &9${replacement.toString().replace("&", "")}")
                    continue
                }
            }
        }
        val value = Pouvoir.functionManager.analysis(formula)
        debug("     &3$formula &7-> &9${value.replace("&", "")}")
        return value
    }

    fun handle(strings: Collection<String>): List<String> {
        val list = LinkedList<String>()
        strings.forEach {
            list.add(handle(it))
        }
        return list
    }
}