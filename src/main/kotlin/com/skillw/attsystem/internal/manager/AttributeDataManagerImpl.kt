package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.configManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.api.manager.AttributeDataManager
import com.skillw.pouvoir.api.event.Time
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import taboolib.platform.util.isNotAir
import java.util.*
import java.util.concurrent.ExecutionException

object AttributeDataManagerImpl : AttributeDataManager() {
    override val key = "AttributeDataManager"
    override val priority: Int = 3
    override val subPouvoir = AttributeSystem

    override fun updateAll() {
        this.keys.forEach { update(it) }
    }

    override var playerBaseAttribute: AttributeData = AttributeData()
    override var entityBaseAttribute: AttributeData = AttributeData()

    private var task: BukkitTask? = null
    private fun clearTask() {
        task?.cancel()
        task = object : BukkitRunnable() {
            override fun run() {
                if (this.isCancelled) return
                try {
                    attributeDataManager.keys.forEach {
                        if (it.livingEntity()?.isValid != true || it.livingEntity()?.isDead != false) {
                            attributeSystemAPI.remove(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.runTaskTimerAsynchronously(
            AttributeSystem.plugin,
            0,
            configManager.attributeClearSchedule
        )
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        clearTask()
        playerBaseAttribute =
            attributeSystemAPI.read(
                Attribute.Oriented.ENTITY,
                configManager["config"].getStringList("options.attribute.base-attribute.player"),
                null
            )
        entityBaseAttribute =
            attributeSystemAPI.read(
                Attribute.Oriented.ENTITY,
                configManager["config"].getStringList("options.attribute.base-attribute.entity"),
                null
            )
    }

    override fun update(entity: Entity): AttributeDataCompound? {
        if (!entity.isAlive()) return null
        try {
            entity as LivingEntity
            val uuid = entity.uniqueId
            val equipmentDataCompound = equipmentDataManager[uuid]!!
            var attributeDataCompound =
                if (attributeDataManager.containsKey(uuid)) attributeDataManager[uuid]!!.clone()
                else AttributeDataCompound()
            attributeDataManager.register(uuid, attributeDataCompound)
            //PRE
            val preEvent =
                AttributeUpdateEvent(Time.BEFORE, entity, attributeDataCompound)
            preEvent.call()
            attributeDataCompound = preEvent.compound
            attributeDataManager.register(uuid, attributeDataCompound.clone())
            //PROCESS
            val equipmentAttribute = AttributeDataCompound()
            equipmentDataCompound.forEach { (key, equipmentData) ->
                now@ for ((equipmentKey, itemStack) in equipmentData) {
                    if (itemStack.isNotAir())
                        equipmentAttribute.operation(
                            equipmentDataManager.readItem(Attribute.Oriented.ENTITY, itemStack, entity, equipmentKey)
                        )
                }
            }
            attributeDataCompound.filter {
                it.value.release
            }.forEach {
                attributeDataCompound.remove(it.key)
            }
            attributeDataCompound.operation(equipmentAttribute)
            attributeDataCompound.register(
                "BASE-ATTRIBUTE",
                if (entity is Player) playerBaseAttribute else entityBaseAttribute
            )
            //AFTER
            val afterEvent =
                AttributeUpdateEvent(Time.AFTER, entity, attributeDataCompound)
            afterEvent.call()
            attributeDataCompound = afterEvent.compound
            attributeDataManager.register(uuid, attributeDataCompound)
            return attributeDataCompound
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return null
    }

    override fun update(uuid: UUID): AttributeDataCompound? {
        if (!uuid.isAlive()) return null
        return update(uuid.livingEntity()!!)
    }

    override fun addAttribute(entity: Entity, key: String, attributes: List<String>, release: Boolean): AttributeData {
        return this.addAttribute(entity.uniqueId, key, attributes, release)
    }

    override fun addAttribute(
        entity: Entity,
        key: String,
        attributeData: AttributeData,
        release: Boolean
    ): AttributeData {
        return this.addAttribute(entity.uniqueId, key, attributeData, release)
    }

    override fun addAttribute(uuid: UUID, key: String, attributes: List<String>, release: Boolean): AttributeData {
        return this.addAttribute(
            uuid,
            key,
            attributeSystemAPI.read(Attribute.Oriented.ENTITY, attributes, uuid),
            release
        )
    }

    override fun addAttribute(uuid: UUID, key: String, attributeData: AttributeData, release: Boolean): AttributeData {
        if (!uuid.isAlive()) {
            return attributeData
        }
        attributeData.release = release
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.register(key, attributeData)
        } else {
            val compound = AttributeDataCompound()
            compound.register(key, attributeData)
            attributeDataManager.register(uuid, compound)
        }
        return attributeData
    }

    override fun removeAttribute(entity: Entity, key: String) {
        if (!entity.isAlive()) return
        remove(entity.uniqueId)
    }

    override fun removeAttribute(uuid: UUID, key: String) {
        if (!uuid.isAlive()) return
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.remove(key)
        }
    }

}