package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.manager.ConfigManager
import taboolib.common.platform.Platform
import taboolib.module.metrics.charts.SingleLineChart
import java.util.regex.Pattern

object ASConfig : ConfigManager(AttributeSystem) {
    override val priority = 0
    override fun defaultOptions(): Map<String, Map<String, Any>> = emptyMap()

    override val isCheckVersion
        get() = this["config"].getBoolean("options.check-version")


    val statTitle: String
        get() = this["config"].getString("stats.title") ?: "&6{name} &e的属性统计:"
    val ignores: List<String>
        get() = this["config"].getStringList("options.attribute.ignores")

    var lineConditionPattern: Pattern = Pattern.compile("options.attribute.line-condition.format")

    private val lineConditionFormat: String
        get() = this["config"].getString("options.attribute.line-condition.format") ?: ".*\\/(?<requirement>.*)"

    val lineConditionSeparator: String
        get() = this["config"].getString("options.attribute.line-condition.separator") ?: ","

    val attributeUpdateTick: Long
        get() = this["config"].getLong("options.attribute.time.attribute-update")

    val attributeClearSchedule: Long
        get() =
            this["config"].getLong("options.attribute.time.attribute-clear")

    override fun onEnable() {
        if (!this["config"].contains("options.attribute.time.attribute-clear")) {
            this["config"].set("options.attribute.time.attribute-clear", 1200L)
            this.fileMap.filter { it.value.name == this["config"].name }
                .firstNotNullOf { this["config"].save(it.key) }
        }
        onReload()
        val metrics =
            taboolib.module.metrics.Metrics(14465, AttributeSystem.plugin.description.version, Platform.BUKKIT)
        metrics.addCustomChart(SingleLineChart("attributes") {
            AttributeSystem.attributeManager.attributes.size
        })
    }

    override fun subReload() {
        lineConditionPattern = Pattern.compile(lineConditionFormat)
    }

    val germSlots: List<String>
        get() {
            return this["slot"].getStringList("germ-slots")
        }
}