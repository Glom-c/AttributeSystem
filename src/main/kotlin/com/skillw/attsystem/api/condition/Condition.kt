package com.skillw.attsystem.api.condition

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.able.Keyable
import org.bukkit.entity.LivingEntity
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class Condition(override val key: String, names: Set<String>, val type: ConditionType) : Keyable<String> {
    abstract fun condition(slot: String, livingEntity: LivingEntity?, matcher: Matcher, text: String): Boolean
    val names = HashSet<Pattern>()

    var config = false

    init {
        names.forEach {
            this.names.add(
                Pattern.compile(
                    "(?<!${ASConfig.lineConditionSymbol})" + it.replace(
                        "{symbol}",
                        ASConfig.lineConditionSymbol
                    )
                )
            )
        }
    }

    override fun register() {
        AttributeSystem.conditionManager.register(this)
    }

    fun typeLine() = type == ConditionType.LINE || type == ConditionType.ALL
    fun typeStrings() = type == ConditionType.STRINGS || type == ConditionType.ALL
    fun typeAll() = type == ConditionType.ALL
    fun isType(type: ConditionType) = this.type == type || this.typeAll()
    enum class ConditionType {
        LINE, STRINGS, ALL
    }
}

