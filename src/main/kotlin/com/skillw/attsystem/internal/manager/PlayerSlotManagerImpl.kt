package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.PlayerSlotManager
import com.skillw.attsystem.api.slot.PlayerSlot
import java.util.*

object PlayerSlotManagerImpl : PlayerSlotManager() {
    override val key = "PlayerSlotManager"
    override val priority: Int = 5
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        
        onReload()
    }

    override fun onReload() {
        clear()
//        console().sendLang("player-slot-reload-start")
        val playerSlotSection = AttributeSystem.configManager["slot"].getConfigurationSection("player")!!
        for (key in playerSlotSection.getKeys(false)) {
            val slot: String
            val requirements = LinkedList<String>()
            if (playerSlotSection.isConfigurationSection(key)) {
                val section = playerSlotSection.getConfigurationSection(key)!!
                slot = section.getString("slot") ?: "NULL"
                requirements.addAll(section.getStringList("requirements"))
            } else {
                slot = playerSlotSection[key].toString()
            }
            if (key != null) {
                val playerSlot = PlayerSlot(key, slot.uppercase(), requirements)
                playerSlot.register()
            }
        }
//        console().sendLang("player-slot-reload-end")
    }

//    override fun register(key: String, value: PlayerSlot) {
//        super.register(key, value)
//        console().sendLang("player-slot-register", key, value.slot, value.requirements.toString())
//    }
}