package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.AttackEvent
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.manager.MechanicGroupManager
import com.skillw.attsystem.api.mechanic.MechanicGroup
import com.skillw.attsystem.internal.message.Message
import com.skillw.attsystem.internal.message.Message.Companion.send
import com.skillw.pouvoir.api.event.Time
import com.skillw.pouvoir.util.FileUtils
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object MechanicGroupManagerImpl : MechanicGroupManager() {
    override val key = "MechanicGroupManager"
    override val priority: Int = 13
    override val subPouvoir = AttributeSystem

    override fun handle(
        triggerKey: String,
        originFightData: FightData
    ): Double {
        val attackMessages = LinkedList<Message>()
        val defendMessages = LinkedList<Message>()
        val attacker = originFightData.attacker as? Player?
        val defender = originFightData.defender as? Player?
        val result = AttributeSystem.poolExecutor.submit<Double> {
            val pre = AttackEvent(Time.BEFORE, originFightData, triggerKey)
            pre.call()
            if (pre.isCancelled) return@submit -1.0
            val eventFightData = pre.fightData
            AttributeSystem.triggerManager.getTriggerHolder(triggerKey, eventFightData)?.call()
                ?: return@submit -1.0
            val result = eventFightData.result
            if (attacker != null)
                attackMessages.addAll(eventFightData.attackMessage)
            if (defender != null)
                defendMessages.addAll(eventFightData.defendMessage)
            val after = AttackEvent(Time.AFTER, eventFightData, triggerKey)
            after.call()
            if (after.isCancelled) return@submit -1.0
            //"${type.key}-${it.key}"
            return@submit result
        }.get()
        AttributeSystem.poolExecutor.execute {
            if (attacker != null)
                attackMessages.send(Message.Type.ATTACK, attacker)
            if (defender != null)
                defendMessages.send(Message.Type.DEFEND, defender)
        }
        return result
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "fight"), MechanicGroup::class.java
        ).forEach {
            it.key.register()
        }
    }
}