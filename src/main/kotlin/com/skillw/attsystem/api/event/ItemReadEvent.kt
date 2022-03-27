package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class ItemReadEvent(
    val entity: LivingEntity,
    val itemStack: ItemStack,
    val strings: List<String>,
    val attributeDataCompound: AttributeDataCompound
) : BukkitProxyEvent() {
    override val allowCancelled = true

}