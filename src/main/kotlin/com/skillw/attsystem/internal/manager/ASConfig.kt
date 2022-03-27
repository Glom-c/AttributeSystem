package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.personal.AttackingMessageType
import com.skillw.attsystem.internal.personal.DefensiveMessageType
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.manager.ConfigManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.Platform
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.expansion.setupPlayerDatabase
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.sendLang
import taboolib.module.metrics.charts.SingleLineChart
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ASConfig : ConfigManager(AttributeSystem) {
    override val priority = 0
    override fun defaultOptions(): Map<String, Map<String, Any>> = emptyMap()

    val skillAPI by lazy {
        Bukkit.getPluginManager().isPluginEnabled("SkillAPI") || Bukkit.getPluginManager()
            .isPluginEnabled("ProSkillAPI")
    }
    val mythicMobs by lazy {
        Bukkit.getPluginManager().isPluginEnabled("MythicMobs")
    }
    val originSkill by lazy {
        Bukkit.getPluginManager().isPluginEnabled("OriginSkill")
    }
    val staticClasses = ConcurrentHashMap<String, Any>()
    override val isCheckVersion
        get() = this["config"].getBoolean("options.check-version")

    val scripts = File(getDataFolder(), "scripts")
    override fun onInit() {
        createIfNotExists("attributes", "BaseAttribute.yml")
        createIfNotExists("conditions", "conditions.yml")
        createIfNotExists("fight", "default.yml")
        createIfNotExists("formula", "example.yml")
        createIfNotExists("read", "default.yml")
        createIfNotExists("mechanic", "default.yml")
        createIfNotExists("damage", "damage.yml")
        createIfNotExists(
            "scripts",
            "mechanics.groovy",
            "mechanics.js",
            "condition.groovy",
            "condition.js"
        )
        Pouvoir.scriptManager.addDir(scripts, AttributeSystem)
    }

    val isPersonalEnable
        get() = this["message"].getBoolean("options.personal")

    val defaultAttackMessageType: AttackingMessageType
        get() = taboolib.common5.Coerce.toEnum(
            (this["message"].getString("options.default.attack") ?: "HOLO").uppercase(),
            AttackingMessageType::class.java
        )

    val defaultDefendMessageType: DefensiveMessageType
        get() = taboolib.common5.Coerce.toEnum(
            (this["message"].getString("options.default.defend") ?: "CHAT").uppercase(),
            DefensiveMessageType::class.java
        )

    val defaultRegainHolo: Boolean
        get() = this["message"].getBoolean("options.default.health-regain-holo")

    val statTitle: String
        get() = this["message"].getString("stats.title") ?: "&e玩家 &6{player} &e的属性统计:"

    val statAttributeFormat
        get() = this["message"].getString("stats.attribute-format") ?: "&7{name}: &c{value}"

    val statStatus
        get() = this["message"].getString("stats.status") ?: "Status:"

    val statNone
        get() = this["message"].getString("stats.none") ?: "None"

    val statStatusValue
        get() = this["message"].getString("stats.status-value") ?: "{key} = {value}"


    val statPlaceholder
        get() = this["message"].getString("stats.placeholder") ?: "Placeholder:"

    val statPlaceholderValue
        get() = this["message"].getString("stats.placeholder-value") ?: "{key} = {value}"

    val numberPattern: String
        get() = this["config"].getString("options.number-pattern").toString()


    val mythicSkillLabelRange: IntRange
        get() = this["config"].getInt("options.mythic-skill.label.min")..this["config"].getInt("options.mythic-skill.label.max")

    val originSkillLabelRange: IntRange
        get() = this["config"].getInt("options.origin-skill.label.min")..this["config"].getInt("options.origin-skill.label.max")


    val isCooldown: Boolean
        get() = this["config"].getString("options.fight.attack-speed.type")?.lowercase().equals("cooldown")
    val skipCrashShot: Boolean
        get() = (this["config"].getString("options.fight.skip-crash-shot") ?: "true").toBoolean()


    val isAttackAnyTime: Boolean
        get() = this["config"].getBoolean("options.fight.attack-speed.damage-any-time")


    val ignores: List<String>
        get() = this["config"].getStringList("options.attribute.ignores")

    val lineConditionSymbol: String
        get() = this["config"].getString("options.attribute.line-condition.symbol") ?: "\\/"

    val lineConditionSeparator: String
        get() = this["config"].getString("options.attribute.line-condition.separator") ?: ","

    val attributeUpdateTick: Long
        get() = this["config"].getLong("options.attribute.time.attribute-update")


    val infoUpdateTick: Long
        get() = this["config"].getLong("options.attribute.time.info-bar-update")


    val isVanillaDamage: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-damage")

    val isVanillaArmor: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-armor")
    val isVanillaRegain: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-regain")
    val combatValueScript: String?
        get() = this["config"].getString("option.attribute.combat-value")
    val fightStatusTime: Long
        get() = this["config"].getLong("options.fight.fight-status.time")


    val healthScale: Int
        get() = this["config"].getInt("options.health-scale")

    val noDamageTicks: Int
        get() = this["config"].getInt("options.fight.no-damage-ticks.value")
    val defaultDistance: Int
        get() = this["config"].getInt("options.fight.vanilla-distance.default")
    val creativeDistance: Int
        get() = this["config"].getInt("options.fight.vanilla-distance.creative")

    val noDamageTicksDisableWorlds: List<String>
        get() = this["config"].getStringList("options.fight.no-damage-ticks.disable-world")


    val debug: Boolean
        get() = this["config"].getBoolean("options.debug")

    val healthRegainSchedule
        get() = this["config"].getLong("options.attribute.time.health-regain")

    val attributeClearSchedule: Long
        get() =
            this["config"].getLong("options.attribute.time.attribute-clear")


    val isAttackForce
        get() = this["config"].getBoolean("options.fight.attack-speed.attack-force")
    val isDistanceEffect
        get() = this["config"].getBoolean("options.fight.distance-attack.effect")
    val isDistanceSound
        get() = this["config"].getBoolean("options.fight.distance-attack.sound")


    override fun onEnable() {
        if (!this["config"].contains("options.attribute.time.attribute-clear")) {
            this["config"].set("options.attribute.time.attribute-clear", 1200L)
            this.fileMap.filter { it.value.name == this["config"].name }
                .firstNotNullOf { this["config"].save(it.key) }
        }
        if (AttributeSystem.config.getBoolean("database.enable")) {
            setupPlayerDatabase(AttributeSystem.config.getConfigurationSection("database")!!)
        } else {
            setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
        }

        onReload()
        val metrics =
            taboolib.module.metrics.Metrics(14465, AttributeSystem.plugin.description.version, Platform.BUKKIT)
        metrics.addCustomChart(SingleLineChart("attributes") {
            AttributeSystem.attributeManager.attributes.size
        })
    }

    val germSlots: List<String>
        get() {
            return this["slot"].getStringList("germ-slots")
        }

    val disableDamageTypes = LinkedList<Material>()
    val disableCooldownTypes = LinkedList<Material>()
    override fun subReload() {
        console().sendLang("script-object-reload-start")
        Pouvoir.scriptManager.reloadDir(scripts, AttributeSystem)
        console().sendLang("script-object-reload-end")
        Bukkit.getScheduler().runTask(AttributeSystem.plugin, Runnable {
            val scale: Int = healthScale
            if (scale != -1) {
                Bukkit.getServer().onlinePlayers.forEach { player: Player ->
                    player.isHealthScaled = true
                    player.healthScale = scale.toDouble()
                }
            } else {
                Bukkit.getServer().onlinePlayers.forEach { player: Player -> player.isHealthScaled = false }
            }
        })
        disableDamageTypes.clear()
        for (material in this["config"].getStringList("options.fight.disable-damage-types")) {
            val xMaterial = XMaterial.matchXMaterial(material)
            if (xMaterial.isPresent) {
                disableDamageTypes.add(xMaterial.get().parseMaterial() ?: continue)
            } else {
                val materialMC = Material.matchMaterial(material)
                disableDamageTypes.add(materialMC ?: continue)
            }
        }
        disableCooldownTypes.clear()
        for (material in this["config"].getStringList("options.fight.attack-speed.no-cooldown-types")) {
            val xMaterial = XMaterial.matchXMaterial(material)
            if (xMaterial.isPresent) {
                disableCooldownTypes.add(xMaterial.get().parseMaterial() ?: continue)
            } else {
                val materialMC = Material.matchMaterial(material)
                disableCooldownTypes.add(materialMC ?: continue)
            }
        }
    }
}