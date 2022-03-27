package com.skillw.attsystem.api.event

import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.ProxyEvent

class ItemLoadEvent(
    val entity: Entity,
    val itemStack: ItemStack
) : ProxyEvent() {
    override val allowCancelled = true

}