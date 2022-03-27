package com.skillw.attsystem.util

import org.bukkit.attribute.Attribute
import taboolib.common.reflect.Reflex
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import java.util.*

/**
 * Attribute 映射类
 *
 * @author sky
 * @since 2019-12-11 19:31
 */
enum class BukkitAttribute(var minecraftKey: String, var simplifiedKey: Array<String>) {
    /**
     * 最大生命值
     */
    MAX_HEALTH("generic.maxHealth", arrayOf("health", "maxHealth")),

    /**
     * 最大跟随距离
     */
    FOLLOW_RANGE("generic.followRange", arrayOf("follow", "followRange")),

    /**
     * 击退抗性
     */
    KNOCKBACK_RESISTANCE("generic.knockbackResistance", arrayOf("knockback", "knockbackResistance")),

    /**
     * 移动速度
     */
    MOVEMENT_SPEED("generic.movementSpeed", arrayOf("speed", "movementSpeed", "walkSpeed")),

    /**
     * 飞行速度
     */
    FLYING_SPEED("generic.flyingSpeed", arrayOf("flySpeed", "flyingSpeed")),

    /**
     * 攻击力
     */
    ATTACK_DAMAGE("generic.attackDamage", arrayOf("damage", "attackDamage")),

    /**
     * 击退
     */
    ATTACK_KNOCKBACK("generic.attackKnockback", arrayOf("damageKnockback", "attackKnockback")),

    /**
     * 攻速
     */
    ATTACK_SPEED("generic.attackSpeed", arrayOf("damageSpeed", "attackSpeed")),

    /**
     * 护甲
     */
    ARMOR("generic.armor", arrayOf("armor")),

    /**
     * 护甲韧性
     */
    ARMOR_TOUGHNESS("generic.armorToughness", arrayOf("toughness", "armorToughness")),

    /**
     * 幸运
     */
    LUCK("generic.luck", arrayOf("luck"));

    fun toBukkit(): Attribute? {
        val attribute: Attribute? = try {
            Attribute.valueOf(name)
        } catch (e: Exception) {
            Attribute.valueOf("GENERIC_" + name)
        }
        return attribute
    }

    fun toNMS(): Any? {
        return if (MinecraftVersion.majorLegacy <= 11300) {
            val attributeBaseClass = nmsClass("AttributeBase")
            nmsClass("GenericAttributes").fields
                .map { classField -> classField.get(null) }.firstOrNull { attribute ->
                    attributeBaseClass.cast(attribute).invokeMethod<String>("getName")!! == (minecraftKey)
                }
        } else
            Reflex(nmsClass("GenericAttributes")).get(this.name);
    }

    fun match(source: String?): Boolean {
        return name.equals(source, ignoreCase = true) || minecraftKey.equals(
            source,
            ignoreCase = true
        ) || Arrays.stream(
            simplifiedKey
        ).anyMatch { key: String -> key.equals(source, ignoreCase = true) }
    }

    companion object {
        fun parse(source: String?): BukkitAttribute? {
            return Arrays.stream(values()).filter { attribute: BukkitAttribute? -> attribute!!.match(source) }
                .findFirst().orElse(null)
        }
    }
}