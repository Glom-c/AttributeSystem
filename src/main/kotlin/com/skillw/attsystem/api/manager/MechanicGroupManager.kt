package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.mechanic.MechanicGroup
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

abstract class MechanicGroupManager : KeyMap<String, MechanicGroup>(), Manager {
    abstract fun handle(
        triggerKey: String, originFightData: FightData
    ): Double
}