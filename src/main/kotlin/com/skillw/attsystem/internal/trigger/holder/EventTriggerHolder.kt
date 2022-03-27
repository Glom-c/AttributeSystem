package com.skillw.attsystem.internal.trigger.holder

import com.skillw.attsystem.api.trigger.TriggerHolder
import org.bukkit.event.Event

open class EventTriggerHolder(override val key: String, override val obj: Event) :
    TriggerHolder {
}