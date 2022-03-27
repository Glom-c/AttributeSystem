package com.skillw.attsystem.api.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.ProxyEvent

class ItemCooldownEvent(
    val player: Player,
    val itemStack: ItemStack,
    var cooldown: Double
) : ProxyEvent() {
    override val allowCancelled = true

}