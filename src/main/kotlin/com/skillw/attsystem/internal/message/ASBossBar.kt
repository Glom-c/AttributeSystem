package com.skillw.attsystem.internal.message

import com.skillw.pouvoir.util.CalculationUtils.resultDouble
import com.skillw.pouvoir.util.PlayerUtils
import com.skillw.pouvoir.util.StringUtils.placeholder
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class ASBossBar(
    private val text: String,
    private val color: String,
    private val style: String,
    private val progress: String
) {
    fun title(player: Player): String {
        return text.placeholder(player)
    }

    fun barColor(player: Player): BarColor {
        return BarColor.valueOf(color.placeholder(player).uppercase())
    }

    fun barStyle(player: Player): BarStyle {
        return BarStyle.valueOf(style.placeholder(player).uppercase())
    }

    fun process(player: Player): Double {
        return progress.placeholder(player).resultDouble()
    }

    fun sendTo(player: Player): BossBar {
        return PlayerUtils.sendBossBar(
            player,
            title(player),
            barColor(player),
            barStyle(player),
            process(player)
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ASBossBar {
            return ASBossBar(
                section.getString("text").toString(),
                section.getString("color") ?: "PURPLE",
                section.getString("style") ?: "SEGMENTED_10",
                section.getString("progress") ?: "1"
            )
        }
    }

}