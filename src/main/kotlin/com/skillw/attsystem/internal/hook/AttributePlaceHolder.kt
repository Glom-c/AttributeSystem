package com.skillw.attsystem.internal.hook

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.placeholder.PouPlaceHolder
import com.skillw.pouvoir.util.NumberUtils.format
import org.bukkit.entity.LivingEntity
import java.math.BigDecimal

object AttributePlaceHolder : PouPlaceHolder("as", AttributeSystem) {

    fun get(
        data: AttributeDataCompound,
        attribute: Attribute,
        params: List<String>,
        livingEntity: LivingEntity
    ): String {
        val status = data.getAttributeStatus(attribute) ?: return "0.0"
        return when (params.size) {
            0 ->
                attribute.readPattern.placeholder("total", attribute, status, livingEntity).toString()
            1 -> {
                attribute.readPattern.placeholder(params[0], attribute, status, livingEntity).toString()
            }
            else ->
                "0.0"
        }
    }

    override fun onPlaceHolderRequest(params: String, livingEntity: LivingEntity, def: String): String {
        val lower = params.lowercase().replace(":", "_")
        val uuid = livingEntity.uniqueId
        val strings = if (lower.contains("_")) lower.split("_").toMutableList() else mutableListOf(lower)
        val first = strings[0]
        when (first) {
            "att" -> {
                val attribute = attributeManager[strings[1]]
                if (attribute != null) {
                    val data = attributeDataManager[uuid] ?: return "0"
                    strings.removeAt(0)
                    strings.removeAt(0)
                    return get(data, attribute, strings, livingEntity)
                }
            }
//            "shield" -> {
//                when (strings[1]) {
//                    "value" ->
//                        return AttributeSystem.getShieldDataManager().getShield(uuid).toString()
//                    else -> {
//                        return "N/A"
//                    }
//                }
//            }
            "equipment" -> {
                strings.removeAt(0)
                if (strings.size < 3) return "0.0"
                val key = strings[0]
                val subKey = strings[1]
                val attKey = strings[2]
                strings.removeAt(0)
                strings.removeAt(0)
                strings.removeAt(0)
                val equipment = equipmentDataManager[uuid]
                if (equipment == null || !equipment.containsKey(key)) return "0.0"
                val item = equipment[key, subKey] ?: return "0.0"
                val attribute = attributeManager[attKey] ?: return "0.0"
                val data = equipmentDataManager.readItem(item)
                return get(data, attribute, strings, livingEntity)
            }
            "formula" -> {
                strings.removeAt(0)
                if (strings.isEmpty()) return "0.0"
                return BigDecimal(formulaManager.calculate(uuid, strings[0])).format()
            }
            "formulastr" -> {
                strings.removeAt(0)
                if (strings.isEmpty()) return "0.0"
                return formulaManager[strings[0]] ?: "N/A"
            }
        }
        return "0.0"
    }
}