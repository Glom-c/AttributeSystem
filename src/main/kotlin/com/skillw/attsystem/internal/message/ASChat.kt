package com.skillw.attsystem.internal.message

import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.StringUtils.placeholder
import org.bukkit.entity.Player
import taboolib.module.chat.colored

class ASChat(val text: StringBuilder) : Message {
    override fun plus(message: Message, type: Message.Type): Message {
        message as ASChat
        text.append(separator(type)).append(message.text)
        return this
    }

    fun separator(type: Message.Type): String {
        return ASConfig["message"].getString("fight-message.chat.${type.name.lowercase()}.separator") ?: "&5|"
    }

    override fun sendTo(vararg players: Player) {
        players.forEach { player -> player.sendMessage(text.toString().placeholder(player).colored()) }
    }

}