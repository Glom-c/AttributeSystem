package com.skillw.attsystem.api.slot

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.able.Keyable
import taboolib.common5.Coerce
import taboolib.type.BukkitEquipment

class PlayerSlot(override val key: String, val slot: String, val requirements: List<String>) : Keyable<String> {
    val bukkitEquipment: BukkitEquipment? =
        if (!Coerce.asInteger(slot).isPresent) BukkitEquipment.fromString(slot) else null

    override fun register() {
        AttributeSystem.playerSlotManager.register(key, this)
    }
}