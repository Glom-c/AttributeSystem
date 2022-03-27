package com.skillw.attsystem.internal.listener

import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.EntityUtils.isAlive
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common.util.random
import taboolib.module.nms.*

object PacketListener {
    @SubscribeEvent
    fun e(event: PacketSendEvent) {
        val packet = event.packet
        when (packet.name) {
            "PacketPlayOutWorldParticles" -> handleParticle(packet)
            "PacketPlayOutEntityStatus" -> handleStatus(packet, event.player)
            else -> return
        }
    }

    private fun handleParticle(packet: Packet) {
        if (MinecraftVersion.majorLegacy >= 11300) {
            val particle = packet.read<Any>("j") ?: return
            if (particle.invokeMethod<String>("a") != "damage_indicator") return
        } else {
            val particle = packet.read<Any>("a") ?: return
            if (particle.toString() != "DAMAGE_INDICATOR") return
        }
        packet.write("i", random(3, 15))
    }

    private val CraftWorld by lazy { obcClass("CraftWorld") }
    private val WorldServer by lazy { nmsClass("WorldServer") }
    private val World by lazy { Class.forName("net.minecraft.world.level.World") }
    private fun handleStatus(packet: Packet, player: Player) {
        val world = player.world
        if (ASConfig.noDamageTicksDisableWorlds.contains(world.name)) return
        val craftWorld = CraftWorld.cast(world)
        val nmsWorld =
            if (MinecraftVersion.majorLegacy >= 11800) World.cast(WorldServer.cast(craftWorld.invokeMethod("getHandle"))) else WorldServer.cast(
                craftWorld.invokeMethod("getHandle")
            )
        val entityId = packet.read<Int>("a") ?: return
        val craftEntity =
            if (MinecraftVersion.majorLegacy >= 11800) World.cast(nmsWorld).javaClass.getMethod(
                "a",
                Int::class.java
            ).invoke(
                World.cast(nmsWorld), entityId.toInt()
            ) ?: return else nmsWorld.invokeMethod(
                "getEntity",
                entityId.toInt()
            ) ?: return
        val bukkitEntity = craftEntity.invokeMethod<Entity>("getBukkitEntity") ?: return
        val status = packet.read<Byte>("b") ?: return
        if (bukkitEntity.isAlive() && status.toInt() == 2) {
            (bukkitEntity as LivingEntity).noDamageTicks = ASConfig.noDamageTicks
        }
    }

}