package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.condition.Condition
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent
import java.util.regex.Matcher

class ConditionEvent(
    val condition: Condition,
    val entity: LivingEntity?,
    val matcher: Matcher,
    val text: String,
    var pass: Boolean = false
) : BukkitProxyEvent() {
    override val allowCancelled = true

}