package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.mechanic.Mechanic
import com.skillw.pouvoir.api.event.Time
import taboolib.common.platform.event.ProxyEvent

class MechanicRunEvent(
    val time: Time,
    val mechanic: Mechanic,
    val fightData: FightData,
    val context: Map<String, Any>,
    val damageType: DamageType,
    var result: Any?
) : ProxyEvent() {
    override val allowCancelled = true
}