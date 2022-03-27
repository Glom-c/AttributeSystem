package com.skillw.attsystem.api.operation

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.able.Keyable

abstract class BaseOperation(override val key: String) : Operation, Keyable<String> {

    var config = false
    override fun register() {
        AttributeSystem.operationManager.register(this)
    }
}