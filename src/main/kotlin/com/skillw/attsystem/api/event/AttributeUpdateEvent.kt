package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.event.Time
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class AttributeUpdateEvent(
    val time: Time,
    val entity: Entity,
    val compound: AttributeDataCompound
) : BukkitProxyEvent() {
    override val allowCancelled = true

}