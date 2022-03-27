package com.skillw.attsystem.api.mechanic

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.LinkedMap
import com.skillw.pouvoir.util.FileUtils.toMap
import com.skillw.pouvoir.util.MessageUtils.wrong
import org.bukkit.configuration.serialization.ConfigurationSerializable

class MechanicData private constructor(override val key: DamageType, val enable: String) : Keyable<DamageType>,
    LinkedMap<Mechanic, Map<String, Any>>(), ConfigurationSerializable {

    companion object {

        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): MechanicData? {
            val damageType = AttributeSystem.damageTypeManager[section.name]
            if (damageType == null) {
                wrong("&eThe DamageType is null in ${section.currentPath}!")
                return null
            }
            val mechanicData = MechanicData(damageType, section.getString("enable") ?: "true")
            for (key in section.getKeys(false)) {
                if (key == "enable") continue
                val machine = AttributeSystem.mechanicManager[key]
                if (machine == null) {
                    wrong("&eThe Mechanic is null in ${section.currentPath}.$key!")
                    continue
                }
                mechanicData[machine] = section.getConfigurationSection(key)!!.toMap()
            }
            return mechanicData
        }
    }

    fun run(fightData: FightData) {
        if (fightData.handle(enable) != "true") return
        for (machine in list) {
            AttributeSystem.debug("   &7- &5Machine: &c${machine.key}")
            val context = this[machine] ?: continue
            val result = machine.run(fightData, fightData.handleMap(context), key)
            AttributeSystem.debug("     &5return: &6$result")
            fightData[machine.key] = result ?: continue
        }
    }

    override fun register() {}
    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["enable"] = enable
        this.forEach { machine, data ->
            map[machine.key] = data
        }
        return map
    }
}