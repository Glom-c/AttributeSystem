package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.ProxyEvent

class ItemReadEvent(
    val entity: LivingEntity,
    val itemStack: ItemStack,
    val strings: List<String>,
    val attributeDataCompound: AttributeDataCompound
) : ProxyEvent() {
    override val allowCancelled = true

}