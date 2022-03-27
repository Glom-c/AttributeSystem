package org.serverct.ersha.jd

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.util.EntityUtils.isAlive
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common5.Coerce

object AttributeAPI {
    @JvmStatic
    fun updateEntityAttribute(entity: Entity) {
        if (entity.isAlive()) AttributeSystem.attributeDataManager.update(entity)
    }

    @JvmStatic
    fun addAttribute(player: Player, source: String, attributeList: List<String>, release: Int) {
        AttributeSystem.attributeDataManager.addAttribute(player, source, attributeList, Coerce.toBoolean(release))
    }

    @JvmStatic
    fun addAttribute(player: Player, source: String, attributeList: List<String>) {
        AttributeSystem.attributeDataManager.addAttribute(player, source, attributeList)
    }

    @Deprecated("")
    @JvmStatic
    fun deleteAttribute(player: String, getAttribute: String) {
        AttributeSystem.attributeDataManager.removeAttribute(Bukkit.getPlayer(player) ?: return, getAttribute)
    }

    @JvmStatic
    fun deleteAttribute(player: Player, source: String) {
        AttributeSystem.attributeDataManager.removeAttribute(player, source)
    }
}