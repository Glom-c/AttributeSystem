package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.manager.DamageTypeManager
import com.skillw.pouvoir.util.FileUtils
import java.io.File

object DamageTypeManagerImpl : DamageTypeManager() {
    override val key = "DamageTypeManager"
    override val priority: Int = 10
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.clear()
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "damage"), DamageType::class.java
        ).forEach {
            it.key.register()
        }
    }

}