package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.Entity
import java.util.*

abstract class FormulaManager : BaseMap<String, String>(), Manager {
    abstract fun calculate(uuid: UUID, key: String, replacement: Map<String, String> = emptyMap()): Double
    abstract fun calculate(entity: Entity, key: String, replacement: Map<String, String> = emptyMap()): Double

    abstract operator fun get(uuid: UUID, key: String): Double
    abstract operator fun get(entity: Entity, key: String): Double
}