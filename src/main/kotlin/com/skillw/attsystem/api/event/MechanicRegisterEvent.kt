package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.mechanic.Mechanic
import com.skillw.pouvoir.api.event.Time
import taboolib.common.platform.event.ProxyEvent

class MechanicRegisterEvent(val time: Time, val mechanic: Mechanic) : ProxyEvent() {
    override val allowCancelled = time != Time.AFTER
}