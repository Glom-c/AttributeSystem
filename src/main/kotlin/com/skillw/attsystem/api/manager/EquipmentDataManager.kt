package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class EquipmentDataManager : BaseMap<UUID, EquipmentDataCompound>(), Manager {


    abstract fun updateAll()

    abstract fun update(entity: Entity): EquipmentDataCompound?

    abstract fun update(uuid: UUID): EquipmentDataCompound?

    abstract fun readItemLore(
        itemStack: ItemStack, livingEntity: LivingEntity? = null
    ): AttributeData?

    abstract fun readItemsLore(
        itemStacks: Collection<ItemStack>, livingEntity: LivingEntity? = null
    ): AttributeData?

    abstract fun readItemLore(
        itemStack: ItemStack, livingEntity: LivingEntity? = null, slot: String
    ): AttributeData?


    abstract fun readItemsLore(
        itemStacks: Collection<ItemStack>, livingEntity: LivingEntity? = null, slot: String
    ): AttributeData?

    abstract fun readItemNBT(
        itemStack: ItemStack, livingEntity: LivingEntity? = null
    ): AttributeDataCompound?

    abstract fun readItemsNBT(
        itemStacks: Collection<ItemStack>, livingEntity: LivingEntity? = null
    ): AttributeDataCompound?

    abstract fun readItem(
        itemStack: ItemStack, livingEntity: LivingEntity? = null
    ): AttributeDataCompound

    abstract fun readItems(
        itemStacks: Collection<ItemStack>, livingEntity: LivingEntity? = null
    ): AttributeDataCompound

    abstract fun readItem(
        itemStack: ItemStack, livingEntity: LivingEntity? = null, slot: String
    ): AttributeDataCompound


    abstract fun readItems(

        itemStacks: Collection<ItemStack>, livingEntity: LivingEntity? = null, slot: String
    ): AttributeDataCompound

}