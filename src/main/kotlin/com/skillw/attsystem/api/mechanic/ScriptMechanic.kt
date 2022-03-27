package com.skillw.attsystem.api.mechanic

import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.Pouvoir
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.function.Function

class ScriptMechanic private constructor(key: String, private val script: String) : Mechanic(key),
    ConfigurationSerializable {
    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("script" to script)
    }

    companion object {
        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): ScriptMechanic? {
            val key = section.name
            val script = section.getString("script") ?: return null
            val mechanic = ScriptMechanic(key, script)
            mechanic.config = true
            return mechanic
        }
    }

    override fun apply(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any? {
        return Pouvoir.scriptManager.invoke(
            script,
            mutableMapOf(
                "data" to fightData,
                "context" to context,
                "damageType" to damageType,
                "handle" to Function<String, String> {
                    return@Function fightData.handle(it)
                })
        )
    }
}