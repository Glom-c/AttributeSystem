package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.FormulaManager
import com.skillw.pouvoir.util.CalculationUtils.resultDouble
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.FileUtils
import com.skillw.pouvoir.util.StringUtils.replacement
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object FormulaManagerImpl : FormulaManager() {
    override val key = "FormulaManager"
    override val priority: Int = 9
    override val subPouvoir = AttributeSystem
    private val replacements = ConcurrentHashMap<String, String>()

    override fun calculate(uuid: UUID, key: String, replacement: Map<String, String>): Double {
        if (!uuid.isAlive()) return 0.0
        val livingEntity = uuid.livingEntity() ?: return 0.0
        return AttributeSystem.poolExecutor.submit<Double?> {
            return@submit this[key]?.replacement(replacement)?.resultDouble(
                livingEntity,
                replacements
            )
        }.get() ?: return 0.0
    }

    override fun calculate(entity: Entity, key: String, replacement: Map<String, String>): Double {
        return calculate(entity.uniqueId, key, replacement)
    }

    override operator fun get(uuid: UUID, key: String): Double {
        return calculate(uuid, key)
    }

    override operator fun get(entity: Entity, key: String): Double {
        return calculate(entity, key)
    }

    override fun onActive() {
        onReload()
    }

    override fun onReload() {
        this.clear()
        FileUtils.listFiles(File(AttributeSystem.plugin.dataFolder, "formula")).forEach {
            val yaml = YamlConfiguration.loadConfiguration(it)
            for (key in yaml.getKeys(false)) {
                this[key] = yaml.getString(key) ?: continue
            }
        }
        replacements.clear()
        this.forEach {
            replacements["{${it.key}}"] = it.value
        }
        this.replaceAll { key, value -> value.replacement(replacements) }
    }
}