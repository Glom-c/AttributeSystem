package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.condition.Condition
import com.skillw.attsystem.api.condition.ScriptCondition
import com.skillw.attsystem.api.event.ConditionEvent
import com.skillw.attsystem.api.manager.ConditionManager
import com.skillw.pouvoir.util.FileUtils
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.console
import taboolib.module.chat.uncolored
import taboolib.module.lang.sendLang
import java.io.File
import java.util.regex.Matcher

object ConditionManagerImpl : ConditionManager() {
    override val key = "ConditionManager"
    override val priority: Int = 7
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.config }.forEach { this.remove(it.key) }
        console().sendLang("condition-reload-start")
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "conditions"), ScriptCondition::class.java
        ).forEach {
            it.key.register()
        }
        console().sendLang("condition-reload-end")
    }

    override fun matches(str: String, type: Condition.ConditionType): Pair<Matcher, Condition>? {
        for ((_, condition) in this) {
            if (!condition.isType(type)) continue
            for (name in condition.names) {
                val matcher = name.matcher(str)
                if (matcher.find()) {
                    return matcher to condition
                }
            }
        }
        return null
    }

    override fun lineConditions(slot: String, matcher: Matcher, livingEntity: LivingEntity?): Boolean {
        return try {
            val requirement = matcher.group("requirement") ?: return true
            val array: List<String> = if (requirement.contains(",")) {
                requirement.split(",")
            } else {
                listOf(requirement)
            }
            for (it in array) {
                if (!AttributeSystem.conditionManager.conditionLine(slot, livingEntity, it)) {
                    return false
                }
            }
            true
        } catch (e: Exception) {
            true
        }
    }

    private fun condition(
        slot: String,
        livingEntity: LivingEntity?,
        str: String,
        type: Condition.ConditionType
    ): Boolean {
        val pair = this.matches(str.uncolored().replace(Regex("§#.{6}"), ""), type) ?: return true
        val (matcher, condition1) = pair
        val pass = condition1.condition(slot, livingEntity, matcher, str)
        val event = ConditionEvent(condition1, livingEntity, matcher, str, pass)
        event.call()
        return event.pass
    }

    override fun conditionLine(slot: String, livingEntity: LivingEntity?, str: String): Boolean {
        return condition(slot, livingEntity, str, Condition.ConditionType.LINE)
    }

    override fun conditionStrings(slot: String, livingEntity: LivingEntity?, strings: List<String>): Boolean {
        for (str in strings) {
            if (!condition(slot, livingEntity, str, Condition.ConditionType.STRINGS)) return false
        }
        return true
    }


    override fun register(key: String, value: Condition) {
        super.register(key, value)
        console().sendLang("condition-register", value.key)
    }
}