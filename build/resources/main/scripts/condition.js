var Player = Java.type('org.bukkit.entity.Player')
var Coerce = Java.type('com.skillw.pouvoir.taboolib.common5.Coerce')


// 可填 js / groovy / 脚本路径::函数名(单行)
// 变量:     entity : LivingEntity?,     text : String,      name : String
// 含义:          实体                      本行文本             名称
// 返回值为 Boolean ， 是否读取此行属性
function level() {
    var hasEntity = entity != "null"
    if (!hasEntity) return true
    if (!(entity instanceof Player)) {
        return true
    }
    var level = Coerce.toInteger(matcher.group("value"))
    return entity.level >= level
}