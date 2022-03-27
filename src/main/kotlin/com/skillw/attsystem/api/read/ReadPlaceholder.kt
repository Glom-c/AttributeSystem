package com.skillw.attsystem.api.read

import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.configuration.ConfigurationSection

class ReadPlaceholder(section: ConfigurationSection?) : LowerMap<String>() {

    init {
        section?.getKeys(false)?.forEach {
            this[it] = section[it].toString()
        }
    }
}