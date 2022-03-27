package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.function.Function2to1
import com.skillw.attsystem.api.trigger.Trigger
import com.skillw.attsystem.api.trigger.TriggerHolder
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.api.map.KeyMap
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

abstract class TriggerManager : KeyMap<String, Trigger>(), Manager {
    abstract val handlers: BaseMap<Trigger, LinkedList<(TriggerHolder) -> Unit>>
    abstract val triggerBuilders: MutableList<Function<String, Trigger?>>
    abstract fun call(triggerHolder: TriggerHolder)
    abstract fun bind(trigger: Trigger, func: (TriggerHolder) -> Unit)
    abstract fun call(triggerHolder: TriggerHolder, consumer: Consumer<TriggerHolder>)
    abstract fun getTrigger(key: String): Trigger?
    abstract fun getTriggerHolder(key: String, fightData: FightData): TriggerHolder?
    abstract val triggerHolderBuilders: MutableList<Function2to1<String, FightData, TriggerHolder?>>
}