package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.manager.FormulaManager
import com.skillw.attsystem.internal.manager.ASConfig.skillAPI
import com.skillw.attsystem.internal.message.ASHologramGroup
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.attsystem.util.DefaultAttribute
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.CalculationUtils.resultDouble
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.FileUtils
import com.skillw.pouvoir.util.MapUtils
import com.skillw.pouvoir.util.NumberUtils.format
import com.skillw.pouvoir.util.StringUtils.placeholder
import com.skillw.pouvoir.util.StringUtils.replacement
import com.sucy.skill.SkillAPI
import com.sucy.skill.api.player.PlayerClass
import org.bukkit.Bukkit
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object FormulaManagerImpl : FormulaManager() {
    override val key = "FormulaManager"
    override val priority: Int = 9
    override val subPouvoir = AttributeSystem
    private val replacements = ConcurrentHashMap<String, String>()
    override var healthRegainScheduled: BukkitTask? = null

    private fun createHealthRegainScheduled(): BukkitTask {
        return object : BukkitRunnable() {
            override fun run() {
                for (uuid in AttributeSystem.attributeDataManager.keys) {
                    val livingEntity = uuid.livingEntity()
                    if (livingEntity == null) {
                        AttributeSystem.attributeSystemAPI.remove(uuid)
                        continue
                    }
                    val maxHealth = livingEntity.getAttribute(BukkitAttribute.MAX_HEALTH)?.value ?: continue
                    if (livingEntity.health == maxHealth) continue
                    var healthRegain = formulaManager[uuid, HEALTH_REGAIN]
                    if (healthRegain == -1.0) continue
                    val health = livingEntity.health
                    val value = health + healthRegain
                    if (value >= maxHealth) {
                        livingEntity.health = maxHealth
                        healthRegain = maxHealth - health
                    } else {
                        livingEntity.health = value
                    }
                    if (healthRegain <= 0) continue
                    if (livingEntity is Player && AttributeSystem.personalManager[uuid]?.regainHolo == true) {
                        AttributeSystem.poolExecutor.execute {
                            val section =
                                ASConfig["message"].getConfigurationSection("health-regain-holo") ?: return@execute
                            val text =
                                (section.getString("text") ?: "&2+ 7a{value}").replace(
                                    "{value}",
                                    healthRegain.format()
                                )
                            val distance = (section.getString("distance") ?: "8").toDoubleOrNull() ?: 8.0
                            val holo = ASHologramGroup(
                                mutableListOf(text.placeholder(livingEntity)),
                                livingEntity.eyeLocation,
                                "health-regain-holo"
                            )
                            val players =
                                livingEntity.getNearbyEntities(distance, distance, distance).filterIsInstance<Player>()
                                    .toMutableList()
                            players.add(livingEntity)
                            holo.sendTo(*players.toTypedArray())
                        }
                    }
                }
            }
        }.runTaskTimer(AttributeSystem.plugin, 0, ASConfig.healthRegainSchedule)
    }

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

    override fun realize(uuid: UUID) {
        realize(uuid.livingEntity() ?: return)
    }

    private fun getSkillAPIHealth(player: Player): Int {
        return if (skillAPI) SkillAPI.getPlayerData(player).classes.stream()
            .mapToInt { aClass: PlayerClass -> aClass.health.toInt() }.sum() else 0
    }

    private val attMap = BaseMap<UUID, MutableMap<BukkitAttribute, AttributeModifier>>()

    private fun realizeAttribute(entity: LivingEntity, bukkitAttribute: BukkitAttribute, value: Double) {
        val uuid = entity.uniqueId
        Bukkit.getScheduler().runTask(AttributeSystem.plugin, Runnable {
            val attribute = entity.getAttribute(bukkitAttribute) ?: return@Runnable
            if (attMap.containsKey(uuid) && attMap[uuid]!!.containsKey(bukkitAttribute)) {
                attribute.removeModifier(attMap[uuid]!![bukkitAttribute]!!)
            }
            val attributeModifier = AttributeModifier(
                entity.uniqueId,
                bukkitAttribute.minecraftKey,
                value,
                AttributeModifier.Operation.ADD_NUMBER
            )
            if (value != 0.0)
                MapUtils.put(attMap, uuid, bukkitAttribute, attributeModifier)
            attribute.removeModifier(attributeModifier)
            attribute.addModifier(attributeModifier)
        })
    }

    private fun realizeHealth(entity: LivingEntity) {
        val maxHealthValue = this[entity, MAX_HEALTH]
        if (maxHealthValue < 0) return
        val maxHealth = maxHealthValue + if (entity is Player) getSkillAPIHealth(entity).toDouble() else 0.0
        realizeAttribute(entity, BukkitAttribute.MAX_HEALTH, maxHealth)
    }

    override fun realize(entity: Entity) {
        if (!entity.isAlive()) return
        entity as LivingEntity
        realizeHealth(entity)

        val movementSpeed = this[entity, MOVEMENT_SPEED]
        entity.setWalkSpeed(movementSpeed)

        val knockbackResistance = this[entity, KNOCKBACK_RESISTANCE]
        realizeAttribute(entity, BukkitAttribute.KNOCKBACK_RESISTANCE, knockbackResistance)

        if (entity !is Player) return
        realizeAttackSpeed(entity)

        val luck = this[entity, LUCK]
        realizeAttribute(entity, BukkitAttribute.LUCK, luck)
    }

    private fun realizeAttackSpeed(entity: Player) {
        val defaultValue = DefaultAttribute.getAttackSpeed(entity.inventory.itemInMainHand.type)
        val fixed =
            if (MinecraftVersion.major >= 10) 0.0 else if (defaultValue == 0.0) 0.0 else 4.0 - defaultValue
        val attributeValue = this[entity, ATTACK_SPEED]
        if (attributeValue == -1.0) return
        val attackSpeed = fixed + attributeValue
        if (attackSpeed <= 0.0) {
            return
        }
        val attributeInstance = entity.getAttribute(BukkitAttribute.ATTACK_SPEED) ?: return
        Bukkit.getScheduler().runTask(AttributeSystem.plugin, Runnable {
            try {
                for (modifier in attributeInstance.modifiers) {
                    attributeInstance.removeModifier(modifier)
                }
                attributeInstance.baseValue = attackSpeed
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun LivingEntity.setWalkSpeed(value: Double) {
        if (value == -1.0) return
        val result: Double = if (value < 0.0) 0.0 else if (value > 1.0) 1.0 else value
        realizeAttribute(this, BukkitAttribute.MOVEMENT_SPEED, result / 2)
    }

    private val EntityLivingClass by lazy { nmsClass("EntityLiving") }
    private val CraftLivingEntity by lazy { obcClass("entity.CraftLivingEntity") }

    fun LivingEntity.getAttribute(bukkitAttribute: BukkitAttribute): AttributeInstance? {
        if (MinecraftVersion.majorLegacy <= 11200) {
            val craftAttributes = EntityLivingClass.cast(CraftLivingEntity.cast(this).invokeMethod<Any>("getHandle")!!)
                .getProperty<Any>("craftAttributes") ?: return null
            val attribute = bukkitAttribute.toBukkit()
            return craftAttributes.invokeMethod<AttributeInstance>("getAttribute", attribute)
        } else {
            return this.getAttribute(bukkitAttribute.toBukkit() ?: return null)
        }
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
        val formulaConfig = ASConfig["formula"]
        val attributesSection = formulaConfig.getConfigurationSection("attribute-formulas") ?: return
        for (key in attributesSection.getKeys(false)) {
            this[key] = attributesSection.getString(key) ?: continue
        }
        val skapiSection = formulaConfig.getConfigurationSection("skill-api") ?: return
        replacements.clear()
        this.forEach {
            replacements["{${it.key}}"] = it.value
        }
        for (key in skapiSection.getKeys(false)) {
            this[key] = skapiSection.getString(key) ?: continue
        }
        this.replaceAll { key, value -> value.replacement(replacements) }
        this.replaceAll { key, value -> value.replacement(replacements) }
        this.replaceAll { key, value -> value.replacement(replacements) }
        healthRegainScheduled?.cancel()
        healthRegainScheduled = createHealthRegainScheduled()
    }
}