package com.skillw.attsystem

import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.manager.*
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.annotation.PManager
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.thread.BasicThreadFactory
import com.skillw.pouvoir.util.FileUtils
import com.skillw.pouvoir.util.MessageUtils.info
import com.skillw.pouvoir.util.Pair
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.BukkitPlugin
import java.io.File
import java.util.concurrent.ScheduledThreadPoolExecutor

object AttributeSystem : Plugin(), SubPouvoir {

    override val key = "AttributeSystem"
    override lateinit var managerData: ManagerData
    override val plugin by lazy {
        BukkitPlugin.getInstance()
    }
    override val poolExecutor: ScheduledThreadPoolExecutor by lazy {
        ScheduledThreadPoolExecutor(
            20,
            BasicThreadFactory.Builder().daemon(true).namingPattern("attribute-system-schedule-pool-%d").build()
        )
    }

    override fun getConfigs(): MutableMap<String, Pair<File, YamlConfiguration>> {
        return mutableMapOf(
            "config" to Pair(config.file!!, FileUtils.loadConfigFile(config.file!!)!!),
            "slot" to Pair(slot.file!!, FileUtils.loadConfigFile(slot.file!!)!!)
        )
    }

    /**
     * Configs
     */

    @Config("config.yml")
    lateinit var config: ConfigFile

    @Config("slot.yml")
    lateinit var slot: ConfigFile

    /**
     * Managers
     */

    @JvmStatic
    @PManager
    lateinit var configManager: ASConfig

    @JvmStatic
    @PManager
    lateinit var attributeSystemAPI: AttributeSystemAPI

    @JvmStatic
    @PManager
    lateinit var readPatternManager: ReadPatternManager

    @JvmStatic
    @PManager
    lateinit var attributeManager: AttributeManager

    @JvmStatic
    @PManager
    lateinit var attributeDataManager: AttributeDataManager

    @JvmStatic
    @PManager
    lateinit var equipmentDataManager: EquipmentDataManager

    @JvmStatic
    @PManager
    lateinit var playerSlotManager: PlayerSlotManager

    @JvmStatic
    @PManager
    lateinit var entitySlotManager: EntitySlotManager

    @JvmStatic
    @PManager
    lateinit var conditionManager: ConditionManager

    @JvmStatic
    @PManager
    lateinit var formulaManager: FormulaManager

    override fun onLoad() {
        load()
        info("&d[&9AttributeSystem&d] &aAttributeSystem is loaded...")
    }

    override fun onEnable() {
        enable()
        info("&d[&9AttributeSystem&d] &aAttributeSystem is enabled...")
    }

    override fun onActive() {
        active()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach {
            it.kickPlayer("Server is closed..")
        }
        disable()
        Bukkit.getScheduler().cancelTasks(this.plugin)
        info("&d[&9AttributeSystem&d] &aAttributeSystem is disabled...")
    }

}