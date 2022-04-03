package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.manager.AttributeManager
import com.skillw.pouvoir.api.map.BaseMap
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
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
        attributes.removeIf { it.release }
        this.nameMap.entries.filter { it.value.release }.forEach { nameMap.remove(it.key) }
    }

    override fun register(key: String, value: Attribute) {
        attributes.removeIf { it == value }
        attributes.add(value)
        attributes.sort()
        put(key, value)
        value.names.forEach {
            nameMap[it] = value
        }
    }
}