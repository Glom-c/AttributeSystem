package com.skillw.attsystem.api.event

import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class ItemLoadEvent(
    val entity: Entity,
    val itemStack: ItemStack
) : BukkitProxyEvent() {
    override val allowCancelled = true

}