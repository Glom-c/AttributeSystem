package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.pouvoir.api.event.Time
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class EquipmentUpdateEvent(
    val time: Time,
    val entity: Entity,
    val equipmentData: EquipmentDataCompound
) : BukkitProxyEvent() {
    override val allowCancelled = false

}