package com.skillw.attsystem.internal.trigger.holder

import com.skillw.attsystem.api.fight.FightData
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MythicTriggerHolder(key: String, fightData: FightData, event: EntityDamageByEntityEvent) :
    DamageTriggerHolder(key, fightData, event) {
    constructor(damage: Int, fightData: FightData, event: EntityDamageByEntityEvent) : this(
        "mythic-mobs-$damage",
        fightData,
        event
    )
}