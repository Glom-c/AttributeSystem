package com.skillw.attsystem.internal.listener

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitRunnable
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object PlayerListener {


    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                val player = event.player
                AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(player.uniqueId) }
            }
        }.runTaskLater(AttributeSystem.plugin, 10)
    }


    @SubscribeEvent
    fun onPlayerLeft(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        attributeSystemAPI.remove(uuid)
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) }
    }

    @SubscribeEvent
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        val player = event.player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val player = event.player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        val player = event.player
        val uuid = player.uniqueId
        submit(delay = 1, async = true) { attributeSystemAPI.update(uuid); cancel() }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) }
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val uuid = player.uniqueId
        AttributeSystem.poolExecutor.execute { AttributeSystem.poolExecutor.execute { attributeSystemAPI.update(uuid) } }
    }
}