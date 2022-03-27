package com.skillw.attsystem.api.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.pouvoir.api.able.Keyable
import java.util.*

class Attribute(
    override val key: String,
    val names: List<String>,
    val readPattern: ReadPattern,
    val priority: Int = 0
) : Keyable<String>, Comparable<Attribute> {
    override fun compareTo(other: Attribute): Int = if (this.priority == other.priority) 0
    else if (this.priority > other.priority) 1
    else -1

    var entity = true
    var release = false

    override fun register() {
        AttributeSystem.attributeManager.register(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attribute

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + readPattern.hashCode()
        return result
    }

    override fun toString(): String {
        return "Attribute(key='$key')"
    }

    class Builder(val key: String, val readPattern: ReadPattern) {
        var entity = true
        var release = false
        var priority: Int = 0
        val names = LinkedList<String>()
        fun build(): Attribute {
            val att = Attribute(key, names, readPattern, priority)
            att.release = release
            att.entity = entity
            return att
        }

    }

    companion object {
        @JvmStatic
        fun createAttribute(
            key: String,
            readPattern: ReadPattern,
            init: Builder.() -> Unit
        ): Attribute {
            val builder = Builder(key, readPattern)
            builder.init()
            return builder.build()
        }
    }
}