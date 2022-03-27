package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeData
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.event.ProxyEvent

open class StringsReadEvent(
    val entity: LivingEntity,
    val strings: List<String>,
    val attributeData: AttributeData
) : ProxyEvent() {
    override val allowCancelled = true

}