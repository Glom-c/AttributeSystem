package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.slot.PlayerSlot
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class PlayerSlotManager : LowerKeyMap<PlayerSlot>(), Manager {

}