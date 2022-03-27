package com.skillw.attsystem.internal.listener

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.sucy.skill.api.event.PlayerCastSkillEvent
import com.sucy.skill.api.event.SkillDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

object SkillAPIListener {
    @SubscribeEvent(EventPriority.LOWEST)
    fun e(event: SkillDamageEvent) {
        val attacker = event.damager
        val defender = event.target
        if (!attacker.isAlive() || !defender.isAlive()) {
            return
        }
        val originDamage = event.damage
        val triggerKey = "skill-api-${event.skill.key}-${event.classification}"
        if (!AttributeSystem.triggerManager.containsKey(triggerKey)) return
        val fightData = FightData(attacker, defender) { put("origin", originDamage); put("event", event) }
        val result = AttributeSystem.mechanicGroupManager.handle(triggerKey, fightData)
        event.damage = if (result == -1.0) originDamage else result
    }

    @SubscribeEvent(EventPriority.LOWEST)
    fun e(event: PlayerCastSkillEvent) {
        val origin = event.skill.cooldown
        event.skill.refreshCooldown()
        event.skill.addCooldown(
            AttributeSystem.formulaManager.calculate(
                event.player,
                "skill-speed",
                mapOf("cooldown" to origin.toString())
            )
        )
    }
}