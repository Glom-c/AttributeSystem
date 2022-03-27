package com.skillw.attsystem.api.equipment

import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.inventory.ItemStack

class EquipmentDataCompound() : LowerMap<EquipmentData>() {


    constructor(equipmentDataCompound: EquipmentDataCompound) : this() {
        for (key in equipmentDataCompound.keys) {
            val equipmentData = equipmentDataCompound[key] ?: continue
            if (equipmentData.release) continue
            this[key] = equipmentData.clone()
        }
    }

    operator fun set(key: String, subKey: String, itemStack: ItemStack): ItemStack {
        if (!this.containsKey(key))
            this[key] = EquipmentData()
        return this[key]!!.put(subKey, itemStack)
    }

    operator fun get(key: String, subKey: String): ItemStack? {
        return this[key]?.get(subKey)
    }
}