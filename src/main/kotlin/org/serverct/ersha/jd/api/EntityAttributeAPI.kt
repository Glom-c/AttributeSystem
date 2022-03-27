package org.serverct.ersha.jd.api

import com.skillw.attsystem.AttributeSystem
import org.bukkit.entity.Entity
import taboolib.common5.Coerce

object EntityAttributeAPI {
    fun addEntityAttribute(entity: Entity, source: String, attribute: List<String>) {
        AttributeSystem.attributeDataManager.addAttribute(entity, source, attribute)
    }

    fun addEntityAttribute(entity: Entity, source: String, attribute: List<String>, release: Int) {
        AttributeSystem.attributeDataManager.addAttribute(entity, source, attribute, Coerce.toBoolean(release))
    }

    fun removeEntityAttribute(entity: Entity, source: String) {
        AttributeSystem.attributeDataManager.removeAttribute(entity, source)
    }
}