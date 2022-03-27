package com.skillw.attsystem.api.condition

import com.skillw.pouvoir.Pouvoir
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import java.util.regex.Matcher

class ScriptCondition(
    key: String,
    type: ConditionType,
    names: Set<String>,
    private val script: String
) : Condition(key, names, type), ConfigurationSerializable {
    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf("type" to type.name.lowercase(), "names" to names.toList(), "script" to script)
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ScriptCondition? {
            val key = section.name
            val type =
                Coerce.toEnum(section["type"].toString().uppercase(), ConditionType::class.java)
                    ?: ConditionType.ALL
            val script = section.getString("script") ?: return null
            val condition = ScriptCondition(key, type, HashSet(section.getStringList("names")), script)
            condition.config = true
            return condition
        }
    }

    override fun condition(slot: String, livingEntity: LivingEntity?, matcher: Matcher, text: String): Boolean {
        val map: MutableMap<String, Any> =
            if (livingEntity == null) mutableMapOf(
                "text" to text,
                "slot" to slot,
                "type" to type.name.lowercase(),
                "matcher" to matcher,
                "entity" to "null"
            ) else mutableMapOf(
                "text" to text,
                "slot" to slot,
                "type" to type.name.lowercase(),
                "matcher" to matcher,
                "entity" to livingEntity
            )
        return Coerce.toBoolean(
            Pouvoir.scriptManager.invoke(
                script,
                argsMap = map
            )
        )
    }


}