package com.skillw.attsystem.internal.command

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.manager.ASConfig.statNone
import com.skillw.attsystem.internal.manager.ASConfig.statPlaceholder
import com.skillw.attsystem.internal.manager.ASConfig.statStatus
import com.skillw.attsystem.internal.manager.ASConfig.statStatusValue
import com.skillw.attsystem.internal.personal.AttackingMessageType
import com.skillw.attsystem.internal.personal.DefensiveMessageType
import com.skillw.attsystem.util.Format.real
import com.skillw.pouvoir.util.EntityUtils
import com.skillw.pouvoir.util.StringUtils.replacement
import com.skillw.pouvoir.util.StringUtils.toStringWithNext
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.common5.Coerce
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import taboolib.module.nms.getName
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import taboolib.platform.util.sendBook
import taboolib.platform.util.sendLang
import java.util.*

@CommandHeader(name = "as", permission = "as.command")
object ASCommand {

    @CommandBody
    val main = mainCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("command-info")
        }
    }

    fun Array<String>.sendMessage(sender: CommandSender) {
        this.forEach {
            sender.sendMessage(it)
        }
    }


    @CommandBody(permission = "as.command.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            AttributeSystem.reload()
            sender.sendLang("command-reload")
        }
    }

    @CommandBody(permission = "as.command.version")
    val version = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("command-version")
        }
    }

    @CommandBody(permission = "as.command.personal")
    val personal = subCommand {
        dynamic {
            suggestion<Player> { sender, context ->
                listOf("AttackingMessageType", "DefensiveMessageType", "RegainHolo")
            }
            dynamic {
                suggestion<Player> { sender, context ->
                    when (context.argument(-1)) {
                        "AttackingMessageType" -> listOf("DISABLE", "TITLE", "ACTION_BAR", "CHAT", "HOLO")
                        "DefensiveMessageType" -> listOf("DISABLE", "TITLE", "ACTION_BAR", "CHAT", "HOLO")
                        "RegainHolo" -> listOf("true", "false")
                        else -> emptyList()
                    }
                }
                execute<Player> { sender, context, argument ->
                    if (!ASConfig.isPersonalEnable) return@execute
                    val data = AttributeSystem.personalManager[sender.uniqueId]!!
                    val type = context.argument(-1)
                    when (type) {
                        "AttackingMessageType" -> data.attacking = AttackingMessageType.valueOf(argument)
                        "DefensiveMessageType" -> data.defensive = DefensiveMessageType.valueOf(argument)
                        "RegainHolo" -> data.regainHolo = Coerce.toBoolean(argument)
                        else -> return@execute
                    }
                    AttributeSystem.personalManager[sender.uniqueId] = data
                    sender.sendLang("command-personal", type, argument)
                }
            }
        }
    }

    @CommandBody(permission = "as.command.stats")
    val stats = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { sender, context ->
                    listOf("text", "book")
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val player = Bukkit.getPlayer(context.argument(-1))
                    if (player == null) {
                        sender.sendLang("command-valid-player", context.argument(-1))
                        return@execute
                    }
                    AttributeSystem.poolExecutor.execute {
                        val title =
                            ASConfig.statTitle.replace("{name}", player.name).replace("{player}", player.name)
                                .colored()
                        val attributeDataCompound =
                            AttributeSystem.attributeDataManager[player.uniqueId] ?: AttributeDataCompound()
                        if (sender is ProxyPlayer) {
                            when (argument) {
                                "book" -> {
                                    sender.cast<Player>().sendBook {
                                        val jsons = attributeStatusToJson(attributeDataCompound, player)
                                        jsons.forEach {
                                            val tellrawJson = TellrawJson()
                                            tellrawJson.append("$title\n")
                                            tellrawJson.append(it)
                                            this.write(tellrawJson)
                                        }
                                    }
                                }
                                else -> {
                                    sendStatText(sender, title, attributeDataCompound, player)
                                }
                            }
                        } else {
                            sendStatText(sender, title, attributeDataCompound, player)
                        }
                    }
                }
            }
        }
    }

    @CommandBody(permission = "as.command.stats")
    val itemstats = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<Player> { sender, context ->
                    AttributeSystem.equipmentDataManager[sender.uniqueId]?.map { it.key }
                }
                dynamic {
                    suggestion<Player> { sender, context ->
                        val list = LinkedList<String>()
                        AttributeSystem.equipmentDataManager[sender.uniqueId]?.values?.forEach { list.addAll(it.keys) }
                        list
                    }
                    dynamic {
                        suggestion<Player> { sender, context ->
                            listOf("text", "book")
                        }
                        execute<Player> { sender, context, argument ->
                            val player = Bukkit.getPlayer(context.argument(-3))
                            if (player == null) {
                                sender.sendLang("command-valid-player", context.argument(-3))
                                return@execute
                            }
                            val key = context.argument(-2)
                            val subKey = context.argument(-1)
                            val itemStack = AttributeSystem.equipmentDataManager[player.uniqueId]?.get(key, subKey)
                            if (itemStack == null) {
                                sender.sendLang("command-valid-item")
                                return@execute
                            }
                            if (itemStack.isAir() || !itemStack.hasLore()) return@execute
                            AttributeSystem.poolExecutor.execute {
                                val title = ASConfig.statTitle.replace("{name}", itemStack.getName())
                                    .replace("{player}", itemStack.getName()).colored()
                                val attributeDataCompound =
                                    AttributeSystem.equipmentDataManager.readItem(
                                        Attribute.Oriented.ALL,
                                        itemStack,
                                        player,
                                        subKey
                                    )
                                when (argument) {
                                    "book" -> {
                                        sender.sendBook {
                                            val jsons = attributeStatusToJson(attributeDataCompound, player)
                                            jsons.forEach {
                                                val tellrawJson = TellrawJson()
                                                tellrawJson.append("$title\n")
                                                tellrawJson.append(it)
                                                this.write(tellrawJson)
                                            }
                                        }
                                    }
                                    else -> {
                                        sendStatText(adaptPlayer(sender), title, attributeDataCompound, player)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @CommandBody(permission = "as.command.stats")
    val entitystats = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                listOf("text", "book")
            }
            execute<ProxyPlayer> { sender, context, argument ->
                val player = sender.cast<Player>()
                val entity = EntityUtils.getEntityRayHit(player, 10.0)
                if (entity == null) {
                    sender.sendLang("command-valid-entity")
                    return@execute
                }
                AttributeSystem.poolExecutor.execute {
                    val name =
                        if (entity is Player) entity.displayName else (if (entity.customName == null) entity.getI18nName() else entity.customName)
                            ?: "null"
                    val title =
                        ASConfig.statTitle.replace("{name}", name).replace("{player}", name)
                            .colored()
                    val attributeDataCompound =
                        AttributeSystem.attributeDataManager[entity.uniqueId] ?: AttributeDataCompound()
                    when (argument) {
                        "book" -> {
                            player.sendBook {
                                val jsons = attributeStatusToJson(attributeDataCompound, entity)
                                jsons.forEach {
                                    val tellrawJson = TellrawJson()
                                    tellrawJson.append("$title\n")
                                    tellrawJson.append(it)
                                    this.write(tellrawJson)
                                }
                            }
                        }
                        else -> {
                            sendStatText(sender, title, attributeDataCompound, entity)
                        }
                    }
                }
            }
        }
    }

    private fun attributeStatusToJson(
        attributeDataCompound: AttributeDataCompound,
        livingEntity: LivingEntity
    ): LinkedList<TellrawJson> {
        val format = ASConfig.statAttributeFormat.colored()
        val attributes = AttributeSystem.attributeManager.attributes
        val finalList = LinkedList<TellrawJson>()
        var list = LinkedList<TellrawJson>()
        for (index in attributes.indices) {
            val attribute = attributes[index]
            val json = TellrawJson()
            json.append(
                format.replacement(
                    mapOf(
                        "{name}" to attribute.names[0],
                        "{value}" to attributeDataCompound.getAttributeTotal(attribute)
                    )
                ) + if (!((index != 0 && index % 12 == 0) || index == attributes.lastIndex)) "\n" else ""
            ).hoverText(
                "$statStatus \n" +
                        attributeDataCompound.getAttributeStatus(attribute).map {
                            statStatusValue.replace("{key}", it.key).replace("{value}", it.value.real())
                        }.run { this.ifEmpty { listOf(statNone) } }.toStringWithNext()
                        + "\n \n"
                        + "$statPlaceholder \n"
                        + attribute.readGroup.placeholders.keys.map {
                    statStatusValue.replace("{key}", it)
                        .replace("{value}", attribute.get(it, attributeDataCompound, livingEntity).real())
                }.run { this.ifEmpty { listOf(statNone) } }.toStringWithNext()
            )
            list.add(json)
            if ((index != 0 && index % 12 == 0) || index == attributes.lastIndex) {
                val tellrawJson = TellrawJson()
                list.forEach {
                    tellrawJson.append(it)
                }
                finalList.add(tellrawJson)
                list = LinkedList()
            }
        }

        return finalList
    }

    private fun sendStatText(
        sender: ProxyCommandSender,
        title: String,
        attributeDataCompound: AttributeDataCompound,
        livingEntity: LivingEntity
    ) {
        sender.sendMessage(title)
        attributeStatusToJson(attributeDataCompound, livingEntity).forEach {
            it.sendTo(sender)
        }
    }

    @CommandBody(permission = "as.command.help")
    val help = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("command-info")
        }
    }


}