package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.event.Time
import org.bukkit.entity.Entity
import taboolib.common.platform.event.ProxyEvent

class AttributeUpdateEvent(
    val time: Time,
    val entity: Entity,
    val compound: AttributeDataCompound
) : ProxyEvent() {
    override val allowCancelled = true

}