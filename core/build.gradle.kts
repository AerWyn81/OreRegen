import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    implementation("xyz.xenondevs:particle:1.8.1")
    implementation(project(":interface"))
    implementation(project(":v1_16_R3"))
    implementation(project(":v1_17_R1"))
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        if (project.hasProperty("cd"))
            archiveFileName.set("OreRegen.jar")
        else
            archiveFileName.set("OreRegen-${archiveVersion.getOrElse("unknown")}.jar")

        destinationDirectory.set(file(System.getenv("outputDir") ?: "$rootDir/build/"))

        minimize()
    }
}

bukkit {
    name = "OreRegen"
    main = "fr.aerwyn81.oreregen.OreRegen"
    authors = listOf("AerWyn81")
    apiVersion = "1.13"
    description = "Regen defined ores and earn rewards on ore break"
    version = rootProject.version.toString()
    website = "https://just2craft.fr"

    commands {
        register("oreregen") {
            description = "Plugin command"
            aliases = listOf("or")
        }
    }

    permissions {
        register("oreregen.use") {
            description = "Allows players to interact with blocks"
            default = BukkitPluginDescription.Permission.Default.NOT_OP
        }
        register("oreregen.admin") {
            description = "Allows access to /oreregen admin commands"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}