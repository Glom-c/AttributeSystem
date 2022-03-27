package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.event.AttributeRegisterEvent
import com.skillw.attsystem.api.manager.AttributeManager
import com.skillw.pouvoir.api.event.Time
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.FileUtils
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File
import java.util.*

object AttributeManagerImpl : AttributeManager() {
    override val key = "AttributeManager"
    override val priority: Int = 2
    override val subPouvoir = AttributeSystem

    val nameMap = BaseMap<String, Attribute>()

    override val attributes: MutableList<Attribute> by lazy {
        Collections.synchronizedList(ArrayList())
    }

    override fun get(key: String): Attribute? {
        return super.get(key) ?: nameMap[key]
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.config }.forEach { this.remove(it.key) }
        console().sendLang("attribute-reload-start")
        attributes.removeIf { it.config }
        this.nameMap.entries.filter { it.value.config }.forEach { nameMap.remove(it.key) }
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "attributes"), Attribute::class.java
        ).forEach {
            it.key.register()
        }
        console().sendLang("attribute-reload-end")
    }

    override fun register(key: String, value: Attribute) {
        val before = AttributeRegisterEvent(Time.BEFORE, value)
        before.call()
        if (before.isCancelled) return
        attributes.add(value)
        attributes.sort()
        put(key, value)
        value.names.forEach {
            nameMap[it] = value
        }
        console().sendLang("attribute-register", key, value.priority)
        val after = AttributeRegisterEvent(Time.AFTER, value)
        after.call()
    }
}