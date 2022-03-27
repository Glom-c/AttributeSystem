package com.skillw.attsystem.api.equipment

import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.inventory.ItemStack

class EquipmentData : LowerMap<ItemStack> {

    constructor()
    constructor(release: Boolean) {
        this.release = release
    }

    constructor(equipmentData: EquipmentData, release: Boolean) {
        this.release = release
        for (key in equipmentData.keys) {
            this[key] = equipmentData[key]!!.clone()
        }
    }

    fun clone(): EquipmentData {
        val equipmentData = EquipmentData()
        this.forEach {
            equipmentData.put(it.key, it.value.clone())
        }
        return equipmentData
    }

    constructor(equipmentData: EquipmentData) : this(equipmentData, true)

    var release = false

    fun release(): EquipmentData {
        this.release = true
        return this
    }

    fun unRelease(): EquipmentData {
        this.release = false
        return this
    }
}