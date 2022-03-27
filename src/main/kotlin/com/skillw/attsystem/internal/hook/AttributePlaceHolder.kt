package com.skillw.attsystem.internal.hook

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.manager.ASConfig.skillAPI
import com.skillw.pouvoir.api.placeholder.PouPlaceHolder
import com.skillw.pouvoir.util.NumberUtils.format
import com.sucy.skill.SkillAPI
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.math.BigDecimal

object AttributePlaceHolder : PouPlaceHolder("as", AttributeSystem) {

    fun get(
        data: AttributeDataCompound,
        attribute: Attribute,
        params: List<String>,
        livingEntity: LivingEntity
    ): String = when (params.size) {
        0 ->
            BigDecimal(data.getAttributeTotal(attribute)).format()
        1 -> {
            BigDecimal(attribute.get(params[0], data, livingEntity)).format()
        }
        else ->
            "0.0"
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
                val data = equipmentDataManager.readItem(attribute.oriented, item)
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
            "mana" -> {
                if (livingEntity !is Player) return "0.0"
                return if (skillAPI) {
                    SkillAPI.getPlayerData(livingEntity).mana.format()
                } else "0.0"
            }
            "health" -> {
                return BigDecimal(livingEntity.health).format()
            }
            "max" -> {
                if (strings.size <= 1) return "0.0"
                when (strings[1]) {
                    "mana" -> {
                        if (livingEntity !is Player) return "0.0"
                        return if (skillAPI) {
                            SkillAPI.getPlayerData(livingEntity).maxMana.format()
                        } else "0.0"
                    }
                    "health" -> {
                        return livingEntity.maxHealth.format()
                    }
                }
            }
        }
        return "0.0"
    }
}