package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.ItemCooldownEvent
import com.skillw.attsystem.api.manager.CooldownManager
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.MapUtils.putFast
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.platform.util.isAir
import java.util.*
import kotlin.math.roundToInt

object CooldownManagerImpl : CooldownManager() {
    override val key = "CooldownManager"
    override val priority: Int = 13
    override val subPouvoir = AttributeSystem

    private val nowCooldowns = BaseMap<UUID, MutableMap<Material, Double>>()
    private val originCooldowns = this
    private val tasks = BaseMap<UUID, MutableMap<Material, BukkitTask>>()

    override fun remove(key: UUID, material: Material) {
        if (nowCooldowns.containsKey(key)) nowCooldowns[key]!!.remove(material)
        if (originCooldowns.containsKey(key)) originCooldowns[key]!!.remove(material)
        if (tasks.containsKey(key) && tasks[key]!!.containsKey(material)) {
            tasks[key]!![material]!!.cancel()
            tasks[key]!!.remove(material)
        }
    }

    override fun remove(player: Player, material: Material) {
        remove(player.uniqueId, material)
    }

    override fun push(key: UUID, material: Material, time: Double) {
        if (tasks.containsKey(key) && tasks[key]!!.containsKey(material)) {
            remove(key, material)
        }
        nowCooldowns.putFast(key, material, time)
        this.putFast(key, material, time)
        tasks.putFast(key, material, object : BukkitRunnable() {
            var count = 0
            val stop = time
            override fun run() {
                if (this.isCancelled) {
                    return
                }
                count++
                val timeNow = nowCooldowns[key]!![material]!! - 1.0
                nowCooldowns.putFast(key, material, timeNow)
                if (count >= stop) {
                    remove(key, material)
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(AttributeSystem.plugin, 0, 1))
    }

    override fun push(player: Player, material: Material, time: Double) {
        return push(player.uniqueId, material, time)
    }

    override fun pull(key: UUID, material: Material): Double {
        if (!nowCooldowns.containsKey(key) || !nowCooldowns[key]!!.containsKey(material)) {
            return 1.0
        }
        val origin = originCooldowns[key]!![material]!!
        val now = nowCooldowns[key]!![material]!!
        val value = (origin - now) / origin
        remove(key, material)
        return value
    }

    override fun pull(player: Player, material: Material): Double {
        return pull(player.uniqueId, material)
    }

    override fun getItemCoolDown(player: Player, material: Material): Int {
        return player.getCooldown(material)
    }

    override fun isItemCoolDown(player: Player, slot: Int): Boolean {
        return getItemCoolDown(player, slot) > 0
    }

    override fun isItemCoolDown(player: Player, itemStack: ItemStack): Boolean {
        return getItemCoolDown(player, itemStack) > 0
    }

    override fun setItemCoolDown(player: Player, material: Material, attackSpeed: Double) {
        if (attackSpeed <= 0.0) {
            return
        }
        if (ASConfig.disableCooldownTypes.contains(material)) return
        val originCoolDown: Double = 1 / attackSpeed
        val coolDownByTick = (originCoolDown * 20)
        push(player.uniqueId, material, coolDownByTick)
        player.setCooldown(material, coolDownByTick.roundToInt())
    }

    override fun setItemCoolDown(player: Player, slot: Int, attackSpeed: Double) {
        if (attackSpeed <= 0.0) {
            return
        }
        val item = player.inventory.getItem(slot)
        if (item.isAir()) return
        val event = ItemCooldownEvent(player, item!!, attackSpeed)
        event.call()
        if (!event.isCancelled)
            setItemCoolDown(player, item.type, event.cooldown)
    }

    override fun setItemCoolDown(player: Player, itemStack: ItemStack, attackSpeed: Double) {
        if (attackSpeed <= 0.0) {
            return
        }
        if (itemStack.isAir()) return
        setItemCoolDown(player, itemStack.type, attackSpeed)
    }

    override fun getItemCoolDown(player: Player, itemStack: ItemStack): Int {
        if (itemStack.isAir()) {
            return 0
        }
        val material = itemStack.type
        return player.getCooldown(material)
    }


    override fun getItemCoolDown(player: Player, slot: Int): Int {
        val item = player.inventory.getItem(slot)
        if (item.isAir()) {
            return 0
        }
        val material = item!!.type
        return player.getCooldown(material)
    }
}