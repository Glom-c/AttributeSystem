package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.pouvoir.api.event.Time
import org.bukkit.entity.Entity
import taboolib.common.platform.event.ProxyEvent

class EquipmentUpdateEvent(
    val time: Time,
    val entity: Entity,
    val equipmentData: EquipmentDataCompound
) : ProxyEvent() {
    override val allowCancelled = false

}