package com.skillw.attsystem.api.event

import org.bukkit.entity.Player
import taboolib.common.platform.event.ProxyEvent

class PlayerInFightEvent(val player: Player) : ProxyEvent() {
    override val allowCancelled = true
}