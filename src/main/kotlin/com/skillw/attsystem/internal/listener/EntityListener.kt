package com.skillw.attsystem.internal.listener

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import taboolib.common.platform.event.SubscribeEvent

object EntityListener {

    @SubscribeEvent
    fun onEntityDead(event: EntityDeathEvent) {
        val livingEntity = event.entity
        if (livingEntity !is Player) {
            AttributeSystem.attributeSystemAPI.remove(livingEntity.uniqueId)
        }
    }

    @SubscribeEvent
    fun onEntityDead(event: EntityRemoveFromWorldEvent) {
        val livingEntity = event.entity
        AttributeSystem.attributeSystemAPI.remove(livingEntity.uniqueId)
    }

    @SubscribeEvent
    fun onMythicMobsSpawn(event: MythicMobSpawnEvent) {
        val attributes = event.mobType.config.getStringList("Attributes")
        val uuid = event.entity.uniqueId
        AttributeSystem.attributeSystemAPI.update(uuid)
        attributeDataManager.addAttribute(uuid, "MYTHIC-BASE-ATTRIBUTE", attributes, false)
    }
}