package com.skillw.attsystem.internal.hook

import com.skillw.pouvoir.Pouvoir
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PlaceHolderHooker : PlaceholderExpansion {

    override val identifier: String = "as"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return Pouvoir.pouPlaceHolderAPI.replace(player, "%as_$args%")
    }
}