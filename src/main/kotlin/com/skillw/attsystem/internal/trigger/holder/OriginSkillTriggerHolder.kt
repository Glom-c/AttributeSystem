package com.skillw.attsystem.internal.trigger.holder

import com.skillw.attsystem.api.fight.FightData
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OriginSkillTriggerHolder(key: String, fightData: FightData, event: EntityDamageByEntityEvent) :
    DamageTriggerHolder(key, fightData, event) {
    constructor(damage: Int, fightData: FightData, event: EntityDamageByEntityEvent) : this(
        "origin-skill-$damage",
        fightData,
        event
    )
}