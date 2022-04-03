package com.skillw.attsystem.internal.command

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.EntityUtils
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
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import taboolib.module.nms.getName
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
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


    @CommandBody(permission = "as.command.stats")
    val stats = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayer(argument)
                if (player == null) {
                    sender.sendLang("command-valid-player", argument)
                    return@execute
                }
                AttributeSystem.poolExecutor.execute {
                    val title =
                        ASConfig.statTitle.replace("{name}", player.name).replace("{player}", player.name)
                            .colored()
                    val attributeDataCompound =
                        AttributeSystem.attributeDataManager[player.uniqueId] ?: AttributeDataCompound()
                    sendStatText(sender, title, attributeDataCompound, player)
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
                    execute<Player> { sender, context, argument ->
                        val player = Bukkit.getPlayer(context.argument(-2))
                        if (player == null) {
                            sender.sendLang("command-valid-player", context.argument(-2))
                            return@execute
                        }
                        val key = context.argument(-1)
                        val subKey = argument
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
                                    itemStack,
                                    player,
                                    subKey
                                )
                            sendStatText(adaptPlayer(sender), title, attributeDataCompound, player, true)
                        }
                    }
                }
            }
        }
    }

    @CommandBody(permission = "as.command.stats")
    val entitystats = subCommand {
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
                sendStatText(sender, title, attributeDataCompound, entity)
            }
        }
    }

    private fun attributeStatusToJson(
        attributeDataCompound: AttributeDataCompound,
        livingEntity: LivingEntity,
        item: Boolean = false
    ): LinkedList<TellrawJson> {
        val attributes = AttributeSystem.attributeManager.attributes
        val list = LinkedList<TellrawJson>()
        for (index in attributes.indices) {
            val attribute = attributes[index]
            if (!attribute.entity && item) continue
            val status = attributeDataCompound.getAttributeStatus(attribute) ?: continue
            val json = attribute.readPattern.stat(
                attribute,
                status,
                livingEntity
            )
            list.add(json)
        }
        return list
    }

    private fun sendStatText(
        sender: ProxyCommandSender,
        title: String,
        attributeDataCompound: AttributeDataCompound,
        livingEntity: LivingEntity,
        item: Boolean = false
    ) {
        sender.sendMessage(title)
        attributeStatusToJson(attributeDataCompound, livingEntity, item).forEach {
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