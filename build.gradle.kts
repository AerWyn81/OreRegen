import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }

    jar {
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
    version = version.toString()
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