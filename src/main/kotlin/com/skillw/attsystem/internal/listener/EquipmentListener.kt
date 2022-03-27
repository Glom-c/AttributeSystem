package com.skillw.attsystem.internal.listener

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.equipment.EquipmentData
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import eos.moe.dragoncore.config.Config.slotSettings
import taboolib.common.platform.event.SubscribeEvent

object EquipmentListener {

    @SubscribeEvent
    fun e(e: PlayerSlotUpdateEvent) {
        val uuid = e.player.uniqueId
        val attributeItems = SlotAPI.getCacheAllSlotItem(e.player)
        attributeItems.entries.removeIf { (key, item) ->
            !slotSettings.containsKey(
                key
            ) || !slotSettings[key]!!.isAttribute || item == null
        }
        val equipmentDataCompound = AttributeSystem.equipmentDataManager[uuid] ?: return
        val equipmentData = EquipmentData()
        equipmentData.putAll(attributeItems)
        equipmentData.unRelease()
        equipmentDataCompound["Dragon-Core"] = equipmentData
        AttributeSystem.equipmentDataManager.register(uuid, equipmentDataCompound)
    }
}