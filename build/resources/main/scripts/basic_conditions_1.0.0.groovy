import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.taboolib.common5.Coerce
import com.sucy.skill.api.event.PlayerManaGainEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

import java.util.regex.Matcher

def altitude() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    def min = Coerce.toInteger(matcher.group("min"))
    def max = 10000
    try {
        def maxValue = Coerce.toInteger(matcher.group("max"))
        max = maxValue
    } catch (Exception e) {
    }
    return entity.location.y >= min && entity.location.y <= max
}

def attribute() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def name = matcher.group("name")
    def value = Coerce.toDouble(matcher.group("value"))
    def attribute = AttributeSystem.attributeManager.get(name)
    if (attribute == null) return true
    def compound = AttributeSystem.attributeDataManager.get(entity.uniqueId)
    return compound == null || compound.getAttributeTotal(attribute) >= value
}

def biome() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def name = matcher.group("name")
    def isIn = !matcher.pattern().toString().contains("不")
    def biome = entity.location.world.getBiome(entity.location.blockX, entity.location.blockY, entity.location.blockZ)
    if (isIn) return biome.name() == name.toUpperCase()
    else return biome.name() != name.toUpperCase()
}

def burning() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def isIn = !matcher.pattern().toString().contains("不")
    if (isIn) return entity.getFireTicks() != 0
    else return entity.getFireTicks() == 0
}

def fighting() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def isIn = !matcher.pattern().toString().contains("不")

    if (isIn) return AttributeSystem.fightManager.isFighting(entity)
    else return !AttributeSystem.fightManager.isFighting(entity)
}

def food() {
    def hasEntity = entity != "null"
    if (!hasEntity || !(entity instanceof Player)) return true
    Player player = entity
    Matcher matcher = matcher
    def value = Coerce.toInteger(matcher.group("value"))
    return player.foodLevel >= value
}

def ground() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def isIn = !matcher.pattern().toString().contains("不")
    if (isIn) return entity.isOnGround()
    else return !entity.isOnGround()
}

def health() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    def min = Coerce.toInteger(matcher.group("min"))
    def max = min
    try {
        def maxValue = Coerce.toInteger(matcher.group("max"))
        max = maxValue
    } catch (Exception e) {
    }
    return entity.health >= min && entity.health <= max
}

def permission() {
    def hasEntity = entity != "null"
    if (!hasEntity || !(entity instanceof Player)) return true
    Player player = entity
    Matcher matcher = matcher
    def value = matcher.group("value")
    return player.hasPermission(value)
}

def water() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def isIn = !matcher.pattern().toString().contains("不")
    if (isIn) return entity.isInWaterOrRain()
    else return !entity.isInWaterOrRain()
}

def weather() {
    def hasEntity = entity != "null"
    if (!hasEntity || !(entity instanceof Player)) return true
    Player player = entity
    Matcher matcher = matcher
    def name = matcher.group("name")
    def isIn = !matcher.pattern().toString().contains("不")
    def weather = player.getPlayerWeather().name()
    if (isIn) return weather == name.toUpperCase()
    else return weather != name.toUpperCase()
}

def world() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    LivingEntity entity = entity
    Matcher matcher = matcher
    def name = matcher.group("name")
    def isIn = !matcher.pattern().toString().contains("不")
    def world = entity.location.world.name
    if (isIn) return world == name
    else return world != name
}

def slot() {
    def hasEntity = entity != "null"
    if (slot == "null") return true
    if (!hasEntity || !(entity instanceof Player)) return true
    Player player = entity
    Matcher matcher = matcher
    def value = matcher.group("value")
    return value.equalsIgnoreCase(slot)
}

//@Listerner(-event com.sucy.skill.api.event.PlayerManaGainEvent)
def manaregion() {
    PlayerManaGainEvent event = event
    Player player = event.playerData.player
    double mana = AttributeSystem.formulaManager.get(player, "公式id")
    event.player.getPlayerData().giveMana(mana)
}

