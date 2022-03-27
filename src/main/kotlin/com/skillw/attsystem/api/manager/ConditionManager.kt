package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.condition.Condition
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap
import org.bukkit.entity.LivingEntity
import java.util.regex.Matcher

abstract class ConditionManager : LowerKeyMap<Condition>(), Manager {
    abstract fun conditionLine(slot: String, livingEntity: LivingEntity?, str: String): Boolean
    abstract fun conditionStrings(slot: String, livingEntity: LivingEntity?, strings: List<String>): Boolean
    abstract fun matches(str: String, type: Condition.ConditionType): Pair<Matcher, Condition>?
    abstract fun lineConditions(slot: String, matcher: Matcher, livingEntity: LivingEntity?): Boolean
}