package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.PersonalManager
import com.skillw.attsystem.internal.personal.PersonalData
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.util.EntityUtils.player
import org.bukkit.entity.Player
import taboolib.expansion.getDataContainer
import java.util.*

object PersonalManagerImpl : PersonalManager() {
    override val key = "PersonalManager"
    override val priority: Int = 12
    override val subPouvoir = AttributeSystem
    override val enable: Boolean
        get() = ASConfig.isPersonalEnable

    override fun onDisable() {
        this.forEach {
            val player = it.key.player() ?: return
            this.pushData(player)
        }
    }

    override fun get(key: UUID): PersonalData {
        if (!this.containsKey(key)) {
            this[key] = PersonalData(key)
        }
        if (!enable) {
            if (super.get(key)!!.default) {
                return super.get(key)!!
            } else if (this.containsKey(key)) {
                super.get(key)!!.default()
            }
        }
        return super.get(key)!!
    }

    override fun pushData(player: Player) {
        val name = player.name
        if (enable)
            Pouvoir.playerDataManager[name, "personal-data"] = this[player.uniqueId].toString()
    }

    override fun pullData(player: Player): PersonalData? {
        val name = player.name
        return if (enable)
            PersonalData.fromJson(Pouvoir.playerDataManager[name, "personal-data"] ?: return null)
        else PersonalData(player.uniqueId)
    }

    override fun hasData(player: Player): Boolean {
        return player.getDataContainer()["personal-data"] != null
    }


}