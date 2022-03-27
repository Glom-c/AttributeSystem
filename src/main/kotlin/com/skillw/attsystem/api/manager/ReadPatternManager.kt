package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class ReadPatternManager : LowerKeyMap<ReadPattern>(), Manager {


}