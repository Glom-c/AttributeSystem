package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.ReadGroupRegisterEvent
import com.skillw.attsystem.api.manager.ReadGroupManager
import com.skillw.attsystem.api.read.ReadGroup
import com.skillw.pouvoir.api.event.Time
import com.skillw.pouvoir.util.FileUtils
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File

object ReadGroupManagerImpl : ReadGroupManager() {
    override val key = "ReadGroupManager"
    override val priority: Int = 1
    override val subPouvoir = AttributeSystem


    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        clear()
        console().sendLang("read-group-reload-start")
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "read"), ReadGroup::class.java
        ).forEach {
            it.key.register()
        }
        console().sendLang("read-group-reload-end")
    }

    override fun register(key: String, value: ReadGroup) {
        val before = ReadGroupRegisterEvent(Time.BEFORE, value)
        before.call()
        if (before.isCancelled) return
        put(key, value)
        console().sendLang("read-group-register", key)
        val after = ReadGroupRegisterEvent(Time.AFTER, value)
        after.call()
    }


}