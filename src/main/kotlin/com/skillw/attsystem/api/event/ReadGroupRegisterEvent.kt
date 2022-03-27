package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.read.ReadGroup
import com.skillw.pouvoir.api.event.Time
import taboolib.common.platform.event.ProxyEvent

class ReadGroupRegisterEvent(val time: Time, val readGroup: ReadGroup) : ProxyEvent() {
    override val allowCancelled = time != Time.AFTER
}