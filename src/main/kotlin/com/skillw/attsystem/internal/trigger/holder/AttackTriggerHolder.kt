package com.skillw.attsystem.internal.trigger.holder

import com.skillw.attsystem.api.fight.FightData
import org.bukkit.event.entity.EntityDamageByEntityEvent

class AttackTriggerHolder(
    fightData: FightData,
    event: EntityDamageByEntityEvent
) :
    DamageTriggerHolder("attack", fightData, event)