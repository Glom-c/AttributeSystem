package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.PlayerInFightEvent
import com.skillw.attsystem.api.event.PlayerOutFightEvent
import com.skillw.attsystem.api.manager.FightManager
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.platform.util.sendLang
import java.util.*

object FightManagerImpl : FightManager() {

    override val key = "FightManager"
    override val priority: Int = 13
    override val subPouvoir = AttributeSystem

    val fights = Collections.synchronizedCollection(HashSet<UUID>())
    val tasks = BaseMap<UUID, BukkitTask>()

    override fun isFighting(uuid: UUID): Boolean {
        return fights.contains(uuid)
    }

    override fun isFighting(entity: Entity): Boolean {
        return isFighting(entity.uniqueId)
    }

    override fun intoFighting(uuid: UUID) {
        if (!fights.contains(uuid)) {
            val event = PlayerInFightEvent(uuid.livingEntity() as? Player? ?: return)
            event.call()
            if (event.isCancelled) return
            (uuid.livingEntity() as? Player?)?.sendLang("fight-in")
        }
        tasks[uuid]?.cancel()
        tasks.remove(uuid)
        fights.add(uuid)
        tasks[uuid] = object : BukkitRunnable() {
            override fun run() {
                outFighting(uuid)
            }
        }.runTaskLaterAsynchronously(AttributeSystem.plugin, ASConfig.fightStatusTime)
    }

    override fun outFighting(uuid: UUID) {
        val event = PlayerOutFightEvent(uuid.livingEntity() as? Player? ?: return)
        event.call()
        if (event.isCancelled) return
        fights.remove(uuid)
        tasks[uuid]?.cancel()
        tasks.remove(uuid)
        (uuid.livingEntity() as? Player?)?.sendLang("fight-out")
    }

    override fun intoFighting(entity: Entity) {
        intoFighting(entity.uniqueId)
    }

    override fun outFighting(entity: Entity) {
        outFighting(entity.uniqueId)
    }

}