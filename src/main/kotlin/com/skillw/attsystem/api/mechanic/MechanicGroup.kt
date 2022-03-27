package com.skillw.attsystem.api.mechanic

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.debug
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.trigger.Trigger
import com.skillw.attsystem.internal.trigger.holder.DamageTriggerHolder
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.LinkedKeyMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player

class MechanicGroup constructor(
    override val key: String,
    val trigger: Trigger
) : Keyable<String>,
    ConfigurationSerializable, LinkedKeyMap<DamageType, MechanicData>() {

    val damageTypes = this.list

    fun run(originData: FightData): Double {
        var result = 0.0
        debug("&7Attacker: &c${originData["attacker-name"]}")
        debug("&7Defender: &c${originData["defender-name"]}")
        for (index in damageTypes.indices) {
            val type = damageTypes[index]
            debug(" &8-> &eDamageType: &6${type.key}")
            val fightData = FightData(originData)
            this[type]!!.run(fightData)
            fightData.forEach {
                originData["${type.key}-${it.key}"] = it.value
            }
            debug("  &7- &dMessage:")
            val attacker = originData.attacker
            val defender = originData.defender
            if (attacker is Player) {
                val message = type.attackMessage(attacker, fightData, index == 0)
                if (message != null)
                    originData.attackMessage.add(message)
            }
            if (defender is Player) {
                val message = type.defendMessage(defender, fightData, index == 0)
                if (message != null)
                    originData.defendMessage.add(message)
            }
            result += fightData.result
        }
        debug("&7Result: &c$result")
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["trigger"] = trigger.key
        for (damageType in damageTypes) {
            map[damageType.key] = this[damageType]?.serialize() ?: continue
        }
        return map
    }

    init {
        AttributeSystem.triggerManager.bind(trigger) {
            val originData = if (it is DamageTriggerHolder) it.fightData else return@bind
            val fightData = FightData(originData)
            val result = run(fightData)
            originData.attackMessage.addAll(fightData.attackMessage)
            originData.defendMessage.addAll(fightData.defendMessage)
            originData.setResult(result)
        }
    }

    companion object {
        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): MechanicGroup? {
            val key = section.name
            val triggerKey = section.getString("trigger")?.lowercase() ?: return null
            val trigger = AttributeSystem.triggerManager.getTrigger(triggerKey) ?: return null
            val mechanicGroup = MechanicGroup(key, trigger)
            for (damageTypeKey in section.getKeys(false)) {
                val damageType = AttributeSystem.damageTypeManager[damageTypeKey] ?: continue
                mechanicGroup[damageType] =
                    MechanicData.deserialize(section.getConfigurationSection(damageTypeKey)!!) ?: continue
            }
            return mechanicGroup
        }
    }

    override fun register() {
        AttributeSystem.mechanicGroupManager.register(this)
    }
}