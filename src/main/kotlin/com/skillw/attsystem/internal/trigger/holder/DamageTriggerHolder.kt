package com.skillw.attsystem.internal.trigger.holder

import com.skillw.attsystem.api.fight.FightData
import org.bukkit.event.Event

open class DamageTriggerHolder(
    override val key: String,
    val fightData: FightData,
    event: Event
) :
    EventTriggerHolder(key, event)