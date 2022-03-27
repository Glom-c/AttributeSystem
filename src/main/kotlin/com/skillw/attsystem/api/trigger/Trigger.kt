package com.skillw.attsystem.api.trigger

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.able.Keyable

open class Trigger protected constructor(override val key: String) : Keyable<String> {
    override fun register() {
        AttributeSystem.triggerManager.register(this)
    }

}