package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.fight.DamageType
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

abstract class DamageTypeManager : KeyMap<String, DamageType>(), Manager {

}