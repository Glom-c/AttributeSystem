package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.ReadPatternManager

object ReadPatternManagerImpl : ReadPatternManager() {
    override val key = "ReadPatternManager"
    override val priority: Int = 1
    override val subPouvoir = AttributeSystem


    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

}