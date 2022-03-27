package com.skillw.attsystem

import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.manager.*
import com.skillw.attsystem.internal.listener.FightListener
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.annotation.PManager
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.thread.BasicThreadFactory
import com.skillw.pouvoir.util.FileUtils
import com.skillw.pouvoir.util.MessageUtils
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
            "formula" to Pair(formula.file!!, FileUtils.loadConfigFile(formula.file!!)!!),
            "message" to Pair(message.file!!, FileUtils.loadConfigFile(message.file!!)!!),
            "slot" to Pair(slot.file!!, FileUtils.loadConfigFile(slot.file!!)!!)
        )
    }

    /**
     * Configs
     */

    @Config("config.yml")
    lateinit var config: ConfigFile

    @Config("formula.yml")
    lateinit var formula: ConfigFile

    @Config("message.yml")
    lateinit var message: ConfigFile

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
    lateinit var readGroupManager: ReadGroupManager

    @JvmStatic
    @PManager
    lateinit var attributeManager: AttributeManager

    @JvmStatic
    @PManager
    lateinit var operationManager: OperationManager

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

    @JvmStatic
    @PManager
    lateinit var damageTypeManager: DamageTypeManager

    @JvmStatic
    @PManager
    lateinit var mechanicManager: MechanicManager

    @JvmStatic
    @PManager
    lateinit var mechanicGroupManager: MechanicGroupManager

    @JvmStatic
    @PManager
    lateinit var cooldownManager: CooldownManager

    @JvmStatic
    @PManager
    lateinit var personalManager: PersonalManager

    @JvmStatic
    @PManager
    lateinit var triggerManager: TriggerManager

    @JvmStatic
    @PManager
    lateinit var fightManager: FightManager

    override fun onLoad() {
        load()
        info("&d[&9AttributeSystem&d] &aAttributeSystem is loaded...")
    }

    override fun onEnable() {
        enable()
        info("&d[&9AttributeSystem&d] &aAttributeSystem is enabled...")
    }

    override fun onActive() {
        Bukkit.getPluginManager().registerEvents(FightListener, plugin)
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

    @JvmStatic
    fun debug(string: String) {
        if (configManager.debug) {
            MessageUtils.debug(string)
        }
    }

}