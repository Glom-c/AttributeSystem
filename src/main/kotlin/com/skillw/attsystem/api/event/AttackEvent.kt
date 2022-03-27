package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.event.Time
import taboolib.common.platform.event.ProxyEvent

class AttackEvent(val time: Time, val fightData: FightData, val trigger: String) : ProxyEvent() {
    override val allowCancelled = true
}