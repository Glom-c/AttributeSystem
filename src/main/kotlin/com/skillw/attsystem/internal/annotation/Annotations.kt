package com.skillw.attsystem.internal.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.condition.Condition
import com.skillw.attsystem.api.condition.ScriptCondition
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.util.MessageUtils.wrong
import taboolib.common5.Coerce

/**
 * Condition
 *
 * @constructor Condition Type Key Names...
 */
object Condition : ScriptAnnotation("Condition", handle@{ data ->
    val compiledFile = data.compiledFile
    val args = if (data.args.contains(",")) {
        data.args.split(",").toMutableList()
    } else {
        null
    }
    val function = data.function
    if (args == null || args.size < 3) {
        wrong("The ScriptAnnotation Condition on the function $function in ${compiledFile.key} has no enough arguments!")
        return@handle
    }
    val key = "annotation-${args[0]}"
    val type =
        Coerce.toEnum(args[1].uppercase(), Condition.ConditionType::class.java)
            ?: Condition.ConditionType.ALL
    args.removeAt(0)
    args.removeAt(0)
    val names = HashSet<String>(args)
    ScriptCondition(key, type, names, "${compiledFile.key}::$function").register()
    compiledFile["register-Condition-this::$function"] = {
        AttributeSystem.conditionManager.remove(key)
    }
})