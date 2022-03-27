package com.skillw.attsystem.api.condition

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.able.Keyable
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class Condition(override val key: String, names: Set<String>, val type: ConditionType) : Keyable<String> {
    abstract fun condition(slot: String, livingEntity: LivingEntity?, matcher: Matcher, text: String): Boolean
    val names = HashSet<Pattern>()

    var release = false

    override fun register() {
        AttributeSystem.conditionManager.register(this)
    }

    fun typeLine() = type == ConditionType.LINE || type == ConditionType.ALL
    fun typeStrings() = type == ConditionType.STRINGS || type == ConditionType.ALL
    fun typeAll() = type == ConditionType.ALL
    fun isType(type: ConditionType) = this.type == type || this.typeAll()
    enum class ConditionType {
        LINE, STRINGS, ALL
    }

    class Builder(val key: String, val type: ConditionType) {
        var release = false
        val names = HashSet<String>()

        private val conditions = LinkedList<(String, LivingEntity?, Matcher, String) -> Boolean>()
        fun condition(
            func: (String, LivingEntity?, Matcher, String) -> Boolean
        ) {
            conditions.add(func)
        }

        fun build(): Condition {
            return object : Condition(key, names, type) {
                override fun condition(
                    slot: String,
                    livingEntity: LivingEntity?,
                    matcher: Matcher,
                    text: String
                ): Boolean {
                    return conditions.any {
                        it(slot, livingEntity, matcher, text)
                    }
                }

                init {
                    this.release = this@Builder.release
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun createCondition(
            key: String,
            type: ConditionType,
            init: Builder.() -> Unit
        ): Condition {
            val builder = Builder(key, type)
            builder.init()
            return builder.build()
        }
    }
}

