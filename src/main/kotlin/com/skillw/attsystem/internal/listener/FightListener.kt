package com.skillw.attsystem.internal.listener

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.manager.CooldownManager.Companion.isItemCoolDown
import com.skillw.attsystem.api.manager.CooldownManager.Companion.pull
import com.skillw.attsystem.api.manager.CooldownManager.Companion.setItemCoolDown
import com.skillw.attsystem.api.manager.FormulaManager
import com.skillw.attsystem.api.manager.FormulaManager.Companion.ATTACK_SPEED
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.manager.ASConfig.creativeDistance
import com.skillw.attsystem.internal.manager.ASConfig.defaultDistance
import com.skillw.attsystem.internal.manager.ASConfig.mythicMobs
import com.skillw.attsystem.internal.manager.ASConfig.originSkill
import com.skillw.attsystem.internal.manager.ASConfig.skillAPI
import com.skillw.attsystem.internal.manager.ASConfig.skipCrashShot
import com.skillw.attsystem.internal.manager.FormulaManagerImpl.getAttribute
import com.skillw.attsystem.internal.trigger.holder.DamageTriggerHolder
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.attsystem.util.StringUtils.material
import com.skillw.pouvoir.util.EntityUtils
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.sucy.skill.api.skills.Skill
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common.util.Location
import taboolib.library.xseries.XSound
import taboolib.module.nms.getName
import taboolib.platform.util.attacker
import taboolib.platform.util.sendLang

object FightListener : Listener {

    private val isSkillDamage
        get() = Skill.isSkillDamage()
    private var isMythicDamage = false
    private var isOriginSkillDamage = false


    private fun clear(event: EntityDamageEvent) {
        EntityDamageEvent.DamageModifier.values().forEach {
            if (event.isApplicable(it)) event.setDamage(it, 0.0)
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(event: EntityDamageEvent) {
        val attacker: LivingEntity? = if (event is EntityDamageByEntityEvent) event.attacker else null
        val defender = event.entity as? LivingEntity? ?: return
        if (!defender.isAlive()) return
        val cause = event.cause.name.lowercase()
        val fightData =
            FightData(attacker, defender) { put("origin", event.damage); put("event", event) }
        DamageTriggerHolder("damage-cause-$cause", fightData, event).call()
        val result = fightData.result
        if (result > 0.0) {
            if (!ASConfig.isVanillaArmor) {
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
            }
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, result)
        } else if (result < 0.0) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
    fun e1(event: EntityDamageByEntityEvent) {
        val attacker = event.attacker ?: return
        if (attacker !is Player) return
        val material = attacker.inventory.itemInMainHand.type.name.material()
        if (ASConfig.disableDamageTypes.contains(material)) {
            event.isCancelled = true
            val nowSlot = attacker.inventory.heldItemSlot
            val newSlot = if (nowSlot == 8) 0 else nowSlot + 1
            attacker.sendLang("disable-damage-type", attacker.inventory.itemInMainHand.getName())
            attacker.inventory.heldItemSlot = newSlot
            return
        }
    }


    @EventHandler(priority = org.bukkit.event.EventPriority.HIGH)
    fun e(event: EntityDamageByEntityEvent) {
        if (isMythicDamage) {
            val origin = event.damage
            clear(event)
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, origin)
            isMythicDamage = false
            return
        }
        if (isOriginSkillDamage) {
            val origin = event.damage
            clear(event)
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, origin)
            isOriginSkillDamage = false
            return
        }
        if (event.isCancelled) return
        if (skillAPI && isSkillDamage) return
        val attacker = event.attacker ?: return
        val defender = event.entity
        if (!attacker.isAlive() || !defender.isAlive()) {
            return
        }
        defender as LivingEntity
        val originDamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE)
        val triggerKey: String = when {
            mythicMobs && originDamage.toInt() in ASConfig.mythicSkillLabelRange -> "mythic-mobs"
            originSkill && originDamage.toInt() in ASConfig.originSkillLabelRange -> "origin-skill"
            else -> "attack"
        }
        if ((event.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.cause != EntityDamageEvent.DamageCause.PROJECTILE) && triggerKey == "attack") return
        var force = 1.0

