package com.skillw.attsystem.internal.message

import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.hologram.HologramBuilder
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.module.chat.colored

class ASHologramGroup(val texts: MutableList<String>, private val location: Location, val node: String) : Message {
    override fun sendTo(vararg players: Player) {
        val section = ASConfig["message"].getConfigurationSection(node)!!
        val begin = section.getConfigurationSection("begin")!!
        val beginLocation = Location(location.world, begin.getDouble("x"), begin.getDouble("y"), begin.getDouble("z"))
        val end = section.getConfigurationSection("end")!!
        val endLocation = Location(location.world, end.getDouble("x"), end.getDouble("y"), end.getDouble("z"))
        val stay = section.getLong("stay")
        val time = section.getInt("time")
        HologramBuilder(location.clone().add(beginLocation))
            .content(texts.colored().toMutableList())
            .stay(stay)
            .animation(time, location.clone().add(endLocation))
            .viewers(*players).build()
    }

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASHologramGroup
        this.texts.addAll(message.texts)
        return this
    }
}