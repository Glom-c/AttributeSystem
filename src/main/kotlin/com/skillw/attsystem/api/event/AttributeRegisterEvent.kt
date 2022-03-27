package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.pouvoir.api.event.Time
import taboolib.common.platform.event.ProxyEvent

class AttributeRegisterEvent(val time: Time, val attribute: Attribute) : ProxyEvent() {
    override val allowCancelled = time != Time.AFTER
}