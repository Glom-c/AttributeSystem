package com.skillw.attsystem.internal.manager

import com.germ.germplugin.api.GermSlotAPI
import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.api.event.ItemLoadEvent
import com.skillw.attsystem.api.event.ItemReadEvent
import com.skillw.attsystem.api.manager.EquipmentDataManager
import com.skillw.pouvoir.api.event.Time
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.MessageUtils.wrong
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import java.util.*
import java.util.concurrent.ExecutionException

object EquipmentDataManagerImpl : EquipmentDataManager() {
    override val key = "EquipmentDataManager"
    override val priority: Int = 4
    override val subPouvoir = AttributeSystem

    override fun get(key: UUID): EquipmentDataCompound? {
        if (!key.isAlive()) {
            return null
        }
        return if (!this.containsKey(key)) {
            EquipmentDataCompound()
        } else super.get(key)
    }


    override fun update(entity: Entity): EquipmentDataCompound? {
        if (!entity.isAlive()) return null
        entity as LivingEntity
        try {
            val uuid = entity.uniqueId
            var equipmentDataCompound =
                if (this.containsKey(uuid)) EquipmentDataCompound(get(uuid)!!) else EquipmentDataCompound()
            val preEvent = EquipmentUpdateEvent(Time.BEFORE, entity, equipmentDataCompound)
            preEvent.call()
            if (preEvent.isCancelled) {
                return equipmentDataCompound
            }
            equipmentDataCompound = preEvent.equipmentData
            equipmentDataManager.register(uuid, equipmentDataCompound)
            equipmentDataCompound.remove("BASE-EQUIPMENT")
            if (entity is Player) {
                loadPlayer(entity, equipmentDataCompound)
            } else {
                loadLivingEntity(entity, equipmentDataCompound)
            }
            val afterEvent = EquipmentUpdateEvent(Time.AFTER, entity, equipmentDataCompound)
            afterEvent.call()
            if (afterEvent.isCancelled) {
                return equipmentDataCompound
            }
            equipmentDataCompound = afterEvent.equipmentData
            equipmentDataManager.register(uuid, equipmentDataCompound)
            return equipmentDataCompound
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun loadLivingEntity(entity: LivingEntity, equipmentDataCompound: EquipmentDataCompound) {
        for ((key, equipmentType) in AttributeSystem.entitySlotManager) {
            val origin: ItemStack? = equipmentType.getItem(entity)
            if (origin == null || origin.isAir()) {
                continue
            }
            val event = ItemLoadEvent(entity, origin)
            val eventItem = event.itemStack
            event.call()
            if (event.isCancelled) {
                return
            }
            if (eventItem.isSimilar(origin)) {
                equipmentDataCompound["BASE-EQUIPMENT", key] = eventItem
                return
            }
            if (eventItem.isNotAir())
                equipmentDataCompound["BASE-EQUIPMENT", key] = eventItem
        }
    }

    private fun loadPlayer(player: Player, equipmentDataCompound: EquipmentDataCompound) {
        val inv = player.inventory
        val manager = AttributeSystem.playerSlotManager
        for (playerSlot in manager.values) {
            val equipmentType = playerSlot.bukkitEquipment
            var origin: ItemStack? = null
            val slotStr = playerSlot.slot
            try {
                origin = if (equipmentType != null) {
                    equipmentType.getItem(player)
                } else {
                    val slot =
                        if (slotStr == "held") player.inventory.heldItemSlot else Integer.parseInt(slotStr)
                    inv.getItem(slot)
                }
            } catch (e: NumberFormatException) {
                wrong("The slot &6$slotStr &emust be a BukkitEquipment or a integer!")
                wrong("BukkitEquipment: [ hand , offhand , helmet , chestplate , leggings , boots  ]")
            }
            if (origin == null || !origin.hasItemMeta() || origin.isAir()) {
                continue
            }
            if (playerSlot.requirements.isNotEmpty() && !playerSlot.requirements.any { origin.hasLore(it) }) {
                continue
            }
            val event = ItemLoadEvent(player, origin)
            event.call()
            if (event.isCancelled) {
                return
            }
            val eventItem = event.itemStack
            if (eventItem.isNotAir())
                equipmentDataCompound["BASE-EQUIPMENT", playerSlot.key] = eventItem
        }
        equipmentDataCompound.remove("Germ-Equipment")
        if (Bukkit.getPluginManager().isPluginEnabled("GermPlugin")) {
            for (slotKey in ASConfig.germSlots) {
                val item = GermSlotAPI.getItemStackFormDatabase(player, slotKey)
                if (item.isAir()) continue
                equipmentDataCompound["Germ-Equipment", slotKey] = item
            }
        }
    }


    override fun updateAll() {
        for (uuid in equipmentDataManager.keys) {
            this.update(uuid)
        }
    }


    override fun update(uuid: UUID): EquipmentDataCompound? {
        return if (uuid.isAlive()) {
            this.update(uuid.livingEntity()!!)
        } else null
    }


    override fun readItemLore(

        itemStack: ItemStack,
        livingEntity: LivingEntity?
    ): AttributeData? {
        return readItemLore(itemStack, livingEntity, "null")
    }

    override fun readItemLore(

        itemStack: ItemStack,
        livingEntity: LivingEntity?,
        slot: String
    ): AttributeData? {
        if (itemStack.hasLore()) {
            return attributeSystemAPI.read(itemStack.itemMeta?.lore ?: return null, livingEntity, slot)
        }
        return null
    }


    override fun readItemsLore(

        itemStacks: Collection<ItemStack>,
        livingEntity: LivingEntity?
    ): AttributeData? {
        return readItemsLore(itemStacks, livingEntity, "null")
    }

    override fun readItemsLore(

        itemStacks: Collection<ItemStack>,
        livingEntity: LivingEntity?,
        slot: String
    ): AttributeData? {
        try {
            return AttributeSystem.poolExecutor.submit<AttributeData> {
                val attributeData = AttributeData()
                for (item: ItemStack in itemStacks) {
                    attributeData.operation(
                        readItemLore(item, livingEntity, slot) ?: continue
                    )
                }
                attributeData
            }.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return null
    }

    override fun readItemNBT(
        itemStack: ItemStack,
        livingEntity: LivingEntity?
    ): AttributeDataCompound? {
        return AttributeDataCompound.fromItem(itemStack)
    }

    override fun readItemsNBT(

        itemStacks: Collection<ItemStack>,
        livingEntity: LivingEntity?
    ): AttributeDataCompound? {
        try {
            return AttributeSystem.poolExecutor.submit<AttributeDataCompound> {
                val attributeDataCompound = AttributeDataCompound()
                for (item: ItemStack in itemStacks) {
                    attributeDataCompound.operation(
                        readItemNBT(item, livingEntity) ?: continue
                    )
                }
                attributeDataCompound
            }.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return null
    }

    override fun readItem(

        itemStack: ItemStack,
        livingEntity: LivingEntity?
    ): AttributeDataCompound {
        return readItem(itemStack, livingEntity, "null")
    }

    override fun readItem(

        itemStack: ItemStack,
        livingEntity: LivingEntity?,
        slot: String
    ): AttributeDataCompound {
        val attributeDataCompound = AttributeDataCompound()
        attributeDataCompound["LORE-ATTRIBUTE"] =
            readItemLore(itemStack, livingEntity, slot)?.release() ?: AttributeData().release()
        attributeDataCompound.operation(readItemNBT(itemStack, livingEntity) ?: AttributeDataCompound())

        val event = ItemReadEvent(
            livingEntity ?: return attributeDataCompound,
            itemStack,
            if (itemStack.hasLore()) itemStack.itemMeta?.lore ?: emptyList() else emptyList(),
            attributeDataCompound
        )
        event.call()

        return if (!event.isCancelled)
            event.attributeDataCompound
        else
            AttributeDataCompound()
    }

    override fun readItems(

        itemStacks: Collection<ItemStack>,
        livingEntity: LivingEntity?
    ): AttributeDataCompound {
        return readItems(itemStacks, livingEntity, "null")
    }

    override fun readItems(

        itemStacks: Collection<ItemStack>,
        livingEntity: LivingEntity?,
        slot: String
    ): AttributeDataCompound {
        try {
            return AttributeSystem.poolExecutor.submit<AttributeDataCompound> {
                val attributeDataCompound = AttributeDataCompound()
                for (item: ItemStack in itemStacks) {
                    attributeDataCompound.operation(
                        readItem(item, livingEntity)
                    )
                }
                attributeDataCompound
            }.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return AttributeDataCompound()
    }
}