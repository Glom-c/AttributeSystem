package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeData
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

open class StringsReadEvent(
    val entity: LivingEntity,
    val strings: List<String>,
    val attributeData: AttributeData
) : BukkitProxyEvent() {
    override val allowCancelled = true

}