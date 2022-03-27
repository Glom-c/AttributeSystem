package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.MechanicRegisterEvent
import com.skillw.attsystem.api.manager.MechanicManager
import com.skillw.attsystem.api.mechanic.Mechanic
import com.skillw.attsystem.api.mechanic.ScriptMechanic
import com.skillw.pouvoir.api.event.Time
import com.skillw.pouvoir.util.FileUtils
import java.io.File

object MechanicManagerImpl : MechanicManager() {
    override val key = "MechanicManager"
    override val priority: Int = 11
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.config }.forEach { this.remove(it.key) }
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "mechanic"), ScriptMechanic::class.java
        ).forEach {
            it.key.register()
        }
    }

    override fun register(key: String, value: Mechanic) {
        val before = MechanicRegisterEvent(Time.BEFORE, value)
        before.call()
        if (before.isCancelled) return
        put(key, value)
        val after = MechanicRegisterEvent(Time.AFTER, value)
        after.call()
    }
}