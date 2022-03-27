package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class AttributeManager : LowerKeyMap<Attribute>(), Manager {

    abstract val attributes: List<Attribute>
}