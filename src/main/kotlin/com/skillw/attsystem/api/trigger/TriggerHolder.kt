package com.skillw.attsystem.api.trigger

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.trigger.AttackTrigger
import com.skillw.pouvoir.api.able.Keyable
import java.util.function.Consumer

interface TriggerHolder : Keyable<String> {
    val trigger: Trigger
        get() = AttributeSystem.triggerManager.getTrigger(key) ?: AttackTrigger
    val obj: Any
    override fun register() {
    }

    fun call() {
        AttributeSystem.triggerManager.call(this)
    }

    fun call(consumer: Consumer<TriggerHolder>) {
        AttributeSystem.triggerManager.call(this, consumer)
    }
}