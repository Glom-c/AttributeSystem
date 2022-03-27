package com.skillw.attsystem.internal.message

import org.bukkit.entity.Player

interface Message {
    enum class Type {
        ATTACK, DEFEND, INFO
    }

    fun sendTo(vararg players: Player)
    fun plus(message: Message, type: Type): Message

    companion object {
        fun List<Message>.send(type: Type, vararg players: Player) {
            if (this.isEmpty()) return
            var message = this[0]
            for (index in 1 until this.size) {
                message = message.plus(this[index], type)
            }
            message.sendTo(*players)
        }
    }
}