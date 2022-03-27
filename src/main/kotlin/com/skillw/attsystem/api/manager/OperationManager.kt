package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.operation.BaseOperation
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class OperationManager : LowerKeyMap<BaseOperation>(), Manager {

}