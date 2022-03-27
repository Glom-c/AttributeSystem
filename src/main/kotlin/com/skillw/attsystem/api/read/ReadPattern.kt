package com.skillw.attsystem.api.read

import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class ReadPattern(
    val map: ConcurrentHashMap<Int, HashSet<String>>,
    val patterns: List<Pattern>
)