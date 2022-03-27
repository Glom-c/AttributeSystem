package com.skillw.attsystem.api.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.personal.PersonalData
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap
import org.bukkit.entity.Player
import java.util.*

abstract class PersonalManager : KeyMap<UUID, PersonalData>(), Manager {

    abstract val enable: Boolean

    abstract fun pushData(player: Player)
    abstract fun pullData(player: Player): PersonalData?
    abstract fun hasData(player: Player): Boolean

    companion object {
        fun Player.pushData() {
            AttributeSystem.personalManager.pushData(this)
        }

        fun Player.pullData(): PersonalData? = AttributeSystem.personalManager.pullData(this)
        fun Player.hasData(): Boolean = AttributeSystem.personalManager.hasData(this)
    }
}