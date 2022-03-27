package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.debug
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.function.Function2to1
import com.skillw.attsystem.api.manager.TriggerManager
import com.skillw.attsystem.api.trigger.Trigger
import com.skillw.attsystem.api.trigger.TriggerHolder
import com.skillw.attsystem.internal.trigger.*
import com.skillw.attsystem.internal.trigger.holder.*
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.MapUtils.addSingle
import com.sucy.skill.api.event.SkillDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

object TriggerManagerImpl : TriggerManager() {
    override val key = "TriggerManager"
    override val priority: Int = 12
    override val subPouvoir = AttributeSystem

    override val triggerBuilders = LinkedList<Function<String, Trigger?>>()
    override val triggerHolderBuilders = LinkedList<Function2to1<String, FightData, TriggerHolder?>>()

    override val handlers = BaseMap<Trigger, LinkedList<(TriggerHolder) -> Unit>>()

    override fun call(triggerHolder: TriggerHolder) {
        call(triggerHolder) {}
    }

    override fun getTrigger(key: String): Trigger? {
        for (func in triggerBuilders) {
            return func.apply(key) ?: continue
        }
        return null
    }

    override fun getTriggerHolder(key: String, fightData: FightData): TriggerHolder? {
        for (func in triggerHolderBuilders) {
            return func.invoke(key, fightData) ?: continue
        }
        return null
    }

    override fun call(triggerHolder: TriggerHolder, consumer: Consumer<TriggerHolder>) {
        handlers[this[triggerHolder.key] ?: return]?.forEach { func ->
            debug("&7Trigger: &c${triggerHolder.key}")
            func.invoke(triggerHolder)
            consumer.accept(triggerHolder)
        }
    }

    override fun bind(trigger: Trigger, func: (TriggerHolder) -> Unit) {
        if (!this.containsKey(trigger.key))
            this.register(trigger)
        handlers.addSingle(trigger) {
            func(it)
        }
    }

    override fun onEnable() {
        triggerBuilders.add { key ->
            return@add when {
                key == "attack" -> AttackTrigger
                key.startsWith("mythic-mobs") -> MythicMobsTrigger(key)
                key.startsWith("origin-skill") -> OriginSkillTrigger(key)
                key.startsWith("skill-api") -> SkillAPITrigger(key)
                key.startsWith("damage-cause") -> DamageEventTrigger(key)
                key.startsWith("event") -> EventTrigger(key)
                else -> null
            }
        }
        triggerHolderBuilders.add { triggerKey, eventFightData ->
            return@add when {
                triggerKey == "attack" -> AttackTriggerHolder(
                    eventFightData,
                    eventFightData["event"] as EntityDamageByEntityEvent
                )
                triggerKey.startsWith("mythic-mobs") -> MythicTriggerHolder(
                    triggerKey,
                    eventFightData,
                    eventFightData["event"] as EntityDamageByEntityEvent
                )
                triggerKey.startsWith("origin-skill") -> OriginSkillTriggerHolder(
                    triggerKey,
                    eventFightData,
                    eventFightData["event"] as EntityDamageByEntityEvent
                )
                triggerKey.startsWith("skill-api") -> SkillAPITriggerHolder(
                    triggerKey,
                    eventFightData,
                    eventFightData["event"] as SkillDamageEvent
                )
                triggerKey.startsWith("damage-cause") -> DamageTriggerHolder(
                    triggerKey,
                    eventFightData,
                    eventFightData["event"] as EntityDamageEvent
                )
                else -> null
            }
        }
        onReload()
    }

    override fun onReload() {
        this.clear()
        handlers.clear()
        AttackTrigger.register()
    }

}