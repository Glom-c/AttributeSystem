package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitTask
import java.util.*

abstract class FormulaManager : BaseMap<String, String>(), Manager {
    companion object {
        const val MAX_HEALTH = "max-health"
        const val MOVEMENT_SPEED = "movement-speed"
        const val HEALTH_REGAIN = "health-regain"
        const val KNOCKBACK_RESISTANCE = "knockback-resistance"

        const val ATTACK_SPEED = "attack-speed"
        const val ATTACK_DISTANCE = "attack-distance"
        const val LUCK = "luck"
    }

    internal abstract var healthRegainScheduled: BukkitTask?

    abstract fun calculate(uuid: UUID, key: String, replacement: Map<String, String> = emptyMap()): Double
    abstract fun calculate(entity: Entity, key: String, replacement: Map<String, String> = emptyMap()): Double

    abstract fun realize(uuid: UUID)
    abstract fun realize(entity: Entity)
    abstract operator fun get(uuid: UUID, key: String): Double
    abstract operator fun get(entity: Entity, key: String): Double
}