package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.configManager
import com.skillw.attsystem.api.manager.EntitySlotManager
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import taboolib.type.BukkitEquipment

object EntitySlotManagerImpl : EntitySlotManager() {
    override val key = "EntitySlotManager"
    override val priority: Int = 6
    override val subPouvoir = AttributeSystem


    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        clear()
//        console().sendLang("entity-slot-reload-start")
        val entitySlotSection = configManager["slot"].getConfigurationSection("entity")!!
        for (key in entitySlotSection.getKeys(false)) {
            val slot = entitySlotSection.getString(key) ?: continue
            var type: BukkitEquipment?
            type = try {
                BukkitEquipment.fromString(slot)!!
            } catch (e: Exception) {
                console().sendLang("equipment-type-error", key)
                null
            } ?: continue
            this.register(key, type)
        }
//        console().sendLang("entity-slot-reload-end")
    }

//    override fun register(key: String, value: BukkitEquipment) {
//        super.register(key, value)
//        console().sendLang("entity-slot-register", key, value.name)
//    }
}