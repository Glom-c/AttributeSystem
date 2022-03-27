package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.mechanic.Mechanic
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

abstract class MechanicManager : KeyMap<String, Mechanic>(), Manager {
}