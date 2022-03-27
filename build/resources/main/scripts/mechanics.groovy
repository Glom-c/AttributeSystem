import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.util.CalculationUtils
import org.bukkit.attribute.Attribute

def damage() {
    //传入 FightData 和 context
    FightData fightData = data
    Map<String, Object> context = context
    //若要处理字符串 用 handle(String)
    //传进来的context已经被handle了一遍 所以不用再handle了
    def enable = context.get("enable").toString()
    def formula = context.get("value").toString()
    if (enable != "true") {
        //以result为键的值将会作为最终伤害
        //Result为0时 即为未命中
        fightData.setResult(0.0)
        //返回值会以 damage 为id 存到FightData里
        return 0.0
    }
    def value = CalculationUtils.getResultDouble(formula)
    fightData.addResult(value)
    //返回值会以 damage 为id 存到FightData里
    return fightData.getResult()
}

def vampire() {
    FightData fightData = data
    Map<String, Object> context = context
    def enable = context.get("enable").toString()
    def formula = context.get("value").toString()
    if (enable != "true") {
        return 0.0
    }
    def attacker = fightData.getAttacker()

    var healthRegain = CalculationUtils.getResultDouble(formula)
    double maxHealth = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
    def healthNow = attacker.health
    def healthValue = healthNow + healthRegain
    if (healthValue >= maxHealth) {
        attacker.health = maxHealth
        healthRegain = maxHealth - healthNow
    } else {
        attacker.health = healthValue
    }
    //建议在伤害类型的配置中配置
//    if (attacker instanceof Player) {
//        attacker.sendMessage("&a吸血: &c$healthRegain")
//    }
    //返回值会以 vampire 为id 存到FightData里
    return healthRegain
}