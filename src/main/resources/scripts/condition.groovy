import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.script.ScriptTool
import org.bukkit.entity.Player

// 变量:     entity : LivingEntity?,     text : String,      name : String
// 含义:          实体                      本行文本             名称
// 返回值为 Boolean ， 是否读取此行属性

//注解注册单行条件
//格式：@Condition(id,type,names...)
//type: line / strings / all
//js也支持注解哦
//@Condition(class,all,职业限制: (<?classes.*>))
def clazz() {
    def hasEntity = entity != "null"
    if (!hasEntity) return true
    if (!(entity instanceof Player)) return true
    def classes = matcher.group("classes").replace(" ", "").split("/")
    if (classes.length == 0) return true
    def stream = Arrays.stream(classes)
    def isSkillAPI = ASConfig.INSTANCE.getSkillAPI()
    if (isSkillAPI) {
        var SkillAPI = ScriptTool.staticClass("com.sucy.skill.SkillAPI")
        return stream.anyMatch { SkillAPI.getPlayerData(entity).isClass(SkillAPI.getClass(it)) }
    } else {
        return stream.anyMatch { entity.hasPermission("as.class.$it") }
    }
}