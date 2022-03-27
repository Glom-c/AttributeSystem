package com.skillw.attsystem.internal.trigger.holder

import com.skillw.attsystem.api.fight.FightData
import com.sucy.skill.api.event.SkillDamageEvent

class SkillAPITriggerHolder(
    key: String,
    fightData: FightData,
    event: SkillDamageEvent
) :
    DamageTriggerHolder(key, fightData, event) {
    constructor(
        skill: String,
        classification: String, fightData: FightData, event: SkillDamageEvent
    ) : this(
        "skill-api-$skill-$classification",
        fightData,
        event
    )
}