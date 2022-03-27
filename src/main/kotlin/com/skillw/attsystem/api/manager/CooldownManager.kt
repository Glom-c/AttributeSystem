package com.skillw.attsystem.api.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class CooldownManager : BaseMap<UUID, MutableMap<Material, Double>>(), Manager {

    abstract fun push(key: UUID, material: Material, time: Double)
    abstract fun push(player: Player, material: Material, time: Double)
    abstract fun remove(key: UUID, material: Material)
    abstract fun remove(player: Player, material: Material)
    abstract fun pull(key: UUID, material: Material): Double
    abstract fun pull(player: Player, material: Material): Double

    abstract fun getItemCoolDown(player: Player, material: Material): Int
    abstract fun getItemCoolDown(player: Player, slot: Int): Int
    abstract fun getItemCoolDown(player: Player, itemStack: ItemStack): Int

    abstract fun setItemCoolDown(player: Player, material: Material, attackSpeed: Double)
    abstract fun setItemCoolDown(player: Player, slot: Int, attackSpeed: Double)
    abstract fun setItemCoolDown(player: Player, itemStack: ItemStack, attackSpeed: Double)

    abstract fun isItemCoolDown(player: Player, itemStack: ItemStack): Boolean
    abstract fun isItemCoolDown(player: Player, slot: Int): Boolean

    companion object {

        fun UUID.push(material: Material, time: Double) = AttributeSystem.cooldownManager.push(this, material, time)
        fun Player.push(material: Material, time: Double) = AttributeSystem.cooldownManager.push(this, material, time)

        fun UUID.remove(material: Material) = AttributeSystem.cooldownManager.remove(this, material)
        fun Player.remove(material: Material) = AttributeSystem.cooldownManager.remove(this, material)

        fun UUID.pull(material: Material): Double = AttributeSystem.cooldownManager.pull(this, material)
        fun Player.pull(material: Material): Double = AttributeSystem.cooldownManager.pull(this, material)

        fun Player.getItemCoolDown(material: Material): Int =
            AttributeSystem.cooldownManager.getItemCoolDown(this, material)

        fun Player.getItemCoolDown(slot: Int): Int = AttributeSystem.cooldownManager.getItemCoolDown(this, slot)
        fun Player.getItemCoolDown(itemStack: ItemStack): Int =
            AttributeSystem.cooldownManager.getItemCoolDown(this, itemStack)

        fun Player.getItemCoolDown(): Int =
            AttributeSystem.cooldownManager.getItemCoolDown(this, this.inventory.itemInMainHand)


        fun Player.setItemCoolDown(material: Material, attackSpeed: Double) =
            AttributeSystem.cooldownManager.setItemCoolDown(this, material, attackSpeed)

        fun Player.setItemCoolDown(slot: Int, attackSpeed: Double) =
            AttributeSystem.cooldownManager.setItemCoolDown(this, slot, attackSpeed)

        fun Player.setItemCoolDown(itemStack: ItemStack, attackSpeed: Double) =
            AttributeSystem.cooldownManager.setItemCoolDown(this, itemStack, attackSpeed)

        fun Player.setItemCoolDown(attackSpeed: Double) =
            AttributeSystem.cooldownManager.setItemCoolDown(this, this.inventory.itemInMainHand, attackSpeed)

        fun Player.isItemCoolDown(itemStack: ItemStack): Boolean =
            AttributeSystem.cooldownManager.isItemCoolDown(this, itemStack)

        fun Player.isItemCoolDown(slot: Int): Boolean = AttributeSystem.cooldownManager.isItemCoolDown(this, slot)
        fun Player.isItemCoolDown(): Boolean =
            AttributeSystem.cooldownManager.isItemCoolDown(this, this.inventory.itemInMainHand)
    }
}