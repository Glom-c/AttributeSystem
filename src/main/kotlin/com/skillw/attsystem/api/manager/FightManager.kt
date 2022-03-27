package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.Entity
import java.util.*

abstract class FightManager : Manager {
    abstract fun isFighting(uuid: UUID): Boolean
    abstract fun isFighting(entity: Entity): Boolean
    abstract fun intoFighting(entity: Entity)
    abstract fun outFighting(entity: Entity)
    abstract fun outFighting(uuid: UUID)
    abstract fun intoFighting(uuid: UUID)
}