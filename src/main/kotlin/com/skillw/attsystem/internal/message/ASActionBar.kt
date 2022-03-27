package com.skillw.attsystem.internal.message

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.PlayerUtils
import com.skillw.pouvoir.util.StringUtils.placeholder
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import taboolib.module.chat.colored

class ASActionBar(
    private val text: StringBuilder
) : Message {

    fun separator(type: Message.Type): String {
        return ASConfig["message"].getString("fight-message.action-bar.${type.name.lowercase()}.separator") ?: "&5|"
    }

    private fun append(text: StringBuilder, type: Message.Type): ASActionBar {
        this.text.append(separator(type)).append(text)
        return this
    }


    override fun sendTo(vararg players: Player) {
        players.forEach { player ->
            PlayerUtils.sendActionBar(
                player,
                text.toString().placeholder(player).colored(),
                ASConfig["message"].getLong("fight-message.action-bar.stay"),
                AttributeSystem.plugin
            )
        }
    }

    fun sendToInfo(player: Player) {
        PlayerUtils.sendActionBar(
            player,
            text.toString().placeholder(player)
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ASActionBar {
            val stringBuilder = StringBuilder()
            section.getStringList("texts").forEach { stringBuilder.append(it.toString()) }
            return ASActionBar(
                stringBuilder
            )
        }
    }

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASActionBar
        return this.append(message.text, type)
    }

}