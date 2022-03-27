package com.skillw.attsystem.api.operation

import com.skillw.pouvoir.Pouvoir
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import taboolib.common5.Coerce

class ScriptOperation(
    key: String,
    private val script: String
) : BaseOperation(key), ConfigurationSerializable {
    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf("script" to script)
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ScriptOperation? {
            val key = section.name
            val script = section.getString("script") ?: return null
            val operation = ScriptOperation(key, script)
            operation.config = true
            return operation
        }
    }

    override fun run(a: Double, b: Double): Double {
        return Coerce.toDouble(
            Pouvoir.scriptManager.invoke(
                script,
                argsMap = mutableMapOf(
                    "a" to a,
                    "b" to b
                )
            )
        )
    }


}