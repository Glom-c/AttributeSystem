plugins {
    java
    id("io.izzel.taboolib") version "1.34"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
}

tasks.dokkaJavadoc.configure {
    val dokkaPath = projectDir.absolutePath.replace(rootDir.absolutePath, "")
    outputDirectory.set(File(rootDir.absolutePath + File.separator + "dokka" + dokkaPath))
    dokkaSourceSets {
        named("main") {
            noJdkLink.set(true)
            noStdlibLink.set(true)
            noAndroidSdkLink.set(true)
            suppressInheritedMembers.set(true)
            suppressObviousFunctions.set(false)
            sourceRoots.setFrom(
                file("src/main/kotlin/com/skillw/attsystem/api"),
                file("src/main/kotlin/com/skillw/attsystem/AttributeSystem.kt")
            )
        }
    }
}

taboolib {

    description {
        contributors {
            name("Glom_")
        }
        dependencies {
            name("Pouvoir")
            name("MythicMobs").optional(true)
        }
    }

    install(
        "common",
        "common-5",
        "module-nms",
        "module-nms-util",
        "module-chat",
        "module-lang",
        "module-configuration",
        "module-metrics",
        "platform-bukkit",
    )

    version = "6.0.7-47"
    classifier = null

}

repositories {
    maven { url = uri("https://repo.tabooproject.org/storages/public/releases") }
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11802:11802:mapped")
    compileOnly("ink.ptms.core:v11802:11802:universal")
    compileOnly("ink.ptms.core:v11604:11604")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}