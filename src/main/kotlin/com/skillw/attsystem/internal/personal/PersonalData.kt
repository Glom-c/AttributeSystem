package com.skillw.attsystem.internal.personal

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.util.GsonUtils
import java.util.*

class PersonalData(override val key: UUID) : Keyable<UUID> {
    var attacking = ASConfig.defaultAttackMessageType
    var defensive = ASConfig.defaultDefendMessageType
    var regainHolo = ASConfig.defaultRegainHolo

    val default: Boolean
        get() = attacking == ASConfig.defaultAttackMessageType &&
                defensive == ASConfig.defaultDefendMessageType &&
                regainHolo == ASConfig.defaultRegainHolo


    fun default() {
        attacking = ASConfig.defaultAttackMessageType
        defensive = ASConfig.defaultDefendMessageType
        regainHolo = ASConfig.defaultRegainHolo
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): PersonalData? {
            return GsonUtils.gson.fromJson(json, PersonalData::class.java)
        }
    }

    override fun toString(): String {
        return GsonUtils.gson.toJson(this)
    }

    override fun register() {
        AttributeSystem.personalManager.register(this)
    }
}