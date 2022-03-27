package com.skillw.attsystem.api.mechanic

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.MechanicRunEvent
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.event.Time


abstract class Mechanic(override val key: String) :
    Keyable<String> {

    abstract fun apply(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any?

    var config = false
    fun run(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any? {
        val pre = MechanicRunEvent(Time.BEFORE, this, fightData, context, damageType, null)
        pre.call()
        if (pre.isCancelled) return null
        val result = apply(fightData, context, damageType)
        val after = MechanicRunEvent(Time.AFTER, this, fightData, context, damageType, result)
        after.call()
        if (after.isCancelled) return null
        return after.result
    }

    final override fun register() {
        AttributeSystem.mechanicManager.register(this)
    }

}