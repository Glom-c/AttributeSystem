package com.skillw.attsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.message.*
import com.skillw.attsystem.internal.personal.AttackingMessageType
import com.skillw.attsystem.internal.personal.DefensiveMessageType
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.FileUtils.toMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player

class DamageType(override val key: String, val name: String, display: Map<String, Any>) : Keyable<String>,
    BaseMap<String, String>(), ConfigurationSerializable {

    init {
        display.forEach { (key, value) ->
            this[key] = value.toString()
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("display" to this.map)
    }

    fun attackMessage(player: Player, fightData: FightData, first: Boolean = false): Message? {
        val message = ASConfig["message"]
        when (AttributeSystem.personalManager[player.uniqueId]?.attacking) {
            AttackingMessageType.DISABLE -> {
                return null
            }
            AttackingMessageType.TITLE -> {
                val title = this["attack-title"].toString().replace("{name}", name)
                val subTitle = this["attack-sub-title"].toString().replace("{name}", name)
                val titleStr = fightData.handle(
                    if (first) message.getString("fight-message.title.attack.title")?.replace("{message}", title)
                        ?: title
                    else title
                )
                val subTitleStr = fightData.handle(
                    if (first) message.getString("fight-message.title.attack.sub-title")
                        ?.replace("{message}", subTitle)
                        ?: subTitle
                    else subTitle
                )
                return ASTitle(StringBuilder(titleStr), StringBuilder(subTitleStr))
            }
            AttackingMessageType.ACTION_BAR -> {
                val attackText = this["attack-action-bar"].toString().replace("{name}", name)
                val text = fightData.handle(
                    if (first) message.getString("fight-message.action-bar.attack.text")
                        ?.replace("{message}", attackText)
                        ?: attackText
                    else attackText
                )
                return ASActionBar(StringBuilder(text))
            }
            AttackingMessageType.CHAT -> {
                val attackText = this["attack-chat"].toString().replace("{name}", name)
                val text = fightData.handle(
                    if (first) message.getString("fight-message.chat.attack.text")
                        ?.replace("{message}", attackText)
                        ?: attackText
                    else attackText
                )
                return ASChat(StringBuilder(text))
            }
            AttackingMessageType.HOLO -> {
                val text = fightData.handle(this["attack-holo"].toString()).replace("{name}", name)
                return ASHologramGroup(mutableListOf(text), fightData.defender.eyeLocation, "fight-message.holo")
            }
            else -> {
                return null
            }
        }
    }

    fun defendMessage(player: Player, fightData: FightData, first: Boolean = false): Message? {
        val message = ASConfig["message"]
        when (AttributeSystem.personalManager[player.uniqueId]?.defensive) {
            DefensiveMessageType.DISABLE -> {
                return null
            }
            DefensiveMessageType.TITLE -> {
                val title = this["defend-title"].toString().replace("{name}", name)
                val subTitle = this["defend-sub-title"].toString().replace("{name}", name)
                val titleStr = fightData.handle(
                    if (first) message.getString("fight-message.title.defend.title")?.replace("{message}", title)
                        ?: title
                    else title
                )
                val subTitleStr = fightData.handle(
                    if (first) message.getString("fight-message.title.defend.sub-title")
                        ?.replace("{message}", subTitle)
                        ?: subTitle
                    else subTitle
                )
                return ASTitle(StringBuilder(titleStr), StringBuilder(subTitleStr))
            }

            DefensiveMessageType.ACTION_BAR -> {
                val defendText = this["defend-action-bar"].toString().replace("{name}", name)
                val text = fightData.handle(
                    if (first) message.getString("fight-message.action-bar.defend.text")
                        ?.replace("{message}", defendText)
                        ?: defendText
                    else defendText
                )
                return ASActionBar(StringBuilder(text))
            }
            DefensiveMessageType.CHAT -> {
                val defendText = this["defend-chat"].toString().replace("{name}", name)
                val text = fightData.handle(
                    if (first) message.getString("fight-message.chat.defend.text")
                        ?.replace("{message}", defendText)
                        ?: defendText
                    else defendText
                )
                return ASChat(StringBuilder(text))
            }
            DefensiveMessageType.HOLO -> {
                val text = fightData.handle(this["defend-holo"].toString()).replace("{name}", name)
                return ASHologramGroup(mutableListOf(text), fightData.defender.eyeLocation, "fight-message.holo")
            }
            else -> {
                return null
            }
        }
    }


    companion object {
        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): DamageType {
            val key = section.name
            val name = section.getString("name") ?: key
            val display = HashMap<String, Any>()
            section.getConfigurationSection("display.attack")?.toMap()?.forEach {
                display["attack-${it.key}"] = it.value
            }
            section.getConfigurationSection("display.defend")?.toMap()?.forEach {
                display["defend-${it.key}"] = it.value
            }
            return DamageType(key, name, display)
        }
    }


    override fun register() {
        AttributeSystem.damageTypeManager.register(this)
    }
}