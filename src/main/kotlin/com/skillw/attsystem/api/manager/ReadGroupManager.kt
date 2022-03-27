package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.read.ReadGroup
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class ReadGroupManager : LowerKeyMap<ReadGroup>(), Manager {


}