var AttributeSystem = Java.type('com.skillw.attsystem.AttributeSystem')

function crit() {
    const enable = context.get("enable").toString()
    const formula = context.get("value").toString()
    if (enable != "true") {
        //返回值会以 crit 为id 存到FightData里
        return 0.0
    }
    const value = CalculationUtils.getResultDouble(formula)
    data.setResult(value)
    return value
}