        var isProjectile = event.damager is Projectile
        if (attacker is Player) {
            //CrashShot
            if (isProjectile && event.damager.hasMetadata("projParentNode") && skipCrashShot) return
            if (!ASConfig.isAttackAnyTime && attacker.isItemCoolDown()) return
            force = if (event.damager.hasMetadata("ATTRIBUTE_SYSTEM_FORCE")) {
                isProjectile = true
                event.damager.getMetadata("ATTRIBUTE_SYSTEM_FORCE")[0].asDouble()
            } else if (ASConfig.isAttackAnyTime && attacker.isItemCoolDown()) {
                if (ASConfig.isAttackForce && !defender.hasMetadata("ATTRIBUTE_SYSTEM_FORCE"))
                    attacker.pull(attacker.inventory.itemInMainHand.type)
                else
                    1.0
            } else if (ASConfig.isAttackForce) {
                val baseDamage: Double = event.getDamage(EntityDamageEvent.DamageModifier.BASE)
                baseDamage / (attacker.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.value ?: baseDamage)
            } else 1.0
        }
        val fightData =
            FightData(attacker, defender) {
                put("origin", originDamage); put("force", force); put("event", event); put(
                "projectile",
                isProjectile.toString()
            )
            }
        val result = AttributeSystem.mechanicGroupManager.handle(triggerKey, fightData)
        when {
            triggerKey == "origin-skill" -> {
                if (result > 0.0) {
                    isOriginSkillDamage = true
                    defender.damage(result, attacker)
                }
            }
            triggerKey.startsWith("mythic-mobs") -> {
                if (result > 0.0) {
                    isMythicDamage = true
                    defender.damage(result, attacker)
                }
            }
            else -> {
                if (result > 0.0) {
                    if (!ASConfig.isVanillaArmor) {
                        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
                    }
                    event.setDamage(EntityDamageEvent.DamageModifier.BASE, result)
                }
            }
        }
        AttributeSystem.fightManager.intoFighting(attacker)
        AttributeSystem.fightManager.intoFighting(defender)
        if (attacker !is Player || !ASConfig.isCooldown || triggerKey != "attack" || isProjectile) return
        attacker.setItemCoolDown(formulaManager[attacker.uniqueId, ATTACK_SPEED])
    }

    @SubscribeEvent
    fun e(event: EntityShootBowEvent) {
        event.projectile.setMetadata("ATTRIBUTE_SYSTEM_FORCE", FixedMetadataValue(AttributeSystem.plugin, event.force))
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        submit(async = true) {
            val player = event.player
            val uuid = player.uniqueId
            if (event.action != Action.LEFT_CLICK_AIR) {
                cancel()
                return@submit
            }
            val gameMode = player.gameMode
            if (gameMode == GameMode.SPECTATOR) {
                cancel()
                return@submit
            }
            val attackDistance = formulaManager[uuid, FormulaManager.ATTACK_DISTANCE]
            val livingEntity = EntityUtils.getEntityRayHit(player, attackDistance)
            if (livingEntity == null
                || livingEntity.location.distance(player.location) <=
                if (gameMode == GameMode.CREATIVE) creativeDistance else defaultDistance
            ) {
                cancel()
                return@submit
            }
            val attackDamage = player.getAttribute(BukkitAttribute.ATTACK_DAMAGE)?.value ?: 0.0
            submit {
                if (ASConfig.isDistanceSound) XSound.ENTITY_PLAYER_ATTACK_SWEEP.play(player, 1.0f, 1.0f)
                if (ASConfig.isDistanceEffect) {
                    val location = livingEntity.eyeLocation
                    ProxyParticle.SWEEP_ATTACK.sendTo(
                        adaptPlayer(player),
                        Location(player.world.name, location.x, location.y, location.z)
                    )
                }
                livingEntity.damage(attackDamage.coerceAtLeast(1.0), player)
            }
            cancel()
            return@submit
        }
    }
}