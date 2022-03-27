package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.configManager
import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.event.StringsReadEvent
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ExecutionException

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override val key = "AttributeSystemAPI"
    override val priority: Int = 100
    override val subPouvoir = AttributeSystem
    private var task: BukkitTask? = null

    override fun onActive() {
        onReload()
    }

    override fun onReload() {
        refreshTask()
    }

    private fun refreshTask() {
        task?.cancel()
        task = object : BukkitRunnable() {
            override fun run() {
                if (this.isCancelled) return
                try {
                    updateAll()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.runTaskTimerAsynchronously(
            AttributeSystem.plugin,
            0,
            configManager.attributeUpdateTick
        )
    }

    override fun read(
        oriented: Attribute.Oriented,
        livingEntity: LivingEntity?,
        vararg strings: String
    ): AttributeData {
        return read(oriented, "null", livingEntity, *strings)
    }

    override fun read(oriented: Attribute.Oriented, strings: List<String>, livingEntity: LivingEntity?): AttributeData {
        return read(oriented, strings, livingEntity, "null")
    }

    override fun read(oriented: Attribute.Oriented, uuid: UUID, vararg strings: String): AttributeData {
        return read(oriented, "null", uuid.livingEntity(), *strings)
    }

    override fun read(oriented: Attribute.Oriented, strings: List<String>, uuid: UUID): AttributeData {
        return read(oriented, strings, uuid.livingEntity(), "null")
    }

    override fun read(
        oriented: Attribute.Oriented,
        slot: String,
        livingEntity: LivingEntity?,
        vararg strings: String
    ): AttributeData {
        return read(oriented, strings.asList(), livingEntity, "null")
    }

    override fun read(
        oriented: Attribute.Oriented,
        strings: List<String>,
        livingEntity: LivingEntity?,
        slot: String
    ): AttributeData {
        try {
            return AttributeSystem.poolExecutor.submit<AttributeData> {
                try {
                    val attributeData = AttributeData()
                    if (!AttributeSystem.conditionManager.conditionStrings(
                            slot,
                            livingEntity,
                            strings
                        )
                    ) return@submit attributeData
                    strings@ for (string in strings) {
                        att@ for (attribute in AttributeSystem.attributeManager.attributes) {
                            if (!attribute.isOriented(oriented)) continue
                            val status = attribute.read(string, livingEntity, slot)
                            if (status != null) {
                                attributeData.operation(attribute, status)
                                continue@strings
                            }
                        }
                    }
                    val event = StringsReadEvent(livingEntity ?: return@submit attributeData, strings, attributeData)
                    event.call()
                    return@submit if (!event.isCancelled) event.attributeData else AttributeData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                null
            }.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return AttributeData()
    }

    override fun read(oriented: Attribute.Oriented, slot: String, uuid: UUID, vararg strings: String): AttributeData {
        return read(oriented, slot, uuid.livingEntity(), *strings)
    }

    override fun read(oriented: Attribute.Oriented, strings: List<String>, uuid: UUID, slot: String): AttributeData {
        return read(oriented, strings, uuid.livingEntity(), slot)
    }

    override fun update(entity: Entity) {
        if (entity.isAlive()) {
            this.update(entity.uniqueId)
        } else {
            this.remove(entity.uniqueId)
        }
    }


    override fun update(uuid: UUID) {
        AttributeSystem.equipmentDataManager.update(uuid)
        AttributeSystem.attributeDataManager.update(uuid)
        AttributeSystem.formulaManager.realize(uuid)
    }


    override fun updateAll() {
        AttributeSystem.attributeDataManager.forEach { uuid, _ ->
            update(uuid)
        }
    }


    override fun remove(entity: Entity) {
        this.remove(entity.uniqueId)
    }


    override fun remove(uuid: UUID) {
        AttributeSystem.attributeDataManager.remove(uuid)
        AttributeSystem.equipmentDataManager.remove(uuid)
//        AttributeSystem.getShieldDataManager().removeByKey(uuid)
    }

}