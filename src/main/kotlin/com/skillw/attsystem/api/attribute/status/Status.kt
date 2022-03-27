package com.skillw.attsystem.api.attribute.status

interface Status {
    fun operation(status: Status): Status
    fun serialize(): Map<String, Any>
    fun clone(): Status
}