package com.skillw.attsystem.internal.message

import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.PlayerUtils
import com.skillw.pouvoir.util.StringUtils.placeholder
import org.bukkit.entity.Player
import taboolib.module.chat.colored

class ASTitle(
    val title: StringBuilder,
    val subTitle: StringBuilder
) : Message {
    fun separator(type: Message.Type): String {
        return ASConfig["message"].getString("fight-message.title.${type.name.lowercase()}.separator") ?: "&5|"
    }

    private fun appendTitle(title: StringBuilder, type: Message.Type): ASTitle {
        if (title.toString() != "null")
            this.title.append(separator(type)).append(title)
        return this
    }

    private fun appendSubtitle(subTitle: StringBuilder, type: Message.Type): ASTitle {
        if (subTitle.toString() != "null")
            this.subTitle.append(separator(type)).append(subTitle)
        return this
    }

    override fun plus(message: Message, type: Message.Type): ASTitle {
        message as ASTitle
        return this.appendTitle(message.title, type).appendSubtitle(message.subTitle, type)
    }

    override fun sendTo(vararg players: Player) {
        val section = ASConfig["message"].getConfigurationSection("fight-message.title")
        players.forEach { player ->
            val titleStr = this.title.toString().placeholder(player)
            val subTitleStr = this.subTitle.toString().placeholder(player)
            val title: String? = if (titleStr != "null") titleStr else null
            val subTitle: String? = if (subTitleStr != "null") subTitleStr else null
            PlayerUtils.sendTitle(
                player,
                title?.colored() ?: "",
                subTitle?.colored() ?: "",
                section?.getInt("fade-in") ?: 0,
                section?.getInt("stay") ?: 20,
                section?.getInt("fade-out") ?: 0
            )
        }
    }
}