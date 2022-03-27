import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.script.ScriptTool
import com.skillw.pouvoir.util.CalculationUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

//@Awake(Reload)
def reload() {
    ASConfig.INSTANCE.getDisableCooldownTypes().add(Material.SHIELD)
}

def shield() {
    FightData fightData = data
    Map<String, Object> context = context
    def player = fightData.getDefender()
    if (!(player instanceof Player)) return 0
    def enable = context.get("enable").toString()
    EntityDamageByEntityEvent event = fightData.get("event")
    event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0.0)
    if (enable != "true" || !player.isBlocking() || player.getCooldown(Material.SHIELD) > 0.0) {
        return 0.0
    }
    def reduce = context.get("reduce").toString()
    def reduceValue = CalculationUtils.getResultDouble(reduce)
    double reduced = 0.0
    double origin = fightData.getResult()
    if (reduceValue >= origin) {
        reduced = origin
    } else {
        reduced = reduceValue
    }
    fightData.setResult(origin - reduced)
    def cooldown = context.get("cooldown").toString().replace("{reduced}", reduced.toString())
    def cooldownValue = CalculationUtils.getResultDouble(cooldown)
    ScriptTool.runTask(() -> player.setCooldown(Material.SHIELD, Math.round(cooldownValue).intValue() * 20))
    return reduceValue
}